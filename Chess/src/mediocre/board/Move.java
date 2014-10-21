package mediocre.board;

import mediocre.def.Definitions;
import mediocre.engine.Engine;
import android.util.Log;

import com.ducv.fff.chess.VirtualBoard;

/**
 * class Move
 * 
 * Contains static methods to analyze a move integer
 * 
 * First created: 2006-02-11
 * 
 * @author Jonatan Pettersson (mediocrechess@gmail.com)
 */
public class Move implements Definitions {
	// Number of bits to shift to get the to-index
	public static final int TO_SHIFT = 7;
	// To get the piece moving
	public static final int PIECE_SHIFT = 14;
	// To get the capture
	public static final int CAPTURE_SHIFT = 18;
	// To the move type
	public static final int TYPE_SHIFT = 22;
	// To get the ordering value
	public static final int ORDERING_SHIFT = 25;
	// 7 bits, masks out the rest of the int when it has been shifted so we only
	// get the information we need
	public static final int SQUARE_MASK = 127;
	// 4 bits
	public static final int PIECE_MASK = 15;
	// 3 bits
	public static final int TYPE_MASK = 7;
	// Equals 00000001111111111111111111111111 use with & which clears the
	// ordering value
	public static final int ORDERING_CLEAR = 0x1FFFFFF;

	/**
	 * @return int Piece moving
	 */
	public static int pieceMoving(int move) {
		// 7 is the offset, so we get negative values for black pieces
		return ((move >> PIECE_SHIFT) & PIECE_MASK) - 7;
	}

	/**
	 * @return int To-index
	 */
	public static int toIndex(int move) {
		return ((move >> TO_SHIFT) & SQUARE_MASK);
	}

	/**
	 * @return int From-index
	 */
	public static int fromIndex(int move) {
		// Since the from-index is first in the integer it doesn't need to be
		// shifted first
		return (move & SQUARE_MASK);
	}

	/**
	 * @return int Piece captured
	 */
	public static int capture(int move) {
		return ((move >> CAPTURE_SHIFT) & PIECE_MASK) - 7;
	}

	/**
	 * @return int Move type
	 */
	public static int moveType(int move) {
		return ((move >> TYPE_SHIFT) & TYPE_MASK);
	}

	/**
	 * @return int Ordering value
	 */
	public static int orderingValue(int move) {
		// Since the ordering value is last in the integer it doesn't need a
		// mask
		return (move >> ORDERING_SHIFT);
	}

	/**
	 * Clears the ordering value and sets it to the new number
	 * 
	 * Important: Ordering value in the move integer cannot be >127
	 * 
	 * @param move
	 *            The move to change
	 * @param value
	 *            The new ordering value
	 * @return move The changed moved integer
	 */
	public static int setOrderingValue(int move, int value) {
		// Clear the ordering value
		move = (move & ORDERING_CLEAR);
		// Change the ordering value and return the new move integer
		return (move | (value << ORDERING_SHIFT));
	}

	/**
	 * Creates a move integer from the gives values
	 * 
	 * @param pieceMoving
	 * @param fromIndex
	 * @param toIndex
	 * @param capture
	 * @param type
	 * @param ordering
	 *            If we want to assign an ordering value at creation time,
	 *            probably won't be used much for now
	 * @return move The finished move integer
	 */
	public static int createMove(int pieceMoving, int fromIndex, int toIndex,
			int capture, int type, int ordering) {

		// "or" every value into the move, start with empty then (in order)
		// from, to, piece moving (offset 7), piece captured (offset 7), move
		// type, ordering value
		return 0 | fromIndex | (toIndex << TO_SHIFT)
				| ((pieceMoving + 7) << PIECE_SHIFT)
				| ((capture + 7) << CAPTURE_SHIFT) | (type << TYPE_SHIFT)
				| (ordering << ORDERING_SHIFT);
	}

