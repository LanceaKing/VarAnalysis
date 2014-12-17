package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.DoStatement;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class DoStatementNode extends StatementNode {

	private LiteralNode conditionString;
	private ExpressionNode condition;	
	private StatementNode body;
	
	/*
	Represent do while statement.
	
	e.g.
	 do {
	   echo $i;
	 } while ($i > 0);
	*/
	public DoStatementNode(DoStatement doStatement) {
		super(doStatement);
		condition = ExpressionNode.createInstance(doStatement.getCondition());
		conditionString = DataNodeFactory.createLiteralNode(condition);
		body = StatementNode.createInstance(doStatement.getBody());
	}

	@Override
	public DataNode execute(Env env) {
		body.execute(env);
		condition.execute(env);
		return WhileStatementNode.execute(env, conditionString, body);
	}
	
}
