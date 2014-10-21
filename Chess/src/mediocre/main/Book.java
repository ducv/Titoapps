package mediocre.main;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;

import mediocre.board.Board;
import mediocre.def.Definitions;
import mediocre.transtable.Zobrist;

public class Book implements Definitions {
	private RandomAccessFile raf;
	private long fileLen;
	/* How much space one entry takes, long+short+short+int */
	private static final long increment = 16;

	private static Book instance = null;

	/**
	 * Singleton (with private constructor and the getInstance method, only one
	 * instance of the class can ever be created)
	 * 
	 * @throws IOException
	 */
	private Book(String bookPath) throws IOException {
		raf = new RandomAccessFile(bookPath, "r");

		fileLen = (raf.length());
	}

	/**
	 * Creates the singleton of the Book and returns it
	 * 
	 * @param bookPath
	 *            Path to the book to use
	 * @return null if the book could not be used, else the Book-object
	 */
	public static Book getInstance(String bookPath) {
		if (instance == null) {
			try {
				instance = new Book(bookPath);
			} catch (IOException e) {
				return null;
			}
		}
		return instance;
	}

	/**
	 * Takes a board, and returns a selected move from the book
	 * 
	 * @param board
	 *            The position to look for
	 * @return The move, null if no move exist
	 */
	public String getMoveFromBoard(Board board) {
		short move = getMoveFromKey(Zobrist.getPolyglotZobristKey(board));

		/* If no move found, return null */
		if (move == -1)
			return "";

		return convertPolyglotMoveToString(move, board);
	}

	/**
	 * Converts a Polyglot move to string, e.g. e2e2, e7e8q etc.
	 * 
	 * @param move
	 *            The move to convert
	 * @param board
	 *            The board the move is played on (to correctly convert
	 *            castling)
	 * @return The converted move
	 */
	private String convertPolyglotMoveToString(short move, Board board) {
		String stringMove = "";

		int toFile = move & 0x7;
		int toRow = (move >> 3) & 0x7;
		int fromFile = (move >> 6) & 0x7;
		int fromRow = (move >> 9) & 0x7;
		int prom = (move >> 12) & 0x7;

		switch (fromFile) {
		case 0:
			stringMove += "a";
			break;
		case 1:
			stringMove += "b";
			break;
		case 2:
			stringMove += "c";
			break;
		case 3:
			stringMove += "d";
			break;
		case 4:
			stringMove += "e";
			break;
		case 5:
			stringMove += "f";
			break;
		case 6:
			stringMove += "g";
			break;
		case 7:
			stringMove += "h";
			break;
		default:
			return null;
		}

		switch (fromRow) {
		case 0:
			stringMove += "1";
			break;
		case 1:
			stringMove += "2";
			break;
		case 2:
			stringMove += "3";
			break;
		case 3:
			stringMove += "4";
			break;
		case 4:
			stringMove += "5";
			break;
		case 5:
			stringMove += "6";
			break;
		case 6:
			stringMove += "7";
			break;
		case 7:
			stringMove += "8";
			break;
		default:
			return null;
		}

		switch (toFile) {
		case 0:
			stringMove += "a";
			break;
		case 1:
			stringMove += "b";
			break;
		case 2:
			stringMove += "c";
			break;
		case 3:
			stringMove += "d";
			break;
		case 4:
			stringMove += "e";
			break;
		case 5:
			stringMove += "f";
			break;
		case 6:
			stringMove += "g";
			break;
		case 7:
			stringMove += "h";
			break;
		default:
			return null;
		}

		switch (toRow) {
		case 0:
			stringMove += "1";
			break;
		case 1:
			stringMove += "2";
			break;
		case 2:
			stringMove += "3";
			break;
		case 3:
			stringMove += "4";
			break;
		case 4:
			stringMove += "5";
			break;
		case 5:
			stringMove += "6";
			break;
		case 6:
			stringMove += "7";
			break;
		case 7:
			stringMove += "8";
			break;
		default:
			return null;
		}

		switch (prom) {
		case 1:
			stringMove += "n";
			break;
		case 2:
			stringMove += "b";
			break;
		case 3:
			stringMove += "r";
			break;
		case 4:
			stringMove += "q";
			break;
		default:
			break;
		}

		/* Reformat castling moves */
		if (stringMove.equals("e1h1") && board.boardArray[E1] == W_KING) {
			stringMove = "e1g1";
		} else if (stringMove.equals("e1a1") && board.boardArray[E1] == W_KING) {
			stringMove = "e1c1";
		} else if (stringMove.equals("e8h8") && board.boardArray[E8] == B_KING) {
			stringMove = "e8g8";
		} else if (stringMove.equals("e8a8") && board.boardArray[E8] == B_KING) {
			stringMove = "e8c8";
		}

		return stringMove;
	}

