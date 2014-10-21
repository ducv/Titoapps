package mediocre.board;

import mediocre.def.Definitions;
import mediocre.transtable.TranspositionTable;

/**
 * class Evaluation
 * 
 * This is class conataining static evaluation methods We have this in an own
 * class to easily switch evaluation if needed
 * 
 * Ideas and inspiration from the open source of Scorpio (Daniel Shawul) and
 * Glaurung (Tord Romstad), and most of all Ed Schröder's fantastic guide
 * 
 * @author Jonatan Pettersson (mediocrechess@gmail.com)
 */
public class Evaluation implements Definitions {
	public static byte[] WB;
	public static byte[] BB;
	public static int[] w_pawnPos;
	public static int[] b_pawnPos;
	public static int passers;
	public static final byte COUNT_BIT = 7;
	public static final byte PAWN_BIT = 8;
	public static final byte MINOR_BIT = 16;
	public static final byte ROOK_BIT = 32;
	public static final byte QUEEN_BIT = 64;
	public static final byte KING_BIT = -128;
	public static final byte ATTACKERS_MASK = 31;
	public static final int[] FILE_TO_BIT_MASK = { 1, 2, 4, 8, 16, 32, 64, 128 };

	public static int w_bestPromDist;
	public static int b_bestPromDist;

	public static int gamePhase;

	// Evaluation constants
	public static final int PINNED_PIECE = 20;
	public static final int PASSED_PAWN = 20;
	public static final int DOUBLED_PAWN = 10;
	public static final int ISOLATED_PAWN = 20;
	public static final int WEAK_PAWN = 15;
	public static final int ROOK_ON_SEVENTH = 20;
	public static final int QUEEN_ON_SEVENTH = 10;
	public static final int ROOK_ON_OPEN = 20;
	public static final int ROOK_ON_SEMI = 15;
	public static final int HUNG_PIECE_PENALTY = 80;
	public static final int BISHOP_PAIR = 50;
	public static final int TEMPO = 10;

	public static final int QUEEN_VALUE = 975;
	public static final int ROOK_VALUE = 500;
	public static final int BISHOP_VALUE = 325;
	public static final int KNIGHT_VALUE = 325;
	public static final int PAWN_VALUE = 100;
	// This array is used for fast evaluation of pieces.
	//
	// Take the integer representing the piece + 7 to get the value
	//
	// E.g. Found black queen
	// -2 + 7 = 5 (looking up in the array will give -QUEEN_VALUE = black queen)
	public static final int[] PIECE_VALUE_ARRAY = { 0, // Not used
			-PAWN_VALUE, // Black pawn
			-KNIGHT_VALUE, // Black knight
			-BISHOP_VALUE, // Black bishop
			-ROOK_VALUE, // Black rook
			-QUEEN_VALUE, // Black queen
			0, // Black king
			0, // Empty square
			0, // White king
			QUEEN_VALUE, // White queen
			ROOK_VALUE, // White rook
			BISHOP_VALUE, // White bishop
			KNIGHT_VALUE, // White knight
			PAWN_VALUE }; // White pawn

	// END evaluation constants

	/*
	 * The following boards should be read as follow:
	 * 
	 * a1, b1, c1, d1, e1, f1, g1, h1, 0,0,0,0,0,0,0,0, a2, b2, c2, d2, e2, f2,
	 * g2, h2, 0,0,0,0,0,0,0,0, a3, b3, c3, d3, e3, f3, g3, h3, 0,0,0,0,0,0,0,0,
	 * a4, b4, c4, d4, e4, f4, g4, h4, 0,0,0,0,0,0,0,0, a5, b5, c5, d5, e5, f5,
	 * g5, h5, 0,0,0,0,0,0,0,0, a6, b6, c6, d6, e6, f6, g6, h6, 0,0,0,0,0,0,0,0,
	 * a7, b7, c7, d7, e7, f7, g7, h7, 0,0,0,0,0,0,0,0, a8, b8, c8, d8, e8, f8,
	 * g8, h8, 0,0,0,0,0,0,0,0
	 */

