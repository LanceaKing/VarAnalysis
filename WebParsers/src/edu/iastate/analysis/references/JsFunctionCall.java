package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsFunctionCall extends RegularReference {

	public JsFunctionCall(String name, PositionRange location) {
		super(name, location);
	}

	@Override
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		return declaringReference instanceof JsFunctionDecl
				&& hasSameName(declaringReference);
	}
	
}