	/**
	 * Finds the given key in the given polyglot-file, then takes all moves for
	 * the key and chooses one randomly (according to the weights)
	 * 
	 * @param searchKey
	 *            Zobrist key for the position
	 * @return A randomly selected move for the position
	 */
	private short getMoveFromKey(long searchKey) {
		/* Stores the moves for the position */
		ArrayList<Short> potentialMoves = new ArrayList<Short>();
		/* Stores the weights for the moves of the position */
		ArrayList<Short> potentialMoveWeights = new ArrayList<Short>();

		/* Holds information from the entry in the book */
		Long currentKey;
		short move;
		short weight;

		/* Keeps track of the total weight */
		int weightSum = 0;

		try {
			long seekPointLow = 0;
			long seekPointHigh = fileLen / increment;
			long seekPointMid = -1;

			/*
			 * Loop through the file, break when seekPointMid has already been
			 * used (which means the position was not found)
			 */
			while (seekPointMid != ((seekPointLow + seekPointHigh) / 2)
					* increment) {
				try {
					/* Set the mid point */
					seekPointMid = ((seekPointLow + seekPointHigh) / 2)
							* increment;

					/* Start searching at the given mid point */
					raf.seek(seekPointMid);

					/* Read the entry */
					currentKey = raf.readLong();
					move = raf.readShort();
					weight = raf.readShort();
					raf.readInt(); // Reads passed the learn integer which is
					// not used

					/*
					 * A key was found, so use the seek point as base to find
					 * all moves connected to the same key (can be both after
					 * and before this seek point)
					 */
					if (currentKey == searchKey) {
						/* Start with adding the move at this seek point */
						potentialMoves.add(move);
						potentialMoveWeights.add(weight);
						weightSum += weight;

						/* Loop backward and add moves */
						long tempSeek = seekPointMid;
						boolean moveFound = true;
						while (moveFound && tempSeek >= 0) {
							/* Move one entry back */
							tempSeek -= increment;
							raf.seek(tempSeek);
							/* Read the entry */
							currentKey = raf.readLong();
							move = raf.readShort();
							weight = raf.readShort();
							raf.readInt();
							/* Add if it is still the same key */
							if (currentKey == searchKey) {
								potentialMoves.add(move);
								potentialMoveWeights.add(weight);
								weightSum += weight;
							} else {
								moveFound = false;
							}
						}

						/* Loop foward and add moves */
						tempSeek = seekPointMid;
						moveFound = true;
						while (moveFound && tempSeek < fileLen) {
							/* Move one entry forward */
							tempSeek += increment;
							raf.seek(tempSeek);
							/* Read the entry */
							currentKey = raf.readLong();
							move = raf.readShort();
							weight = raf.readShort();
							raf.readInt();
							/* Add if it is still the same key */
							if (currentKey == searchKey) {
								potentialMoves.add(move);
								potentialMoveWeights.add(weight);
								weightSum += weight;
							} else {
								moveFound = false;
							}
						}

						/* All moves found so break out of the search */
						break;
					}

					/*
					 * The key was not a match so calculate which way the search
					 * should go next. Since the long will go negative at too
					 * high numbers checks are made for this
					 */
					if ((currentKey < 0 && searchKey < 0)
							|| (currentKey > 0 && searchKey > 0)) {
						if (currentKey < searchKey) {
							seekPointLow = seekPointMid / increment;
						} else {
							seekPointHigh = seekPointMid / increment;
						}
					} else if (currentKey > 0 && searchKey < 0) {
						seekPointLow = seekPointMid / increment;
					} else {
						seekPointHigh = seekPointMid / increment;
					}

				} catch (EOFException e) {
					/* Break out of the loop when the end of the file is reached */
					break;
				}
			}
			/* Close the book stream */
			// raf.close();
		} catch (IOException e) {
			System.err.println("Error while reading book");
			return -1;
		}

		/* Select a move and return it */
		if (potentialMoves.size() == 0)
			return -1;
		return selectMove(potentialMoves, potentialMoveWeights, weightSum);
	}

	/**
	 * Selects a random move based on the weights
	 * 
	 * @param moves
	 *            A list of moves
	 * @param weights
	 *            A list of weights corresponding to the moves
	 * @param weightSum
	 *            Total sum of the weights
	 * @return One of the moves randomly selected according to the weights
	 */
	private short selectMove(ArrayList<Short> moves, ArrayList<Short> weights,
			int weightSum) {
		Random rand = new Random();
		/* Get a random number between 0 and 1 */
		double randomNumber = rand.nextDouble();

		double totalWeight = 0;
		int i;
		/* Loop through all the moves */
		for (i = 0; i < moves.size(); i++) {
			/* Each loop add the total weight */
			totalWeight += (double) weights.get(i) / (double) weightSum;

			/*
			 * If the randomnumber is lower than the current total weight pick
			 * this move by breaking and leaving i as the index
			 */
			if (randomNumber < totalWeight)
				break;
		}

		return moves.get(i);
	}
}
