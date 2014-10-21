package com.ducv.fff.chess.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ChessDBTransact {

	private SQLiteDatabase mDatabase = null;
	private ChessDBOpenHelper mChessDBOpenHelper = null;
	private static final String TAG = "com.fpt.chess.utils.database.ChessDBTransact";

	/**
	 * 
	 */
	public ChessDBTransact(Context context) {
		this(context, ChessDBOpenHelper.DATABASE_NAME,
				ChessDBOpenHelper.VERSION);
	}

	/**
	 * Support methods for
	 */
	public ChessDBTransact(Context context, String mDatabaseFileName,
			int version) {
		// TODO Auto-generated constructor stub
		mChessDBOpenHelper = new ChessDBOpenHelper(context, mDatabaseFileName,
				null, version);
		mDatabase = null;
	}

	/**
	 * 
	 * @param i
	 *            : top _id to get Game has id in (i - maxSize > 0 ? 0 : i -
	 *            maxSize,i);
	 * @param isAscended
	 * @param maxSize
	 * @return
	 */

	public Cursor getGames(int i, boolean next) {
		mDatabase = mChessDBOpenHelper.getReadableDatabase();
		Cursor cursor = mDatabase.rawQuery("SELECT " + ChessDBOpenHelper.ID
				+ ", " + ChessDBOpenHelper.EVENT + ", "
				+ ChessDBOpenHelper.SITE + ", " + ChessDBOpenHelper.DATE + ", "
				+ ChessDBOpenHelper.ROUND + ", " + ChessDBOpenHelper.WHITE
				+ ", " + ChessDBOpenHelper.BLACK + ", "
				+ ChessDBOpenHelper.WHITE_TIME + ", "
				+ ChessDBOpenHelper.BLACK_TIME + ", "
				+ ChessDBOpenHelper.TIME_MOVE + ", "
				+ ChessDBOpenHelper.RESULT + ", " + ChessDBOpenHelper.ECO
				+ ", " + ChessDBOpenHelper.FEN + ", "
				+ ChessDBOpenHelper.PLYCOUNT + ", " + ChessDBOpenHelper.FEN
				+ ", " + ChessDBOpenHelper.MOVES + " from "
				+ ChessDBOpenHelper.TABLE_NAME
				+ (next ? (" WHERE _id <= ") : (" WHERE _id >= ")) + i
				+ " ORDER BY " + ChessDBOpenHelper.ID
				+ (next ? " DESC " : " ASC ") + "LIMIT 0,32", null);
		// mDatabase.close();
		return cursor;
	}

	public Cursor getGames(int i, boolean isAscended, int maxSize) {
		Log.i(TAG, "i: " + i + " maxSize: " + maxSize);
		// TODO Auto-generated method stub
		mDatabase = mChessDBOpenHelper.getReadableDatabase();
		Cursor cursor = mDatabase.query(ChessDBOpenHelper.TABLE_NAME,
				new String[] { ChessDBOpenHelper.ID, ChessDBOpenHelper.EVENT,
						ChessDBOpenHelper.SITE, ChessDBOpenHelper.DATE,
						ChessDBOpenHelper.ROUND, ChessDBOpenHelper.WHITE,
						ChessDBOpenHelper.BLACK, ChessDBOpenHelper.WHITE_TIME,
						ChessDBOpenHelper.BLACK_TIME,
						ChessDBOpenHelper.TIME_MOVE,
						ChessDBOpenHelper.RESULT, ChessDBOpenHelper.ECO,
						ChessDBOpenHelper.FEN, ChessDBOpenHelper.PLYCOUNT,
						ChessDBOpenHelper.FEN, ChessDBOpenHelper.MOVES

				}, ChessDBOpenHelper.ID + " = " + i, null, null, null,
				ChessDBOpenHelper.ID);
		// Log.d(TAG,"Cursor size: " + cursor.getCount());
		mDatabase.close();
		return cursor;
	}

	public Cursor search(String patern) {
		// TODO: Search in Database
		String[] tokens = new String[100];

		Log.i(TAG, "Patern:" + patern + "|");

		mDatabase = mChessDBOpenHelper.getReadableDatabase();
		String query = "SELECT " + ChessDBOpenHelper.ID + ", "
				+ ChessDBOpenHelper.EVENT + ", " + ChessDBOpenHelper.SITE
				+ ", " + ChessDBOpenHelper.DATE + ", "
				+ ChessDBOpenHelper.ROUND + ", " + ChessDBOpenHelper.WHITE
				+ ", " + ChessDBOpenHelper.BLACK + ", "
				+ ChessDBOpenHelper.WHITE_TIME + ", "
				+ ChessDBOpenHelper.BLACK_TIME + ", "
				+ ChessDBOpenHelper.TIME_MOVE + ", "
				+ ChessDBOpenHelper.RESULT + ", " + ChessDBOpenHelper.ECO
				+ ", " + ChessDBOpenHelper.FEN + ", "
				+ ChessDBOpenHelper.PLYCOUNT + ", " + ChessDBOpenHelper.FEN
				+ ", " + ChessDBOpenHelper.MOVES + " FROM "
				+ ChessDBOpenHelper.TABLE_NAME + " WHERE "
				+ ChessDBOpenHelper.EVENT + "  LIKE \'%" + patern + "%\' OR "
				+ ChessDBOpenHelper.SITE + "  LIKE \'%" + patern + "%\' OR "
				+ ChessDBOpenHelper.DATE + "  LIKE \'%" + patern + "%\' OR "
				+ ChessDBOpenHelper.WHITE + "  LIKE \'%" + patern + "%\' OR "
				+ ChessDBOpenHelper.BLACK + "  LIKE \'%" + patern + "%\' OR "
				+ ChessDBOpenHelper.ECO + "  LIKE \'%" + patern + "%\' OR "
				+ ChessDBOpenHelper.MOVES + "  LIKE \'%" + patern + "%\'"/*
																		 * +
																		 * "%\' OR "
																		 * //+
																		 * ChessDBOpenHelper
																		 * .TAG
																		 * +
																		 * "  LIKE \'%"
																		 * +
																		 * patern
																		 * +
																		 * "%\' OR "
																		 * +
																		 * ChessDBOpenHelper
																		 * .
																		 * RESULT
																		 * +
																		 * "  LIKE \'%"
																		 * +
																		 * patern
																		 * +
																		 * "%\' OR "
																		 * +
																		 * ChessDBOpenHelper
																		 * .
																		 * PLYCOUNT
																		 * +
																		 * " = "
																		 * +
																		 * patern
																		 * +
																		 * " OR "
																		 * +
																		 * ChessDBOpenHelper
																		 * .
																		 * WHITE_ELO
																		 * +
																		 * " = "
																		 * +
																		 * patern
																		 * +
																		 * " OR "
																		 * +
																		 * ChessDBOpenHelper
																		 * .
																		 * BLACK_ELO
																		 * +
																		 * " = "
																		 * +
																		 * patern
																		 */
				+ " ORDER BY " + ChessDBOpenHelper.ID + " DESC LIMIT 0,32";
		Log.i(TAG, query);

		Cursor cursor = mDatabase.rawQuery(query, null);

		return cursor;
	}

	/**
	 * Game basic information
	 * 
	 * @param event
	 * @param site
	 * @param date
	 * @param round
	 * @param white
	 * @param black
	 * @param white_elo
	 * @param black_elo
	 * @param result
	 * @param eco
	 * @param plycount
	 * @return ROWID of the inserted game
	 */
	public long insertGameToDatabase(String event, String site, String date,
			int round, String white, String black, int white_time,
			int black_time, int time_move, 
			String result, String eco, int plycount, String fen, String moves) {
		int _id = getMaxGameId();
		mDatabase = mChessDBOpenHelper.getWritableDatabase();
		ContentValues initialvalues = new ContentValues();
		initialvalues.put(ChessDBOpenHelper.ID, _id + 1);
		initialvalues.put(ChessDBOpenHelper.EVENT, event);
		initialvalues.put(ChessDBOpenHelper.SITE, site);
		initialvalues.put(ChessDBOpenHelper.DATE, date);
		initialvalues.put(ChessDBOpenHelper.ROUND, round);
		initialvalues.put(ChessDBOpenHelper.WHITE, white);
		initialvalues.put(ChessDBOpenHelper.BLACK, black);
		initialvalues.put(ChessDBOpenHelper.WHITE_TIME, white_time);
		initialvalues.put(ChessDBOpenHelper.BLACK_TIME, black_time);
		initialvalues.put(ChessDBOpenHelper.TIME_MOVE , time_move);
		initialvalues.put(ChessDBOpenHelper.RESULT, result);
		initialvalues.put(ChessDBOpenHelper.ECO, eco);
		initialvalues.put(ChessDBOpenHelper.PLYCOUNT, plycount);
		initialvalues.put(ChessDBOpenHelper.FEN, fen);
		initialvalues.put(ChessDBOpenHelper.MOVES, moves);
		return mDatabase.insert(ChessDBOpenHelper.TABLE_NAME, null,
				initialvalues);

	}

	public int getMaxGameId() {
		mDatabase = mChessDBOpenHelper.getReadableDatabase();
		Cursor cursor = mDatabase.rawQuery("SELECT MAX ("
				+ ChessDBOpenHelper.ID + ") FROM "
				+ ChessDBOpenHelper.TABLE_NAME, null);
		// Return
		if (cursor.moveToFirst()) {
			// Log.i(TAG,"getMaxGameId1: " + cursor.getInt(0));
			mDatabase.close();
			// Log.i(TAG,"getMaxGameId2: " + cursor.getInt(0));
			int i = cursor.getInt(0);
			cursor.close();
			return i;
		} else {
			cursor.close();
			mDatabase.close();
			return 0;
		}
	}

	public int getNumberOfGames() {
		// TODO: Get number of games in database
		mDatabase = mChessDBOpenHelper.getReadableDatabase();
		Cursor cursor = mDatabase.rawQuery("SELECT COUNT ("
				+ ChessDBOpenHelper.ID + ") FROM "
				+ ChessDBOpenHelper.TABLE_NAME, null);

		if (cursor.moveToFirst()) {
			mDatabase.close();
			int i = cursor.getInt(0);
			cursor.close();
			return i;
		} else {
			cursor.close();
			mDatabase.close();
			return 0;
		}
	}

	public void statistics() {
		// TODO: Statistics all database;
	}

	public String getExportFormatString(int i) {
		// TODO get ExportStringFormat of game where id = i
		return null;
	}

	public void deleteId(int i) {
		// TODO Auto-generated method stub
		mDatabase = mChessDBOpenHelper.getWritableDatabase();
		mDatabase.execSQL("DELETE FROM " + ChessDBOpenHelper.TABLE_NAME
				+ " WHERE " + ChessDBOpenHelper.ID + " = " + i);
		mDatabase.close();
	}

	public void updateGameFenInDatabase(int id, String fen) {
		// TODO Auto-generated method stub
		mDatabase = mChessDBOpenHelper.getWritableDatabase();
		Log.i(TAG, "id: " + id + " fen: " + fen);
		// ContentValues updateValue = new ContentValues();
		// updateValue.put(ChessDBOpenHelper.FEN, fen);
		// mDatabase.update(ChessDBOpenHelper.FEN,updateValue ,
		// ChessDBOpenHelper.ID + " = " + id, null);
		mDatabase.execSQL("UPDATE " + ChessDBOpenHelper.TABLE_NAME + " SET "
				+ ChessDBOpenHelper.FEN + "=\'" + fen + "\' WHERE "
				+ ChessDBOpenHelper.ID + " = " + id);
		mDatabase.close();
	}

	public void clearDatabase() {
		// TODO Auto-generated method stub
		mDatabase = mChessDBOpenHelper.getWritableDatabase();
		mDatabase.delete(ChessDBOpenHelper.TABLE_NAME, null, null);
		mDatabase.close();
	}

	public void safeClose() {
		// TODO Auto-generated method stub
		try {
			// commit();
			mDatabase.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void commit() {
		if (mDatabase != null)
			try {
				mDatabase.endTransaction();
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
	}
}
