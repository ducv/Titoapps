/**
 * 
 */
package com.ducv.fff.chess.utils;

import android.os.Bundle;
import android.util.Log;

public class History {

	private Node[] allNodes;
	private int historyIndex;
	private int cursor;

	/**
	 * 
	 */
	public History() {
		// TODO Auto-generated constructor stub
		allNodes = new Node[1024];
		historyIndex = 0;
		cursor = 0;
	}

	public void addNode(Node node) {
		//
		historyIndex = cursor;
		//
		allNodes[historyIndex++] = node;
		cursor++;
	}

	public Node getTopNode() {
		if (historyIndex >= 1)
			return allNodes[historyIndex - 1];
		else
			return null;
	}

	public int getNumberOfNodes() {
		return historyIndex;
	}

	public Node getNodeAt(int index) {
		if (index >= 0 && index <= (historyIndex - 1))
			return allNodes[index];
		else
			return null;
	}

	public Node goUpper() {
		// TODO Auto-generated method stub
		if (cursor >= 1)
			return allNodes[--cursor];
		else
			return null;
	}

	public Node goLowwer() {
		if (cursor < historyIndex) {
			return allNodes[cursor++];
		} else
			return null;
	}

	public boolean canForward() {
		return cursor != historyIndex;
	}

	public String getLog() {
		// TODO Auto-generated method stub
		StringBuffer log = new StringBuffer("");
		for (int i = 1; i <= historyIndex; i++) {
			if (i != 1)
				log.append(" "); // Sily
			if (i % 2 == 1) {
				log.append("" + (i + 1) / 2);
				log.append(". ");
			}
			log.append(allNodes[i - 1].getNotation());
		}
		return log.toString();
	}

	public void saveInstanceState(Bundle outState) {
		outState.putInt("historyIndex", historyIndex);
		outState.putInt("cursor", cursor);
		for (int i = 0; i < historyIndex; i++) {
			outState.putInt("move" + i, allNodes[i].getMove());
			outState.putString("notation" + i, allNodes[i].getNotation());
			outState.putString("inputNotation" + i, allNodes[i]
					.getInputNotion());
			outState.putString("fen" + i, allNodes[i].getFEN());
		}
	}

	public void restoreInstanceState(Bundle savedInstanceState) {
		historyIndex = savedInstanceState.getInt("historyIndex");
		cursor = savedInstanceState.getInt("cursor");
		for (int i = 0; i < historyIndex; i++) {
			int move = savedInstanceState.getInt("move" + i);
			String notation = savedInstanceState.getString("notation" + i);
			String inputNotation = savedInstanceState.getString("inputNotation"
					+ i);
			String fen = savedInstanceState.getString("fen" + i);
			Node node = new Node(move, notation, inputNotation, fen);
			Log.i("RestoreHistory", "move:" + move + "\tnotation:" + notation
					+ "\tInputNotation: " + inputNotation + "\t" + "\tfen: "
					+ fen);
			allNodes[i] = node;
		}
	}
}
