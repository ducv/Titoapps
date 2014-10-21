
package com.ducv.fff.chess;

import java.util.ArrayList;

import mediocre.board.Board;
import mediocre.board.Evaluation;
import mediocre.board.Move;
import mediocre.def.Definitions;
import mediocre.engine.Engine;
import android.util.Log;

import com.ducv.fff.chess.utils.HistoryTree;
import com.ducv.fff.chess.utils.Node;

public class VirtualBoard extends Board {

	public static final int BLACK_MATES = 1;
	public static final int WHITE_MATES = 2;
	public static final int WHITE_STALEMATE = 3;
	public static final int BLACK_STALEMATE = 4;
	public static final int DRAW_BY_50_MOVES_RULE = 5;
	public static final int DRAW_BY_REPETITION = 6;
	public static final int DRAW_BY_METERIAL = 7;

	private boolean isGameOver;
	private HistoryTree historyTree;
	private int gameHistoryIndex;
	private int[] gameHistory;
	private final String TAG = "com.fpt.chess.VirtualBoard";

	/**
	 * 
	 */
	public VirtualBoard() {
		// TODO Auto-generated constructor stub
		super();
		historyTree = new HistoryTree();
		isGameOver = false;
		gameHistoryIndex = 0;
		gameHistory = new int[4096];
	}

	/**
	 * Clone this object but only get Board object to do some move To avoid
	 * IndexOutOfBoundException
	 */
	@Override
	public synchronized Board clone() {
		Board clonedBoard = new Board();
		clonedBoard.inputFen(this.getFen());
		return clonedBoard;
	}

	/**
	 * get88Array[][] 
	 * 
	 * @return a two dimensions array - present the board in form of 8x8 array
	 */
	public int[][] get8x8Array() {
		int[][] array = new int[8][8];
		int[] tmp = this.get64Array();
		for (int i = 0; i < 8; i++) {
			System.arraycopy(tmp, i * 8, array[i], 0, 8);
		}
		return array;
	}

