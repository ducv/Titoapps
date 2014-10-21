package mediocre.test;

import java.util.ArrayList;

import mediocre.board.Board;
import mediocre.board.Move;

/**
 * class Perft
 * 
 * This class runs a test on different positions to make sure all moves are
 * generated correctly.
 * 
 * It has its own main-method so it can be run separately
 * 
 * @author Jonatan Pettersson (mediocrechess@gmail.com)
 */
public class Perft {

	/**
	 * Goes through the test-positions declared in this class, the argument is
	 * the max number of plies the correct solution can contain (to limit the
	 * time)
	 * 
	 * @param args
	 *            max number of plies
	 */
	public static void main(String args[]) {
		Board board = new Board();
		long maxPly = 40060325; // Standard depth
		int maxDepth = 20;
		// doPrint = false;

		/* Set the depth in args */
		if (args.length > 0) {
			try {
				maxDepth = Integer.parseInt(args[0]);
				maxPly = Long.parseLong(args[1]);
			} catch (NumberFormatException e) {
			}
		}

		/* Set up the positions */
		ArrayList<PerftTestPos> positions = new ArrayList<PerftTestPos>();

		/* Pos 1 - Start pos */
		long[] pos1ans = { -1L, 20L, 400L, 8902L, 197281L, 4865609L,
				119060324L, 3195901860L, 84998978956L, 2439530234167L,
				69352859712417L };
		positions.add(new PerftTestPos("Pos 1",
				"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
				pos1ans));

		/* Pos 2 */
		long[] pos2ans = { -1L, 48L, 2039L, 97862L, 4085603L, 193690690L,
				8031647685L };
		positions
				.add(new PerftTestPos(
						"Pos 2",
						"r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1",
						pos2ans));

		/* Pos 3 */
		long[] pos3ans = { -1L, 50L, 279L };
		positions.add(new PerftTestPos("Pos 3",
				"8/3K4/2p5/p2b2r1/5k2/8/8/1q6 b - - 1 67", pos3ans));

		/* Pos 4 */
		long[] pos4ans = { -1L, -1L, -1L, -1L, -1L, -1L, 38633283L };
		positions.add(new PerftTestPos("Pos 4",
				"8/7p/p5pb/4k3/P1pPn3/8/P5PP/1rB2RK1 b - d3 0 28", pos4ans));

		/* Pos 5 */
		long[] pos5ans = { -1L, -1L, -1L, -1L, -1L, 11139762L };
		positions
				.add(new PerftTestPos(
						"Pos 5",
						"rnbqkb1r/ppppp1pp/7n/4Pp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3",
						pos5ans));

		/* Pos 6 */
		long[] pos6ans = { -1L, -1L, -1L, -1L, -1L, -1L, 11030083L, 178633661L };
		positions.add(new PerftTestPos("Pos 6",
				"8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", pos6ans));

		/* Check all the positions to the given depth */
		long startTot = System.currentTimeMillis();
		for (PerftTestPos ptp : positions) {
			System.out.println(ptp.getName());
			for (int i = 1; i < maxDepth && i < ptp.answerLength(); i++) {
				if (ptp.getAnswerAtDepth(i) != -1L
						&& ptp.getAnswerAtDepth(i) < maxPly) {
					board.inputFen(ptp.getFen());
					long start = System.currentTimeMillis();
					long answer = perft(board, i, false);
					System.out.print("  Depth: " + i + " Answer: " + answer);
					if (answer == ptp.getAnswerAtDepth(i)) {
						System.out.print(" (Correct)");
					} else {
						System.out.print(" (Incorrect)");
					}
					System.out
							.println(" Time: "
									+ convertMillis(System.currentTimeMillis()
											- start));
				}
			}

		}
		System.out.println("Total time: "
				+ convertMillis(System.currentTimeMillis() - startTot));
	}

	private static class PerftTestPos {
		private String name;
		private String fen;
		private long[] answers;

