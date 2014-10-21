/**
 * 
 */
package com.ducv.fff.chess.utils.pgn;

import java.nio.channels.FileChannel;
import java.util.ArrayList;

import com.ducv.fff.chess.utils.HistoryTree;

public class PGNFileReader {
	private String filename;
	private FileChannel readerchannel;
	private String pgnData;
	private Attributes attributes;
	private HistoryTree historyTree;
	private ArrayList<String> notations;

	/**
	 * 
	 */
	public PGNFileReader() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