	/**
	 * get64Array[] 
	 * 
	 * @return a one dimension array - present the board in form of 64 Int array
	 */
	public int[] get64Array() {
		int[] tmp = new int[64];
		int i = 0;
		int index = 112; // Keeps track of the index on the board

		while (index >= 0) // Run until end of the real board
		{
			if ((index & 0x88) != 0) // Reached the end of a rank
			{
				index -= 24; // Jump to the next rank
			} else // The index is on the real board
			{
				switch (boardArray[index]) {
				// Add the piece on the square
				case W_KING:
					tmp[i++] = W_KING;
					break;
				case W_QUEEN:
					tmp[i++] = W_QUEEN;
					break;
				case W_ROOK:
					tmp[i++] = W_ROOK;
					break;
				case W_BISHOP:
					tmp[i++] = W_BISHOP;
					break;
				case W_KNIGHT:
					tmp[i++] = W_KNIGHT;
					break;
				case W_PAWN:
					tmp[i++] = W_PAWN;
					break;
				case B_KING:
					tmp[i++] = B_KING;
					break;
				case B_QUEEN:
					tmp[i++] = B_QUEEN;
					break;
				case B_ROOK:
					tmp[i++] = B_ROOK;
					break;
				case B_BISHOP:
					tmp[i++] = B_BISHOP;
					break;
				case B_KNIGHT:
					tmp[i++] = B_KNIGHT;
					break;
				case B_PAWN:
					tmp[i++] = B_PAWN;
					break;
				default:
					tmp[i++] = 0; // If no piece, increment the empty square
					// count
				}
				index++; // Go to the next square
			}
		}
		return tmp;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isGameOver() {
		return this.isGameOver;
	}

	/**
	 * Game Over flag
	 * 
	 * @param b
	 */
	public void setGameOver(boolean b) {
		isGameOver = b;
	}

	public boolean isTurnWhite() {
		return this.toMove == Definitions.WHITE_TO_MOVE;
	}

	public boolean isTurnBlack() {
		return this.toMove == Definitions.BLACK_TO_MOVE;
	}

	public int getMoveOfInputNotation(String inputNotation) {
		// TODO Auto-generated method stub
		int[] legalMoves = new int[128];
		int totalMoves = gen_allLegalMoves(legalMoves, 0); // All moves
		Log.i("input:", inputNotation);
		for (int i = 0; i < totalMoves; i++) {
			if (Move.inputNotation(legalMoves[i]).equals(inputNotation)) {
				return legalMoves[i]; // Có
			}
		}
		return 0;
	}
	
	public String[] getLegalMoves(String inputNotation){
		String legalString[] ;
		int[] legalMoves = new int[128];
		int totalMoves = gen_allLegalMoves(legalMoves, 0); // All moves
		legalString = new String[totalMoves];
		for (int i = 0; i < totalMoves; i++) {
			 legalString[i] = Move.inputNotation(legalMoves[i]);
		}
		return legalString;
	}

	public int getMoveOfNotation(String notation) {

		int[] legalMoves = new int[128];
		int totalMoves = gen_allLegalMoves(legalMoves, 0); // All moves

		for (int i = 0; i < totalMoves; i++) {
			// Careful
			if (Move.notation(legalMoves[i], this).equals(notation)) {
				return legalMoves[i]; // Có
			}
		}
		return 0;
	}

	public boolean makeInputNotationMove(String inputNotation) {
		// XXXXXXXXXXXXXXXXXXXX???
		if (inputNotation.equals("0-0-0"))
			inputNotation = "O-O-O";
		if (inputNotation.equals("0-0"))
			inputNotation = "O-O";
		if (inputNotation.endsWith("#") || inputNotation.endsWith("+"))
			inputNotation = inputNotation.substring(0,
					inputNotation.length() - 1);
		// XXXXXXXXXXXXXXXXXXXxx
		int move = getMoveOfInputNotation(inputNotation);
		if (move != 0) {
			// String notation = Move.notation(move, this);
			makeRealMove(move);
			// String fen = this.getFen();
			// Node node = new Node(move,notation,inputNotation,fen);
			// historyTree.addNode(node);
			return true;
		} else {
			// isGameOver = true;
			Log.i("Move:", "can not make move:" + move);
			// throw new ImpossibleMoveException(move);
		}
		return false;
	}

	public boolean makeNotationMove(String notation) {
		if (notation.equals("0-0-0"))
			notation = "O-O-O";
		if (notation.equals("0-0"))
			notation = "O-O";
		// Remove if the function Move.notation is implemented perfectly
		/*
		 * if(notation.endsWith("#") || notation.endsWith("+")) notation =
		 * notation.substring(0,notation.length()-1);
		 */
		// Log.i(TAG, "make move:" + notation);
		int move = 0;
		try {
			move = Move.convertNotationToMove(notation, this); // 

			/*
			 * int pieceMoving = Move.pieceMoving(move); int fromIndex =
			 * Move.fromIndex(move); int toIndex = Move.toIndex(move); int
			 * capture = Move.capture(move); int moveType = Move.moveType(move);
			 * Log.i(TAG,"Move1: " + move); Log.i("pieceMoving: ","" +
			 * pieceMoving); Log.i("fromIndex","" + fromIndex);
			 * Log.i("toIndex","" + toIndex); Log.i("capture","" + capture);
			 * Log.i("moveType","" + moveType);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (move != 0) {
			// String inputNotation = Move.inputNotation(move);
			makeRealMove(move);
			// String fen = this.getFen();
			// Node node = new Node(move,notation,inputNotation,fen);
			// historyTree.addNode(node);
			return true;
		} else {
			Log.i("Move:", "can not make move:" + notation);
			// throw new ImpossibleMoveException(notation);
		}
		return false;
	}

	// ==========================================
	public void makeRealMove(int move) {
		// Log.i(TAG, "makeRealMove: " + move);
		// String notation = Move.notation(move, this);
		String inputNotation = Move.inputNotation(move);
		// int capture = Move.capture(move);
		String notation = Move.notation(move, this);
		try {
			super.makeMove(move);
			gameHistory[gameHistoryIndex++] = move;
		} catch (Exception e) {
			/*
			 * Log.i(TAG, "makeRealMove: " + move);
			 * 
			 * Log.i("FEN: ",this.getFen());
			 * Log.i("makeRealMove-Notation:",notation);
			 */
		}

		String fen = this.getFen();
		// Log.i("Capture value","" + capture);
		// Log.i("makeRealMove-Fen:",fen);
		// Log.i("makeRealMove-Notation:",notation);
		Node node = new Node(move, notation, inputNotation, fen);
		historyTree.addNode(node);
	}

	public boolean forwardMove() {
		Node childNode = historyTree.getLower();// getLastChild();
		if (childNode != null) {
			this.makeMove(childNode.getMove());
			return true;
		} else
			return false;
	}

	public HistoryTree getHistoryTree() {
		return this.historyTree;
	}

	public void clearHistoryTree() {
		historyTree.clear();
	}

	/**
	 * Check the current state of game: win - loose - draw - not end
	 * 
	 * @Note: Clone this object if there is any IndexOutOfBoundException
	 * @return
	 */
	public synchronized int getGameState() {
		int[] legalMoves = new int[128];
		Board board = this.clone();
		if (board.gen_allLegalMoves(legalMoves, 0) == 0) {
			if (Engine.isInCheck(this)) {
				this.setGameOver(true);
				if (this.isTurnWhite()) {
					return BLACK_MATES; // "0-1 (Black mates)";
				} else {
					return WHITE_MATES; // "1-0 (White mates)";
				}
			} else {
				this.setGameOver(true);
				if (this.isTurnWhite()) {
					return BLACK_STALEMATE; // "0-1 (Black mates)";
				} else {
					return WHITE_STALEMATE; // "1-0 (White mates)";
				}
			}
		}

		if (this.movesFifty >= 100) {
			this.setGameOver(true);
			return DRAW_BY_50_MOVES_RULE; // "1/2-1/2 (50 moves rule)";
		}

		// NOTE: If it not necessary
		/*
		 * for (int i = 0; i < gameHistoryIndex; i++) { int repetitions = 0; for
		 * (int j = i + 1; j < gameHistoryIndex; j++) { if (gameHistory[i] ==
		 * gameHistory[j]) repetitions++; if (repetitions == 2) {
		 * this.setGameOver(true); return DRAW_BY_REPETITION; //
		 * "1/2-1/2 (Drawn by repetition)"; } } }
		 */
		if (Evaluation.drawByMaterial(this, 0)) {
			this.setGameOver(true);
			return DRAW_BY_METERIAL; // "1/2-1/2 (Drawn by material)";
		}
		return -1; //
	}

	/**
	 * HitsArrayList is a array list of hit(Notion Move)
	 * 
	 * @param hitsArrayList
	 */
	public void makeBatchMoves(ArrayList<String> hitsArrayList) {
		int size = hitsArrayList.size();
		for (int i = 0; i < size; i++) {
			// Log.d(TAG,"" + hitsArrayList.get(i));
			this.makeNotationMove(hitsArrayList.get(i));
		}
		// Log.d(TAG, "makeBatchMoves Completed");
	}

	public void makeBatchMoves(int[] array, int size) {
		for (int i = 0; i < size; i++) {
			makeRealMove(array[i]);
		}
	}

	/*
	 * public void unmakeLastMove() { if(historyIndex >= 1) {
	 * this.unmakeMove(history[historyIndex]); } }
	 */

	public int[] getMoveArray() throws NullPointerException, Exception {
		int size = historyTree.getNumberOfNodes();
		int array[] = new int[size];
		Node n = historyTree.getRoot();
		for (int i = 0; i < size; i++) {
			array[i] = n.getMove();
			n = n.getFirstChildNode();
		}
		return array;
	}

	/**
	 * Parse pgnData String like: "1. f4 c5 2. Nf3 g6 3. e3 Bg7 4. Nc3 d6 5. Bc4
	 * e6 6. O-O Ne7 7. Qe1 a6 8. a4 Nbc6 9. e4 Nb4 10. Qd1 d5 11. Ba2 dxe4 12.
	 * Nxe4 Nxa2 13. Rxa2 Qd5 14. Nc3 Bxc3 15. dxc3 Qxa2 16. b3 Bd7 17. Ne5 Rd8
	 * 18. f5 exf5 19. Qd6 Be6 20. Qxc5 h6 21. Ba3 O-O 22. Qxe7 Rfe8 23. Qf6
	 * Qxa3 24. Nxg6 Qc5+ 25. Kh1 Kh7 0-1"
	 * 
	 * @param pgnData
	 */
	public void parseMovesString(String pgnData) {
		// TODO Auto-generated method stub
		Log.i(TAG, "pgnData: " + pgnData);
		/*
		 * ArrayList<String> moves =
		 * DatabaseView.getMoveArrayFromString(pgnData); if(moves != null) { int
		 * size = moves.size(); for ( int i =0; i< size; i++) { try {
		 * Log.i(TAG,moves.get(i)); this.makeNotationMove(moves.get(i)); } catch
		 * (Exception e) { Log.e(TAG,"Make move Error: " + moves.get(i));
		 * e.printStackTrace(); } } return; }
		 */

		// / Remove it if you want
		pgnData = pgnData.replace('\n', ' ');
		// Log.i(TAG,"pgnData:|" + pgnData + "|");
		int l = pgnData.length();
		// Vector<String> moves = new Vector<String>();
		String move = "";
		char c = 0;
		boolean ok = false;
		/** Lam don gian truoc */
		for (int i = 0; i < l; i++) {
			c = pgnData.charAt(i);
			if ((c >= 'A' && c <= 'Z' && ok) || (c >= 'a' && c <= 'z' && ok)) {
				ok = false;
				int ii = pgnData.indexOf(' ', i);
				if (ii > 0 && ii < l) {
					try {
						// System.out.println("i: " + i + " , ii: " + ii);
						move = pgnData.substring(i, ii);
						// moves.add(move);
						this.makeNotationMove(move);
					} catch (Exception e) {
						e.printStackTrace();
						Log.e(TAG, "There is a problem!\n" + e.toString());
					}
					i = ii;
				} else {
					break;
				}
				ii = pgnData.indexOf(' ', i + 1);
				if (ii > 0 && ii < l) {
					try {
						// System.out.println("i: " + i + " , ii: " + ii);
						move = pgnData.substring(i + 1, ii).trim();
						// moves.add(move);
						this.makeNotationMove(move);
					} catch (Exception e) {
						e.printStackTrace();
						Log.e(TAG, "There is a problem!\n" + e.toString());
					}
					i = ii;
				} else {
					break;
				}

			} else if ((c == ' ') && ok) {
				ok = false;
				int ii = pgnData.indexOf(' ', i + 1);
				if (ii > 0 && ii < l) {
					try {
						// System.out.println("i: " + i + " , ii: " + ii);
						move = pgnData.substring(i + 1, ii).trim();
						// moves.add(move);
						this.makeNotationMove(move);
					} catch (Exception e) {
						e.printStackTrace();
						Log.e(TAG, "There is a problem!\n" + e.toString());
					}
					i = ii;
				} else {
					break;
				}
				ii = pgnData.indexOf(' ', i + 1);
				if (ii > 0 && ii < l) {
					try {
						// System.out.println("i: " + i + " , ii: " + ii);
						move = pgnData.substring(i + 1, ii).trim();
						// moves.add(move);
						this.makeNotationMove(move);
					} catch (Exception e) {
						e.printStackTrace();
						Log.e(TAG, "There is a problem!\n" + e.toString());
					}
					i = ii;
				} else {
					ok = false;
					break;
				}
			} else if (c == '.') {
				ok = true;
			} else {
				ok = false;
			}
		}

		// int s = moves.size();
		Log.d(TAG, "Parsing completed!");
		/*
		 * for ( int i = 0; i < s ; i++ ) { Log.d(TAG,moves.get(i)); }
		 */
	}
}