		public PerftTestPos(String name, String fen, long[] answers) {
			this.name = name;
			this.fen = fen;
			this.answers = answers;
		}

		public String getName() {
			return name;
		}

		public long getAnswerAtDepth(int depth) {
			if (depth > answers.length) {
				return -1;
			}

			return answers[depth];
		}

		public String getFen() {
			return fen;
		}

		public int answerLength() {
			return answers.length;
		}
	}

	/**
	 * Start the perft search
	 * 
	 * @param board
	 *            The board to search
	 * @param depth
	 *            The depth to search to
	 * @param divide
	 *            Should we divide the first moves or just return the total
	 *            value
	 * @return number of nodes
	 */
	public static long perft(Board board, int depth, boolean divide) {
		long nNodes;
		long zobrist = board.zobristKey;

		if (divide) {
			nNodes = divide(board, depth);
		} else {
			nNodes = miniMax(board, depth);
		}

		if (zobrist != board.zobristKey)
			System.out.println("Error in zobrist update!");

		return nNodes;

	}

	/**
	 * Keeps track of every starting move and its number of child moves, and
	 * then prints it on the screen.
	 * 
	 * @param board
	 *            The position to search
	 * @param depth
	 *            The depth to search to
	 */
	private static long divide(Board board, int depth) {
		int[] moves = new int[128];
		int totalMoves = board.gen_allLegalMoves(moves, 0);
		Long[] children = new Long[128];

		for (int i = 0; i < totalMoves; i++) {

			board.makeMove(moves[i]);
			children[i] = new Long(miniMax(board, depth - 1));
			board.unmakeMove(moves[i]);
		}

		long nodes = 0;
		for (int i = 0; i < totalMoves; i++) {
			System.out.print(Move.inputNotation(moves[i]) + " ");
			System.out.println((children[i]).longValue());
			nodes += (children[i]).longValue();
		}

		System.out.println("Moves: " + totalMoves);
		return nodes;
	}

	/**
	 * Generates every move from the position on board and returns the total
	 * number of moves found to the depth
	 * 
	 * @param board
	 *            The board used
	 * @param depth
	 *            The depth currently at
	 * @return int The number of moves found
	 */
	private static long miniMax(Board board, int depth) {
		long nodes = 0;

		if (depth == 0)
			return 1;

		int[] moves = new int[128];
		int totalMoves = board.gen_allLegalMoves(moves, 0);

		for (int i = 0; i < totalMoves; i++) {
			board.makeMove(moves[i]);
			nodes += miniMax(board, depth - 1);
			board.unmakeMove(moves[i]);
		}

		return nodes;
	}

	/**
	 * Takes number and converts it to minutes, seconds and fraction of a second
	 * also includes leading zeros
	 * 
	 * @param millis
	 *            the Milliseconds to convert
	 * @return String the conversion
	 */
	public static String convertMillis(long millis) {
		long minutes = millis / 60000;
		long seconds = (millis % 60000) / 1000;
		long fracSec = (millis % 60000) % 1000;

		String timeString = "";

		// Add minutes to the string, if no minutes this part will not add to
		// the string
		if (minutes < 10 && minutes != 0)
			timeString += "0" + Long.toString(minutes) + ":";
		else if (minutes >= 10)
			timeString += Long.toString(minutes) + ":";

		// Add seconds to the string
		if (seconds == 0)
			timeString += "0";
		else if (minutes != 0 && seconds < 10)
			timeString += "0" + Long.toString(seconds);
		else if (seconds < 10)
			timeString += Long.toString(seconds);
		else
			timeString += Long.toString(seconds);

		timeString += ".";

		// Add fractions of a second to the string
		if (fracSec == 0)
			timeString += "000";
		else if (fracSec < 10)
			timeString += "00" + Long.toString(fracSec);
		else if (fracSec < 100)
			timeString += "0" + Long.toString(fracSec);
		else
			timeString += Long.toString(fracSec);

		return timeString;
	}
}
