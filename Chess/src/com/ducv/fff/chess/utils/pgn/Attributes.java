package com.ducv.fff.chess.utils.pgn;

public class Attributes {
	private String event = null;
	private String site = null;
	private String date = null;
	private String round = null;
	private String white = null;
	private String black = null;
	private String result = null;
	private String eco = null;
	private String whiteElo = null;
	private String blackElo = null;
	private String plyCount = null;
	private String fen = null;

	public Attributes() {
		this.event = "";
		this.site = "";
		this.date = "";
		this.result = "";
		this.white = "";
		this.black = "";
		this.eco = "";
		this.whiteElo = "";
		this.blackElo = "";
		this.plyCount = "";
		this.fen = "";
	}

	public void set(String att, String value) {
		if (att.equals("Event"))
			this.event = value;
		else if (att.equals("Site")) {
			this.site = value;
		} else if (att.equals("Date")) {
			this.date = value;
		} else if (att.equals("Round")) {
			this.round = value;
		} else if (att.equals("White")) {
			this.white = value;
		} else if (att.equals("Black")) {
			this.black = value;
		} else if (att.equals("Result")) {
			this.result = value;
		} else if (att.equals("Eco")) {
			this.eco = value;
		} else if (att.equals("WhiteElo")) {
			this.whiteElo = value;
		} else if (att.equals("BlackElo")) {
			this.blackElo = value;
		} else if (att.equals("PlyCount")) {
			this.plyCount = value;
		} else if (att.equals("Fen")) {
			this.fen = value;
		}
	}

	/**
	 * Ngo ngan qua
	 * 
	 * @param att
	 * @return
	 */
	public String getTag(String att) {
		if (att.equals("Event"))
			return this.event;
		else if (att.equals("Site")) {
			return this.site;
		} else if (att.equals("Date")) {
			return this.date;
		} else if (att.equals("White")) {
			return this.white;
		} else if (att.equals("Black")) {
			return this.black;
		} else if (att.equals("Result")) {
			return this.result;
		} else if (att.equals("Eco")) {
			return this.eco;
		} else if (att.equals("Round")) {
			return this.round;
		} else if (att.equals("BlackElo")) {
			return this.blackElo;
		} else if (att.equals("WhiteElo")) {
			return this.whiteElo;
		} else if (att.equals("PlyCount")) {
			return this.plyCount;
		} else if (att.equals("Fen")) {
			return this.fen;
		} else
			return " ";
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Event \"" + this.event + "\"]\n");
		if (this.site != null)
			buffer.append("[Site \"" + this.site + "\"]\n");
		if (this.date != null)
			buffer.append("[Date \"" + this.date + "\"]\n");
		if (this.round != null)
			buffer.append("[Round \"" + this.round + "\"]\n");
		if (this.white != null)
			buffer.append("[White \"" + this.white + "\"]\n");
		if (this.black != null)
			buffer.append("[Black \"" + this.black + "\"]\n");
		if (this.eco != null)
			buffer.append("[Eco \"" + this.eco + "\"]\n");
		if (this.whiteElo != null)
			buffer.append("[WhiteElo \"" + this.whiteElo + "\"]\n");
		if (this.blackElo != null)
			buffer.append("[BlackElo \"" + this.blackElo + "\"]\n");
		if (this.result != null)
			buffer.append("[Result \"" + this.result + "\"]\n");
		if (this.fen != null)
			buffer.append("[FEN \"" + this.fen + "\"]\n");
		if (this.plyCount != null)
			buffer.append("[PlyCount \"" + this.plyCount + "\"]\n");
		return buffer.toString();
	}

}
