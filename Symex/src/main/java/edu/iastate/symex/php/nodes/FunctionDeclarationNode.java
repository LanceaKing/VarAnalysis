package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.ast.nodes.FunctionDeclaration;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class FunctionDeclarationNode extends StatementNode {
	
	private String name;	// The name of the function	
	
	private ArrayList<FormalParameterNode> formalParameters = new ArrayList<FormalParameterNode>();	// The parameters of the function 
	
	private BlockNode body;	// The body of the function
	
	/*
	Represents a function declaration 
	
	e.g. function foo() {}
	 
	 function &foo() {}
	 
	 function foo($a, int $b, $c = 5, int $d = 6) {}
	 
	 function foo(); -abstract function in class declaration
	*/
	public FunctionDeclarationNode(FunctionDeclaration functionDeclaration) {
		super(functionDeclaration);
		name = functionDeclaration.getFunctionName().getName();
		for (FormalParameter formalParameter : functionDeclaration.formalParameters()) {
			formalParameters.add(new FormalParameterNode(formalParameter));
		}
		body = new BlockNode(functionDeclaration.getBody());
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<FormalParameterNode> getFormalParameters() {
		return new ArrayList<FormalParameterNode>(formalParameters);
	}
	
	public BlockNode getBody() {
		return body;
	}
	
	/*
	 * The following code is used from BabelRef to identify PHP variable entities.
	 */
	// BEGIN OF BABELREF CODE
//	public interface IFormalParameterListener {
//		public void formalParameterFound(IdentifierNode variableName, String scope);
//	}
//	
//	public static IFormalParameterListener formalParameterListener = null;
	// END OF BABELREF CODE

	@Override
	public DataNode execute(Env env) {
		/*
		 * The following code is used from BabelRef to identify PHP variable entities.
		 */
		// BEGIN OF BABELREF CODE
//		if (formalParameterListener != null) {
//			for (FormalParameterNode formalParameterNode : formalParameters) {
//				if (formalParameterNode.getParameterName() instanceof IdentifierNode) {
//					IdentifierNode parameterName = (IdentifierNode) formalParameterNode.getParameterName();
//					String scope = "FUNCTION_SCOPE_" + getName(); // @see edu.iastate.symex.php.nodes.VariableNode.getFunctionScope(env)
//					formalParameterListener.formalParameterFound(parameterName, scope);
//				}
//			}
//		}
		// END OF BABELREF CODE
			
		env.putFunction(name, this);
		return null;
	}

}