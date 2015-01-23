package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ExpressionStatement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode.ControlNode;

/**
 * 
 * @author HUNG
 *
 */
public class ExpressionStatementNode extends StatementNode {

	private ExpressionNode expression;

	/*
	This class holds the expression that should be evaluated. 

	e.g. $a = 5;
	 $a;
	 3+2;
	*/
	public ExpressionStatementNode(ExpressionStatement expressionStatement) {
		super(expressionStatement);
		expression = ExpressionNode.createInstance(expressionStatement.getExpression());
	}
	
	@Override
	public DataNode execute_(Env env) {
		DataNode retValue = expression.execute(env); 
		if (retValue == ControlNode.EXIT) // e.g. the function call die() returns EXIT
			return ControlNode.EXIT;
		else // TODO Might need to handle multiple returned CONTROL values here?
			return ControlNode.OK;
	}
	
}
