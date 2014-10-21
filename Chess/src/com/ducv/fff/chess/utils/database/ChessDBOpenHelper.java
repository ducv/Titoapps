package com.ducv.fff.chess.utils.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class ChessDBOpenHelper extends SQLiteOpenHelper {

	public final static String TAG = "com.fpt.chess.utils.database.ChessDBOpenHelper";
	public final static String DATABASE_NAME = "pluto_chess.db";
	public final static int VERSION = 1;
	public final static String TABLE_NAME = "games";
	public final static String ID = "_id";
	public final static String EVENT = "event";
	public final static String SITE = "site";
	public final static String DATE = "date";
	public final static String ROUND = "round";
	public final static String WHITE = "white";
	public final static String BLACK = "black";
	public final static String WHITE_TIME = "white_time";
	public final static String BLACK_TIME = "black_time";
	public final static String TIME_MOVE = "white_time_move";
	public final static String RESULT = "result";
	public final static String ECO = "eco";
	public final static String FEN = "fen";
	public final static String PLYCOUNT = "ply_count";
	public final static String MOVES = "moves";
	public final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ " ( " + ID + " INTEGER PRIMARY KEY , " + EVENT
			+ " TEXT NOT NULL , " + SITE + " TEXT NOT NULL , " + DATE
			+ " TEXT NOT NULL , " + ROUND + " INTEGER , " + WHITE + " TEXT , "
			+ BLACK + " TEXT , " + WHITE_TIME + " INTEGER , " + BLACK_TIME
			+ " INTEGER , " + TIME_MOVE + " INTEGER , " + RESULT + " TEXT , "
			+ ECO + " TEXT NULL , " + FEN + " TEXT NULL , " + PLYCOUNT
			+ " INTEGER NULL , " + MOVES + " TEXT NOT NULL)";

	public ChessDBOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		// super(context, name, null, version);
		super(context, DATABASE_NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE);

		// db = getWritableDatabase();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);

	}

}
