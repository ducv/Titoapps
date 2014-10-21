package com.ducv.fff.chess;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewDebug.HierarchyTraceType;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ducv.fff.chess.ui.VisualBoard;
import com.ducv.fff.chess.utils.database.ChessDBOpenHelper;
import com.ducv.fff.chess.utils.database.ChessDBTransact;
import com.ducv.fff.chess.utils.pgn.Attributes;
import com.ducv.fff.chess.utils.pgn.PGNGame;

public class DatabaseView extends Activity {

	public static final int FILE_PATH_REQUEST_CODE = 201;
	private static final int FILE_WRITING_ERROR = 101;

	private static ChessDBTransact dbTransact;
	private static HorizontalScrollView horizontalScrollView;
	private static FrameLayout frameLayout = null;
	private static final String DEFAULT_DATABASE_NAME = "fff_chess.db";
	private static final int DEFAULT_DATABSE_VER = 1;

	// private HashMap<Integer, PGNGame> games = new HashMap<Integer,
	// PGNGame>();
	// private HashMap<Integer, VisualBoard> boards = new HashMap<Integer,
	// VisualBoard>();

	private Integer[] keys;
	private ArrayList<PGNGame> gamesArray = new ArrayList<PGNGame>();
	private PGNGame currentGame;
	public ProgressDialog mProgress;
	public static final String TAG = "pluto.DatabaseView";

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mProgress != null)
				mProgress.dismiss();
		}
	};
	private LinearLayout mainLayout;
	private LinearLayout row1Layout;
	private LinearLayout row2Layout;
	private LinearLayout row3Layout;
	private LinearLayout.LayoutParams row1LayoutParams;
	private LinearLayout.LayoutParams row2LayoutParams;
	private LinearLayout.LayoutParams row3LayoutParams;
	private VisualBoard previewBoard;
	private LinearLayout row1col1Layout;
	private LinearLayout row3col2Layout;
	private ListView gamesListView;
	private Button openButton;

	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	HashMap<Integer, Integer> keysMap = new HashMap<Integer, Integer>();
	private int currentGameId = 0;
	private SimpleAdapter adapter;
	private int currentCount = 0;
	private int currentFocus = 0;

	private int width;
	private int boardWidth;
	private int height;
	private int buttonHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		width = disp.getWidth();
		height = disp.getHeight();
		if (width == 240) {
			boardWidth = 200;
		} else {
			boardWidth = width;
		}
		if (height <= 480) {
			buttonHeight = 40;
		} else {
			buttonHeight = 80;
		}
		dbTransact = new ChessDBTransact(this/* , mDatabaseFileName, version */);
		currentGameId = dbTransact.getMaxGameId();
		setupViews();
		/*
		 * mProgress = ProgressDialog.show(this, "Please wait!",
		 * "Reading database...", true, true);
		 */
		setContentView(createContentView());/*
											 * ChessDBTransact dbTransact = new
											 * ChessDBTransact(this,
											 * DEFAULT_DATABASE_NAME,
											 * DEFAULT_DATABSE_VER);
											 */
		getFirst();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case FILE_WRITING_ERROR: {

		}
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// start here
		// getFirst();

		super.onResume();
	}

	public void setupViews() {
		horizontalScrollView = new HorizontalScrollView(this);
		horizontalScrollView.setPadding(10, 10, 10, 10);
		horizontalScrollView.setHorizontalScrollBarEnabled(false);
		frameLayout = new FrameLayout(this);
		horizontalScrollView.addView(frameLayout);
		this.setContentView(horizontalScrollView);
	}

	public void getFirst() {
		dbTransact = new ChessDBTransact(this/* , mDatabaseFileName, version */);
		currentGameId = dbTransact.getMaxGameId();
		if (currentGameId == 0) {
			// ???
			dbTransact
					.insertGameToDatabase(
							"play vs Android",
							"Leiden NED",
							"1994.10.??",
							1,
							"Android(8)",
							"The King",
							0,
							0,
							0,
							"1-0",
							"0",
							34,
							"",
							"1.e4 e5 2.Nf3 f5 3.Nxe5 Qf6 4.Nc4 fxe4 5.Nc3 Qf7 6.Ne3 c6 7.Nxe4 d5 8.Ng5 Qf6 9.Nf3 d4 10.Nc4 b5 11.Qe2+ Kd8 12.Nce5 Bc5 13.Qe4 Ne7 14.Bxb5 Re8 15.b4 Bf5 16.Qh4 Ng6 17.Qxf6+ gxf6 18.O-O Nxe5 19.Nh4 Bxc2 20.Bxc6 Nbxc6 21.bxc5 Bd3 22.Re1 Nb4 23.Bb2 Nc2 24.Rec1 Nxa1 25.Bxa1 Nc6 26.Nf3 Re4 27.Ne1 Ba6 28.a3 Kc7 29.Nc2 Rd8 30.Ne1 Rde8  31.Nf3 Ne5 32.Nxd4 Nd3 33.Rf1 Re1 34.Nf3");
			dbTransact
					.insertGameToDatabase(
							"play vs Android",
							"Leiden NED",
							"1994.10.??",
							0,
							"Android(10)",
							"Bionic",
							0,
							0,
							0,
							"1-0",
							"0",
							45,
							"",
							"1.e4 d5 2.exd5 Nf6 3.Bb5+ Nbd7 4.d4 Nxd5 5.c4 N5f6 6.Nf3 c6 7.Ba4 Nb6 8.Bb3 Bg4 9.Nc3 Bxf3 10.Qxf3 Qxd4 11.Be3 Qd3 12.Rd1 Qg6 13.O-O e5 14.Qh3 Bb4 15.Na4 Nxa4 16.Bxa4 O-O 17.a3 Be7 18.f4 exf4 19.Rxf4 a6 20.Rd2 Qb1+ 21.Rf1 Qa2 22.Bd4 Qxc4 23.Bb3 Qb5 24.Qg3 Nh5 25.Qd3 Qxd3 26.Rxd3 Rad8 27.Rdd1 Rxd4 28.Rxd4 Bc5 29.Rfd1 Rd8 30.Rf1 Bxd4+ 31.Kh1 Rf8 32.Bd1 Nf6 33.b4 Bb2 34.a4 Nd5 35.b5 axb5 36.axb5 Ne3 37.Re1 Nxd1 38.Rxd1 cxb5 39.g4 Re8 40.Rd7 Re1+ 41.Kg2 f6 42.h4 b6 43.Rd6 Bc3 44.h5 Ba5 45.Kf2 Re8");
			dbTransact
					.insertGameToDatabase(
							"together",
							"Cairo EGY",
							"2009.07.12",
							5,
							"Kareim Wageih",
							"Mohamed Ezat",
							2253,
							2459,
							0,
							"1-0",
							"0",
							40,
							"",
							"1. d4 Nf6 2. Nf3 c5 3. d5 b5 4. Bg5 Ne4 5. Bf4 Bb7 6. Qd3 f5 7. Nbd2 c4 8. Qd4 Na6 9. Nxe4 fxe4 10. Qxe4 e6 11. c3 Nc7 12. Bg5 Qb8 13. Qh4 Nxd5 14. e4 Ne7 15. Rd1 Ng6 16. Qh5 Bxe4 17. Nh4 Bc5 18. f3 O-O 19. Rxd7 Qe5 20. Rd2 Bxf3");
			dbTransact.commit();

		}
//		mProgress = ProgressDialog.show(this, "Please wait!",
//				"Reading database...", true, true);
//		new Thread(new Runnable() {
//
//			public void run() {
				// TODO Auto-generated method stub
				getGamesFirstFrom(currentGameId);
//				Message msg = new Message();
//				mHandler.sendMessage(msg);
//			}
//		}).start();
	}

	public void getGamesFirstFrom(int max) {
		getGamesFirstFrom(max, true);
	}

	public void getGamesFirstFrom(int max, boolean next) {
		final Cursor cursor = dbTransact.getGames(max, next);

		if (cursor == null) {
			Log.e(TAG, "Cursor == null");
			return;
		}
		if (cursor.getCount() < 1) {
			cursor.close();
			return;
		} else {
			currentCount = cursor.getCount();
			mHandler.post(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub
					row2Layout.removeAllViews();
					row2Layout.addView(gamesListView,
							new LinearLayout.LayoutParams(width, height
									- buttonHeight - boardWidth));

				}
			});
		}

		gamesArray.clear();
		list.clear();

		keys = new Integer[1024];
		int _k = 0;

		int _id_index = cursor.getColumnIndex(ChessDBOpenHelper.ID);
		int _event_index = cursor.getColumnIndex(ChessDBOpenHelper.EVENT);
		int _site_index = cursor.getColumnIndex(ChessDBOpenHelper.SITE);
		int _date_index = cursor.getColumnIndex(ChessDBOpenHelper.DATE);
		int _round_index = cursor.getColumnIndex(ChessDBOpenHelper.ROUND);
		int _white_index = cursor.getColumnIndex(ChessDBOpenHelper.WHITE);
		int _black_index = cursor.getColumnIndex(ChessDBOpenHelper.BLACK);
		int _white_elo_index = cursor
				.getColumnIndex(ChessDBOpenHelper.WHITE_TIME);
		int _black_elo_index = cursor
				.getColumnIndex(ChessDBOpenHelper.BLACK_TIME);
		int _result_index = cursor.getColumnIndex(ChessDBOpenHelper.RESULT);
		int _eco_index = cursor.getColumnIndex(ChessDBOpenHelper.ECO);
		int _ply_count_index = cursor
				.getColumnIndex(ChessDBOpenHelper.PLYCOUNT);
		int _fen_index = cursor.getColumnIndex(ChessDBOpenHelper.FEN);
		int _move_index = cursor.getColumnIndex(ChessDBOpenHelper.MOVES);

		if (next ? cursor.moveToFirst() : cursor.moveToLast()) {
			while (true) {
				int id = cursor.getInt(_id_index);

				String moves = cursor.getString(_move_index).trim();
				Attributes att = new Attributes();
				String result = "";
				att.set("Event", cursor.getString(_event_index));
				att.set("Site", cursor.getString(_site_index));
				att.set("Date", cursor.getString(_date_index));
				att.set("Round", cursor.getString(_round_index));
				att.set("White", cursor.getString(_white_index));
				att.set("Black", cursor.getString(_black_index));
				att.set("WhiteElo", cursor.getString(_white_elo_index));
				att.set("BlackElo", cursor.getString(_black_elo_index));
				att.set("Result", cursor.getString(_result_index));
				att.set("Eco", cursor.getString(_eco_index));
				att.set("PlyCount", cursor.getString(_ply_count_index));
				att.set("Fen", cursor.getString(_fen_index));

				String wResult = "*";
				String bResult = "*";
				if (moves.endsWith(" *")) {
					result = "Unknown!";
					moves = moves.substring(0, moves.length() - 2);

				} else if (moves.endsWith(" 1-0")) {
					result = "White wins!";
					wResult = "1";
					bResult = "0";
					moves = moves.substring(0, moves.length() - 4);
				} else if (moves.endsWith(" 0-1")) {
					wResult = "0";
					bResult = "1";
					result = "Black wins!";
					moves = moves.substring(0, moves.length() - 4);
				} else if (moves.endsWith(" 1/2-1/2")) {
					wResult = "1/2";
					bResult = "1/2";
					result = "Draw!";
					moves = moves.substring(0, moves.length() - 8);
				}
				// Log.i(TAG, "Moves: " + moves);
				PGNGame game = new PGNGame(att, moves);

				HashMap<String, String> gHashMap = new HashMap<String, String>();

				gHashMap.put("date", att.getTag("Date"));
				gHashMap.put("white", wResult + " " + att.getTag("White"));
				gHashMap.put("site", att.getTag("Site"));
				gHashMap.put("black", bResult + " " + att.getTag("Black"));
				gamesArray.add(game);
				list.add(gHashMap);
				// Log.i("Id:", "" + id);
				// Move to next result;
				currentGameId = id;
				keys[_k++] = id;
				if (!(next ? cursor.moveToNext() : cursor.moveToPrevious()))
					break;
			}
			mHandler.post(new Runnable() {

				public void run() {

					adapter = new SimpleAdapter(DatabaseView.this, list,
							R.layout.database_view_games_list_view,
							new String[] { "date", "white", "site", "black" },
							new int[] { R.id.dbview_date_text,
									R.id.dbview_whitename_and_result,
									R.id.dbview_site_text,
									R.id.dbview_blackname_and_result });

					gamesListView.setAdapter(adapter);
				}
			});
			focus(0);
		}
		cursor.deactivate();
		cursor.close();
		dbTransact.safeClose();
	}

	public void deleteId(int id) {
		ChessDBTransact dbTransact = new ChessDBTransact(this,
				DEFAULT_DATABASE_NAME, DEFAULT_DATABSE_VER);
		dbTransact.deleteId(id);
		//
	}

	public LinearLayout createContentView() {
		mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setGravity(Gravity.CENTER_VERTICAL);
		row1Layout = new LinearLayout(this);
		row1Layout.setOrientation(LinearLayout.VERTICAL);
		row1Layout.setGravity(Gravity.CENTER_VERTICAL);
		row1LayoutParams = new LinearLayout.LayoutParams(width, boardWidth);

		row1col1Layout = new LinearLayout(this);

		previewBoard = new VisualBoard(this, boardWidth, 2);

		row1col1Layout.addView(previewBoard, new LinearLayout.LayoutParams(
				boardWidth, boardWidth));
		row1col1Layout.setGravity(Gravity.CENTER);
		row1Layout.addView(row1col1Layout, new LinearLayout.LayoutParams(width,
				width));

		// WWWWWWWWWWWW - row 2
		row2Layout = new LinearLayout(this);
		row2LayoutParams = new LinearLayout.LayoutParams(width, height
				- boardWidth - buttonHeight);
		// scrollView = new ScrollView(this);
		gamesListView = getListView();
		// scrollView.addView(gamesListView,new
		// LinearLayout.LayoutParams(320,200-48));
		// adapter
		row2Layout.addView(gamesListView, new LinearLayout.LayoutParams(width,
				(height - boardWidth - buttonHeight)));

		// WWWWWWWWWWWW - row 3
		row3Layout = new LinearLayout(this);
		row3LayoutParams = new LinearLayout.LayoutParams(width, buttonHeight);
		row3Layout.setOrientation(LinearLayout.VERTICAL);
		openButton = new Button(this);
		openButton.setBackgroundResource(R.drawable.button_custom);
		openButton.setLayoutParams(new LayoutParams(width, buttonHeight));
		openButton.setText("Load Game");
		openButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				openCurrentGame();
			}
		});
		row3col2Layout = new LinearLayout(this);
		row3col2Layout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		row3col2Layout.setOrientation(LinearLayout.VERTICAL);
		row3col2Layout.setGravity(Gravity.CENTER);

		row3col2Layout.addView(openButton);
		row3Layout.addView(row3col2Layout);

		mainLayout.addView(row1Layout, row1LayoutParams);
		mainLayout.addView(row2Layout, row2LayoutParams);
		mainLayout.addView(row3Layout, row3LayoutParams);

		return mainLayout;
	}

	public ListView getListView() {
		gamesListView = new ListView(this);
		/*
		 * HashMap<String,String> test = new HashMap<String,String>();
		 * test.put("date", "31-08-2009"); test.put("white", "0 Thua");
		 * test.put("site", "Kftghcfthctfhcftyhc 987 6 9o ro lam");
		 * test.put("black","1 Chansfnvojdsfv roi"); list.add(test);
		 */
		adapter = new SimpleAdapter(
				this,
				list,
				R.layout.database_view_games_list_view,
				new String[] { "date", "white", "site", "black" },
				new int[] { R.id.dbview_date_text,
						R.id.dbview_whitename_and_result,
						R.id.dbview_site_text, R.id.dbview_blackname_and_result });

		gamesListView.setAdapter(adapter);
		gamesListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (currentFocus != arg2)
					focus(arg2);
			}
		});
		return gamesListView;
	}

	public void focus(int i) {
		if (i >= gamesArray.size() || i < 0)
			return;
		try {
			currentGame = gamesArray.get(i);
			final VirtualBoard vboard = new VirtualBoard();
			vboard.setupStart('0');
			String fen = currentGame.get("Fen");
			if (fen != null) {
				if (fen.length() >= 4) { // So shit
					try {
						vboard.inputFen(fen);
						Log.i("FEN found:", fen);
					} catch (Exception e) {
						vboard.parseMovesString(currentGame.get("Moves"));
						dbTransact.updateGameFenInDatabase(keys[i],
								vboard.getFen());
						dbTransact.commit();

					}
				} else {
					vboard.parseMovesString(currentGame.get("Moves"));
					dbTransact
							.updateGameFenInDatabase(keys[i], vboard.getFen());
					dbTransact.commit();
				}
			} else {
				vboard.parseMovesString(currentGame.get("Moves"));
				dbTransact.updateGameFenInDatabase(keys[i], vboard.getFen());
				dbTransact.commit();
			}
			// vboard.parseMovesString(currentGame.get("Moves"));
			/*
			 * Looper.prepare(); new Handler().post(new Runnable() {
			 * 
			 * public void run() { // TODO Auto-generated method stub
			 * previewBoard.updateByArray(vboard.get8x8Array()); } });
			 */
			previewBoard.updateByArray(vboard.get8x8Array());
			currentFocus = i;
		} catch (Exception e) {
			Log.i(TAG, "Exception: " + e.toString());
		} finally {
			//
		}
	}

	public void nextGames() {
		getGamesFirstFrom(currentGameId - 1);
	}

	public void previousGames() {
		getGamesFirstFrom(currentGameId + currentCount, false);
	}

	public void openCurrentGame() {
		Intent intent = new Intent(this, PlayActivity.class);
		String moves = "";
		moves = currentGame.get("Moves");
		intent.putExtra("new_game", false);
		intent.putExtra("Fen", currentGame.get("Fen"));
		intent.putExtra("moves", moves);
		intent.putExtra("White", currentGame.get("White"));
		intent.putExtra("Black", currentGame.get("Black"));
		intent.putExtra("Result", currentGame.get("Result"));

		if (currentGame.get("Event").equals("play vs Android")) {
			intent.putExtra("PLAYERS", 1);
		} else {
			intent.putExtra("PLAYERS", 2);
		}
		intent.putExtra("WhiteTime", currentGame.get("WhiteElo"));
		intent.putExtra("BlackTime", currentGame.get("BlackElo"));
		intent.putExtra("MoveTime", currentGame.get("Eco"));

		intent.putExtra("PLAYER1", currentGame.get("White"));
		intent.putExtra("PLAYER2", currentGame.get("Black"));
		startActivity(intent);
		previewBoard.free();
		previewBoard = null;
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			previewBoard.free();
			previewBoard = null;
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	//

}