	// Positioning of the knights
	public static final int[] W_KNIGHT_POS = { -50, -40, -30, -25, -25, -30,
			-40, -50, 0, 0, 0, 0, 0, 0, 0, 0, -35, -25, -15, -10, -10, -15,
			-25, -35, 0, 0, 0, 0, 0, 0, 0, 0, -20, -10, 0, 5, 5, 0, -10, -20,
			0, 0, 0, 0, 0, 0, 0, 0, -10, 0, 10, 15, 15, 10, 0, -10, 0, 0, 0, 0,
			0, 0, 0, 0, -5, 5, 15, 20, 20, 15, 5, -5, 0, 0, 0, 0, 0, 0, 0, 0,
			-5, 5, 15, 20, 20, 15, 5, -5, 0, 0, 0, 0, 0, 0, 0, 0, -20, -10, 0,
			5, 5, 0, -10, -20, 0, 0, 0, 0, 0, 0, 0, 0, -135, -25, -15, -10,
			-10, -15, -25, -135, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static final int[] B_KNIGHT_POS = { -135, -25, -15, -10, -10, -15,
			-25, -135, 0, 0, 0, 0, 0, 0, 0, 0, -20, -10, 0, 5, 5, 0, -10, -20,
			0, 0, 0, 0, 0, 0, 0, 0, -5, 5, 15, 20, 20, 15, 5, -5, 0, 0, 0, 0,
			0, 0, 0, 0, -5, 5, 15, 20, 20, 15, 5, -5, 0, 0, 0, 0, 0, 0, 0, 0,
			-10, 0, 10, 15, 15, 10, 0, -10, 0, 0, 0, 0, 0, 0, 0, 0, -20, -10,
			0, 5, 5, 0, -10, -20, 0, 0, 0, 0, 0, 0, 0, 0, -35, -25, -15, -10,
			-10, -15, -25, -35, 0, 0, 0, 0, 0, 0, 0, 0, -50, -40, -30, -25,
			-25, -30, -40, -50, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static final int[] KNIGHT_POS_ENDING = { -10, -5, -5, -5, -5, -5,
			-5, -10, 0, 0, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0,
			0, 0, 0, 0, 0, -5, 0, 5, 5, 5, 5, 0, -5, 0, 0, 0, 0, 0, 0, 0, 0,
			-5, 0, 5, 10, 10, 5, 0, -5, 0, 0, 0, 0, 0, 0, 0, 0, -5, 0, 5, 10,
			10, 5, 0, -5, 0, 0, 0, 0, 0, 0, 0, 0, -5, 0, 5, 5, 5, 5, 0, -5, 0,
			0, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 0,
			0, -10, -5, -5, -5, -5, -5, -5, -10, 0, 0, 0, 0, 0, 0, 0, 0 };
	// END positioning of knights

	// Positioning of the bishops
	public static final int[] W_BISHOP_POS = { -20, -15, -15, -13, -13, -15,
			-15, -20, 0, 0, 0, 0, 0, 0, 0, 0, -5, 0, -5, 0, 0, -5, 0, -5, 0, 0,
			0, 0, 0, 0, 0, 0, -6, -2, 4, 2, 2, 4, -2, -6, 0, 0, 0, 0, 0, 0, 0,
			0, -4, 0, 2, 10, 10, 2, 0, -4, 0, 0, 0, 0, 0, 0, 0, 0, -4, 0, 2,
			10, 10, 2, 0, -4, 0, 0, 0, 0, 0, 0, 0, 0, -6, -2, 4, 2, 2, 4, -2,
			-6, 0, 0, 0, 0, 0, 0, 0, 0, -5, 0, -2, 0, 0, -2, 0, -5, 0, 0, 0, 0,
			0, 0, 0, 0, -8, -8, -6, -4, -4, -6, -8, -8, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static final int[] B_BISHOP_POS = { -8, -8, -6, -4, -4, -6, -8, -8,
			0, 0, 0, 0, 0, 0, 0, 0, -5, 0, -2, 0, 0, -2, 0, -5, 0, 0, 0, 0, 0,
			0, 0, 0, -6, -2, 4, 2, 2, 4, -2, -6, 0, 0, 0, 0, 0, 0, 0, 0, -4, 0,
			2, 10, 10, 2, 0, -4, 0, 0, 0, 0, 0, 0, 0, 0, -4, 0, 2, 10, 10, 2,
			0, -4, 0, 0, 0, 0, 0, 0, 0, 0, -6, -2, 4, 2, 2, 4, -2, -6, 0, 0, 0,
			0, 0, 0, 0, 0, -5, 0, -5, 0, 0, -5, 0, -5, 0, 0, 0, 0, 0, 0, 0, 0,
			-20, -15, -15, -13, -13, -15, -15, -20, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static final int[] BISHOP_POS_ENDING = { -18, -12, -9, -6, -6, -9,
			-12, -18, 0, 0, 0, 0, 0, 0, 0, 0, -12, -6, -3, 0, 0, -3, -6, -12,
			0, 0, 0, 0, 0, 0, 0, 0, -9, -3, 0, 3, 3, 0, -3, -9, 0, 0, 0, 0, 0,
			0, 0, 0, -6, 0, 3, 6, 6, 3, 0, -6, 0, 0, 0, 0, 0, 0, 0, 0, -6, 0,
			3, 6, 6, 3, 0, -6, 0, 0, 0, 0, 0, 0, 0, 0, -9, -3, 0, 3, 3, 0, -3,
			-9, 0, 0, 0, 0, 0, 0, 0, 0, -12, -6, -3, 0, 0, -3, -6, -12, 0, 0,
			0, 0, 0, 0, 0, 0, -18, -12, -9, -6, -6, -9, -12, -18, 0, 0, 0, 0,
			0, 0, 0, 0 };
	// END positioning of bishops

	public static final int[] W_ROOK_POS = { -6, -3, 0, 3, 3, 0, -3, -6, 0, 0,
			0, 0, 0, 0, 0, 0, -6, -3, 0, 3, 3, 0, -3, -6, 0, 0, 0, 0, 0, 0, 0,
			0, -6, -3, 0, 3, 3, 0, -3, -6, 0, 0, 0, 0, 0, 0, 0, 0, -6, -3, 0,
			3, 3, 0, -3, -6, 0, 0, 0, 0, 0, 0, 0, 0, -6, -3, 0, 3, 3, 0, -3,
			-6, 0, 0, 0, 0, 0, 0, 0, 0, -6, -3, 0, 3, 3, 0, -3, -6, 0, 0, 0, 0,
			0, 0, 0, 0, -6, -3, 0, 3, 3, 0, -3, -6, 0, 0, 0, 0, 0, 0, 0, 0, -6,
			-3, 0, 3, 3, 0, -3, -6, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static final int[] B_ROOK_POS = { -6, -3, 0, 3, 3, 0, -3, -6, 0, 0,
			0, 0, 0, 0, 0, 0, -6, -3, 0, 3, 3, 0, -3, -6, 0, 0, 0, 0, 0, 0, 0,
			0, -6, -3, 0, 3, 3, 0, -3, -6, 0, 0, 0, 0, 0, 0, 0, 0, -6, -3, 0,
			3, 3, 0, -3, -6, 0, 0, 0, 0, 0, 0, 0, 0, -6, -3, 0, 3, 3, 0, -3,
			-6, 0, 0, 0, 0, 0, 0, 0, 0, -6, -3, 0, 3, 3, 0, -3, -6, 0, 0, 0, 0,
			0, 0, 0, 0, -6, -3, 0, 3, 3, 0, -3, -6, 0, 0, 0, 0, 0, 0, 0, 0, -6,
			-3, 0, 3, 3, 0, -3, -6, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static final int[] ROOK_POS_ENDING = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0 };

	public static final int[] W_QUEEN_POS = { -10, -10, -10, -10, -10, -10,
			-10, -10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static final int[] B_QUEEN_POS = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -10, -10, -10, -10, -10,
			-10, -10, -10, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static final int[] QUEEN_POS_ENDING = { -24, -16, -12, -8, -8, -12,
			-16, -24, 0, 0, 0, 0, 0, 0, 0, 0, -16, -8, -4, 0, 0, -4, -8, -16,
			0, 0, 0, 0, 0, 0, 0, 0, -12, -4, 0, 4, 4, 0, -4, -12, 0, 0, 0, 0,
			0, 0, 0, 0, -8, 0, 4, 8, 8, 4, 0, -8, 0, 0, 0, 0, 0, 0, 0, 0, -8,
			0, 4, 8, 8, 4, 0, -8, 0, 0, 0, 0, 0, 0, 0, 0, -12, -4, 0, 4, 4, 0,
			-4, -12, 0, 0, 0, 0, 0, 0, 0, 0, -16, -8, -4, 0, 0, -4, -8, -16, 0,
			0, 0, 0, 0, 0, 0, 0, -24, -16, -12, -8, -8, -12, -16, -24, 0, 0, 0,
			0, 0, 0, 0, 0 };

	/* Positioning of the pawns */
	public static final int[] B_PAWN_POS = { -15, -5, 0, 5, 5, 0, -5, -15, 0,
			0, 0, 0, 0, 0, 0, 0, -15, -5, 0, 5, 5, 0, -5, -15, 0, 0, 0, 0, 0,
			0, 0, 0, -15, -5, 0, 5, 5, 0, -5, -15, 0, 0, 0, 0, 0, 0, 0, 0, -15,
			-5, 0, 15, 15, 0, -5, -15, 0, 0, 0, 0, 0, 0, 0, 0, -15, -5, 0, 25,
			25, 0, -5, -15, 0, 0, 0, 0, 0, 0, 0, 0, -15, -5, 0, 15, 15, 0, -5,
			-15, 0, 0, 0, 0, 0, 0, 0, 0, -15, -5, 0, 5, 5, 0, -5, -15, 0, 0, 0,
			0, 0, 0, 0, 0, -15, -5, 0, 5, 5, 0, -5, -15, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static final int[] W_PAWN_POS = { -15, -5, 0, 5, 5, 0, -5, -15, 0,
			0, 0, 0, 0, 0, 0, 0, -15, -5, 0, 5, 5, 0, -5, -15, 0, 0, 0, 0, 0,
			0, 0, 0, -15, -5, 0, 15, 15, 0, -5, -15, 0, 0, 0, 0, 0, 0, 0, 0,
			-15, -5, 0, 25, 25, 0, -5, -15, 0, 0, 0, 0, 0, 0, 0, 0, -15, -5, 0,
			15, 15, 0, -5, -15, 0, 0, 0, 0, 0, 0, 0, 0, -15, -5, 0, 5, 5, 0,
			-5, -15, 0, 0, 0, 0, 0, 0, 0, 0, -15, -5, 0, 5, 5, 0, -5, -15, 0,
			0, 0, 0, 0, 0, 0, 0, -15, -5, 0, 5, 5, 0, -5, -15, 0, 0, 0, 0, 0,
			0, 0, 0 };

	/* Positioning of the pawns in the endgame */
	public static final int[] B_PAWN_POS_ENDING = { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static final int[] W_PAWN_POS_ENDING = { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0 };

	// The following two king positions will be used in opening and middle game
	public static final int[] W_KING_POS = { 10, 20, 0, 0, 0, 10, 20, 10, 0, 0,
			0, 0, 0, 0, 0, 0, 10, 15, 0, 0, 0, 0, 15, 10, 0, 0, 0, 0, 0, 0, 0,
			0, -10, -20, -20, -25, -25, -20, -20, -10, 0, 0, 0, 0, 0, 0, 0, 0,
			-15, -25, -40, -40, -40, -40, -25, -15, 0, 0, 0, 0, 0, 0, 0, 0,
			-30, -40, -40, -40, -40, -40, -40, -30, 0, 0, 0, 0, 0, 0, 0, 0,
			-40, -50, -50, -50, -50, -50, -50, -40, 0, 0, 0, 0, 0, 0, 0, 0,
			-50, -50, -50, -50, -50, -50, -50, -50, 0, 0, 0, 0, 0, 0, 0, 0,
			-50, -50, -50, -50, -50, -50, -50, -50, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static final int[] B_KING_POS = { -50, -50, -50, -50, -50, -50, -50,
			-50, 0, 0, 0, 0, 0, 0, 0, 0, -50, -50, -50, -50, -50, -50, -50,
			-50, 0, 0, 0, 0, 0, 0, 0, 0, -40, -50, -50, -50, -50, -50, -50,
			-40, 0, 0, 0, 0, 0, 0, 0, 0, -30, -40, -40, -40, -40, -40, -40,
			-30, 0, 0, 0, 0, 0, 0, 0, 0, -15, -25, -40, -40, -40, -40, -25,
			-15, 0, 0, 0, 0, 0, 0, 0, 0, -10, -20, -20, -25, -25, -20, -20,
			-10, 0, 0, 0, 0, 0, 0, 0, 0, 10, 15, 0, 0, 0, 0, 15, 10, 0, 0, 0,
			0, 0, 0, 0, 0, 10, 20, 0, 0, 0, 10, 20, 10, 0, 0, 0, 0, 0, 0, 0, 0

	};

	// Used to encourage the kings to move to the center in the ending
	public static final int[] KING_POS_ENDING = { -20, -15, -10, -10, -10, -10,
			-15, -20, 0, 0, 0, 0, 0, 0, 0, 0, -15, -5, 0, 0, 0, 0, -5, -15, 0,
			0, 0, 0, 0, 0, 0, 0, -10, 0, 5, 5, 5, 5, 0, -10, 0, 0, 0, 0, 0, 0,
			0, 0, -10, 0, 5, 10, 10, 5, 0, -10, 0, 0, 0, 0, 0, 0, 0, 0, -10, 0,
			5, 10, 10, 5, 0, -10, 0, 0, 0, 0, 0, 0, 0, 0, -10, 0, 5, 5, 5, 5,
			0, -10, 0, 0, 0, 0, 0, 0, 0, 0, -15, -5, 0, 0, 0, 0, -5, -15, 0, 0,
			0, 0, 0, 0, 0, 0, -20, -15, -10, -10, -10, -10, -10, -20, 0, 0, 0,
			0, 0, 0, 0, 0 };

	// Marks the outpost squares for knight, do not put outpost values
	// on the edges since we check for protecting pawns without checking out of
	// board
	public static final int[] W_KNIGHT_OUTPOST = { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 5, 10, 10,
			5, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 5, 10, 10, 5, 2, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 4, 5, 5, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static final int[] B_KNIGHT_OUTPOST = { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 4, 5, 5, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 5, 10, 10,
			5, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 5, 10, 10, 5, 2, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static final byte[] KING_ATTACK_PATTERN = { // Straight from Ed
			// Schröder's site
			// . P N N R R R R Q Q Q Q Q Q Q Q K K K K K K K K K K K K K K K K
			// P P N N P N N R R R R P N N R R R R Q Q Q Q Q Q Q Q
			// P P N N N P P N N P N N R R R R
			0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 2, 2, 2, 3, 3, 3, 0, 0, 0, 0, 1, 1,
			2, 2, 2, 3, 3, 3, 3, 3, 3, 3 };
	public static final int[] KING_ATTACK_EVAL = { 0, 2, 3, 6, 12, 18, 25, 37,
			50, 75, 100, 125, 150, 175, 200, 225, 250, 275, 300, 325, 350, 375,
			400, 425, 450, 475, 500, 525, 550, 575, 600, 600, 600, 600, 600,
			600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600,
			600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600,
			600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600,
			600, 600, 600, 600, 600, 600 };

	public static final int[] FIRST_BIT_TO_FILE_MASK = { 8, 0, 1, 0, 2, 0, 1,
			0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2,
			0, 1, 0, 5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1,
			0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 6, 0, 1, 0, 2, 0, 1, 0, 3,
			0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1,
			0, 5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2,
			0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 7, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1,
			0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 5,
			0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1,
			0, 3, 0, 1, 0, 2, 0, 1, 0, 6, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2,
			0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 5, 0, 1,
			0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3,
			0, 1, 0, 2, 0, 1, 0 };

	// The closer the piece is to the opponent's king the better,
	// knights score higher than bishops since bishops can attack from afar as
	// well
	public static final int[] TROPISM_KNIGHT = { 0, 3, 3, 2, 1, 0, 0, 0 };
	public static final int[] TROPISM_BISHOP = { 0, 2, 2, 1, 0, 0, 0, 0 };
	public static final int[] TROPISM_ROOK = { 0, 3, 2, 1, 0, 0, 0, 0 };
	public static final int[] TROPISM_QUEEN = { 0, 4, 3, 2, 1, 0, 0, 0 };

	public static final int[] PASSED_RANK_BONUS = { 0, 10, 20, 40, 60, 120,
			150, 0 };

	public static int evaluate(Board board, TranspositionTable evalHash,
			TranspositionTable pawnHash, boolean printIt) {
		if (drawByMaterial(board, 0))
			return 0;

		final int hashEval = evalHash.probeEval(board.zobristKey);
		if (hashEval != EVALNOTFOUND) {
			return hashEval * board.toMove;
		}

		int attackedSquare;

		WB = new byte[128];
		BB = new byte[128];

		w_pawnPos = new int[10];
		b_pawnPos = new int[10];
		passers = 0;

		w_bestPromDist = 100; // Initialize to a high value so we can change
		// easily below
		b_bestPromDist = 100;

		int w_mobility = 0;
		int b_mobility = 0;
		int w_material = 0; // TODO: Handle material incrementally in makeMove
		// and unmakeMove
		int b_material = 0;
		int w_piecePos = 0;
		int b_piecePos = 0;
		int w_tropism = 0;
		int b_tropism = 0;
		int tempo = 0;

		boolean wPawnOnSecond = false; // Remembers if there was a pawn on
		// 2nd/7th rank, used to determine if
		// placing a rook on 7th should be
		// rewarded
		boolean bPawnOnSeventh = false;

		int index, file, rank;

		gamePhase = getGamePhase(board); // Get the phase the game is in TODO:
		// Handle this incrementally in
		// makeMove and unmakeMove

		// Decide if we should use ordinary piece tables or endgame tables for
		// evaluating piece positions
		boolean useEndingTables;
		if (gamePhase <= PHASE_OPENING)
			useEndingTables = false;
		else
			useEndingTables = true;

		// Evaluate trapped pieces
		final int w_trappedEval = w_trapped(board);
		final int b_trappedEval = b_trapped(board);

		// Pawns
		for (int i = 0; i < board.w_pawns.count; i++) {
			index = board.w_pawns.pieces[i];
			w_material += PAWN_VALUE; // Collect value
			if (gamePhase >= PHASE_ENDING)
				w_material += 20; // Pawns are worth a bit extra in the ending
			if (!useEndingTables)
				w_piecePos += W_PAWN_POS[index]; // Evaluate its position
			else
				w_piecePos += W_PAWN_POS_ENDING[index]; // Position if it is an
			// ending

			attackedSquare = index + 17; // Record where the pawn attacks
			if (((attackedSquare) & 0x88) == 0) {
				WB[attackedSquare]++;
				WB[attackedSquare] |= PAWN_BIT;
			}
			attackedSquare = index + 15;
			if (((attackedSquare) & 0x88) == 0) {
				WB[attackedSquare]++;
				WB[attackedSquare] |= PAWN_BIT;
			}

			rank = board.rank(index);
			file = board.row(index);

			// Add it to the pawn array for more pawn evaluation later

			// No pawn on this file so far, so add it as both most forward and
			// most backward pawn on the file
			if (w_pawnPos[file + 1] == 0)
				w_pawnPos[file + 1] = 0 | rank | (rank << 16);
			// The new pawn is more backward then the old most backward pawn so
			// add it
			else if ((w_pawnPos[file + 1] & 0xFFFF) > rank)
				w_pawnPos[file + 1] = (w_pawnPos[file + 1] & 0xFFFF0000) | rank;
			// The new pawn is more forward than the old most forward pawn so
			// add it
			else if (((w_pawnPos[file + 1] & 0xFFFF0000) >> 16) < rank)
				w_pawnPos[file + 1] = (w_pawnPos[file + 1] & 0xFFFF)
						| (rank << 16);

			if (rank == 1)
				wPawnOnSecond = true; // Atleast one pawn on the second rank so
			// placing a rook/queen there might be
			// worth it
		}

		for (int i = 0; i < board.b_pawns.count; i++) {
			index = board.b_pawns.pieces[i];
			b_material += PAWN_VALUE;
			if (gamePhase >= PHASE_ENDING)
				b_material += 20; // Pawns are worth a bit extra in the ending
			if (!useEndingTables)
				b_piecePos += B_PAWN_POS[index];
			else
				b_piecePos += B_PAWN_POS_ENDING[index];

			attackedSquare = index - 17;
			if (((attackedSquare) & 0x88) == 0) {
				BB[attackedSquare]++;
				BB[attackedSquare] |= PAWN_BIT;
			}
			attackedSquare = index - 15;
			if (((attackedSquare) & 0x88) == 0) {
				BB[attackedSquare]++;
				BB[attackedSquare] |= PAWN_BIT;
			}

			rank = board.rank(index);
			file = board.row(index);
			if (b_pawnPos[file + 1] == 0)
				b_pawnPos[file + 1] = 0 | rank | (rank << 16);
			else if ((b_pawnPos[file + 1] & 0xFFFF) < rank)
				b_pawnPos[file + 1] = (b_pawnPos[file + 1] & 0xFFFF0000) | rank;
			else if (((b_pawnPos[file + 1] & 0xFFFF0000) >> 16) > rank)
				b_pawnPos[file + 1] = (b_pawnPos[file + 1] & 0xFFFF)
						| (rank << 16);

			if (rank == 6)
				bPawnOnSeventh = true; // Atleast one pawn on the seventh rank
			// so placing a rook/queen there might
			// be worth it
		}
		// The pawnPos arrays are now filled so we can evaluate the pawns

		// See if we have any information in the pawn hash
		// Don't probe if no pawns (i.e. pawn zobrist = 0)
		final int w_pawnStructure;
		final int b_pawnStructure;
		if (board.pawnZobristKey == 0) {
			w_pawnStructure = 0;
			b_pawnStructure = 0;
		} else {
			final int hashPawn = pawnHash.probePawnEval(board.pawnZobristKey);

			if (hashPawn != EVALNOTFOUND) {

				w_pawnStructure = (hashPawn & 0xFFFF) - 0x3FFF;
				b_pawnStructure = (hashPawn >> 16) - 0x3FFF;
			} else {
				w_pawnStructure = w_pawnEval(board);
				b_pawnStructure = b_pawnEval(board);
				pawnHash.recordPawnEval(board.pawnZobristKey, w_pawnStructure,
						b_pawnStructure, passers);
			}
		}

		int passerEval = evaluatePassers(board);

		// We now know if either side has an unstoppable passer so reward it
		if (w_bestPromDist < b_bestPromDist)
			passerEval += 600;
		else if (b_bestPromDist < w_bestPromDist)
			passerEval -= 600;

		// Knights
		for (int i = 0; i < board.w_knights.count; i++) {
			w_material += KNIGHT_VALUE;
			index = board.w_knights.pieces[i];
			if (!useEndingTables)
				w_piecePos += W_KNIGHT_POS[index];
			else
				w_piecePos += KNIGHT_POS_ENDING[index];

			// The knight is placed on one of the outpost squares
			if (W_KNIGHT_OUTPOST[index] != 0) {
				// If the knight is protected by one pawn award the value in the
				// array,
				// if protected by two pawns award double the value, and award
				// nothing if it
				// is not protected by a pawn
				if (board.boardArray[index - 15] == W_PAWN
						&& board.boardArray[index - 17] == W_PAWN)
					w_piecePos += 2 * W_KNIGHT_OUTPOST[index];
				else if (board.boardArray[index - 15] == W_PAWN)
					w_piecePos += W_KNIGHT_OUTPOST[index];
				else if (board.boardArray[index - 17] == W_PAWN)
					w_piecePos += W_KNIGHT_OUTPOST[index];
			}

			w_mobility += gen_attack_wknight(board, index);
			w_tropism += TROPISM_KNIGHT[board.distance(board.b_king.pieces[0],
					index)];
		}
		for (int i = 0; i < board.b_knights.count; i++) {
			b_material += KNIGHT_VALUE;
			index = board.b_knights.pieces[i];
			if (!useEndingTables)
				b_piecePos += B_KNIGHT_POS[index];
			else
				b_piecePos += KNIGHT_POS_ENDING[index];

			if (B_KNIGHT_OUTPOST[index] != 0) {
				if (board.boardArray[index + 15] == B_PAWN
						&& board.boardArray[index + 17] == B_PAWN)
					b_piecePos += 2 * B_KNIGHT_OUTPOST[index];
				else if (board.boardArray[index + 15] == B_PAWN)
					b_piecePos += B_KNIGHT_OUTPOST[index];
				else if (board.boardArray[index + 17] == B_PAWN)
					b_piecePos += B_KNIGHT_OUTPOST[index];
			}

			b_mobility += gen_attack_bknight(board, board.b_knights.pieces[i]);
			b_tropism += TROPISM_KNIGHT[board.distance(board.w_king.pieces[0],
					index)];
		}
		// Bishops
		for (int i = 0; i < board.w_bishops.count; i++) {
			w_material += BISHOP_VALUE;
			index = board.w_bishops.pieces[i];
			if (!useEndingTables)
				w_piecePos += W_BISHOP_POS[index];
			else
				w_piecePos += BISHOP_POS_ENDING[index];

			w_mobility += gen_attack_wbishop(board, board.w_bishops.pieces[i]);
			w_tropism += TROPISM_BISHOP[board.distance(board.b_king.pieces[0],
					index)];
		}
		for (int i = 0; i < board.b_bishops.count; i++) {
			b_material += BISHOP_VALUE;
			index = board.b_bishops.pieces[i];
			if (!useEndingTables)
				b_piecePos += B_BISHOP_POS[index];
			else
				b_piecePos += BISHOP_POS_ENDING[index];

			b_mobility += gen_attack_bbishop(board, board.b_bishops.pieces[i]);
			b_tropism += TROPISM_BISHOP[board.distance(board.w_king.pieces[0],
					index)];
		}
		// Bishop pair bonus
		if (board.w_bishops.count >= 2)
			w_piecePos += BISHOP_PAIR;
		if (board.b_bishops.count >= 2)
			b_piecePos += BISHOP_PAIR;

		// Rooks
		for (int i = 0; i < board.w_rooks.count; i++) {
			index = board.w_rooks.pieces[i];
			file = board.row(index);
			rank = board.rank(index);
			w_material += ROOK_VALUE;
			if (!useEndingTables)
				w_piecePos += W_ROOK_POS[index];
			else
				w_piecePos += ROOK_POS_ENDING[index];

			w_mobility += gen_attack_wrook(board, index);

			// Rook on row with only enemy pawns
			if (w_pawnPos[file + 1] == 0 && b_pawnPos[file + 1] != 0)
				w_piecePos += ROOK_ON_SEMI;
			// Rook on open row
			else if (w_pawnPos[file + 1] == 0)
				w_piecePos += ROOK_ON_OPEN;
			// Rook on seventh rank
			if (rank == 6
					&& (bPawnOnSeventh || board.rank(board.b_king.pieces[0]) == 7))
				w_piecePos += ROOK_ON_SEVENTH;

			// TODO: Add bonus for rooks behind passed pawns

			w_tropism += TROPISM_ROOK[board.distance(board.b_king.pieces[0],
					index)];

		}
		for (int i = 0; i < board.b_rooks.count; i++) {
			index = board.b_rooks.pieces[i];
			file = board.row(index);
			rank = board.rank(index);
			b_material += ROOK_VALUE;
			if (!useEndingTables)
				b_piecePos += B_ROOK_POS[index];
			else
				b_piecePos += ROOK_POS_ENDING[index];

			b_mobility += gen_attack_brook(board, index);

			if (b_pawnPos[file + 1] == 0 && w_pawnPos[file + 1] != 0)
				b_piecePos += ROOK_ON_SEMI;
			else if (b_pawnPos[file + 1] == 0)
				b_piecePos += ROOK_ON_OPEN;
			if (rank == 1
					&& (wPawnOnSecond || board.rank(board.w_king.pieces[0]) == 0))
				b_piecePos += ROOK_ON_SEVENTH;

			// TODO: Add bonus for rooks behind passed pawns

			b_tropism += TROPISM_ROOK[board.distance(board.w_king.pieces[0],
					index)];
		}

		// Queens
		for (int i = 0; i < board.w_queens.count; i++) {
			w_material += QUEEN_VALUE;
			index = board.w_queens.pieces[i];
			if (!useEndingTables)
				w_piecePos += W_QUEEN_POS[index];
			else
				w_piecePos += QUEEN_POS_ENDING[index];

			w_mobility += gen_attack_wqueen(board, index);

			// Queen on 7th
			if (board.rank(index) == 6
					&& (bPawnOnSeventh || board.rank(board.b_king.pieces[0]) == 7))
				w_piecePos += QUEEN_ON_SEVENTH;

			w_tropism += TROPISM_QUEEN[board.distance(board.b_king.pieces[0],
					index)];
		}
		for (int i = 0; i < board.b_queens.count; i++) {
			b_material += QUEEN_VALUE;
			index = board.b_queens.pieces[i];
			if (!useEndingTables)
				b_piecePos += B_QUEEN_POS[index];
			else
				b_piecePos += QUEEN_POS_ENDING[index];

			b_mobility += gen_attack_bqueen(board, index);

			if (board.rank(index) == 1
					&& (wPawnOnSecond || board.rank(board.w_king.pieces[0]) == 0))
				b_piecePos += QUEEN_ON_SEVENTH;

			b_tropism += TROPISM_QUEEN[board.distance(board.w_king.pieces[0],
					index)];
		}
		// Kings
		if (!useEndingTables)
			w_piecePos += W_KING_POS[board.w_king.pieces[0]];
		else
			w_piecePos += KING_POS_ENDING[board.w_king.pieces[0]];
		gen_attack_wking(board, board.w_king.pieces[0]);

		if (!useEndingTables)
			b_piecePos += B_KING_POS[board.b_king.pieces[0]];
		else
			b_piecePos += KING_POS_ENDING[board.b_king.pieces[0]];
		gen_attack_bking(board, board.b_king.pieces[0]);

		// King safety, only opening and middle game
		final int w_kingAttacked;
		final int b_kingAttacked;
		final int w_kingDefense;
		final int b_kingDefense;
		if (gamePhase <= PHASE_MIDDLE) {
			w_kingAttacked = w_kingAttacked(board); // Check for black pieces
			// attacking the king
			b_kingAttacked = b_kingAttacked(board);
			w_kingDefense = w_kingDefense(board); // Check for general defense
			// measures (pawn shield
			// etc)
			b_kingDefense = b_kingDefense(board);

			if (board.toMove == WHITE_TO_MOVE) {
				if (b_kingAttacked <= -75)
					tempo += TEMPO;
				tempo += TEMPO;
			} else {
				if (w_kingAttacked <= -75)
					tempo -= TEMPO;
				tempo -= TEMPO;
			}
		} else {
			w_kingAttacked = 0;
			b_kingAttacked = 0;
			w_kingDefense = 0;
			b_kingDefense = 0;
		}

		final int w_hungPiece;
		final int b_hungPiece;

		if (board.toMove == WHITE_TO_MOVE) {
			w_hungPiece = w_hungPiece(board);
			b_hungPiece = 0; // Only penalize the side moving for hung pieces
		} else {
			b_hungPiece = b_hungPiece(board);
			w_hungPiece = 0; // Only penalize the side moving for hung pieces
		}

		final int totalEval = (w_material - b_material)
				+ (w_trappedEval - b_trappedEval) + (w_piecePos - b_piecePos)
				+ passerEval + (w_mobility - b_mobility)
				+ (w_pawnStructure - b_pawnStructure)
				+ (w_kingAttacked - b_kingAttacked)
				+ (w_kingDefense - b_kingDefense) + (w_tropism - b_tropism)
				+ (w_hungPiece - b_hungPiece) + tempo;

		// Adjust the score for likelyhood of a draw
		final int finalEval = drawProbability(board, totalEval, w_material,
				b_material);

		if (printIt) {
			int drawProbAdjust = totalEval - finalEval;

			int wTempoEval = tempo < 0 ? 0 : tempo;
			int bTempoEval = tempo < 0 ? tempo : 0;

			int totalWhite = w_material + w_trappedEval + w_piecePos
					+ passerEval + w_mobility + w_pawnStructure
					+ w_kingAttacked + w_kingDefense + w_tropism + w_hungPiece
					+ wTempoEval;
			int totalBlack = b_material + b_trappedEval + b_piecePos
					+ passerEval + b_mobility + b_pawnStructure
					+ b_kingAttacked + b_kingDefense + b_tropism + b_hungPiece
					+ bTempoEval;

			System.out.println("                   White Black Total");
			System.out.format("Material.......... %5d %5d %5d\n", w_material,
					b_material, (w_material - b_material));
			System.out.format("Positioning....... %5d %5d %5d\n", w_piecePos,
					b_piecePos, (w_piecePos - b_piecePos));
			System.out.format("Trapped........... %5d %5d %5d\n",
					w_trappedEval, b_trappedEval,
					(w_trappedEval - b_trappedEval));
			System.out.format("Mobility.......... %5d %5d %5d\n", w_mobility,
					b_mobility, (w_mobility - b_mobility));
			System.out.format("Pawn structure.... %5d %5d %5d\n",
					w_pawnStructure, b_pawnStructure,
					(w_pawnStructure - b_pawnStructure));
			System.out.format("King attacked..... %5d %5d %5d\n",
					w_kingAttacked, b_kingAttacked,
					(w_kingAttacked - b_kingAttacked));
			System.out.format("King defense...... %5d %5d %5d\n",
					w_kingDefense, b_kingDefense,
					(w_kingDefense - b_kingDefense));
			System.out.format("Tropism........... %5d %5d %5d\n", w_tropism,
					b_tropism, (w_tropism - b_tropism));
			System.out.format("Hung pieces....... %5d %5d %5d\n", w_hungPiece,
					b_hungPiece, (w_hungPiece - b_hungPiece));
			System.out.format("Tempo............. %5d %5d %5d\n", wTempoEval,
					bTempoEval, tempo);
			System.out.format("\nTotal eval........ %5d %5d %5d\n", totalWhite,
					totalBlack, totalEval);
			System.out.format("Adjusted to draw..     -     - %5d\n",
					drawProbAdjust);
			System.out
					.format("Final eval........     -     - %5d\n", finalEval);
		}

		evalHash.recordEval(board.zobristKey, finalEval);

		return finalEval * board.toMove;
	}

	/**
	 * Determines if two or more pieces are 'hanging' in the position for white,
	 * hanging means either that the piece is not defended or attacked by a
	 * lesser valued piece (rook attacked by a pawn for example)
	 * 
	 * @param board
	 * @return hungPiecePenalty
	 */
	public static int w_hungPiece(Board board) {
		int hungPiecesCount = 0;
		int hungPiecePenalty = 0;
		int square;

		// Knights
		for (int i = 0; i < board.w_knights.count; i++) {
			square = board.w_knights.pieces[i];
			if ((BB[square] > 0 && WB[square] == 0)
					|| (BB[square] & PAWN_BIT) > 0)
				hungPiecesCount++;
		}
		// Bishops
		for (int i = 0; i < board.w_bishops.count; i++) {
			square = board.w_bishops.pieces[i];
			if ((BB[square] > 0 && WB[square] == 0)
					|| (BB[square] & PAWN_BIT) > 0)
				hungPiecesCount++;
		}
		// Rooks
		for (int i = 0; i < board.w_rooks.count; i++) {
			square = board.w_rooks.pieces[i];
			if ((BB[square] > 0 && WB[square] == 0)
					|| (BB[square] & PAWN_BIT) > 0
					|| (BB[square] & MINOR_BIT) > 0)
				hungPiecesCount++;
		}
		// Queens
		for (int i = 0; i < board.w_queens.count; i++) {
			square = board.w_queens.pieces[i];
			if ((BB[square] > 0 && WB[square] == 0)
					|| (BB[square] & PAWN_BIT) > 0
					|| (BB[square] & MINOR_BIT) > 0
					|| (BB[square] & ROOK_BIT) > 0)
				hungPiecesCount++;
		}

		if (hungPiecesCount == 2)
			hungPiecePenalty -= HUNG_PIECE_PENALTY;
		else if (hungPiecesCount >= 2)
			hungPiecePenalty -= 2 * HUNG_PIECE_PENALTY;

		return hungPiecePenalty;
	}

	// END w_hungPiece()

	/**
	 * Determines if two or more pieces are 'hanging' in the position for black,
	 * hanging means either that the piece is not defended or attacked by a
	 * lesser valued piece (rook attacked by a pawn for example)
	 * 
	 * @param board
	 * @return hungPiecePenalty
	 */
	public static int b_hungPiece(Board board) {
		int hungPiecesCount = 0;
		int hungPiecePenalty = 0;
		int square;

		// Knights
		for (int i = 0; i < board.b_knights.count; i++) {
			square = board.b_knights.pieces[i];
			if ((WB[square] > 0 && BB[square] == 0)
					|| (WB[square] & PAWN_BIT) > 0)
				hungPiecesCount++;
		}
		// Bishops
		for (int i = 0; i < board.b_bishops.count; i++) {
			square = board.b_bishops.pieces[i];
			if ((WB[square] > 0 && BB[square] == 0)
					|| (WB[square] & PAWN_BIT) > 0)
				hungPiecesCount++;
		}
		// Rooks
		for (int i = 0; i < board.b_rooks.count; i++) {
			square = board.b_rooks.pieces[i];
			if ((WB[square] > 0 && BB[square] == 0)
					|| (WB[square] & PAWN_BIT) > 0
					|| (WB[square] & MINOR_BIT) > 0)
				hungPiecesCount++;
		}
		// Queens
		for (int i = 0; i < board.b_queens.count; i++) {
			square = board.b_queens.pieces[i];
			if ((WB[square] > 0 && BB[square] == 0)
					|| (WB[square] & PAWN_BIT) > 0
					|| (WB[square] & MINOR_BIT) > 0
					|| (WB[square] & ROOK_BIT) > 0)
				hungPiecesCount++;
		}

		if (hungPiecesCount == 2)
			hungPiecePenalty -= HUNG_PIECE_PENALTY;
		else if (hungPiecesCount >= 2)
			hungPiecePenalty -= 2 * HUNG_PIECE_PENALTY;

		return hungPiecePenalty;
	}

	// END b_hungPiece()

	/**
	 * Returns the material count for the side
	 * 
	 * @param board
	 *            The position
	 * @param side
	 *            The side to count material for
	 * @return material The material balance on the board in centipawns
	 */
	public static int material(Board board, int side) {
		int material = 0;
		if (side == WHITE) {
			material += board.w_pawns.count * PAWN_VALUE;
			material += board.w_rooks.count * ROOK_VALUE;
			material += board.w_queens.count * QUEEN_VALUE;
			material += board.w_bishops.count * BISHOP_VALUE;
			material += board.w_knights.count * KNIGHT_VALUE;
		} else {
			material += board.b_pawns.count * PAWN_VALUE;
			material += board.b_rooks.count * ROOK_VALUE;
			material += board.b_queens.count * QUEEN_VALUE;
			material += board.b_bishops.count * BISHOP_VALUE;
			material += board.b_knights.count * KNIGHT_VALUE;
		}

		return material;
	}

	// END materialOnly()

	/**
	 * @param board
	 *            The position to check
	 * @param side
	 *            -1 to check if black has enough material to win, 1 if white,
	 *            and 0 if both
	 * @return true if drawn, false if not
	 */
	public static boolean drawByMaterial(Board board, int side) {
		if (side == WHITE) {
			if (board.w_pawns.count != 0 || board.w_rooks.count != 0
					|| board.w_queens.count != 0 || board.w_bishops.count > 1
					|| board.w_knights.count > 2) {
				return false;
			}
			if ((board.w_bishops.count > 0 && board.w_knights.count > 0)) {
				return false;
			}

			return true;
		} else if (side == BLACK) {
			if (board.b_pawns.count != 0 || board.b_rooks.count != 0
					|| board.b_queens.count != 0 || board.b_bishops.count > 1
					|| board.b_knights.count > 2) {
				return false;
			}
			if ((board.b_bishops.count > 0 && board.b_knights.count > 0)) {
				return false;
			}

			return true;
		}

		if (board.w_pawns.count != 0 || board.b_pawns.count != 0
				|| board.w_rooks.count != 0 || board.b_rooks.count != 0
				|| board.w_queens.count != 0 || board.b_queens.count != 0
				|| board.w_bishops.count > 1 || board.b_bishops.count > 1
				|| board.w_knights.count > 2 || board.b_knights.count > 2) {
			return false;
		}
		if ((board.w_bishops.count > 0 && board.w_knights.count > 0)
				|| (board.b_bishops.count > 0 && board.b_knights.count > 0)) {
			return false;
		}

		return true;
	}

	// END drawByMaterial

	/**
	 * Takes the total evaluation and adjusts towards 0 if a draw is likely
	 * 
	 * @param board
	 *            The position
	 * @param totalEval
	 *            The total evaluation before adjustment
	 * @return adjustedScore The final evaluation after adjustment
	 */
	private static int drawProbability(Board board, int totalEval,
			int w_material, int b_material) {
		int adjustedScore = totalEval;
		int w_rank, w_row, b_rank, b_row;
		int fifty = board.movesFifty;

		if (gamePhase == PHASE_ENDING) {
			// If the score says one side is winning but that side does not
			// have enough material to win (e.g. only a bishop) return draw
			if (adjustedScore > 0 && drawByMaterial(board, WHITE))
				return 0;
			if (adjustedScore < 0 && drawByMaterial(board, BLACK))
				return 0;

			// Opposite color bishops
			// If there is exactly one bishop on both sides and they
			// are of opposite color and neither side has more that 4 pawns,
			// reduce the score towards 0 with 20%
			if (board.w_bishops.count == 1 && board.b_bishops.count == 1
					&& board.w_pawns.count <= 4 && board.b_pawns.count <= 4) {
				w_rank = board.rank(board.w_bishops.pieces[0]);
				w_row = board.row(board.w_bishops.pieces[0]);
				b_rank = board.rank(board.b_bishops.pieces[0]);
				b_row = board.row(board.b_bishops.pieces[0]);

				if (((w_rank + w_row) & 1) != ((b_rank + b_row) & 1)) {
					adjustedScore = 80 * adjustedScore / 100;
				}

			}
		}

		// 50 moves rule
		// Starting at move 20 without pawn moves or captures the score
		// is adjusted with 1% towards 0 for every move, this way
		// it won't suddenly run into the 50 moves rule
		if (fifty > 20)
			adjustedScore = (120 - fifty) * adjustedScore / 100;

		return adjustedScore;
	}

	// END drawProbability()

	/**
	 * Calculates things like pawn cover and pawn storms to evaluate the
	 * protection of the king
	 * 
	 * @param board
	 *            The position we're examining
	 * @return totalDefense The total defense value of the king
	 */
	private static int w_kingDefense(Board board) {
		int totalDefense = 0;
		int pawnRank;
		int king_index = board.w_king.pieces[0];
		int king_file = board.row(king_index);
		int oking_file = board.row(board.b_king.pieces[0]); // Opponent's king

		// White pawn shield

		// Find the rank the pawn in front of the king is on
		pawnRank = (w_pawnPos[king_file + 1] & 0xFFFF);
		// If there is no pawn in front of the king, penalize with 36
		if (pawnRank == 0) {
			totalDefense -= 36;
		}
		// If there is a pawn in front of the king penalize with the number
		// of ranks it has advanced
		// e.g. advanced 2 ranks (on rank 4 (=3 on the board))
		// 36 - (7-3)*(7-3) = 20 penalty
		else {
			totalDefense -= 36 - ((7 - pawnRank) * (7 - pawnRank));
		}
		// Look for the pawn to the right in front of the king, make sure
		// we are not off the board and do the same thing as above
		if ((king_file + 1 + 1) != 9) {
			pawnRank = (w_pawnPos[king_file + 1 + 1] & 0xFFFF);
			if (pawnRank == 0) {
				totalDefense -= 36;
			} else {
				totalDefense -= 36 - ((7 - pawnRank) * (7 - pawnRank));
			}
		}
		if ((king_file) != 0) {
			pawnRank = (w_pawnPos[king_file] & 0xFFFF);
			if (pawnRank == 0) {
				totalDefense -= 36;
			} else {
				totalDefense -= 36 - ((7 - pawnRank) * (7 - pawnRank));
			}
		}

		// Encourage proper placement of the bishop in fianchetto pawn structure

		// King on queenside
		if (king_file < 4) {
			// If the B pawn is on rank 3 and there is a bishop on B2, award
			// points for proper fianchetto
			if ((w_pawnPos[2] & 0xFFFF) == 2
					&& board.boardArray[B2] == W_BISHOP)
				totalDefense += 20;
		}
		// King on kingside
		else if (king_file > 4) {
			// If the G pawn is on rank 3 and there is a bishop on G2, award
			// points for proper fianchetto
			if ((w_pawnPos[7] & 0xFFFF) == 2
					&& board.boardArray[G2] == W_BISHOP)
				totalDefense += 20;
		}
		// If the king is still on the original square, the F2 square is weak so
		// the pawn should not be moved
		if (king_index == E1 && board.boardArray[F2] != W_PAWN)
			totalDefense -= 10;

		// If the kings are castled in different directions a pawn storm is
		// benefitial
		// so reduce the defense score if the opponent has advanced his pawns on
		// that side
		if (Math.abs(king_file - oking_file) > 2) {
			totalDefense -= 5 * (7 - (b_pawnPos[king_file + 1] & 0xFFFF) + 7
					- (b_pawnPos[king_file + 1 + 1] & 0xFFFF) + 7 - (b_pawnPos[king_file + 1 - 1] & 0xFFFF));
		}

		return totalDefense;

	}

	// END w_kingDefense()

	/**
	 * Calculates things like pawn cover and pawn storms to evaluate the
	 * protection of the king
	 * 
	 * @param board
	 *            The position we're examining
	 * @return totalDefense The total defense value of the king
	 */
	private static int b_kingDefense(Board board) {
		int totalDefense = 0;
		int pawnRank;
		int king_index = board.b_king.pieces[0];
		int king_file = board.row(king_index);
		int oking_file = board.row(board.w_king.pieces[0]); // Opponent's king

		// White pawn shield

		// Find the rank the pawn in front of the king is on
		pawnRank = (b_pawnPos[king_file + 1] & 0xFFFF);
		// If there is no pawn in front of the king, penalize with 36
		if (pawnRank == 0) {
			totalDefense -= 36;
		}
		// If there is a pawn in front of the king penalize with the number
		// of ranks it has advanced
		// e.g. advanced 2 ranks (on rank 4 (=3 on the board))
		// 36 - (7-3)*(7-3) = 20 penalty
		else {
			totalDefense -= 36 - ((pawnRank) * (pawnRank));
		}
		// Look for the pawn to the right in front of the king, make sure
		// we are not off the board and do the same thing as above
		if ((king_file + 1 + 1) != 9) {
			pawnRank = (b_pawnPos[king_file + 1 + 1] & 0xFFFF);
			if (pawnRank == 0) {
				totalDefense -= 36;
			} else {
				totalDefense -= 36 - ((pawnRank) * (pawnRank));
			}
		}
		if ((king_file) != 0) {
			pawnRank = (b_pawnPos[king_file] & 0xFFFF);
			if (pawnRank == 0) {
				totalDefense -= 36;
			} else {
				totalDefense -= 36 - ((pawnRank) * (pawnRank));
			}
		}

		// Encourage proper placement of the bishop in fianchetto pawn structure

		// King on queenside
		if (king_file < 4) {
			// If the B pawn is on rank 3 and there is a bishop on B2, award
			// points for proper fianchetto
			if ((b_pawnPos[2] & 0xFFFF) == 2
					&& board.boardArray[B7] == B_BISHOP)
				totalDefense += 20;
		}
		// King on kingside
		else if (king_file > 4) {
			// If the G pawn is on rank 3 and there is a bishop on G2, award
			// points for proper fianchetto
			if ((b_pawnPos[7] & 0xFFFF) == 2
					&& board.boardArray[G7] == B_BISHOP)
				totalDefense += 20;
		}
		// If the king is still on the original square, the F2 square is weak so
		// the pawn should not be moved
		if (king_index == E8 && board.boardArray[F7] != B_PAWN)
			totalDefense -= 10;

		// If the kings are castled in different directions a pawn storm is
		// benefitial
		// so reduce the defense score if the opponent has advanced his pawns on
		// that side
		if (Math.abs(king_file - oking_file) > 2) {
			totalDefense -= 5 * ((w_pawnPos[king_file + 1] & 0xFFFF)
					+ (w_pawnPos[king_file + 1 + 1] & 0xFFFF) + (w_pawnPos[king_file + 1 - 1] & 0xFFFF));
		}

		return totalDefense;

	}

	// END w_kingDefense()

	/**
	 * Counts the pieces attacking the squares around the white king
	 * 
	 * @param board
	 *            The position to check
	 * @return totalAttack The total value from the attacking pieces (is negated
	 *         before returned so it can be added to kingSafety)
	 */
	private static int w_kingAttacked(Board board) {
		int totalAttack = 0;
		int attackedCount = 0;
		byte flag = 0;
		int kingIndex = board.w_king.pieces[0];
		int attackedIndex;

		// Inital attack count depending on where the king is located is not
		// needed
		// in Mediocre since this is handled by the piecetables

		// Start with squares two squares in front of the king
		// Here we only gather what type of pieces is attacking, we
		// do not increase the attackers count
		attackedIndex = kingIndex + 31;
		if (((attackedIndex) & 0x88) == 0) {
			flag |= BB[attackedIndex];
		}
		attackedIndex = kingIndex + 32;
		if (((attackedIndex) & 0x88) == 0) {
			flag |= BB[attackedIndex];
		}
		attackedIndex = kingIndex + 33;
		if (((attackedIndex) & 0x88) == 0) {
			flag |= BB[attackedIndex];
		}

		// Now we check the squares to the left, right and behind the king
		// Here we increase the attackedCount for every square that is attacked
		// and one more if it is also only protected by the own king
		attackedIndex = kingIndex + 1;
		if (((attackedIndex) & 0x88) == 0 && BB[attackedIndex] != 0) {
			attackedCount++;
			flag |= BB[attackedIndex];
			if (WB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}
		attackedIndex = kingIndex - 1;
		if (((attackedIndex) & 0x88) == 0 && BB[attackedIndex] != 0) {
			attackedCount++;
			flag |= BB[attackedIndex];
			if (WB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}
		attackedIndex = kingIndex - 15;
		if (((attackedIndex) & 0x88) == 0 && BB[attackedIndex] != 0) {
			attackedCount++;
			flag |= BB[attackedIndex];
			if (WB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}
		attackedIndex = kingIndex - 16;
		if (((attackedIndex) & 0x88) == 0 && BB[attackedIndex] != 0) {
			attackedCount++;
			flag |= BB[attackedIndex];
			if (WB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}
		attackedIndex = kingIndex - 17;
		if (((attackedIndex) & 0x88) == 0 && BB[attackedIndex] != 0) {
			attackedCount++;
			flag |= BB[attackedIndex];
			if (WB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}

		// Now we do the squares just in front of the king, it works
		// the same as before but we also att one count if no own piece is
		// placed there
		attackedIndex = kingIndex + 15;
		if (((attackedIndex) & 0x88) == 0 && BB[attackedIndex] != 0) {
			attackedCount++;
			flag |= BB[attackedIndex];
			if (board.boardArray[attackedIndex] <= 0)
				attackedCount++; // Empty square or enemy piece just in front of
			// the king
			if (WB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}
		attackedIndex = kingIndex + 16;
		if (((attackedIndex) & 0x88) == 0 && BB[attackedIndex] != 0) {
			attackedCount++;
			flag |= BB[attackedIndex];
			if (board.boardArray[attackedIndex] <= 0)
				attackedCount++; // Empty square or enemy piece just in front of
			// the king
			if (WB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}
		attackedIndex = kingIndex + 17;
		if (((attackedIndex) & 0x88) == 0 && BB[attackedIndex] != 0) {
			attackedCount++;
			flag |= BB[attackedIndex];
			if (board.boardArray[attackedIndex] <= 0)
				attackedCount++; // Empty square or enemy piece just in front of
			// the king
			if (WB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}

		// We now have all the information about the pieces attacking the
		// squares
		// around the king so time to evaluate

		// We get the attack pattern by shifting three steps in 'flag' so we
		// only have the attacking pieces pattern left, then use the attackers
		// mask
		// to mask out anything 'left' of the piece bits (sometimes we get a
		// negative number
		// and a bit something like 11111111111111111010 which of course causes
		// out of bounds
		// in the king_attack_pattern array)
		//
		// The king_attack_pattern is built so the number we get from a certain
		// set of pieces
		// (the pattern) gives us an extra count, some combinations of pieces
		// are
		// more dangerous than others
		attackedCount += KING_ATTACK_PATTERN[((flag >> 3) & ATTACKERS_MASK)];

		// Now we have the attacked count and can simply get the value of the
		// attack from the KING_ATTACK_EVAL table

		totalAttack = KING_ATTACK_EVAL[attackedCount];

		return -totalAttack;
	}

	// END w_kingAttack()

	/**
	 * Counts the pieces attacking the squares around the black king
	 * 
	 * @param board
	 *            The position to check
	 * @return totalAttack The total value from the attacking pieces (is negated
	 *         before returned so it can be added to kingSafety)
	 */
	private static int b_kingAttacked(Board board) {
		int totalAttack = 0;
		int attackedCount = 0;
		byte flag = 0;
		int kingIndex = board.b_king.pieces[0];
		int attackedIndex;

		// Inital attack count depending on where the king is located is not
		// needed
		// in Mediocre since this is handled by the piecetables

		// Start with squares two squares in front of the king
		// Here we only gather what type of pieces is attacking, we
		// do not increase the attackers count
		attackedIndex = kingIndex - 31;
		if (((attackedIndex) & 0x88) == 0) {
			flag |= WB[attackedIndex];
		}
		attackedIndex = kingIndex - 32;
		if (((attackedIndex) & 0x88) == 0) {
			flag |= WB[attackedIndex];
		}
		attackedIndex = kingIndex - 33;
		if (((attackedIndex) & 0x88) == 0) {
			flag |= WB[attackedIndex];
		}

		// Now we check the squares to the left, right and behind the king
		// Here we increase the attackedCount for every square that is attacked
		// and one more if it is also only protected by the own king
		attackedIndex = kingIndex + 1;
		if (((attackedIndex) & 0x88) == 0 && WB[attackedIndex] != 0) {
			attackedCount++;
			flag |= WB[attackedIndex];
			if (BB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}
		attackedIndex = kingIndex - 1;
		if (((attackedIndex) & 0x88) == 0 && WB[attackedIndex] != 0) {
			attackedCount++;
			flag |= WB[attackedIndex];
			if (BB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}
		attackedIndex = kingIndex + 15;
		if (((attackedIndex) & 0x88) == 0 && WB[attackedIndex] != 0) {
			attackedCount++;
			flag |= WB[attackedIndex];
			if (BB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}
		attackedIndex = kingIndex + 16;
		if (((attackedIndex) & 0x88) == 0 && WB[attackedIndex] != 0) {
			attackedCount++;
			flag |= WB[attackedIndex];
			if (BB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}
		attackedIndex = kingIndex + 17;
		if (((attackedIndex) & 0x88) == 0 && WB[attackedIndex] != 0) {
			attackedCount++;
			flag |= WB[attackedIndex];
			if (BB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}

		// Now we do the squares just in front of the king, it works
		// the same as before but we also att one count if no own piece is
		// placed there
		attackedIndex = kingIndex - 15;
		if (((attackedIndex) & 0x88) == 0 && WB[attackedIndex] != 0) {
			attackedCount++;
			flag |= WB[attackedIndex];
			if (board.boardArray[attackedIndex] >= 0)
				attackedCount++; // Empty square or enemy piece just in front of
			// the king
			if (BB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}
		attackedIndex = kingIndex - 16;
		if (((attackedIndex) & 0x88) == 0 && WB[attackedIndex] != 0) {
			attackedCount++;
			flag |= WB[attackedIndex];
			if (board.boardArray[attackedIndex] >= 0)
				attackedCount++; // Empty square or enemy piece just in front of
			// the king
			if (BB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}
		attackedIndex = kingIndex - 17;
		if (((attackedIndex) & 0x88) == 0 && WB[attackedIndex] != 0) {
			attackedCount++;
			flag |= WB[attackedIndex];
			if (board.boardArray[attackedIndex] >= 0)
				attackedCount++; // Empty square or enemy piece just in front of
			// the king
			if (BB[attackedIndex] == 129)
				attackedCount++; // 129 translates to 10000001 in binary form,
			// meaning only one attacker, and that
			// attacker is the king
		}

		attackedCount += KING_ATTACK_PATTERN[((flag >> 3) & ATTACKERS_MASK)];
		totalAttack = KING_ATTACK_EVAL[attackedCount];

		return -totalAttack;
	}

	// END b_kingAttack()

	private static int evaluatePassers(Board board) {
		int passerEval = 0;
		int whitePassers = passers & 0xFFFF;
		int blackPassers = (passers & 0xFFFF0000) >> 16;
		int index;
		int file;
		int rank;
		int rankBonus;
		int whiteEval = 0;
		int blackEval = 0;
		int promDist;

		// White
		while (whitePassers != 0) {

			file = FIRST_BIT_TO_FILE_MASK[whitePassers];
			rank = (w_pawnPos[file + 1] & 0xFFFF0000) >> 16;
			index = (((rank) << 4) | (file));

			rankBonus = (gamePhase >= PHASE_ENDING) ? PASSED_RANK_BONUS[rank]
					: PASSED_RANK_BONUS[rank] / 2;

			whiteEval += rankBonus;

			// The passer is blocked so remove half the bonus
			if (board.boardArray[index + 16] != EMPTY_SQUARE) {
				whiteEval -= rankBonus / 2;
			}

			// Protected passer
			if ((WB[index] & PAWN_BIT) != 0) {
				whiteEval += rankBonus / 2;
			}

			// Check how far the passed pawn is from the enemy king
			// the farther the better, and more is given if it is an ending
			if (gamePhase <= PHASE_MIDDLE) {
				whiteEval += (rankBonus * Math.abs(board
						.row(board.b_king.pieces[0])
						- file)) / 14;

			} else // Ending
			{
				whiteEval += (rankBonus * Math.abs(board
						.row(board.b_king.pieces[0])
						- file)) / 10;
			}

			// If no pieces left (i.e. pawn ending) check if the opponent
			// king can catch the passer, do not do this if this is the least
			// advanced of a doubled passer
			if (gamePhase == PHASE_PAWN_ENDING) {
				promDist = 7 - rank; // Moves to promotion

				// If the opponent is to move we add one square
				// representing the black king moving towards the pawn
				// I.e. the pawn needs to be one square closer to promotion
				// to not be caught
				if (board.toMove == BLACK_TO_MOVE)
					promDist++;

				// If the own king is on the same file, and in front of the pawn
				// we need to add one move to remove the king
				if (board.row(board.w_king.pieces[0]) == file
						&& board.rank(board.w_king.pieces[0]) > rank)
					promDist++;

				// If the pawn is on the original square it can move two squares
				// so remove one square from the distance
				if (rank == 1)
					promDist--;

				// Compare the number of moves it takes for the passer to
				// promote
				// with the number of moves it takes for opponent king to
				// reach the promotion square
				if (promDist < Math.max(Math.abs(board
						.rank(board.b_king.pieces[0])
						- rank), Math.abs(board.row(board.b_king.pieces[0])
						- file))) {
					w_bestPromDist = Math.min(w_bestPromDist, promDist);
				}
			}

			whitePassers ^= FILE_TO_BIT_MASK[file];
		}
		// Black
		while (blackPassers != 0) {
			file = FIRST_BIT_TO_FILE_MASK[blackPassers];
			rank = (b_pawnPos[file + 1] & 0xFFFF0000) >> 16;
			index = (((rank) << 4) | (file));

			rankBonus = (gamePhase >= PHASE_ENDING) ? PASSED_RANK_BONUS[7 - rank]
					: PASSED_RANK_BONUS[7 - rank] / 2;

			blackEval += rankBonus;

			if (board.boardArray[index - 16] != EMPTY_SQUARE) {
				blackEval -= rankBonus / 2;
			}
			if ((BB[index] & PAWN_BIT) != 0) {
				blackEval += rankBonus / 2;
			}
			if (gamePhase <= PHASE_MIDDLE) {
				blackEval += (rankBonus * Math.abs(board
						.row(board.w_king.pieces[0])
						- file)) / 14;

			} else {
				blackEval += (rankBonus * Math.abs(board
						.row(board.w_king.pieces[0])
						- file)) / 10;
			}
			if (gamePhase == PHASE_PAWN_ENDING) {
				promDist = rank;
				if (board.toMove == WHITE_TO_MOVE)
					promDist++;
				if (board.row(board.b_king.pieces[0]) == file
						&& board.rank(board.b_king.pieces[0]) < rank)
					promDist++;
				if (rank == 6)
					promDist--;
				if (promDist < Math.max(Math.abs(board
						.rank(board.w_king.pieces[0])
						- rank), Math.abs(board.row(board.w_king.pieces[0])
						- file))) {
					b_bestPromDist = Math.min(b_bestPromDist, promDist);
				}
			}

			blackPassers ^= FILE_TO_BIT_MASK[file];
		}

		passerEval = whiteEval - blackEval;

		return passerEval;
	}

	/**
	 * Evaluates the white pawns and adds passed pawn to the array for later
	 * analysis
	 * 
	 * @param board
	 *            The position
	 * @return pawnEval The evaluation of the pawn strucutre (without passed
	 *         pawns)
	 */
	private static int w_pawnEval(Board board) {
		int index, file, rank, testIndex;
		boolean tempWeak;
		int pawnEval = 0;
		for (int i = 0; i < board.w_pawns.count; i++) {
			index = board.w_pawns.pieces[i];
			file = board.row(index);
			rank = board.rank(index);

			// Check for doubled pawn
			// If the rank recorded is not the same as this pawn which is
			// on the same row, there is another pawn on the same row
			// so the pawns are doubled
			if ((w_pawnPos[file + 1] & 0xFFFF) != rank) {
				pawnEval -= DOUBLED_PAWN;
			}

			// Check for isolated pawn
			// If the row to the right and left of this row does not contain
			// any friendly pawns, the pawn is isolated
			if (w_pawnPos[file + 1 + 1] == 0 && w_pawnPos[file + 1 - 1] == 0) {
				pawnEval -= ISOLATED_PAWN;
			}
			// If it's not isolated it could be backwards/weak
			// There are two scenarios, either the pawn can be
			// left behind (backwards) so it can not advance and be protected by
			// other pawns
			// or it could be pushed too far (weak) so no other pawns can
			// advance and
			// protect it
			else if ((WB[index] & PAWN_BIT) == 0) // If no pawn is protecting it
			{
				tempWeak = true;
				// If the pawn moved atleast two ranks
				if (rank >= 3) {
					// If the square to two ranks behind to the left is a
					// friendly pawn
					if (((index - 33) & 0x88) == 0
							&& board.boardArray[(index - 33)] == W_PAWN) {
						testIndex = index - 17;
						// If the friendly pawn is not blocked by a black pawn
						// and the square
						// it is advancing to is protected by a friendly pawn or
						// not attacked
						// by an enemy pawn
						if ((testIndex & 0x88) == 0
								&& board.boardArray[testIndex] != B_PAWN
								&& (WB[testIndex] & PAWN_BIT) >= (BB[testIndex] & PAWN_BIT)) {
							// The pawn can be supported by a friendly pawn
							// advance
							// so it is not weak
							tempWeak = false;
						}
					}
					// Same as above but checking for friendly pawn to the right
					else if (((index - 31) & 0x88) == 0
							&& board.boardArray[(index - 31)] == W_PAWN) {
						testIndex = index - 15;
						if ((testIndex & 0x88) == 0
								&& board.boardArray[testIndex] != B_PAWN
								&& (WB[testIndex] & PAWN_BIT) >= (BB[testIndex] & PAWN_BIT)) {
							// The pawn can be supported by a friendly pawn
							// advance
							// so it is not weak
							tempWeak = false;
						}
					}
					// The pawn can not be supported by a friendly pawn
					// advancing so check
					// if it can advance itself and be supported
					else {
						testIndex = index + 16;
						// If the square in front of the pawn is not blocked and
						// the opponent
						// is not controlling the square with it's pawns, it is
						// not backwards
						if (Math.abs(board.boardArray[testIndex]) != W_PAWN
								&& (WB[testIndex] & PAWN_BIT) >= (BB[testIndex] & PAWN_BIT)) {
							tempWeak = false;
						}

					}

					// Give penalty for the weak/backwards pawn
					if (tempWeak) {
						pawnEval -= WEAK_PAWN;
					}

				}
			}

			// TODO: Doubled passed pawns are valued too high
			// Check if the pawn is passed
			if (((w_pawnPos[file + 1] & 0xFFFF0000) >> 16) == rank) // Make sure
			// it is the
			// most
			// forward
			// pawn
			// we're
			// checking
			{
				if ((b_pawnPos[file + 1] == 0 || (b_pawnPos[file + 1] & 0xFFFF) < rank)
						&& // Either no enemy pawn on same rank, or behind the
						// pawn
						(b_pawnPos[file + 1 + 1] == 0 || (b_pawnPos[file + 1 + 1] & 0xFFFF) <= rank)
						&& // Either no enemy pawn to the right, or next to or
						// behind the pawn
						(b_pawnPos[file + 1 - 1] == 0 || (b_pawnPos[file + 1 - 1] & 0xFFFF) <= rank)) // Either
				// no
				// enemy
				// pawn
				// to
				// the
				// left,
				// or
				// next
				// to
				// or
				// behind
				// the
				// pawn
				{

					passers = passers | FILE_TO_BIT_MASK[file];

				}
			}
		}
		return pawnEval;

	}

	// END w_pawnEval()

	/**
	 * Evaluates the black pawns and adds passed pawn to the array for later
	 * analysis
	 * 
	 * @param board
	 *            The position
	 * @return pawnEval The evaluation of the pawn strucutre (without passed
	 *         pawns)
	 */
	private static int b_pawnEval(Board board) {
		int index, file, rank, testIndex;
		boolean tempWeak;
		int pawnEval = 0;
		for (int i = 0; i < board.b_pawns.count; i++) {
			index = board.b_pawns.pieces[i];
			file = board.row(index);
			rank = board.rank(index);

			// Check for doubled pawn
			// If the rank recorded is not the same as this pawn which is
			// on the same row, there is another pawn on the same row
			// so the pawns are doubled
			if ((b_pawnPos[file + 1] & 0xFFFF) != rank) {
				pawnEval -= DOUBLED_PAWN;
			}

			// Check for isolated pawn
			// If the row to the right and left of this row does not contain
			// any friendly pawns, the pawn is isolated
			if (b_pawnPos[file + 1 + 1] == 0 && b_pawnPos[file + 1 - 1] == 0) {
				pawnEval -= ISOLATED_PAWN;
			}
			// If it's not isolated it could be backwards/weak
			// There are two scenarios, either the pawn can be
			// left behind (backwards) so it can not advance and be protected by
			// other pawns
			// or it could be pushed too far (weak) so no other pawns can
			// advance and
			// protect it
			else if ((BB[index] & PAWN_BIT) == 0) // If no pawn is protecting it
			{
				tempWeak = true;
				// If the pawn moved atleast two ranks
				if (rank <= 5) {
					// If the square to two ranks behind to the left is a
					// friendly pawn
					if (((index + 33) & 0x88) == 0
							&& board.boardArray[(index + 33)] == B_PAWN) {
						testIndex = index + 17;
						// If the friendly pawn is not blocked by a black pawn
						// and the square
						// it is advancing to is protected by a friendly pawn or
						// not attacked
						// by an enemy pawn
						if ((testIndex & 0x88) == 0
								&& board.boardArray[testIndex] != W_PAWN
								&& (BB[testIndex] & PAWN_BIT) >= (WB[testIndex] & PAWN_BIT)) {
							// The pawn can be supported by a friendly pawn
							// advance
							// so it is not weak
							tempWeak = false;
						}
					}
					// Same as above but checking for friendly pawn to the right
					else if (((index + 31) & 0x88) == 0
							&& board.boardArray[(index + 31)] == B_PAWN) {
						testIndex = index + 15;
						if ((testIndex & 0x88) == 0
								&& board.boardArray[testIndex] != W_PAWN
								&& (BB[testIndex] & PAWN_BIT) >= (WB[testIndex] & PAWN_BIT)) {
							// The pawn can be supported by a friendly pawn
							// advance
							// so it is not weak
							tempWeak = false;
						}
					}
					// The pawn can not be supported by a friendly pawn
					// advancing so check
					// if it can advance itself and be supported
					else {
						testIndex = index + 16;
						// If the square in front of the pawn is not blocked and
						// the opponent
						// is not controlling the square with it's pawns, it is
						// not backwards
						if (Math.abs(board.boardArray[testIndex]) != W_PAWN
								&& (BB[testIndex] & PAWN_BIT) >= (WB[testIndex] & PAWN_BIT)) {
							tempWeak = false;
						}

					}

					// Give penalty for the weak/backwards pawn
					if (tempWeak) {
						pawnEval -= WEAK_PAWN;
					}

				}
			}

			if (((b_pawnPos[file + 1] & 0xFFFF0000) >> 16) == rank) // Make sure
			// it is the
			// most
			// forward
			// pawn
			// we're
			// checking
			{
				// Check if the pawn is passed (see white for comments)
				if ((w_pawnPos[file + 1] == 0 || (w_pawnPos[file + 1] & 0xFFFF) > rank)
						&& // Either no enemy pawn on same rank, or behind the
						// pawn
						(w_pawnPos[file + 1 + 1] == 0 || (w_pawnPos[file + 1 + 1] & 0xFFFF) >= rank)
						&& // Either no enemy pawn to the right, or next to or
						// behind the pawn
						(w_pawnPos[file + 1 - 1] == 0 || (w_pawnPos[file + 1 - 1] & 0xFFFF) >= rank)) // Either
				// no
				// enemy
				// pawn
				// to
				// the
				// left,
				// or
				// next
				// to
				// or
				// behind
				// the
				// pawn
				{
					passers = passers | (FILE_TO_BIT_MASK[file] << 16);
				}
			}
		}
		return pawnEval;

	}

	// END b_pawnEval()

	/**
	 * Fills the WB array with attacks from the knight and calculates and
	 * returns the mobility of the piece
	 * 
	 * @param board
	 *            The position the knight is in
	 * @param square
	 *            The square it is on
	 * @return mobility_total The total mobility value of the piece
	 */
	private static int gen_attack_wknight(Board board, int square) {
		int mobility_all = 0;
		int mobility_safe = 0;
		int mobility_total = 0;
		int attackedSquare;

		// Loop through the 8 different deltas
		for (int i = 0; i < 8; i++) {
			attackedSquare = square + knight_delta[i];
			if ((attackedSquare & 0x88) == 0) {
				// Add the attack
				WB[attackedSquare] |= MINOR_BIT;
				WB[attackedSquare]++;

				// If square is empty add mobility
				if (board.boardArray[attackedSquare] == EMPTY_SQUARE) {
					mobility_all++;
					if ((BB[attackedSquare] & PAWN_BIT) == 0) {
						mobility_safe++;
					}
				}
			}
		}

		// The total mobility is 2 times the safe mobility plus the unsafe
		// mobility
		mobility_total = (2 * mobility_safe + mobility_all);

		// If the piece only can move to one safe square it's mobility is so
		// restricted
		// that it is likely to be trapped so penalize this
		if (mobility_safe == 1) {
			// A 'trapped' piece further up on the board is worse than closer to
			// home
			// since it risks being captured further up
			mobility_total -= ((board.rank(square) + 1) * 5) / 2;
		}
		// If the piece have no safe squares it is just as good as trapped so
		// penalize
		// this even harder
		else if (mobility_safe == 0) {
			mobility_total -= ((board.rank(square) + 1) * 5);
		}

		return mobility_total;
	}

	// END gen_attack_wknight()

	/**
	 * Fills the BB array with attacks from the knight and calculates and
	 * returns the mobility of the piece
	 * 
	 * @param board
	 *            The position the knight is in
	 * @param square
	 *            The square it is on
	 * @return mobility_total The total mobility value of the piece
	 */
	private static int gen_attack_bknight(Board board, int square) {
		int mobility_all = 0;
		int mobility_safe = 0;
		int mobility_total = 0;
		int attackedSquare;

		for (int i = 0; i < 8; i++) {
			attackedSquare = square + knight_delta[i];
			if ((attackedSquare & 0x88) == 0) {
				BB[attackedSquare] |= MINOR_BIT;
				BB[attackedSquare]++;
				if (board.boardArray[attackedSquare] == EMPTY_SQUARE) {
					mobility_all++;
					if ((WB[attackedSquare] & PAWN_BIT) == 0) {
						mobility_safe++;
					}
				}
			}
		}

		// The total mobility is 2 times the safe mobility plus the unsafe
		// mobility
		mobility_total = (2 * mobility_safe + mobility_all);

		// If the piece only can move to one safe square it's mobility is so
		// restricted
		// that it is likely to be trapped so penalize this
		if (mobility_safe == 1) {
			// A 'trapped' piece further up on the board is worse than closer to
			// home
			// since it risks being captured further up
			mobility_total -= ((7 - board.rank(square) + 1) * 5) / 2;
		}
		// If the piece have no safe squares it is just as good as trapped so
		// penalize
		// this even harder
		else if (mobility_safe == 0) {
			mobility_total -= ((7 - board.rank(square) + 1) * 5);
		}

		return mobility_total;
	}

	// END gen_attack_bknight()

	/**
	 * Works same as for knights but check all squares in the direction of the
	 * delta
	 * 
	 * It also detects pinned piecess
	 * 
	 * @param board
	 *            The position the piece is in
	 * @param square
	 *            The square the piece is on
	 * @return mobility_total The total mobility score of the piece
	 */
	public static int gen_attack_wbishop(Board board, int square) {
		int mobility_all = 0;
		int mobility_safe = 0;
		int mobility_total = 0;
		int attackedSquare;
		int attackedPiece;

		for (int i = 0; i < 4; i++) {
			attackedSquare = square + bishop_delta[i];
			while ((attackedSquare & 0x88) == 0
					&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {
				WB[attackedSquare] |= MINOR_BIT;
				WB[attackedSquare]++;
				mobility_all++;
				if ((BB[attackedSquare] & PAWN_BIT) == 0) {
					mobility_safe++;
				}
				attackedSquare += bishop_delta[i];
			}
			// We exited the loop so check if we are still on the board
			// if we are we ran into a piece and can add the final attack
			if ((attackedSquare & 0x88) == 0) {
				WB[attackedSquare] |= MINOR_BIT;
				WB[attackedSquare]++;

				attackedPiece = board.boardArray[attackedSquare];

				// X-ray attack. If we ran into an own queen we keep checking
				// squares behind it
				// and add attacks to all empty squares. However we don't add
				// mobility or attacks
				// on opponent pieces since the piece can not actually reach
				// there yet.
				if (attackedPiece == W_QUEEN) {
					attackedSquare += bishop_delta[i];
					while ((attackedSquare & 0x88) == 0
							&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {
						WB[attackedSquare] |= MINOR_BIT;
						WB[attackedSquare]++;
						attackedSquare += bishop_delta[i];
					}
				}

				// If the attacked piece is a enemy knight, rook or queen
				// these are the type of pieces that can be pinned by a bishop
				else if (attackedPiece == B_KNIGHT || attackedPiece == B_ROOK
						|| attackedPiece == B_QUEEN) {
					// Keep on going and see if we run in to the enemy king
					// If we do the piece is pinned
					while ((attackedSquare & 0x88) == 0
							&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {

						attackedSquare += bishop_delta[i];
					}
					if ((attackedSquare & 0x88) == 0) {
						attackedPiece = board.boardArray[attackedSquare];
						// If the attacked piece is a king queen or rook (less
						// value than the bishop)
						// the previous attacked piece is pinned
						if (attackedPiece == B_KING || attackedPiece == B_QUEEN
								|| attackedPiece == B_ROOK) {
							// Since this is the bishop pinning, we add a bonus
							// to mobility
							// which translate to negative for the side with the
							// pinned piece
							mobility_total += PINNED_PIECE;
						}
					}
				}
			}
		}

		// The total mobility is 2 times the safe mobility plus the unsafe
		// mobility
		mobility_total += (2 * mobility_safe + mobility_all);

		// If the piece only can move to one safe square it's mobility is so
		// restricted
		// that it is likely to be trapped so penalize this
		if (mobility_safe == 1) {
			// A 'trapped' piece further up on the board is worse than closer to
			// home
			// since it risks being captured further up
			mobility_total -= ((board.rank(square) + 1) * 5) / 2;
		}
		// If the piece have no safe squares it is just as good as trapped so
		// penalize
		// this even harder
		else if (mobility_safe == 0) {
			mobility_total -= ((board.rank(square) + 1) * 5);
		}

		return mobility_total;

	}

	// END gen_attack_wbishop()

	/**
	 * Works same as for knights but check all squares in the direction of the
	 * delta
	 * 
	 * It also detects pinned pieces
	 * 
	 * @param board
	 *            The position the piece is in
	 * @param square
	 *            The square the piece is on
	 * @return mobility_total The total mobility score of the piece
	 */
	public static int gen_attack_bbishop(Board board, int square) {
		int mobility_all = 0;
		int mobility_safe = 0;
		int mobility_total = 0;
		int attackedSquare;
		int attackedPiece;

		for (int i = 0; i < 4; i++) {
			attackedSquare = square + bishop_delta[i];
			while ((attackedSquare & 0x88) == 0
					&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {
				BB[attackedSquare] |= MINOR_BIT;
				BB[attackedSquare]++;
				mobility_all++;
				if ((WB[attackedSquare] & PAWN_BIT) == 0) {
					mobility_safe++;
				}
				attackedSquare += bishop_delta[i];
			}
			// We exited the loop so check if we are still on the board
			// if we are we ran into a piece and can add the final attack
			if ((attackedSquare & 0x88) == 0) {
				BB[attackedSquare] |= MINOR_BIT;
				BB[attackedSquare]++;

				attackedPiece = board.boardArray[attackedSquare];

				// X-ray attack
				if (attackedPiece == B_QUEEN) {
					attackedSquare += bishop_delta[i];
					while ((attackedSquare & 0x88) == 0
							&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {
						BB[attackedSquare] |= MINOR_BIT;
						BB[attackedSquare]++;
						attackedSquare += bishop_delta[i];
					}
				}

				// See if the attacked piece is a enemy knight, rook or queen
				// these are the type of pieces that can be pinned by a bishop
				else if (attackedPiece == W_KNIGHT || attackedPiece == W_ROOK
						|| attackedPiece == W_QUEEN) {
					// Keep on going and see if we run in to the enemy king
					// If we do the piece is pinned
					while ((attackedSquare & 0x88) == 0
							&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {

						attackedSquare += bishop_delta[i];
					}
					if ((attackedSquare & 0x88) == 0) {
						attackedPiece = board.boardArray[attackedSquare];
						// If the attacked piece is a king queen or rook (less
						// value than the bishop)
						// the previous attacked piece is pinned
						if (attackedPiece == W_KING || attackedPiece == W_QUEEN
								|| attackedPiece == W_ROOK) {
							// Since this is the bishop pinning, we add a bonus
							// to mobility
							// which translate to negative for the side with the
							// pinned piece
							mobility_total += PINNED_PIECE;
						}
					}
				}
			}
		}

		// The total mobility is 2 times the safe mobility plus the unsafe
		// mobility
		mobility_total += (2 * mobility_safe + mobility_all);

		// If the piece only can move to one safe square it's mobility is so
		// restricted
		// that it is likely to be trapped so penalize this
		if (mobility_safe == 1) {
			// A 'trapped' piece further up on the board is worse than closer to
			// home
			// since it risks being captured further up
			mobility_total -= ((7 - board.rank(square) + 1) * 5) / 2;
		}
		// If the piece have no safe squares it is just as good as trapped so
		// penalize
		// this even harder
		else if (mobility_safe == 0) {
			mobility_total -= ((7 - board.rank(square) + 1) * 5);
		}

		return mobility_total;

	}

	// END gen_attack_wbishop()

	/**
	 * Works same as for knights but check all squares in the direction of the
	 * delta
	 * 
	 * It also detects pinned piecess
	 * 
	 * @param board
	 *            The position the piece is in
	 * @param square
	 *            The square the piece is on
	 * @return mobility_total The total mobility score of the piece
	 */
	public static int gen_attack_wrook(Board board, int square) {
		int mobility_all = 0;
		int mobility_safe = 0;
		int mobility_total = 0;
		int attackedSquare;
		int attackedPiece;

		for (int i = 0; i < 4; i++) {
			attackedSquare = square + rook_delta[i];
			while ((attackedSquare & 0x88) == 0
					&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {
				WB[attackedSquare] |= ROOK_BIT;
				WB[attackedSquare]++;
				mobility_all++;
				if ((BB[attackedSquare] & PAWN_BIT) == 0
						&& (BB[attackedSquare] & MINOR_BIT) == 0) {
					mobility_safe++;
				}
				attackedSquare += rook_delta[i];
			}
			if ((attackedSquare & 0x88) == 0) {
				WB[attackedSquare] |= ROOK_BIT;
				WB[attackedSquare]++;

				attackedPiece = board.boardArray[attackedSquare];

				if (attackedPiece == W_ROOK || attackedPiece == W_QUEEN) {
					attackedSquare += rook_delta[i];
					while ((attackedSquare & 0x88) == 0
							&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {
						WB[attackedSquare] |= ROOK_BIT;
						WB[attackedSquare]++;
						attackedSquare += rook_delta[i];
					}
				} else if (attackedPiece == B_KNIGHT
						|| attackedPiece == B_BISHOP
						|| attackedPiece == B_QUEEN) {
					while ((attackedSquare & 0x88) == 0
							&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {

						attackedSquare += rook_delta[i];
					}
					if ((attackedSquare & 0x88) == 0) {
						attackedPiece = board.boardArray[attackedSquare];
						if (attackedPiece == B_KING || attackedPiece == B_QUEEN) {
							mobility_total += PINNED_PIECE;
						}
					}
				}
			}
		}

		mobility_total += (2 * mobility_safe + mobility_all);

		if (mobility_safe == 1) {
			mobility_total -= ((board.rank(square) + 1) * 5) / 2;
		} else if (mobility_safe == 0) {
			mobility_total -= ((board.rank(square) + 1) * 5);
		}

		return mobility_total;

	}

	// END gen_attack_wrook()

	/**
	 * Works same as for knights but check all squares in the direction of the
	 * delta
	 * 
	 * It also detects pinned piecess
	 * 
	 * @param board
	 *            The position the piece is in
	 * @param square
	 *            The square the piece is on
	 * @return mobility_total The total mobility score of the piece
	 */
	public static int gen_attack_brook(Board board, int square) {
		int mobility_all = 0;
		int mobility_safe = 0;
		int mobility_total = 0;
		int attackedSquare;
		int attackedPiece;

		for (int i = 0; i < 4; i++) {
			attackedSquare = square + rook_delta[i];
			while ((attackedSquare & 0x88) == 0
					&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {
				BB[attackedSquare] |= ROOK_BIT;
				BB[attackedSquare]++;
				mobility_all++;
				if ((WB[attackedSquare] & PAWN_BIT) == 0
						&& (WB[attackedSquare] & MINOR_BIT) == 0) {
					mobility_safe++;
				}
				attackedSquare += rook_delta[i];
			}
			if ((attackedSquare & 0x88) == 0) {
				BB[attackedSquare] |= ROOK_BIT;
				BB[attackedSquare]++;

				attackedPiece = board.boardArray[attackedSquare];
				if (attackedPiece == B_ROOK || attackedPiece == B_QUEEN) {
					attackedSquare += rook_delta[i];
					while ((attackedSquare & 0x88) == 0
							&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {
						BB[attackedSquare] |= ROOK_BIT;
						BB[attackedSquare]++;
						attackedSquare += rook_delta[i];
					}
				} else if (attackedPiece == W_KNIGHT
						|| attackedPiece == W_BISHOP
						|| attackedPiece == W_QUEEN) {
					while ((attackedSquare & 0x88) == 0
							&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {

						attackedSquare += rook_delta[i];
					}
					if ((attackedSquare & 0x88) == 0) {
						attackedPiece = board.boardArray[attackedSquare];
						if (attackedPiece == W_KING || attackedPiece == W_QUEEN) {
							mobility_total += PINNED_PIECE;
						}
					}
				}
			}
		}

		mobility_total += (2 * mobility_safe + mobility_all);

		if (mobility_safe == 1) {
			mobility_total -= ((7 - board.rank(square) + 1) * 5) / 2;
		} else if (mobility_safe == 0) {
			mobility_total -= ((7 - board.rank(square) + 1) * 5);
		}

		return mobility_total;

	}

	// END gen_attack_brook()

	/**
	 * Works same as for knights but check all squares in the direction of the
	 * delta
	 * 
	 * No detection for pinned pieces for queens (only thing they can pin
	 * against is king and this should be quite uncommon, temporary and not very
	 * dangerous)
	 * 
	 * @param board
	 *            The position the piece is in
	 * @param square
	 *            The square the piece is on
	 * @return mobility_total The total mobility score of the piece
	 */
	public static int gen_attack_wqueen(Board board, int square) {
		int mobility_all = 0;
		int mobility_safe = 0;
		int mobility_total = 0;
		int attackedSquare;
		int attackedPiece;

		for (int i = 0; i < 8; i++) {
			attackedSquare = square + queen_delta[i];
			while ((attackedSquare & 0x88) == 0
					&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {
				WB[attackedSquare] |= QUEEN_BIT;
				WB[attackedSquare]++;
				mobility_all++;
				if ((BB[attackedSquare] & PAWN_BIT) == 0
						&& (BB[attackedSquare] & MINOR_BIT) == 0
						&& (BB[attackedSquare] & ROOK_BIT) == 0) {
					mobility_safe++;
				}
				attackedSquare += queen_delta[i];
			}
			if ((attackedSquare & 0x88) == 0) {
				WB[attackedSquare] |= QUEEN_BIT;
				WB[attackedSquare]++;

				attackedPiece = board.boardArray[attackedSquare];

				if (attackedPiece == W_QUEEN
						|| (attackedPiece == W_ROOK && i >= 4)
						|| (attackedPiece == W_BISHOP && i <= 3)) {
					attackedSquare += queen_delta[i];
					while ((attackedSquare & 0x88) == 0
							&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {
						WB[attackedSquare] |= QUEEN_BIT;
						WB[attackedSquare]++;
						attackedSquare += queen_delta[i];
					}
				}
			}
		}

		mobility_total += (2 * mobility_safe + mobility_all);

		if (mobility_safe == 1) {
			mobility_total -= ((board.rank(square) + 1) * 5) / 2;
		} else if (mobility_safe == 0) {
			mobility_total -= ((board.rank(square) + 1) * 5);
		}

		return mobility_total;

	}

	// END gen_attack_wqueen()

	/**
	 * Works same as for knights but check all squares in the direction of the
	 * delta
	 * 
	 * No detection for pinned pieces for queens (only thing they can pin
	 * against is king and this should be quite uncommon, temporary and not very
	 * dangerous)
	 * 
	 * @param board
	 *            The position the piece is in
	 * @param square
	 *            The square the piece is on
	 * @return mobility_total The total mobility score of the piece
	 */
	public static int gen_attack_bqueen(Board board, int square) {
		int mobility_all = 0;
		int mobility_safe = 0;
		int mobility_total = 0;
		int attackedSquare;
		int attackedPiece;

		for (int i = 0; i < 8; i++) {
			attackedSquare = square + queen_delta[i];
			while ((attackedSquare & 0x88) == 0
					&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {
				BB[attackedSquare] |= QUEEN_BIT;
				BB[attackedSquare]++;
				mobility_all++;
				if ((WB[attackedSquare] & PAWN_BIT) == 0
						&& (WB[attackedSquare] & MINOR_BIT) == 0
						&& (WB[attackedSquare] & ROOK_BIT) == 0) {
					mobility_safe++;
				}
				attackedSquare += queen_delta[i];
			}
			if ((attackedSquare & 0x88) == 0) {
				BB[attackedSquare] |= QUEEN_BIT;
				BB[attackedSquare]++;

				attackedPiece = board.boardArray[attackedSquare];

				// if(attackedPiece == B_ROOK || attackedPiece == B_QUEEN)
				if (attackedPiece == B_QUEEN
						|| (attackedPiece == B_ROOK && i >= 4)
						|| (attackedPiece == B_BISHOP && i <= 3)) {
					attackedSquare += queen_delta[i];
					while ((attackedSquare & 0x88) == 0
							&& board.boardArray[attackedSquare] == EMPTY_SQUARE) {
						BB[attackedSquare] |= QUEEN_BIT;
						BB[attackedSquare]++;
						attackedSquare += queen_delta[i];
					}
				}
			}
		}

		mobility_total += (2 * mobility_safe + mobility_all);

		if (mobility_safe == 1) {
			mobility_total -= ((7 - board.rank(square) + 1) * 5) / 2;
		} else if (mobility_safe == 0) {
			mobility_total -= ((7 - board.rank(square) + 1) * 5);
		}

		return mobility_total;

	}

	// END gen_attack_bqueen()

	/**
	 * Fills the WB array with attacks from the king
	 * 
	 * @param board
	 *            The position the knight is in
	 * @param square
	 *            The square it is on
	 * @return mobility_total The total mobility value of the piece
	 */
	private static void gen_attack_wking(Board board, int square) {
		int attackedSquare;

		// Loop through the 8 different deltas
		for (int i = 0; i < 8; i++) {
			attackedSquare = square + king_delta[i];
			if ((attackedSquare & 0x88) == 0) {
				// Add the attack
				WB[attackedSquare] |= KING_BIT;
				WB[attackedSquare]++;
			}
		}
	}

	// END gen_attack_wking()

	/**
	 * Fills the BB array with attacks from the king
	 * 
	 * @param board
	 *            The position the knight is in
	 * @param square
	 *            The square it is on
	 * @return mobility_total The total mobility value of the piece
	 */
	private static void gen_attack_bking(Board board, int square) {
		int attackedSquare;

		// Loop through the 8 different deltas
		for (int i = 0; i < 8; i++) {
			attackedSquare = square + king_delta[i];
			if ((attackedSquare & 0x88) == 0) {
				// Add the attack
				BB[attackedSquare] |= KING_BIT;
				BB[attackedSquare]++;
			}
		}
	}

	// END gen_attack_wking()

	/**
	 * Takes a few common trapped piece patterns and checks if any of them
	 * exists on the board
	 * 
	 * Also includes some devolopment pattern like blocked center pawns
	 * 
	 * For white pieces
	 * 
	 * @return trapped_eval The penalty if one or more patterns exists 0 if no
	 *         exist
	 */
	private static int w_trapped(Board board) {
		int trapped_eval = 0;

		// Knights
		if (board.boardArray[A7] == W_KNIGHT && board.boardArray[B7] == B_PAWN
				&& board.boardArray[C6] == B_PAWN) {
			trapped_eval -= 100;
		}
		if (board.boardArray[H7] == W_KNIGHT && board.boardArray[G7] == B_PAWN
				&& board.boardArray[F6] == B_PAWN) {
			trapped_eval -= 100;
		}
		if (board.boardArray[A8] == W_KNIGHT
				&& (board.boardArray[A7] == B_PAWN || board.boardArray[C7] == B_PAWN)) {
			trapped_eval -= 50;
		}
		if (board.boardArray[H8] == W_KNIGHT
				&& (board.boardArray[H7] == B_PAWN || board.boardArray[F7] == B_PAWN)) {
			trapped_eval -= 50;
		}

		// Bishops
		if (board.boardArray[A7] == W_BISHOP && board.boardArray[B6] == B_PAWN) {
			trapped_eval -= 100;
			if (board.boardArray[C7] == B_PAWN)
				trapped_eval -= 50; // Even more if the trapping pawn is
			// supported
		}
		if (board.boardArray[B8] == W_BISHOP && board.boardArray[C7] == B_PAWN) {
			trapped_eval -= 100;
			if (board.boardArray[B6] == B_PAWN)
				trapped_eval -= 50; // Even more if it can't get out via A7
		}
		if (board.boardArray[H7] == W_BISHOP && board.boardArray[G6] == B_PAWN) {
			trapped_eval -= 100;
			if (board.boardArray[F7] == B_PAWN)
				trapped_eval -= 50; // Even more if the trapping pawn is
			// supported
		}
		if (board.boardArray[G8] == W_BISHOP && board.boardArray[F7] == B_PAWN) {
			trapped_eval -= 100;
			if (board.boardArray[G6] == B_PAWN)
				trapped_eval -= 50; // Even more if it can't get out via H7
		}
		if (board.boardArray[A6] == W_BISHOP && board.boardArray[B5] == B_PAWN) {
			trapped_eval -= 100;
		}
		if (board.boardArray[H6] == W_BISHOP && board.boardArray[G5] == B_PAWN) {
			trapped_eval -= 100;
		}

		// Rooks (trapped in the corner by the own king)
		if ((board.boardArray[G1] == W_ROOK || board.boardArray[G2] == W_ROOK
				|| board.boardArray[H1] == W_ROOK || board.boardArray[H2] == W_ROOK)
				&& (board.boardArray[G1] == W_KING || board.boardArray[F1] == W_KING)) {
			trapped_eval -= 50;
		}
		if ((board.boardArray[A1] == W_ROOK || board.boardArray[A2] == W_ROOK
				|| board.boardArray[B1] == W_ROOK || board.boardArray[B2] == W_ROOK)
				&& (board.boardArray[C1] == W_KING || board.boardArray[B1] == W_KING)) {
			trapped_eval -= 50;
		}

		// Blocked center pawn
		if (board.boardArray[D2] == W_PAWN
				&& board.boardArray[D3] != EMPTY_SQUARE) {
			trapped_eval -= 20;
			if (board.boardArray[C1] == W_BISHOP)
				trapped_eval -= 30; // Even more if there is still a bishop on
			// c1
		}
		if (board.boardArray[E2] == W_PAWN
				&& board.boardArray[E3] != EMPTY_SQUARE) {
			trapped_eval -= 20;
			if (board.boardArray[F1] == W_BISHOP)
				trapped_eval -= 30; // Even more if there is still a bishop on
			// f1
		}
		return trapped_eval;
	}

	// END w_trapped()
	/**
	 * Takes a few common trapped piece patterns and checks if any of them
	 * exists on the board
	 * 
	 * Also includes some devolopment pattern like blocked center pawns
	 * 
	 * For black pieces
	 * 
	 * @return trapped_eval The penalty if one or more patterns exists 0 if no
	 *         exist
	 */
	private static int b_trapped(Board board) {
		int trapped_eval = 0;

		// Knights
		if (board.boardArray[A2] == B_KNIGHT && board.boardArray[B2] == W_PAWN
				&& board.boardArray[C3] == W_PAWN) {
			trapped_eval -= 100;
		}
		if (board.boardArray[H2] == B_KNIGHT && board.boardArray[G2] == W_PAWN
				&& board.boardArray[F3] == W_PAWN) {
			trapped_eval -= 100;
		}
		if (board.boardArray[A1] == B_KNIGHT
				&& (board.boardArray[A2] == W_PAWN || board.boardArray[C2] == W_PAWN)) {
			trapped_eval -= 50;
		}
		if (board.boardArray[H1] == B_KNIGHT
				&& (board.boardArray[H2] == W_PAWN || board.boardArray[F2] == W_PAWN)) {
			trapped_eval -= 50;
		}

		// Bishops
		if (board.boardArray[A2] == B_BISHOP && board.boardArray[B3] == W_PAWN) {
			trapped_eval -= 100;
			if (board.boardArray[C2] == W_PAWN)
				trapped_eval -= 50; // Even more if the trapping pawn is
			// supported
		}
		if (board.boardArray[B1] == B_BISHOP && board.boardArray[C2] == W_PAWN) {
			trapped_eval -= 100;
			if (board.boardArray[B3] == W_PAWN)
				trapped_eval -= 50; // Even more if it can't get out via A7
		}
		if (board.boardArray[H2] == B_BISHOP && board.boardArray[G3] == W_PAWN) {
			trapped_eval -= 100;
			if (board.boardArray[F2] == W_PAWN)
				trapped_eval -= 50; // Even more if the trapping pawn is
			// supported
		}
		if (board.boardArray[G1] == B_BISHOP && board.boardArray[F2] == W_PAWN) {
			trapped_eval -= 100;
			if (board.boardArray[G3] == W_PAWN)
				trapped_eval -= 50; // Even more if it can't get out via H7
		}
		if (board.boardArray[A3] == B_BISHOP && board.boardArray[B4] == W_PAWN) {
			trapped_eval -= 100;
		}
		if (board.boardArray[H3] == B_BISHOP && board.boardArray[G4] == W_PAWN) {
			trapped_eval -= 100;
		}

		// Rooks (trapped in the corner by the own king)
		if ((board.boardArray[G8] == B_ROOK || board.boardArray[G7] == B_ROOK
				|| board.boardArray[H8] == B_ROOK || board.boardArray[H7] == B_ROOK)
				&& (board.boardArray[G8] == B_KING || board.boardArray[F8] == B_KING)) {
			trapped_eval -= 50;
		}
		if ((board.boardArray[A8] == B_ROOK || board.boardArray[A7] == B_ROOK
				|| board.boardArray[B8] == B_ROOK || board.boardArray[B7] == B_ROOK)
				&& (board.boardArray[C8] == B_KING || board.boardArray[B8] == B_KING)) {
			trapped_eval -= 50;
		}

		// Blocked center pawn
		if (board.boardArray[D7] == B_PAWN
				&& board.boardArray[D6] != EMPTY_SQUARE) {
			trapped_eval -= 20;
			if (board.boardArray[C8] == B_BISHOP)
				trapped_eval -= 30; // Even more if there is still a bishop on
			// c1
		}
		if (board.boardArray[E7] == B_PAWN
				&& board.boardArray[E6] != EMPTY_SQUARE) {
			trapped_eval -= 20;
			if (board.boardArray[F8] == B_BISHOP)
				trapped_eval -= 30; // Even more if there is still a bishop on
			// f1
		}
		return trapped_eval;
	}

	// END b_trapped()

	/**
	 * This method returns the game phase
	 * 
	 * @param board
	 *            The board to decide game phase on
	 * @return gamePhase The phase the position on the board is in
	 */
	public static int getGamePhase(Board inputBoard) {
		int phase = PHASE_OPENING; // Initialize to opening
		int gamePhaseCheck = 0; // Initialize the count

		gamePhaseCheck += inputBoard.w_knights.count;
		gamePhaseCheck += inputBoard.b_knights.count;
		gamePhaseCheck += inputBoard.w_bishops.count;
		gamePhaseCheck += inputBoard.b_bishops.count;
		gamePhaseCheck += inputBoard.w_rooks.count * 2;
		gamePhaseCheck += inputBoard.b_rooks.count * 2;
		gamePhaseCheck += inputBoard.w_queens.count * 4;
		gamePhaseCheck += inputBoard.b_queens.count * 4;

		if (gamePhaseCheck == 0)
			phase = PHASE_PAWN_ENDING;
		else if (gamePhaseCheck <= 8)
			phase = PHASE_ENDING;
		else if (gamePhaseCheck > 20)
			phase = PHASE_OPENING;
		else
			phase = PHASE_MIDDLE;

		return phase;
	}
	// END getGamePhase()

}
