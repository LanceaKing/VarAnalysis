package datamodelviewer;

import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class MyTree {
	
	private MyTreeNode root;
	
	public MyTree(DataNode root) {
		this.root = new MyTreeNode(root, null);
	}

	public MyTreeNode getRoot() {
		return root;
	}
	
}