	/**
	 * Returns a string holding the short notation of the move
	 * 
	 * @return String Short notation
	 */
	public static String notation(int move) {
		// Get the values from the move
		int pieceMoving = pieceMoving(move);
		int fromIndex = fromIndex(move);
		int toIndex = toIndex(move);
		int capture = capture(move);
		int moveType = moveType(move);

		final StringBuilder notation = new StringBuilder();

		// Add the piece notation
		switch (pieceMoving) {
		case W_KING: {
			if (moveType == SHORT_CASTLE)
				return "0-0";
			if (moveType == LONG_CASTLE)
				return "0-0-0";
			notation.append("K");
			break;
		}
		case B_KING: {
			if (moveType == SHORT_CASTLE)
				return "0-0";
			if (moveType == LONG_CASTLE)
				return "0-0-0";
			notation.append("K");
			break;
		}
		case W_QUEEN:
		case B_QUEEN:
			notation.append("Q");
			break;
		case W_ROOK:
		case B_ROOK:
			notation.append("R");
			break;
		case W_BISHOP:
		case B_BISHOP:
			notation.append("B");
			break;
		case W_KNIGHT:
		case B_KNIGHT:
			notation.append("N");
			break;
		}

		// The move is a capture
		if (capture != 0) {
			// If the moving piece is a pawn we need to add the row it's moving
			// from
			if ((pieceMoving == W_PAWN) || (pieceMoving == B_PAWN)) {
				// Find the row
				notation.append("abcdefgh".charAt(fromIndex % 16));
			}
			notation.append("x");
		}

		// Find the row
		notation.append("abcdefgh".charAt(toIndex % 16));

		// Add the rank
		notation.append((toIndex - (toIndex % 16)) / 16 + 1);

		/*
		 * if (moveType == EN_PASSANT) notation.append(" e.p.");
		 */

		// Add promotion
		switch (moveType) {
		case PROMOTION_QUEEN:
			notation.append("=Q");
			break;
		case PROMOTION_ROOK:
			notation.append("=R");
			break;
		case PROMOTION_BISHOP:
			notation.append("=B");
			break;
		case PROMOTION_KNIGHT:
			notation.append("=N");
			break;
		}

		return notation.toString();
	}

	/**
	 * Returns the move on the form 'e2e4', that is only the from and to square
	 * 
	 * @return String The input notation
	 */
	public static String inputNotation(int move) {

		// Gather the information from the move int
		int fromIndex = fromIndex(move);
		int toIndex = toIndex(move);
		int moveType = moveType(move);

		StringBuilder inputNotation = new StringBuilder();
		positionToString(fromIndex, inputNotation);
		positionToString(toIndex, inputNotation);

		// Check for promotion
		switch (moveType) {
		case PROMOTION_QUEEN:
			inputNotation.append("q");
			break;
		case PROMOTION_ROOK:
			inputNotation.append("r");
			break;
		case PROMOTION_BISHOP:
			inputNotation.append("b");
			break;
		case PROMOTION_KNIGHT:
			inputNotation.append("n");
			break;
		}
		/*
		 * if(board.enPassant != -1) { if(toIndex == board.enPassant) { moveType
		 * = EN_PASSANT; } }
		 */
		return inputNotation.toString();
	}

	private static void positionToString(final int position,
			final StringBuilder buffer) {
		buffer.append("abcdefgh".charAt(position % 16));
		buffer.append(((position - (position % 16)) / 16) + 1);
	}

