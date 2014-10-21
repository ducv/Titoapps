/**
 * Nguyen van Duong - G11.TD - FPT Software
 * Smart Chess - A chess training program for Android
 * <<<****>>>
 */
/**
 * A tree for goNext and goPrevious and saving all game states.
 */
package com.ducv.fff.chess.utils;

import android.util.Log;

public class HistoryTree {
	private int numberOfNodes;
	private Node root;
	private Node currentNode; // This is a bad thing
	// Please replace it by index
	private int[] history = new int[1024];
	private int history_index = 0;

	/**
	 * 
	 */
	public HistoryTree() {
		// TODO Auto-generated constructor stub
		numberOfNodes = 0;
		root = null;
		currentNode = null;
	}

	public void addNode(Node node) {
		// Log.i("HistoryTree", "" + node.getMove());
		if (currentNode == null || root == null) {
			root = node;
			currentNode = root;
		} else {
			/*
			 * Node r = root; while(r.getFirstChildNode() != null) { r =
			 * r.getFirstChildNode(); } r.setFirstChild(node);
			 * node.setFather(r);
			 */
			currentNode.addChild(node);
			currentNode = node;
		}
		currentNode = node;
		// Please update the index of
		history[history_index++] = node.getMove();
	}

	/*
	 * public void addNode(Node father, Node child) { if (father == null) {
	 * String detailMessage = "You can not add a child to a null father"; throw
	 * new RuntimeException(detailMessage); } else { father.addChild(child);
	 * currentNode = child; } }
	 */

	public Node getRoot() {
		return root;
	}

	public Node getCurrentNode() {
		return this.currentNode;
	}

	public int getNumberOfNodes() {
		return this.numberOfNodes;
	}

	public Node goUpper() {
		// TODO Auto-generated method stub
		Node n = null;
		if ((n = currentNode.getFather()) != null) {
			currentNode = n;
			return currentNode;
		} else {
			return null;
		}
	}

	public Node getLower() {
		// TODO Auto-generated method stub
		Node n = null;
		if ((n = currentNode.getFirstChildNode()) != null) {
			currentNode = n;
			return currentNode;
		} else {
			return null;
		}
	}

	public int up() {

		if (history_index >= 1) {
			history_index--;
			Log.i("History_Index", "" + this.history[history_index]);
			return this.history[history_index];
		} else
			return 0;
	}

	public void clear() {
		// TODO Auto-generated method stub
		// xx
	}

	@Override
	public String toString() {
		return "";
	}

	public String getLastMove() {
		return currentNode.getNotation();
	}
}
