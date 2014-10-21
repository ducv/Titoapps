package com.ducv.fff.chess.utils.pgn;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.ducv.fff.chess.VirtualBoard;

public class PGNGame {

	private Attributes attributes;
	private ArrayList<String> hits;
	private String moves = "";
	public static final String TAG = "duongn.smartchess.utils.pgn.PGNGame";

	/**
	 * The String representation of the pattern used to match the attributes of
	 * the PGN file.
	 * 
	 * @see #ATTRIBUTES_PATTERN
	 */
	static final public String ATTRIBUTES_STRING_PATTERN = "\\[[^\\[]*\\]";

	/**
	 * The String representation of the pattern used to match the hits of the
	 * PGN file.<br>
	 * This pattern is composed in two parts: <br>
	 * The first one is for the hit number<br>
	 * <code>[0-9]+[.]</code><br>
	 * The second one is for the hit (composed in two same part seperate with a
	 * space)<br>
	 * <code>([a-zA-Z]*[0-9][\\+]?|[O]+[\\-][O]+[\\+]?)[ ]([a-zA-Z]*[0-9][\\+]?|[O]+[\\-][O]+[\\+]?)</code>
	 * 
	 * @see #HITS_PATTERN
	 */
	static final public String HITS_STRING_PATTERN = "[0-9]+[.]+([ ]?)"
			+ "([a-zA-Z]*[0-9][\\+]?|[O]+[\\-][O]+[\\+]?)[ ]([a-zA-Z]*[0-9][\\+]?|[O]+[\\-][O]+[\\+]?)";
	/**
	 * The String representation of the pattern used to check a number validity.
	 * 
	 * @see #NUMBER_VALIDITY_PATTERN
	 */
	static final private String NUMBER_VALIDITY_STRING_PATTERN = "[0-9]+";

	/**
	 * <code>Pattern</code> used to parse the attributes of the PGN file. It
	 * compiles with the {@link #ATTRIBUTES_STRING_PATTERN} pattern. *
	 * 
	 * @see Pattern
	 */
	static final public Pattern ATTRIBUTES_PATTERN = Pattern
			.compile(ATTRIBUTES_STRING_PATTERN);
	/**
	 * <code>Pattern</code> used to parse the hits of the PGN file. It compiles
	 * with the {@link #HITS_STRING_PATTERN} pattern.
	 * 
	 * @see Pattern
	 */
	static final public Pattern HITS_PATTERN = Pattern
			.compile(HITS_STRING_PATTERN);
	/**
	 * <code>Pattern</code> used to check the validity of a Number. It compiles
	 * with the {@link #NUMBER_VALIDITY_STRING_PATTERN} pattern.
	 * 
	 * @see Pattern
	 */
	static final public Pattern NUMBER_VALIDITY_PATTERN = Pattern
			.compile(NUMBER_VALIDITY_STRING_PATTERN);

	public PGNGame() {
		// new HistoryTree();
		hits = new ArrayList<String>();
		attributes = new Attributes();
	}

	public PGNGame(Attributes att, String _moves) {
		this.attributes = att;
		this.moves = _moves;
		hits = new ArrayList<String>();
		// TODO: Parse hit
	}

	public void addHit(String pgnHit) {
		Log.i(TAG, "Hit found: " + pgnHit);
		hits.add(pgnHit);
	}