	/**
	 * 
	 * @param move
	 * @param virtualBoard
	 * @return
	 */
	/*
	 * public static String notation(int move, VirtualBoard virtualboard) {
	 * Board board = virtualboard.clone(); int [] allMoves = new int [128]; int
	 * nMoves = board.gen_allLegalMoves(allMoves, 0); return
	 * Move.notation(nMoves, board,allMoves,nMoves); }
	 */
	/**
	 * Returns a string holding the short notation of the move
	 * 
	 * @return String Short notation
	 */
	public static String notation(int move, VirtualBoard virtualboard/*
																	 * , int []
																	 * allMoves
																	 * ,int
																	 * nMoves
																	 */) {
		// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
		Board board = virtualboard.clone();
		int[] allMoves = new int[128];
		int nMoves = board.gen_allLegalMoves(allMoves, 0);
		// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
		// Get the values from the move
		int pieceMoving = pieceMoving(move);
		int fromIndex = fromIndex(move);
		int toIndex = toIndex(move);
		int capture = capture(move);
		int moveType = moveType(move);

		// Log.i("Info:","pieceMoving: " + pieceMoving + "\tfromIndex:" +
		// fromIndex + "\ttoIndex:" + toIndex
		// + "\tcapture:" + capture + "\tmoveType:" + moveType);
		final StringBuilder notation = new StringBuilder();

		// Add the piece notation
		switch (pieceMoving) {
		case W_KING: {
			if (moveType == SHORT_CASTLE)
				return "O-O";
			if (moveType == LONG_CASTLE)
				return "O-O-O";
			notation.append("K");
			break;
		}
		case B_KING: {
			if (moveType == SHORT_CASTLE)
				return "O-O";
			if (moveType == LONG_CASTLE)
				return "O-O-O";
			notation.append("K");
			break;
		}
		case W_QUEEN:
		case B_QUEEN:
			notation.append("Q");
			break;
		case W_ROOK:
		case B_ROOK:
			notation.append("R");
			break;
		case W_BISHOP:
		case B_BISHOP:
			notation.append("B");
			break;
		case W_KNIGHT:
		case B_KNIGHT:
			notation.append("N");
			break;
		}

		// /WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
		// Find moving pieces which has the same destition
		int[] samePieces = new int[10]; // Max is 10, hehe
		int _spIndex = 0;
		for (int i = 0; i < nMoves; i++) {
			// if there are
			if (pieceMoving == pieceMoving(allMoves[i])
					&& toIndex == toIndex(allMoves[i]) && pieceMoving != W_PAWN
					&& pieceMoving != B_PAWN) {
				// Save to array
				samePieces[_spIndex++] = allMoves[i];
			}
		}

		// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
		// phan biet neu nhu co >2 quan cung loai va cung to 1 dich
		// Phan biet file ( a-h) truoc, rank ( 1-8) sau
		if (_spIndex > 1) {
			// file truoc
			int file = fromIndex % 16;
			// int files[] = new int [10];
			int filesindex = 0;
			for (int i = 0; i < _spIndex; i++) {
				int f = fromIndex(samePieces[i]);
				f = f % 16;
				if (f == file) {
					filesindex++;// files[filesindex++] = samePieces[i];
				}
			}
			// rank sau
			int rank = (fromIndex - (fromIndex % 16)) / 16 + 1;
			// int ranks[] = new int [10];
			int ranksindex = 0;
			for (int i = 0; i < _spIndex; i++) {
				int t = fromIndex(samePieces[i]);
				t = (t - (t % 16)) / 16 + 1;
				if (t == rank)
					ranksindex++;// ranks[ranksindex++] = samePieces[i];
			}
			if (filesindex == 1)
				notation.append("abcdefgh".charAt(file));
			else if (ranksindex == 1)
				notation.append(rank);
			else {
				notation.append("abcdefgh".charAt(file));
				notation.append(rank);
			}
		}

		// /WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
		// The move is a capture
		if (capture != 0) {
			// If the moving piece is a pawn we need to add the row it's moving
			// from
			if ((pieceMoving == W_PAWN) || (pieceMoving == B_PAWN)) {
				// Find the row
				notation.append("abcdefgh".charAt(fromIndex % 16));
			}
			notation.append("x");
		}

		// Find the row
		notation.append("abcdefgh".charAt(toIndex % 16));

		// Add the rank
		notation.append((toIndex - (toIndex % 16)) / 16 + 1);

		if (moveType == EN_PASSANT) {
			// notation.append(" e.p.");
			// return notation.toString();
		}

		// Add promotion
		switch (moveType) {
		case PROMOTION_QUEEN:
			notation.append("=Q");
			break;
		case PROMOTION_ROOK:
			notation.append("=R");
			break;
		case PROMOTION_BISHOP:
			notation.append("=B");
			break;
		case PROMOTION_KNIGHT:
			notation.append("=N");
			break;
		}
		// /WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
		// / Add # or + to the end
		// Log.d("Move.notation", "create accurate notation move:" + notation);
		try {
			board.makeMove(move);
		} catch (IndexOutOfBoundsException e) {
			Log.e("Move.notation", "IndexOutofBoundException");
			notation.append("#");
			board = null;
			allMoves = null;
			System.gc();
			Log.d("Move.notation", "Creating notation completed: " + notation);
			return notation.toString();
		}
		if (Engine.isInCheck(board)) {
			int[] legalMoves = new int[128];
			if (board.gen_allLegalMoves(legalMoves, 0) == 0) {
				notation.append("#");
				virtualboard.setGameOver(true);
			} else
				notation.append("+");
		} else {
			int[] legalMoves = new int[128];
			if (board.gen_allLegalMoves(legalMoves, 0) == 0) {
				virtualboard.setGameOver(true);
			}
		}
		// board.unmakeMove(move); // do not necessary
		board = null;
		allMoves = null;
		// Log.d("Move.notation", "Creating notation completed: " + notation);
		return notation.toString();
	}

