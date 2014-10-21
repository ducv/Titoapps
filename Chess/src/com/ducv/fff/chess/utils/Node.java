package com.ducv.fff.chess.utils;

import java.util.Vector;

public class Node {
	private int move;
	private String notation;
	private String inputNotation;
	private String fen;
	private String comment = "";
	private Vector<Node> childNodes;
	private int numberOfChilds;
	private Node father;

	/**
	 * 
	 */
	public Node(Node father, int move, String notation, String inputNotation,
			String fen) {
		// TODO Auto-generated constructor stub
		this.father = father;
		this.move = move;
		this.notation = notation;
		this.inputNotation = inputNotation;
		this.fen = fen;
		childNodes = new Vector<Node>();
		numberOfChilds = 0;
	}

	public Node(int move, String notation, String inputNotation, String fen) {
		// TODO Auto-generated constructor stub
		this.father = null;
		this.move = move;
		this.notation = notation;
		this.inputNotation = inputNotation;
		this.fen = fen;
		childNodes = new Vector<Node>();
		numberOfChilds = 0;
	}

	public int getMove() {
		return this.move;
	}

	public String getNotation() {
		return this.notation;
	}

	public String getInputNotion() {
		return this.inputNotation;
	}

	public String getFEN() {
		return this.fen;
	}

	public Node getFirstChildNode() {
		if (!childNodes.isEmpty())
			return childNodes.get(0);
		else
			return null;
	}

	public Node getChild(int i) {
		if (childNodes.size() > i + 1)
			return childNodes.get(i);
		else
			return null;
	}

	public void setNotation(String s) {
		this.notation = s;
	}

	public void addChild(Node child) {
		childNodes.add(numberOfChilds, child);
		numberOfChilds++;
	}

	/**
	 * Please use addChild instead of this by default; But this is a clear way
	 * :D
	 * 
	 * @param child
	 */
	public void setFirstChild(Node child) {
		childNodes.add(0, child);
		numberOfChilds = 1;
	}

	public int getNumberOfChilds() {
		return this.numberOfChilds;
	}

	public Node getFather() {
		return this.father;
	}

	public void setFather(Node node) {
		this.father = node;
	}

	public void setComment(String mComment) {
		this.comment = mComment;
	}

	public String getComment() {
		// TODO Auto-generated method stub
		return this.comment;
	}
}