	/**
	 * [Event "event_name"] [Site "site_name"] [Date "date"] [Round
	 * "round_number"] [White "player_name"] [Black "player_name"] [Result
	 * "result"] [WhiteElo "elo_number"] [BlackElo "elo_number"] [ECO "eco"]
	 * 
	 * @param att
	 */
	public void parseAttributes(String att) {
		// Log.i(TAG, "parseAttributes");
		int start = att.indexOf('[');
		int end = att.indexOf(']', start);
		while (start >= 0 && end >= 0) {
			String s = att.substring(start, end);
			if (s.startsWith("[Event ")) {
				attributes.set("Event", getAttributes(s));
			} else if (s.startsWith("[Site ")) {
				attributes.set("Site", getAttributes(s));
			} else if (s.startsWith("[Date ")) {
				attributes.set("Date", getAttributes(s));
			} else if (s.startsWith("[Round ")) {
				attributes.set("Round", getAttributes(s));
			} else if (s.startsWith("[White ")) {
				attributes.set("White", getAttributes(s));
			} else if (s.startsWith("[Black ")) {
				attributes.set("Black", getAttributes(s));
			} else if (s.startsWith("[WhiteElo ")) {
				attributes.set("WhiteElo", getAttributes(s));
			} else if (s.startsWith("[BlackElo")) {
				attributes.set("BlackElo", getAttributes(s));
			} else if (s.startsWith("ECO")) {
				attributes.set("ECO", getAttributes(s));
			} else if (s.startsWith("PlyCount")) {
				attributes.set("PlyCount", getAttributes(s));
			} else if (s.startsWith("Fen")) {
				attributes.set("Fen", getAttributes(s));
			}
			// Log.i(TAG,"Start: " + start + ", end: " + end);
			start = att.indexOf('[', start + 1);
			end = att.indexOf(']', start + 1);
		}
	}

	/**
	 * 
	 * @param s
	 *            has form [XXX "YYY"]
	 * @return YYY
	 */
	public static String getAttributes(String s) {
		int f1 = s.indexOf('\"');
		int f2 = s.indexOf('\"', f1 + 1);
		return s.substring(f1 + 1, f2);
	}

	/**
	 * This is not efficient way! Please rewrite
	 * 
	 * @param hits
	 */
	public void parseHits(String hits) {
		// TODO Auto-generated method stub
		// Log.i(TAG, "Parsing hits");
		StringBuilder newHit = new StringBuilder();
		String[] strings = hits.split("\n");
		for (String s : strings) {
			newHit.append(s + (s.endsWith(" ") ? "" : " "));
		}
		// List<PGNHit> list=new ArrayList<PGNHit>();

		Log.i(TAG, "newHit: " + newHit.toString());
		Matcher matcher = HITS_PATTERN.matcher(newHit.toString());
		while (matcher.find()) {
			Log.i(TAG, "Here");
			String[] str = matcher.group().split("\\.");
			if (str.length < 1) {
				continue;
			} else {
				Log.d(TAG, "str[0]: " + str[0]);
				Log.d(TAG, "str[1]: " + str[1]);
				String[] m = str[1].trim().split("\\ ");
				/*
				 * if(m[0].length() > 0 && !m[0].equals(" ")) this.addHit(m[0]);
				 */
				this.addHit(m[0]);
				if (m.length > 1)
					this.addHit(m[1]);
				/*
				 * System.out.println(m[0]); System.out.println(m[1]); if
				 * (m.length > 1) this.addHit(m[1]); // Add Hit???
				 */}
		}
	}

	public String getAttributes() {
		return attributes.toString();
	}

	public String getHitsString() {
		int size = hits.size();
		if (size == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= size; i++) {
			if (i % 2 == 1)
				sb.append((i + 1) / 2 + " ");
			sb.append(hits.get(i - 1));
			sb.append(" ");
		}
		return sb.substring(0, sb.length() - 1);
	}

	public ArrayList<String> getHitsArrayList() {
		return this.hits;
	}

	public String get(String s) {
		if (s.equals("Moves")) {
			return this.moves;
		} else
			return attributes.getTag(s);
	}

	public void setupBoard(VirtualBoard virtualboard) {
		// virtualboard.makeMove(move);
	}

	public Attributes getAttribute() {
		// TODO Auto-generated method stub
		return this.attributes;
	}

	public void setMoves(String moves2) {
		// TODO Auto-generated method stub
		this.moves = moves2;
	}
}