	/**
	 * TODO Convert a move from notation to int
	 * 
	 * @param notation
	 * @param board
	 * @return converted move in int
	 */
	public static int convertNotationToMove(String notation, Board board) {
		notation = notation.trim();
		// Log.i("ConvertNotationToMOvve",notation);
		boolean isWhiteMove = (board.toMove == Definitions.WHITE_TO_MOVE);
		if (notation.equals("O-O"))
			return createMove(isWhiteMove ? W_KING : B_KING, isWhiteMove ? 4
					: 116, isWhiteMove ? 6 : 118, 0, SHORT_CASTLE, 0);
		else if (notation.equals("O-O-O"))
			return createMove(isWhiteMove ? W_KING : B_KING, isWhiteMove ? 4
					: 116, isWhiteMove ? 2 : 114/* 1 : 113 */, 0, LONG_CASTLE,
					0);

		if (notation.endsWith("+") || notation.endsWith("#"))
			notation = notation.substring(0, notation.length() - 1);

		int moveType = ORDINARY_MOVE;
		if (notation.endsWith(" e.p.")) {
			moveType = EN_PASSANT;
			notation = notation.substring(0, notation.length() - 5);
		} else if (notation.endsWith("=Q")) {
			moveType = PROMOTION_QUEEN;
			notation = notation.substring(0, notation.length() - 2);
		} else if (notation.endsWith("=R")) {
			moveType = PROMOTION_ROOK;
			notation = notation.substring(0, notation.length() - 2);
		} else if (notation.endsWith("=B")) {
			moveType = PROMOTION_BISHOP;
			notation = notation.substring(0, notation.length() - 2);
		} else if (notation.endsWith("=N")) {
			moveType = PROMOTION_KNIGHT;
			notation = notation.substring(0, notation.length() - 2);
		}

		char c;
		int rank = 0;
		int file = 0;
		c = notation.charAt(notation.length() - 1);
		rank = c - '1';
		c = notation.charAt(notation.length() - 2);
		file = c - 'a';

		notation = notation.substring(0, notation.length() - 2);
		int toIndex = file + rank * 16;

		// Log.i("IsCapture:",notation);
		int capture = 0;
		if (notation.length() != 0)
			if (notation.charAt(notation.length() - 1) == 'x') {
				capture = board.boardArray[toIndex];
				// Log.e("Capture Piece: "," Piece: " + capture);
				notation = notation.substring(0, notation.length() - 1);
				// Log.i("IsCapture after Cut:",notation);
			}
		// Log.d("converNotation2:","Truoc:" + notation);
		int pieceMoving = isWhiteMove ? W_PAWN : B_PAWN;
		if (notation.length() == 0)
			pieceMoving = isWhiteMove ? W_PAWN : B_PAWN;
		else {
			c = notation.charAt(0);
			switch (c) {
			case 'K':
				pieceMoving = isWhiteMove ? W_KING : B_KING;
				notation = notation.substring(1);
				break;
			case 'Q':
				pieceMoving = isWhiteMove ? W_QUEEN : B_QUEEN;
				notation = notation.substring(1);
				break;
			case 'R':
				pieceMoving = isWhiteMove ? W_ROOK : B_ROOK;
				notation = notation.substring(1);
				break;
			case 'B':
				pieceMoving = isWhiteMove ? W_BISHOP : B_BISHOP;
				notation = notation.substring(1);
				break;
			case 'N':
				pieceMoving = isWhiteMove ? W_KNIGHT : B_KNIGHT;
				notation = notation.substring(1);
				break;
			default:
				break;
			}
		}

		// ############################ En passant processing
		if (board.enPassant != -1) {
			if (toIndex == board.enPassant) {
				moveType = EN_PASSANT;
			}
		}
		// ############################
		// Log.d("converNotation3:","Sau:" + notation);

		int fromIndex = 0;
		// Tinh vi tri (index) cua quan di(phan nay rat kho
		if (notation.length() == 0) { // dang Nf3 nghia la length cua no = 0;
			/*
			 * ArrayList<Integer> arr = new ArrayList<Integer>(); for (int i =
			 * 0; i < 128; i++) { if (pieceMoving == board.boardArray[i])
			 * arr.add(i); fromIndex = i; // }
			 */
			// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
			int[] legalMoves = new int[128];
			int totalMoves = board.gen_allLegalMoves(legalMoves, 0); // All
																		// moves

			for (int i = 0; i < totalMoves; i++) {
				// Careful
				if (pieceMoving == pieceMoving(legalMoves[i])) {
					if (toIndex == toIndex(legalMoves[i])) {
						if (moveType == moveType(legalMoves[i])) {
							if (true/* capture == capture(legalMoves[i]) */) {
								return legalMoves[i];
							}
						}
					}
				}
			}
			// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
		} else if (notation.length() == 1) { // Dang Reg1 nghia la length cua no
												// = 1
			c = notation.charAt(0);
			int index;
			if (c >= 'a' && c <= 'h') {
				index = c - 'a';
				/*
				 * for (int i = index * 16; i < 128; i++) if (pieceMoving ==
				 * board.boardArray[i]) fromIndex = i;
				 */
				// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
				int[] legalMoves = new int[128];
				int totalMoves = board.gen_allLegalMoves(legalMoves, 0); // All
																			// moves

				for (int i = 0; i < totalMoves; i++) {
					// Careful
					if (pieceMoving == pieceMoving(legalMoves[i])) {
						if (toIndex == toIndex(legalMoves[i])) {
							if (moveType == moveType(legalMoves[i])) {
								if (true/* capture == capture(legalMoves[i]) */) {
									if (index == fromIndex(legalMoves[i]) % 16)
										return legalMoves[i];
								}
							}
						}
					}
				}
				// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
			} else if (c >= '1' && c <= '8') {
				index = c - '1';
				/*
				 * for (int i = index; i < 128; i += 16) if (pieceMoving ==
				 * board.boardArray[i]) fromIndex = i;
				 */
				// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
				int[] legalMoves = new int[128];
				int totalMoves = board.gen_allLegalMoves(legalMoves, 0); // All
																			// moves

				for (int i = 0; i < totalMoves; i++) {
					// Careful
					if (pieceMoving == pieceMoving(legalMoves[i])) {
						if (toIndex == toIndex(legalMoves[i])) {
							if (moveType == moveType(legalMoves[i])) {
								if (true/* capture == capture(legalMoves[i]) */) {
									if (index == (fromIndex(legalMoves[i]) - fromIndex(legalMoves[i]) % 16) / 16)
										return legalMoves[i];
								}
							}
						}
					}
				}
				// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
			} else
				throw new RuntimeException("Notation Format Error");
		} else if (notation.length() == 2) {
			c = notation.charAt(0);
			int tofile = c - 'a';
			int torank = c - '1';
			fromIndex = tofile + torank * 16;
		} else
			throw new RuntimeException("Notation Format Error");

		int move = createMove(pieceMoving, fromIndex, toIndex, capture,
				moveType, 0);
		/*
		 * Log.i("pieceMoving: ","" + pieceMoving); Log.i("fromIndex","" +
		 * fromIndex); Log.i("toIndex","" + toIndex); Log.i("capture","" +
		 * capture); Log.i("moveType","" + moveType);
		 */
		// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW

		// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
		return move;
	}

}
