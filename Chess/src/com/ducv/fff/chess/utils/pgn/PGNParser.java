/**
 * 
 */
package com.ducv.fff.chess.utils.pgn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import android.util.Log;

public class PGNParser {
	private static final String TAG = "com.fpt.chess.utils.pgn.PGNParse";

	enum State {
		start, full_move_number, move, comment, end
	};

	static final public String ATTRIBUTES_STRING_PATTERN = "\\[[^\\[]*\\]";

	static final public String HITS_STRING_PATTERN = "[0-9]+[.]"
			+ "([a-zA-Z]*[0-9][\\+]?|[O]+[\\-][O]+[\\+]?)[ ]([a-zA-Z]*[0-9][\\+]?|[O]+[\\-][O]+[\\+]?)";

	static final private String NUMBER_VALIDITY_STRING_PATTERN = "[0-9]+";

	static final public Pattern ATTRIBUTES_PATTERN = Pattern
			.compile(ATTRIBUTES_STRING_PATTERN);

	static final public Pattern HITS_PATTERN = Pattern
			.compile(HITS_STRING_PATTERN);

	static final public Pattern NUMBER_VALIDITY_PATTERN = Pattern
			.compile(NUMBER_VALIDITY_STRING_PATTERN);

	/**
	 * 
	 */
	public PGNParser() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Here: game is a String with all information you need in a game Attributes
	 * ( Seven tag); Hits;
	 * 
	 * @param game
	 */
	public static PGNGame getPGNGame(String game) {

		int last = game.lastIndexOf(']');
		String attributes = game.substring(0, last);
		String moves = game.substring(last + 1);
		// Log.i(TAG, "hits:" + moves);
		PGNGame pgnGame = new PGNGame();
		pgnGame.parseAttributes(attributes.trim());
		pgnGame.setMoves(moves);
		// pgnGame.parseHits(moves.trim());
		return pgnGame;
	}

	/**
	 * Get the PGN Strings of a PGN file Every string reperesent a game
	 * 
	 * @param filename
	 *            : the pgn file
	 * @return
	 */
	public static ArrayList<String> splitPGNFile(String pgnFilename) {
		ArrayList<String> games = new ArrayList<String>();
		BufferedReader fileContentBuffer = getFileContentBuffer(pgnFilename);
		StringBuffer game = null;
		if (fileContentBuffer != null) {
			while (true) {
				String line = null;
				try {
					line = fileContentBuffer.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.i(TAG, "Error when reading file content");
					break;
				}
				try {
					if (line == null || !fileContentBuffer.ready()) {
						if (game != null)
							if (game.length() > 0)
								games.add(game.toString());
						break;
					} else {
						line = line.trim();
						if (line.startsWith("[Event ")) {
							if (game != null)
								if (game.length() > 0)
									games.add(game.toString());
							game = new StringBuffer();
							game.append(line + '\n');
						} else if (line.length() != 0) {
							game.append(line + '\n');
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					break;
				}
			}
		} else {
			Log.i(TAG, "File content buffer is null");
		}
		try {
			fileContentBuffer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "Error while close fileContentBuffer");
		}
		return games;
	}

	/**
	 * Get the PGN Strings of a PGN file Every string reperesent a game
	 * 
	 * @param filename
	 *            : the pgn file
	 * @return
	 */
	public static ArrayList<String> splitPGNFile(
			BufferedReader fileContentBuffer) {
		ArrayList<String> games = new ArrayList<String>();
		StringBuffer game = null;
		if (fileContentBuffer != null) {
			while (true) {
				String line = null;
				try {
					line = fileContentBuffer.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.i(TAG, "Error when reading file content");
					break;
				}
				try {
					if (line == null /* || !fileContentBuffer.ready() */) {
						if (game != null)
							if (game.length() > 0)
								games.add(game.toString());
						break;
					}
					// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
					else if (!fileContentBuffer.ready()) {
						if (game != null)
							if (game.length() > 0) {
								line = line.trim();
								if (line.length() != 0)
									game.append(line + '\n');
								games.add(game.toString());
							}
						break;
					}
					// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
					else {
						line = line.trim();
						if (line.startsWith("[Event ")) {
							if (game != null)
								if (game.length() > 0)
									games.add(game.toString());
							game = new StringBuffer();
							game.append(line + '\n');
						} else if (line.length() != 0) {
							if (game != null)
								game.append(line + '\n');
							else
								continue;
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					break;
				}
			}
		} else {
			Log.i(TAG, "File content buffer is null");
		}
		try {
			fileContentBuffer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "Error while close fileContentBuffer");
		}
		return games;
	}

	/**
	 * Get the buffer of content of a file
	 * 
	 * @param filename
	 *            : the name of the file we need to get content
	 * @return The buffer of file content
	 */
	public static BufferedReader getFileContentBuffer(String filename) {
		File file = new File(filename);
		Reader reader = null;
		try {
			reader = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "getFileContentBuffer error: FileNotFoundException");
			return null;
		}
		BufferedReader buffer = new BufferedReader(reader);
		return buffer;
	}
}
