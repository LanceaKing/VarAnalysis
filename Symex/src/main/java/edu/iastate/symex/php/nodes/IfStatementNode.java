package edu.iastate.symex.php.nodes;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.php.internal.core.ast.nodes.IfStatement;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.core.BranchEnv;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.SpecialNode.ControlNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode.BooleanNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode.UnsetNode;
import edu.iastate.symex.instrumentation.WebAnalysis;

/**
 * 
 * @author HUNG
 *
 */
public class IfStatementNode extends StatementNode {

	private ExpressionNode condition;
	private StatementNode trueStatement;
	private StatementNode falseStatement;
	
	/*
	Represents if statement 

	e.g. 
	 if ($a > $b) {
	   echo "a is bigger than b";
	 } elseif ($a == $b) {
	   echo "a is equal to b";
	 } else {
	   echo "a is smaller than b";
	 },
	 
	 if ($a):
	   echo "a is bigger than b";
	   echo "a is NOT bigger than b";
	 endif;
	 */
	public IfStatementNode(IfStatement ifStatement) {
		super(ifStatement);
		condition = ExpressionNode.createInstance(ifStatement.getCondition());
		trueStatement = StatementNode.createInstance(ifStatement.getTrueStatement());
		falseStatement = (ifStatement.getFalseStatement() != null ? StatementNode.createInstance(ifStatement.getFalseStatement()) : null);
	}
	
	@Override
	public DataNode execute_(Env env) {
		return IfStatementNode.execute(env, condition, trueStatement, falseStatement, false);
	}
	
	/**
	 * Executes different branches and updates the env accordingly.
	 * Depending on the evaluated result of the condition, we may execute only one branch or both branches.
	 * @see {@link edu.iastate.symex.php.nodes.ConditionalExpressionNode#execute(Env)}
	 */
	public static DataNode execute(Env env, ExpressionNode condition, PhpNode trueNode, PhpNode falseNode, boolean isExpression) {
		DataNode evaluatedCondition = condition.execute(env);
		BooleanNode conditionValue = evaluatedCondition.convertToBooleanValue();
		
		/*
		 * If condition evaluates to either TRUE or FALSE, then execute the corresponding branch only.
		 */
		if (conditionValue.isTrueValue()) {
			if (trueNode != null)
				return trueNode.execute(env);
			else 
				return (isExpression ? UnsetNode.UNSET : ControlNode.OK);
		}
		else if (conditionValue.isFalseValue()) {
			if (falseNode != null)
				return falseNode.execute(env);
			else
				return (isExpression ? UnsetNode.UNSET : ControlNode.OK);
		}
		
		/*
		 * Else, execute both branches.
		 */
		Constraint constraint = ConstraintFactory.createAtomicConstraint(condition.getSourceCode(), condition.getLocation());
		
		return execute(env, constraint, trueNode, falseNode, isExpression);
	}
	
	/**
	 * Executes different branches and updates the env accordingly.
	 * @see {@link edu.iastate.symex.php.nodes.SwitchStatementNode.FakeSwitchStatementNode#execute(Env)}
	 */
	public static DataNode execute(Env env, Constraint constraint, PhpNode trueNode, PhpNode falseNode, boolean isExpression) {
		/*
		 * Execute the branches
		 */
		HashMap<PhpVariable, DataNode> dirtyVarsInTrueBranch = new HashMap<PhpVariable, DataNode>();
		HashMap<PhpVariable, DataNode> dirtyVarsInFalseBranch = new HashMap<PhpVariable, DataNode>();
		DataNode trueBranchRetValue = (isExpression ? UnsetNode.UNSET : ControlNode.OK);
		DataNode falseBranchRetValue = (isExpression ? UnsetNode.UNSET : ControlNode.OK);

		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		if (WebAnalysis.isEnabled())
			WebAnalysis.onTrueBranchExecutionStarted(env);
		// END OF WEB ANALYSIS CODE
		
		if (trueNode != null) {
			BranchEnv trueBranchEnv = new BranchEnv(env, constraint);
			trueBranchRetValue = trueNode.execute(trueBranchEnv);
			dirtyVarsInTrueBranch = env.backtrackAfterExecution(trueBranchEnv);
		}

		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		if (WebAnalysis.isEnabled())
			WebAnalysis.onFalseBranchExecutionStarted(env);
		// END OF WEB ANALYSIS CODE
		
		if (falseNode != null) {
			BranchEnv falseBranchEnv = new BranchEnv(env, ConstraintFactory.createNotConstraint(constraint));
			falseBranchRetValue = falseNode.execute(falseBranchEnv);
			dirtyVarsInFalseBranch = env.backtrackAfterExecution(falseBranchEnv);
		}
		
		/*
		 * Update variables' values after executing the two branches.
		 * 
		 * If a branch contains an exit/return statement, then we apply a different treatment.
		 * For example, with an ifStatement: if (C) { A; return; } else B;
		 *   we disregard the results from the true branch entirely.
		 * The reason is that we don't want values in the "exception" flows to intefere with values in the normal flow.
		 *   (e.g., if there are echo statements in the exception branch, we don't want to them to get concatenated with other strings after the branches.)
		 * To prevent some important values (output, return values) from getting lost due to the above cutting,
		 *    at exit/return statements, we store these values immediately.
		 * See the implementation of ReturnStatement and FunctionInvocation(exit) for more details.
		 */
		if (isExitOrReturn(trueBranchRetValue)) {
			if (isExitOrReturn(falseBranchRetValue)) {
				// Do nothing
			}
			else {
				// Exception case
				env.updateWithOneBranchOnly(dirtyVarsInFalseBranch);
			}
		}
		else {
			if (isExitOrReturn(falseBranchRetValue)) {
				// Exception case
				env.updateWithOneBranchOnly(dirtyVarsInTrueBranch);
			}
			else {
				// Normal case
				env.updateAfterBranchExecution(constraint, dirtyVarsInTrueBranch, dirtyVarsInFalseBranch);
			}
		}
		
		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		if (WebAnalysis.isEnabled())
			WebAnalysis.onBothBranchesExecutionFinished(new HashSet<PhpVariable>(dirtyVarsInTrueBranch.keySet()), new HashSet<PhpVariable>(dirtyVarsInFalseBranch.keySet()), env);
		// END OF WEB ANALYSIS CODE
		
		return DataNodeFactory.createCompactSelectNode(constraint, trueBranchRetValue, falseBranchRetValue);		
	}
	
	/**
	 * Returns true if the CONTROL value is an EXIT or RETURN.
	 */
	private static boolean isExitOrReturn(DataNode control) {
		return control == ControlNode.EXIT || control == ControlNode.RETURN;
	}

}
