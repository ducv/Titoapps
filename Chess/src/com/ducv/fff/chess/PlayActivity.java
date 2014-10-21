package com.ducv.fff.chess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import mediocre.board.Move;
import mediocre.def.Definitions;
import mediocre.engine.Engine;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ducv.fff.chess.ui.Audio;
import com.ducv.fff.chess.ui.ChessClock;
import com.ducv.fff.chess.ui.NavigateButton;
import com.ducv.fff.chess.ui.PlayerImageButton;
import com.ducv.fff.chess.ui.VisualBoard;
import com.ducv.fff.chess.utils.EngineTask;
import com.ducv.fff.chess.utils.History;
import com.ducv.fff.chess.utils.Node;
import com.ducv.fff.chess.utils.database.ChessDBTransact;
import com.ducv.fff.preference.Preference;

public class PlayActivity extends Activity implements Definitions {

	/**
	 * 
	 */
	public static final int EASY = 1;
	public static final int NORMAL = 2;
	public static final int HARD = 3;
	public static final int NET = 4;
	AudioManager audiomanager;

	/*
	 * 
	 */

	public static final int SIGLE_PLAYER = 1;
	public static final int TWO_PLAYER = 2;
	/*
	 * 
	 * private static final int MENU_BACK = Menu.FIRST; private static final int
	 * MENU_NEXT = Menu.FIRST + 1; private static final int MENU_ZOOM =
	 * Menu.FIRST + 2; private static final int MENU_OPEN = Menu.FIRST + 3;
	 * private static final int MENU_MORE = Menu.FIRST + 4; private static final
	 * int MENU_QUIT = Menu.FIRST + 5;
	 * 
	 * private static final int MENU_NEW_GAME = Menu.FIRST + 6; private static
	 * final int MENU_SAVE_GAME = Menu.FIRST + 7; private static final int
	 * MENU_PAUSE_RESUME = Menu.FIRST + 8; private static final int
	 * MENU_PREFERENCES = Menu.FIRST + 9; private static final int MENU_HELP =
	 * Menu.FIRST + 10;
	 */

	private static final int MENU_PAUSE = Menu.FIRST;
	private static final int MENU_NEW = Menu.FIRST + 1;
	private static final int MENU_OPEN = Menu.FIRST + 2;
	private static final int MENU_SAVE = Menu.FIRST + 3;
	private static final int MENU_OPTIONS = Menu.FIRST + 4;
	private static final int MENU_QUIT = Menu.FIRST + 5;

	public static final int GAME_OVER_DIALOG = 99;
	public static final int SAVE_GAME_DIALOG = 100;

	public static final int BLACK_PROMOTION_DIALOG = 101;
	public static final int WHITE_PROMOTION_DIALOG = 102;

	public static final int QUIT_GAME_DIALOG = 103;
	public static final int HELP_DIALOG = 104;
	public static final int ABOUT_DIALOG = 105;
	public static final int LOW_MEMORY_DIALOG = 106;
	public static final int LOG_VIEW_DIALOG = 107; // the logview(tab activity)

	public static final int GE_BLACKMATES = 80;
	public static final int GE_WHITEMATES = 81;
	public static final int GE_BLACKSTALEMATES = 82;
	public static final int GE_WHITESTALEMATES = 83;
	public static final int GE_DRAWBY50MOVESRULES = 84;
	public static final int GE_DRAWBYREPETITION = 85;
	public static final int GE_DRAWBYMETERIAL = 86;
	public static final int GNE_NORMAL = 87;
	public static final int GNE_PAUSE = 88;
	public static final int GNE_STOP = 89;

	private static final int BLACK_MATES_DIALOG = 90;
	private static final int WHITE_MATES_DIALOG = 91;
	private static final int WHITE_STALEMATE_DIALOG = 92;
	private static final int BLACK_STALEMATE_DIALOG = 93;
	private static final int DRAW_BY_50_MOVES_RULE_DIALOG = 94;
	private static final int DRAW_BY_REPETITION_DIALOG = 95;
	private static final int DRAW_BY_METERIAL_DIALOG = 96;

	// /exit dialog
	private static final int EXIT_DIALOG = 1001;

	public static final String TAG = "camper";
	public static final int OPEN_GAME_REQUEST = 100;
	public static final int SETUP_GAME_OPTIONS = 200;
	private int width;
	private int height;
	protected VisualBoard visualboard;
	protected VirtualBoard virtualboard;
	protected VirtualBoard oldVirtualboard;
	private ChessClock blackClock;
	private ChessClock whiteClock;
	private int blackTime = 600000;
	private int whiteTime = 600000;
	private NavigateButton pauseresumeButton;
	private Dialog white_promotion_dialog;
	private Dialog black_promotion_dialog;
	private boolean whiteIsUser;
	private boolean blackIsUser;
	private boolean paused;
	private boolean started = false;
	private int depth = 3;
	private int timeLeft = 1000;
	private int moveTime = 1000;
	private int increment = 1000;
	private OnClickListener promotionDialogListener;
	private OnTouchListener boardTouchListener;
	private Character PROMOTION = 'Q';
	private History history;
	private Intent mIntent;
	private Handler mHandler;

	private ArrayList<HashMap<String, String>> allMoves = new ArrayList<HashMap<String, String>>();
	private int current_index = 0;
	HashMap<String, String> nullMove = new HashMap<String, String>();
	// The colume for

	private String blackName = "black";
	private String whiteName = "white";
	private String event = "";
	private String site = "";
	private int round = 0;
	// private int blackElo = 0;
	// private int whiteElo = 0;
	private int players = 1;
	private char handicap = '0';
	private String strPlayer1, strPlayer2;
	private ProgressBar thinking;

	// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW

	private LinearLayout mainLayout = null;
	private LinearLayout bottomLayout = null;
	private LinearLayout boardLayout;
	private LinearLayout playLayout;
	private LinearLayout topBar;
	private LinearLayout bottomBar;
	private LinearLayout titleBar;// //maybe for advertising
	private LinearLayout controlButtonLayout;
	private int statusBarHeight;
	private TextView blackText;
	private TextView whiteText;

	// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
	// /////dimentions
	private int titleHeight = 50;
	private int optionBarHeight = 50;
	private int barWidth = 320;
	private int playHeight;
	private int playWidth;
	private int boardDimen;
	// //button Undu
	private Button undo;
	private boolean showThinking;
	private boolean newgame;

	// ////Imagebutton for result
	private ImageView checkImage, winningImage;
	private LinearLayout topResult, bottomResult;

	// ////////////
	public static enum Orientation {
		PORTRAIT, LANSCAPE
	};

	public static enum Player {
		USER, ANDROID, NETWORK
	}; // ? Unused

	private static enum STATE {
		NOTEND, BLACKWIN, WHITEWIN, DRAW
	};

	private static STATE currentState = STATE.NOTEND;
	private Orientation orientation;
	private String currentMove = "";
	private Runnable backRunnable;
	private Runnable forwardRunnable;
	private Runnable makeAIMoveRunnable;
	private boolean queenAsDefaultPromotion = false;
	private boolean canGoBack = true;
	private boolean disableScreenLock;
	private EngineTask aiTask = new EngineTask(this, visualboard);
	private int maxTime = 60;
	private WakeLock mWakeLock;

	/* OrientationEventListener mListener; */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Audio.init(this, new int[] { R.raw.check, R.raw.victory,
				R.raw.piecedrop, R.raw.select });
		audiomanager = ((AudioManager) this.getSystemService("audio"));
		getPrefs(); // TODO: preferences
		// if (!Preference.getStatusBar(this)) {
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// }
		/**********************************************************/
		// ////get extra
		players = getIntent().getExtras().getInt("PLAYERS", SIGLE_PLAYER);
		strPlayer1 = getIntent().getExtras().getString("PLAYER1");
		strPlayer2 = getIntent().getExtras().getString("PLAYER2");
		newgame = getIntent().getExtras().getBoolean("new_game");
		if (strPlayer1.equals("")) {
			strPlayer1 = "Player 1";
		}
		if (strPlayer2.equals("")) {
			strPlayer2 = "Player 2";
		}

		if (!newgame) {
			moveTime = Integer.parseInt(getIntent().getExtras().getString(
					"MoveTime"));
			if (players == 1) {
				String str = strPlayer1.substring(8, 10);
				if (str.charAt(1) == '0') {
					depth = 10;
				} else {
					depth = str.charAt(0) - '0';
				}
			}
		}

		// ///
		getCurrentScreenResolution();
		/**********************************************************/
		whiteIsUser = true;
		blackIsUser = false;
		// thinking = new ProgressBar(this, null,
		// android.R.attr.progressBarStyleSmall);
		calculateBoard();
		// TODO: create new
		// Log.i(TAG, "create new");

		checkImage = new ImageView(this);
		winningImage = new ImageView(this);
		checkImage.setBackgroundResource(R.drawable.check);
		winningImage.setBackgroundResource(R.drawable.winner);

		setupViews();
		LinearLayout mainContentView = getDefaultLayout();// createContentView(orientation);
		// visualboard.setupDefault();
		LinearLayout.LayoutParams mainLayoutParam = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		this.setContentView(mainContentView, mainLayoutParam);

		virtualboard = new VirtualBoard();
		oldVirtualboard = new VirtualBoard();
		virtualboard.setupStart(handicap);
		visualboard.setArray(virtualboard.get8x8Array());

		mIntent = this.getIntent();
		backRunnable = new Runnable() {
			public void run() {
				goBack();
			}
		};
		forwardRunnable = new Runnable() {
			public void run() {
				goForward();
			}
		};
		makeAIMoveRunnable = new Runnable() {
			public void run() {
				if (players == SIGLE_PLAYER)
					startAI();
			}
		};
		// The older way do it
		// level = mIntent.getIntExtra("level", EASY);
		// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				// Log.i(TAG,"catched:" + msg.what);
				switch (msg.what) {
				case GE_BLACKMATES:
					currentState = STATE.BLACKWIN;
					if (blackIsUser == !whiteIsUser) {
						showGameOver(GE_BLACKMATES);
					} else
						showDialog(BLACK_MATES_DIALOG);
					break;
				case GE_WHITEMATES:
					currentState = STATE.WHITEWIN;
					if (blackIsUser == !whiteIsUser) {
						showGameOver(GE_WHITEMATES);
					} else
						showDialog(WHITE_MATES_DIALOG);
					break;
				case GE_BLACKSTALEMATES:
					currentState = STATE.BLACKWIN;
					if (blackIsUser == !whiteIsUser) {
						showGameOver(GE_BLACKSTALEMATES);
					} else
						showDialog(BLACK_STALEMATE_DIALOG);
					break;
				case GE_WHITESTALEMATES:
					currentState = STATE.WHITEWIN;
					if (blackIsUser == !whiteIsUser) {
						showGameOver(GE_WHITESTALEMATES);
					} else
						showDialog(WHITE_STALEMATE_DIALOG);
					break;
				case GE_DRAWBY50MOVESRULES:
					currentState = STATE.DRAW;
					if (blackIsUser == !whiteIsUser) {
						int time = getUserTime();
						int minutes = 10;
						int seconds = 0;
						time -= 40;
						minutes = time / 60000;
						seconds = (time - minutes * 60000) / 1000;
						String timeString = String.format("%02d:%02d", minutes,
								seconds);
						showYouDraw(timeString);
					} else
						showDialog(DRAW_BY_50_MOVES_RULE_DIALOG);
					break;
				case GE_DRAWBYREPETITION:
					currentState = STATE.DRAW;
					if (blackIsUser == !whiteIsUser) {
						int time = getUserTime();
						int minutes = 10;
						int seconds = 0;
						time -= 40;
						minutes = time / 60000;
						seconds = (time - minutes * 60000) / 1000;
						String timeString = String.format("%02d:%02d", minutes,
								seconds);
						showYouDraw(timeString);
					} else
						showDialog(DRAW_BY_REPETITION_DIALOG);
					break;
				case GE_DRAWBYMETERIAL:
					currentState = STATE.DRAW;
					if (blackIsUser == !whiteIsUser) {
						int time = getUserTime();
						int minutes = 10;
						int seconds = 0;
						time -= 40;
						minutes = time / 60000;
						seconds = (time - minutes * 60000) / 1000;
						String timeString = String.format("%02d:%02d", minutes,
								seconds);
						showYouDraw(timeString);
					} else
						showDialog(DRAW_BY_METERIAL_DIALOG);
					break;
				case GNE_NORMAL: {
					// Game not end - continue
					System.gc();
					currentState = STATE.NOTEND;
					if (virtualboard.isTurnBlack()) {
						blackClock.resume();
						whiteClock.pause();
					} else {
						blackClock.pause();
						whiteClock.resume();
					}
					if (!whiteIsUser || !blackIsUser
							&& (players == SIGLE_PLAYER)) {
						startAI();
					}

				}
				}
				if (currentState != STATE.NOTEND) {
					blackClock.pause();
					whiteClock.pause();
				}
				super.handleMessage(msg);
			}
		};
		oldVirtualboard = virtualboard;
	}

	public void setupViews() {
		// TODO: setup all views and ...
		visualboard = new VisualBoard(this, boardDimen, players);
		// blackClock = new ChessClock(this);
		// whiteClock = new ChessClock(this);
		pauseresumeButton = new NavigateButton(this);
		pauseresumeButton.setImageBitmap(NavigateButton.pause);
		pauseresumeButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (paused) {
					resume();
				} else {
					pause();
				}
			}
		});
		promotionDialogListener = new OnClickListener() {

			public void onClick(View v) {
				switch (v.getId()) {
				default:
				case R.id.b_queen_button:
				case R.id.w_queen_button:
					PROMOTION = 'q';
					break;
				case R.id.b_rook_button:
				case R.id.w_rook_button:
					PROMOTION = 'r';
					break;
				case R.id.w_knight_button:
				case R.id.b_knight_button:
					PROMOTION = 'n';
					break;
				case R.id.b_bishop_button:
				case R.id.w_bishop_button:
					PROMOTION = 'b';
					break;
				}
				// You must check the null condition
				if (black_promotion_dialog != null)
					if (black_promotion_dialog.isShowing())
						black_promotion_dialog.dismiss();
				if (white_promotion_dialog != null)
					if (white_promotion_dialog.isShowing())
						white_promotion_dialog.dismiss();
				currentMove += PROMOTION;
				makeUserMove();
			}
		};

		boardTouchListener = new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (v.getId() == visualboard.getId()) {
					// Process events here
					float x = event.getX();
					float y = event.getY();
					if (virtualboard.isGameOver())
						return false;
					if (!visualboard.isMutable()
							|| (x >= visualboard.getBoardSize())
							|| (y >= visualboard.getBoardSize()))
						return false;
					else if (!whiteIsUser && virtualboard.isTurnWhite()) {
						return false;
					} else if (!blackIsUser && virtualboard.isTurnBlack()) {
						return false;
					} else if (!blackIsUser && !whiteIsUser) {
						return false;
					} else {
						visualboard.setMutable(false);
						processTouchEvent((int) y / visualboard.getCellSize(),
								(int) x / visualboard.getCellSize(),
								event.getAction());
						visualboard.setMutable(true);
					}
					return true;
				} else if (true) {
					// Chua lam gi ca
				}
				return false;
			}
		};
		// Add listener
		visualboard.setOnTouchListener(boardTouchListener);
	}

	public void processTouchEvent(int r, int c, int action) {
		if (r >= 8 || c >= 8)
			return;
		if (visualboard.isFlip()) {
			r = 7 - r;
			c = 7 - c;
		}
		// //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		int[][] array = virtualboard.get8x8Array(); //
		// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
		if (visualboard.getHighlightedCells() == 0
				&& action == MotionEvent.ACTION_DOWN) {
			if ((virtualboard.isTurnWhite() && array[r][c] > 0 && whiteIsUser)
					|| (virtualboard.isTurnBlack() && array[r][c] < 0 && blackIsUser)) {
				currentMove += "abcdefgh".charAt(c);
				currentMove += (8 - r);
				visualboard.markHighlighted(r, c);
				/**
				 * @author Ducv get legal move
				 */
				String legalString[] = virtualboard.getLegalMoves(currentMove);
				int move = virtualboard.getMoveOfInputNotation(currentMove);
				String notation = Move.notation(move, virtualboard);
				for (int i = 0; i < legalString.length; i++) {
					if (legalString[i].contains(currentMove)) {
						int row = legalString[i].charAt(3) - '0';
						int colume = legalString[i].charAt(2) - 'a';
						visualboard.markCanMove(8 - row, colume);
					}
				}

				return;
			}
		} else if (visualboard.getHighlightedCells() == 1
				&& action == MotionEvent.ACTION_DOWN) {
			if ((virtualboard.isTurnWhite() && array[r][c] <= 0 && whiteIsUser)
					|| (virtualboard.isTurnBlack() && array[r][c] >= 0 && blackIsUser)) {
				currentMove += "abcdefgh".charAt(c);
				currentMove += (8 - r);
				visualboard.markHighlighted(r, c);

				/**
				 * @author Ducv get legal move
				 */
				String legalString[] = virtualboard.getLegalMoves(currentMove);
				for (int i = 0; i < legalString.length; i++) {
					if (legalString[i].contains(currentMove)) {
						int row = legalString[i].charAt(3) - '0';
						int colume = legalString[i].charAt(2) - 'a';
						visualboard.markCanMove(8 - row, colume);
					}
				}

				if ((r == 0 || r == 7) //
						//
						&& (array[8 - Integer.parseInt(currentMove.substring(1,
								2))]["abcdefgh".indexOf(currentMove.charAt(0))] == Definitions.W_PAWN || array[8 - Integer
								.parseInt(currentMove.substring(1, 2))]["abcdefgh"
								.indexOf(currentMove.charAt(0))] == Definitions.B_PAWN)) {
					// TODO: if queen is default promotion
					if (queenAsDefaultPromotion) {
						PROMOTION = 'Q';
						visualboard.clearHighlightedCells();
						visualboard.clearCanMoveHightLight();
						currentMove += PROMOTION;
						makeUserMove();
						return;
					}
					// TODO: Show promotion Dialog
					else if (virtualboard.isTurnBlack() && blackIsUser) {
						visualboard.clearHighlightedCells(); // so bad
						visualboard.clearCanMoveHightLight();
						for (int i = 0; i < legalString.length; i++) {
							if (legalString[i].contains(currentMove)) {
								int row = legalString[i].charAt(3) - '0';
								if (8 - row == r) {
									showDialog(BLACK_PROMOTION_DIALOG);
									return;
								}
							}
						}

						return;
					} else if (virtualboard.isTurnWhite() && whiteIsUser) {
						visualboard.clearHighlightedCells(); // so bad
						visualboard.clearCanMoveHightLight();
						for (int i = 0; i < legalString.length; i++) {
							if (legalString[i].contains(currentMove)) {
								int row = legalString[i].charAt(3) - '0';
								if (8 - row == r) {
									showDialog(WHITE_PROMOTION_DIALOG);
									return;
								}
							}
						}

						return;
					}
				}
			} else if ((virtualboard.isTurnWhite() && array[r][c] > 0 && whiteIsUser)
					|| (virtualboard.isTurnBlack() && array[r][c] < 0 && blackIsUser)) {
				currentMove = "";
				currentMove += "abcdefgh".charAt(c);
				currentMove += (8 - r);
				visualboard.clearHighlightedCells();
				visualboard.markHighlighted(r, c);
				visualboard.clearCanMoveHightLight();
				/**
				 * @author Ducv get legal move
				 */
				String legalString[] = virtualboard.getLegalMoves(currentMove);
				for (int i = 0; i < legalString.length; i++) {
					if (legalString[i].contains(currentMove)) {
						int row = legalString[i].charAt(3) - '0';
						int colume = legalString[i].charAt(2) - 'a';
						visualboard.markCanMove(8 - row, colume);
					}
				}
			}
			return;
		}
		if (visualboard.getHighlightedCells() == 2
				&& action == MotionEvent.ACTION_UP) {
			visualboard.clearHighlightedCells();
			visualboard.clearCanMoveHightLight();
			makeUserMove();
			// Log.i(TAG,currentMove);
		}

	}

	/**
	 * Rewrite all
	 */
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, "onSaveInstanceState");
		outState.putBoolean("paused", paused);
		outState.putInt("blackTime", blackClock.getTime());
		outState.putInt("whiteTime", whiteClock.getTime());
		outState.putCharSequence("fen", virtualboard.getFen());
		outState.putBoolean("started", started);
		outState.putBoolean("whiteIsUser", whiteIsUser);
		outState.putBoolean("blackIsUser", blackIsUser);
		outState.putCharSequence("currentMove", currentMove);
		outState.putBoolean("canGoBack", canGoBack);
		visualboard.saveState(outState);
		// ??
		// outState.putParcelableArrayList("notationmovetree",allMoves);
		history.saveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		/*
		 * menu.add(0, MENU_BACK, 0, "Back").setIcon(R.drawable.back);
		 * menu.add(0, MENU_NEXT, 0, "Next").setIcon(R.drawable.next);
		 * menu.add(0, MENU_ZOOM, 0, "Zoom Out").setIcon(
		 * R.drawable.ic_menu_zoom_out); menu.add(0, MENU_OPEN, 0,
		 * "Open").setIcon(R.drawable.open_icon); SubMenu sMenu =
		 * menu.addSubMenu(0, MENU_MORE, 0, "More...").setIcon(
		 * android.R.drawable.ic_menu_more);
		 * 
		 * menu.add(0, MENU_QUIT, 0, "Quit").setIcon(
		 * android.R.drawable.ic_lock_power_off);
		 * 
		 * sMenu.add(0, MENU_NEW_GAME, 0, "New").setIcon(
		 * android.R.drawable.ic_menu_add); sMenu.add(0, MENU_SAVE_GAME, 0,
		 * "Save").setIcon( android.R.drawable.ic_menu_save); sMenu.add(0,
		 * MENU_PAUSE_RESUME, 0, "Pause/Resume").setIcon(
		 * android.R.drawable.ic_media_pause); sMenu.add(0, MENU_PREFERENCES, 0,
		 * "Preferences").setIcon( android.R.drawable.ic_menu_preferences);
		 * sMenu.add(0, MENU_HELP, 0, "Help").setIcon(
		 * android.R.drawable.ic_menu_help);
		 */
		if (paused)
			menu.add(0, MENU_PAUSE, 0, "Resume").setIcon(
					android.R.drawable.ic_media_play);
		else
			menu.add(0, MENU_PAUSE, 0, "Pause").setIcon(
					android.R.drawable.ic_media_pause);
		menu.add(0, MENU_NEW, 0, "New").setIcon(android.R.drawable.ic_menu_add);
		menu.add(0, MENU_OPEN, 0, "Open").setIcon(R.drawable.open_icon);
		menu.add(0, MENU_SAVE, 0, "Save").setIcon(
				android.R.drawable.ic_menu_save);
		menu.add(0, MENU_OPTIONS, 0, "Options").setIcon(
				android.R.drawable.ic_menu_preferences);
		menu.add(0, MENU_QUIT, 0, "Quit").setIcon(
				android.R.drawable.ic_lock_power_off);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/*
		 * case MENU_BACK: { if (!canGoBack) return false;
		 * mHandler.removeCallbacks(makeAIMoveRunnable);
		 * mHandler.postAtFrontOfQueue(backRunnable); break; }
		 */
		/*
		 * case MENU_NEXT: { if (!history.canForward()) return false;
		 * mHandler.removeCallbacks(makeAIMoveRunnable);
		 * mHandler.postAtFrontOfQueue(forwardRunnable); break; }
		 */
		/*
		 * case MENU_ZOOM: { if (zoomedIn) { item.setTitle("Zoom Out");
		 * item.setIcon(R.drawable.ic_menu_zoom_out); zoomedIn = false;
		 * zoomIn(); } else { item.setTitle("Zoom In");
		 * item.setIcon(android.R.drawable.ic_menu_zoom); zoomedIn = true;
		 * zoomOut(); } break; }
		 */
		case MENU_NEW: {
			mHandler.post(new Runnable() {
				public void run() {
					newGame();
				}
			});
			break;
		}
		case MENU_OPEN: {
			Intent iIntent = new Intent();
			iIntent.setClass(this, DatabaseView.class);
			startActivityForResult(iIntent, OPEN_GAME_REQUEST);
			break;
		}
		case MENU_PAUSE: {
			if (paused) {
				resume();
				item.setIcon(android.R.drawable.ic_media_pause);
				item.setTitle("Pause");
			} else {
				pause();
				item.setIcon(android.R.drawable.ic_media_play);
				item.setTitle("Resume");
			}
			break;
		}
		case MENU_SAVE: {
			// Do something here
			// StartActivity
			mHandler.post(new Runnable() {
				public void run() {
					saveGame();
				}
			});
			break;
		}
		case MENU_OPTIONS: {
			// TODO Lam 1 cai Activity khac
			Intent prIntent = new Intent();
			prIntent.setClass(this, ChessPreferences.class);
			startActivity(prIntent);
			break;
		}
			/*
			 * case MENU_HELP: { // TODO Tao 1 cai Activity Help pause(); // Ha
			 * sach Intent prIntent = new Intent(); prIntent.setClass(this,
			 * HelpActivity.class); startActivity(prIntent); break; }
			 */
		case MENU_QUIT: {
			// clean all data
			pause();
			finish();
		}
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		menu.setHeaderTitle("Menu");
		menu.add("Competition type!").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						Log.i("OnMenuItemSelected:", "clicked");
						return false;
					}
				});
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onContextItemSelected(item);
	}

	protected void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
		// newGame();
	}

	protected void onPause() {
		pause();
		super.onPause();
		if (aiTask != null) {
			if (!aiTask.isCancelled())
				aiTask.cancel(true);
		}
		blackClock.pause();
		whiteClock.pause();
		// aiTask = null;
		if (mWakeLock != null)
			try {
				if (mWakeLock.isHeld())
					mWakeLock.release();
			} catch (RuntimeException re) {
				//
			}
		System.gc();

		/*
		 * SharedPreferences.Editor ed = sPrefs.edit(); //ed.putString("level",
		 * mCurViewMode); ed.commit();
		 */

	}

	protected void onResume() {
		super.onResume();
		getPrefs();
		if (disableScreenLock) {
			final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			this.mWakeLock = pm.newWakeLock(
					PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
			this.mWakeLock.acquire();
		}
		// if (!load) {
		resume();
		// load = false;
		// }
	}

	protected void onRestart() {
		Log.i(TAG, "onRestart");
		super.onRestart();
	}

	protected void onStop() {
		// Log.i(TAG,"onStop");
		if (aiTask != null)
			aiTask.cancel(true);
		System.gc();
		super.onDestroy();
	}

	protected void onDestroy() {
		// Log.i(TAG,"onDestroy");
		System.gc();
		super.onDestroy();
	}

	public void newGame() {
		// /remove winner image
		try {
			bottomResult.removeAllViews();
			topResult.removeAllViews();
		} catch (Exception e) {
			// TODO: handle exception
		}
		cancelAI(true);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			//
		} catch (Exception e) {
			//
		}
		virtualboard = null;
		System.gc();
		virtualboard = new VirtualBoard();
		virtualboard.setupStart(handicap);
		virtualboard.setGameOver(false);
		currentMove = "";
		currentState = STATE.NOTEND;
		// WWWWWWWWWWWWWWWWWWWWWWW
		turnOnPauseButton();
		// WWWWWWWWWWWWWWWWWWWWWWW
		visualboard.clearHighlightedCells();
		visualboard.clearCanMoveHightLight();
		visualboard.clearHistory();
		visualboard.updateByArray(virtualboard.get8x8Array());
		history = new History();

		allMoves = new ArrayList<HashMap<String, String>>();
		nullMove.put("moveFull", "1. ");

		nullMove.put("whiteMove", "*");
		nullMove.put("whiteComment", "");
		nullMove.put("white", "*");
		nullMove.put("blackMove", "");
		nullMove.put("blackComment", "");
		nullMove.put("black", "");
		allMoves.add(nullMove);

		if (newgame) {
			blackClock.restart();
			whiteClock.restart();
			whiteIsUser = true;
			blackIsUser = false;
			visualboard.setCurPlayer(1);
		} else {
			if (players == 1) {
				whiteClock.resume();// player 1
			} else {
				whiteClock.resume();// player 1
				blackClock.resume();
			}
		}

		if (!(whiteIsUser && blackIsUser) && (players == SIGLE_PLAYER))
			startAI();

		if (players == 1) {
			queenAsDefaultPromotion = true;
		} else {
			queenAsDefaultPromotion = false;
		}
		check();

	}

	public void goBack() {
		if (players == SIGLE_PLAYER
				&& ((virtualboard.isTurnWhite() && whiteIsUser) || (virtualboard
						.isTurnBlack() && blackIsUser))) {
			Node n = history.goUpper();
			if (n == null)
				return;
			// if (!paused) {
			// pause();
			// }
			whiteClock.pause();
			int m = n.getMove();
			try {
				virtualboard.unmakeMove(m);
				virtualboard.setGameOver(false);
				current_index--;
			} catch (Exception e) {
				// Log.e(TAG,"Exception when unmakeMove");
			}
			n = history.goUpper();
			m = n.getMove();
			try {
				virtualboard.unmakeMove(m);
				virtualboard.setGameOver(false);
				current_index--;
			} catch (Exception e) {
				// Log.e(TAG,"Exception when unmakeMove");
			}
			// /history
			visualboard.updateByArray(virtualboard.get8x8Array());
			visualboard.clearHistory();
			Node hist = history.goUpper();
			if (hist != null) {
				m = hist.getMove();
				String inputNotation = Move.inputNotation(m);
				visualboard.markHistory(8 - (inputNotation.charAt(1) - '0'),
						inputNotation.charAt(0) - 'a');
				visualboard.markHistory(8 - (inputNotation.charAt(3) - '0'),
						inputNotation.charAt(2) - 'a');
				history.goLowwer();
			}
			visualboard.clearCanMoveHightLight();
			visualboard.clearHighlightedCells();
			blackClock.resume();
			currentMove = "";
		} else if (players == TWO_PLAYER
				&& ((virtualboard.isTurnWhite() && whiteIsUser) || (virtualboard
						.isTurnBlack() && blackIsUser))) {

			Node n = history.goUpper();
			if (n == null)
				return;
			// if (!paused) {
			// pause();
			// }
			/*
			 * if(!aiTask.isCancelled()) return;
			 */
			blackClock.pause();
			whiteClock.pause();
			int m = n.getMove();
			try {
				virtualboard.unmakeMove(m);
				virtualboard.setGameOver(false);
				current_index--;

			} catch (Exception e) {
				// Log.e(TAG,"Exception when unmakeMove");
			}
			// /history
			if (whiteIsUser) {
				virtualboard.toMove = BLACK_TO_MOVE;
				whiteIsUser = false;
				blackIsUser = true;
				visualboard.setCurPlayer(2);
			} else {
				virtualboard.toMove = WHITE_TO_MOVE;
				whiteIsUser = true;
				blackIsUser = false;
				visualboard.setCurPlayer(1);
			}
			visualboard.updateByArray(virtualboard.get8x8Array());
			visualboard.clearHistory();
			// if(current_index>2){
			Node hist = history.goUpper();
			if (hist != null) {
				m = hist.getMove();
				String inputNotation = Move.inputNotation(m);
				visualboard.markHistory(8 - (inputNotation.charAt(1) - '0'),
						inputNotation.charAt(0) - 'a');
				visualboard.markHistory(8 - (inputNotation.charAt(3) - '0'),
						inputNotation.charAt(2) - 'a');
				history.goLowwer();
			}
			visualboard.clearCanMoveHightLight();
			visualboard.clearHighlightedCells();
			currentMove = "";
		}
		check();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (aiTask != null)
			aiTask.cancel(true);
		pause();
		System.gc();
		showDialog(LOW_MEMORY_DIALOG);
	}

	public void goForward() {
		if (aiTask != null)
			aiTask.cancel(true);
		Node n = history.goLowwer();
		if (n == null)
			return;
		int m = n.getMove();
		try {
			virtualboard.makeMove(m);
			visualboard.updateByArray(virtualboard.get8x8Array());
			current_index++;
		} catch (IndexOutOfBoundsException iobe) {
			//
		} catch (Exception e) {
			//
		}

		if (!history.canForward()) {
			resume();
		}
	}

	public void pause() {
		blackTime = blackClock.getTime();
		whiteTime = blackClock.getTime();
		paused = true;
		pauseresumeButton.setImageBitmap(NavigateButton.resume);
	}

	/**
	 * Super sily
	 */
	public void resume() {
		if (!paused) {
			if (newgame) {
				newGame();
			} else {
				newGame();
				String pgnData = getIntent().getStringExtra("moves");
				whiteName = getIntent().getStringExtra("White");
				blackName = getIntent().getStringExtra("Black");
				String fen = getIntent().getStringExtra("Fen");
				if (fen != null) {
					if (fen.length() > 10)
						virtualboard.inputFen(fen);
					else
						virtualboard.parseMovesString(pgnData);
				} else if (pgnData != null)
					virtualboard.parseMovesString(pgnData);
				visualboard.updateByArray(virtualboard.get8x8Array());

			}

			// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
			if (virtualboard.isTurnBlack()) {
				whiteIsUser = false;
				blackIsUser = true;
				visualboard.setCurPlayer(0);
				whiteClock.pause();// /player 1
			} else {
				whiteIsUser = true;
				blackIsUser = false;
				visualboard.setCurPlayer(1);
				blackClock.pause();
			}

			// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

			pauseresumeButton.setImageBitmap(NavigateButton.pause);
			decide();
		} else {
			if (players == 1) {
				if (virtualboard.isTurnBlack()) {
					virtualboard = oldVirtualboard;
					startAI();
					blackClock.resume();
					whiteClock.pause();
				} else if (virtualboard.isTurnWhite()) {
					blackClock.pause();
					whiteClock.resume();
				}
			} else if (players == 2) {
				if (virtualboard.isTurnBlack()) {
					blackClock.resume();
					whiteClock.pause();
				} else if (virtualboard.isTurnWhite()) {
					blackClock.pause();
					whiteClock.resume();
				}
			}

		}
		paused = false;

	}

	private void getCurrentScreenResolution() {
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		width = disp.getWidth();
		height = disp.getHeight();
		statusBarHeight = 0;
		// }
	}

	/**
     * 
     */
	public Dialog onCreateDialog(int id) {
		// Log.i(TAG,"onCreateDialog");
		switch (id) {
		case BLACK_MATES_DIALOG:
			thinking.setVisibility(View.INVISIBLE);
			if (Preference.getSound(PlayActivity.this))
				Audio.play(audiomanager, R.raw.victory, false);
			return new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon)
					.setTitle("Game over")
					.setMessage("Black mates!")
					.setPositiveButton("New Game",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									newGame();
								}
							})
					.setNegativeButton("Hide",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									//
								}
							}).create();
		case WHITE_MATES_DIALOG:
			thinking.setVisibility(View.INVISIBLE);
			if (Preference.getSound(PlayActivity.this)) {
				Audio.play(audiomanager, R.raw.victory, false);
			}

			return new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon)
					.setTitle("Game over")
					.setMessage("White mates!")
					.setPositiveButton("New Game",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									newGame();
								}
							})
					.setNegativeButton("Hide",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									//
								}
							}).create();
		case WHITE_STALEMATE_DIALOG:
			thinking.setVisibility(View.INVISIBLE);
			if (Preference.getSound(PlayActivity.this)) {
				Audio.play(audiomanager, R.raw.victory, false);
			}
			return new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon)
					.setTitle("Game over")
					.setMessage("White stalemate!")
					.setPositiveButton("New Game",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									newGame();
								}
							})
					.setNegativeButton("Hide",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									//
								}
							}).create();
		case BLACK_STALEMATE_DIALOG:
			thinking.setVisibility(View.INVISIBLE);
			if (Preference.getSound(PlayActivity.this)) {
				Audio.play(audiomanager, R.raw.victory, false);
			}
			return new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon)
					.setTitle("Game over")
					.setMessage("Black stalemate!")
					.setPositiveButton("New Game",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									newGame();
								}
							})
					.setNegativeButton("Hide",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									//
								}
							}).create();
		case DRAW_BY_50_MOVES_RULE_DIALOG:
			thinking.setVisibility(View.INVISIBLE);
			return new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon)
					.setTitle("Game over")
					.setMessage("Draw by 50 moves rule!")
					.setPositiveButton("New Game",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									newGame();
								}
							})
					.setNegativeButton("Hide",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									//
								}
							}).create();
		case DRAW_BY_REPETITION_DIALOG:
			thinking.setVisibility(View.INVISIBLE);
			return new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon)
					.setTitle("Game over")
					.setMessage("Draw by repetition!")
					.setPositiveButton("New Game",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									newGame();
								}
							})
					.setNegativeButton("Hide",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									//
								}
							}).create();
		case DRAW_BY_METERIAL_DIALOG:
			thinking.setVisibility(View.INVISIBLE);
			return new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon)
					.setTitle("Game over")
					.setMessage("Draw by meterial!")
					.setPositiveButton("New Game",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									newGame();
								}
							})
					.setNegativeButton("Hide",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									//
								}
							}).create();
		case LOW_MEMORY_DIALOG:
			thinking.setVisibility(View.INVISIBLE);
			return new AlertDialog.Builder(this)
					.setIcon(R.drawable.stop_alert)
					.setTitle("Low memory!")
					.setMessage(
							"Smart Chess is running at low memory environment."
									+ " Be careful with yours programs.")
					.setPositiveButton("Quit",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									if (aiTask != null)
										aiTask.cancel(true);
									System.gc();
									finish();
								}
							})
					.setNegativeButton("Continue",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									//
								}
							}).create();
		case LOG_VIEW_DIALOG:
			return new AlertDialog.Builder(this)
					.setIcon(R.drawable.log_view)
					.setTitle("Log viewer")
					.setMessage(this.getLog())
					.setNegativeButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									//
								}
							}).create();
		case GAME_OVER_DIALOG:
			// add process code here
			return null;
		case SAVE_GAME_DIALOG:
			// add process code here
			return null;
		case BLACK_PROMOTION_DIALOG: {
			black_promotion_dialog = new Dialog(this);
			black_promotion_dialog.setContentView(R.layout.black_promotion);
			black_promotion_dialog.setCancelable(false);

			Button bq = (Button) black_promotion_dialog
					.findViewById(R.id.b_queen_button);
			bq.setOnClickListener(promotionDialogListener);
			Button br = (Button) black_promotion_dialog
					.findViewById(R.id.b_rook_button);
			br.setOnClickListener(promotionDialogListener);
			Button bb = (Button) black_promotion_dialog
					.findViewById(R.id.b_bishop_button);
			bb.setOnClickListener(promotionDialogListener);
			Button bn = (Button) black_promotion_dialog
					.findViewById(R.id.b_knight_button);
			bn.setOnClickListener(promotionDialogListener);
			return black_promotion_dialog;
		}
		case WHITE_PROMOTION_DIALOG: {
			white_promotion_dialog = new Dialog(this);
			white_promotion_dialog.setContentView(R.layout.white_promotion);
			white_promotion_dialog.setCancelable(false);

			Button wq = (Button) white_promotion_dialog
					.findViewById(R.id.w_queen_button);
			wq.setOnClickListener(promotionDialogListener);
			Button wr = (Button) white_promotion_dialog
					.findViewById(R.id.w_rook_button);
			wr.setOnClickListener(promotionDialogListener);
			Button wb = (Button) white_promotion_dialog
					.findViewById(R.id.w_bishop_button);
			wb.setOnClickListener(promotionDialogListener);
			Button wn = (Button) white_promotion_dialog
					.findViewById(R.id.w_knight_button);
			wn.setOnClickListener(promotionDialogListener);
			return white_promotion_dialog;
		}
		}
		return null;
	}

	/**
	 * TODO: Check: State of current game ->> What would we do in next step
	 */
	public void decide() {
		// aiThread = null;
		// System.gc();
		Message msg = new Message();
		int state = virtualboard.getGameState();
		switch (state) {
		case VirtualBoard.BLACK_MATES:
			msg.what = GE_BLACKMATES;
			break;
		case VirtualBoard.WHITE_MATES:
			msg.what = GE_WHITEMATES;
			break;
		case VirtualBoard.BLACK_STALEMATE:
			msg.what = GE_BLACKSTALEMATES;
			break;
		case VirtualBoard.WHITE_STALEMATE:
			msg.what = GE_WHITESTALEMATES;
			break;
		case VirtualBoard.DRAW_BY_50_MOVES_RULE:
			msg.what = GE_DRAWBY50MOVESRULES;
			break;
		case VirtualBoard.DRAW_BY_METERIAL:
			msg.what = GE_DRAWBYMETERIAL;
			break;
		case VirtualBoard.DRAW_BY_REPETITION:
			msg.what = GE_DRAWBYREPETITION;
			break;
		case -1:
		default:
			msg.what = GNE_NORMAL;
			break;
		}
		mHandler.sendMessage(msg);
	}

	/**
	 * make an Ai move: only one move :)
	 */
	public void startAI() {
		// Log.i(TAG,"startAI");
		oldVirtualboard = virtualboard;
		while (true) { // / I didn't intend to make it loop forever
			if (!virtualboard.isGameOver()) {
				if (virtualboard.isTurnWhite() && !whiteIsUser) {
					// Log.i(TAG,"makeWhiteMove");
					aiTask = new EngineTask(this, visualboard);
					aiTask.execute(virtualboard);
					// Log.i("AIStarted","AIStarted");
					blackClock.pause();
					// whiteClock.resume();
					thinking.setVisibility(View.VISIBLE);
					// whiteIcon.toOn();
				} else if (virtualboard.isTurnBlack() && !blackIsUser) {
					// Log.i(TAG,"makeBlackMove");
					aiTask = new EngineTask(this, visualboard);
					aiTask.execute(virtualboard);
					whiteClock.pause();
					thinking.setVisibility(View.VISIBLE);
				} else
				// TODO: Something more didn't declare here
				{
					check();
					if (blackClock.getTime() <= 0
							&& blackClock.getMaxTime() > 0) {
						virtualboard.setGameOver(true);
						Message msg = new Message();
						msg.what = GE_WHITEMATES;
						mHandler.sendMessage(msg);
					}
					if (Preference.getSound(PlayActivity.this)) {
						Audio.play(audiomanager, R.raw.piecedrop, false);
					}
					thinking.setVisibility(View.INVISIBLE);
					return;
				}

			} else
				Log.i(TAG, "gameIsOver");
			break; // remove this
		}
	}

	/**
	 * 
	 * @return true if current moving user is local user
	 */
	public boolean currentTurnUser() {
		if (blackIsUser && virtualboard.isTurnBlack())
			return true;
		if (whiteIsUser && virtualboard.isTurnWhite())
			return true;
		return false;
	}

	/**
	 * ???
	 * 
	 * @param inputNotation
	 */
	/*
	 * public void makeMove(String inputNotation) {
	 * virtualboard.makeInputNotationMove(inputNotation);
	 * visualboard.updateByArray(virtualboard.get8x8Array()); decide(); }
	 */

	/**
	 * make currentMove: the move in inputNotation format like: e2e4 or b8c6 if
	 * moving sucessful then save move and fen into history tree
	 */
	public void makeUserMove() {
		/*
		 * if(virtualboard.enPassant != -1) { if(currentMove.length() == 4) {
		 * int cf = currentMove.charAt(0) - 'a'; int rf = currentMove.charAt(1)
		 * - '1'; int fromIndex = rf*16 + cf; int ct = currentMove.charAt(2) -
		 * 'a'; int rt = currentMove.charAt(3) - '1'; int toIndex = rt*16 + ct;
		 * if(virtualboard.boardArray[fromIndex] == Board.W_PAWN &&
		 * virtualboard.boardArray[toIndex] == Board.EMPTY_SQUARE && cf == ct &&
		 * (rf-rt)==2) if(toIndex == virtualboard.enPassant) currentMove =
		 * currentMove + " e.p."; } }
		 */
		visualboard.clearHistory();
		int move = virtualboard.getMoveOfInputNotation(currentMove);
		String notation = Move.notation(move, virtualboard);
		if (virtualboard.makeInputNotationMove(currentMove)) {
			String fen = virtualboard.getFen();
			visualboard.markHistory(8 - (currentMove.charAt(1) - '0'),
					currentMove.charAt(0) - 'a');
			visualboard.markHistory(8 - (currentMove.charAt(3) - '0'),
					currentMove.charAt(2) - 'a');
			Node node = new Node(move, notation, currentMove, fen); // create
			if (paused)
				resume();
			else
				decide();
			this.addNodeToTree(node); // Save to the history tree
			visualboard.updateByArray(virtualboard.get8x8Array());
			turnOnPauseButton();
			if (Preference.getSound(PlayActivity.this)) {
				Audio.play(audiomanager, R.raw.piecedrop, false);
			}
			if (players == TWO_PLAYER) {
				if (whiteIsUser) {
					whiteIsUser = false;
					blackIsUser = true;
					visualboard.setCurPlayer(0);

				} else {
					whiteIsUser = true;
					blackIsUser = false;
					visualboard.setCurPlayer(1);
				}
			}
			check();
			if (blackClock.getTime() <= 0 && blackClock.getMaxTime() > 0) {
				virtualboard.setGameOver(true);
				Message msg = new Message();
				msg.what = GE_WHITEMATES;
				mHandler.sendMessage(msg);
			} else if (whiteClock.getTime() <= 0 && whiteClock.getMaxTime() > 0) {
				virtualboard.setGameOver(true);
				Message msg = new Message();
				msg.what = GE_BLACKMATES;
				mHandler.sendMessage(msg);
			}
		}
		currentMove = ""; // to Empty
		/*
		 * if (paused) resume(); else decide();
		 */
	}

	private void check() {
		// /remove check image
		try {
			bottomResult.removeAllViews();
			topResult.removeAllViews();
		} catch (Exception e) {
			// TODO: handle exception
		}
		// //
		if (Engine.isInCheck(virtualboard) && virtualboard.getGameState() == -1) {
			if (!whiteIsUser) {
				topResult.addView(checkImage);
				if (Preference.getSound(PlayActivity.this))
					Audio.play(audiomanager, R.raw.check, false);
			} else {
				bottomResult.addView(checkImage);
				if (Preference.getSound(PlayActivity.this))
					Audio.play(audiomanager, R.raw.check, false);
			}
		}
	}

	/**
	 * Write node to the history tree
	 * 
	 * @param node
	 *            : A Node object <CODE>com.fpt.chess.utils.Node</CODE>
	 */
	public void addNodeToTree(Node node) {
		// Log.i(TAG,"Node: move:" + node.getMove() + ",notation: " +
		// node.getNotation()
		// + ",inputNotation: " + node.getInputNotion()
		// + ",fen: " + node.getFEN());
		started = true;

		String notation = node.getNotation();
		if (virtualboard.isTurnBlack()) {
			notation = notation.replace('K', '\u2654');
			notation = notation.replace('Q', '\u2655');
			notation = notation.replace('R', '\u2656');
			notation = notation.replace('B', '\u2657');
			notation = notation.replace('N', '\u2658');
		} else {
			notation = notation.replace('K', '\u265A');
			notation = notation.replace('Q', '\u265B');
			notation = notation.replace('R', '\u265C');
			notation = notation.replace('B', '\u265D');
			notation = notation.replace('N', '\u265E');
		}

		//
		if (virtualboard.isTurnBlack()) // that's white move
		{
			HashMap<String, String> moveHashMap = allMoves
					.get(allMoves.size() - 1);
			moveHashMap.put("moveFull", "" + virtualboard.movesFull + ".");
			moveHashMap.put("whiteMove", notation);
			moveHashMap.put("whiteComment", node.getComment());
			String white = moveHashMap.get("whiteMove")
					+ (moveHashMap.get("whiteComment").length() != 0 ? " {}"
							: "");
			moveHashMap.put("white", white);
			moveHashMap.put("blackComment", "");
			moveHashMap.put("blackMove", "*");
			String black = moveHashMap.get("blackMove")
					+ (moveHashMap.get("blackComment").length() != 0 ? " {}"
							: "");
			moveHashMap.put("black", black);

		} else {
			HashMap<String, String> moveHashMap = allMoves
					.get(allMoves.size() - 1);

			moveHashMap.put("blackMove", notation);
			moveHashMap.put("blackComment", node.getComment());
			String black = moveHashMap.get("blackMove")
					+ (moveHashMap.get("blackComment").length() != 0 ? " {}"
							: "");
			moveHashMap.put("black", black);

			if (!virtualboard.isGameOver()) {
				moveHashMap = new HashMap<String, String>();
				moveHashMap.put("moveFull", "" + virtualboard.movesFull + ".");
				moveHashMap.put("whiteMove", "*");
				moveHashMap.put("whiteComment", "");
				String white = moveHashMap.get("whiteMove")
						+ (moveHashMap.get("whiteComment").length() != 0 ? " {}"
								: "");
				moveHashMap.put("white", white);
				moveHashMap.put("blackMove", "");
				moveHashMap.put("blackComment", "");
				moveHashMap.put("black", "");
				allMoves.add(moveHashMap);
			}
		}

		current_index++;
		history.addNode(node);

	}

	/**
	 * Log is a string represent the moves
	 * 
	 * @return Log String like: 1.e4 e5 2.Nf3 Nc6 3.Bc4 Nf6 4.O-O Be7
	 */
	public String getLog() {
		// TODO generate log (PGN)
		String log = "";
		// log = "FEN: " + virtualboard.getFen();
		log = history.getLog();
		int state = virtualboard.getGameState(); // Lam cach nay ko hay lam
		switch (state) {
		case VirtualBoard.BLACK_MATES:
		case VirtualBoard.BLACK_STALEMATE: {
			/*
			 * if(Engine.isInCheck(virtualboard)) //? Move.notation is completed
			 * log += "# 0-1"; else
			 */log += " 0-1";
			break;
		}
		case VirtualBoard.WHITE_MATES:
		case VirtualBoard.WHITE_STALEMATE: {
			/*
			 * if(Engine.isInCheck(virtualboard)) //? Move.notation is completed
			 * log += "# 1-0"; else
			 */log += " 1-0";
			break;
		}
		case VirtualBoard.DRAW_BY_50_MOVES_RULE:
		case VirtualBoard.DRAW_BY_METERIAL:
		case VirtualBoard.DRAW_BY_REPETITION: {
			log += " 1/2-1/2";
			break;
		}
		default:
			log += " *";
			break;
		}
		return log;
	}

	/**
	 * The depth of AI when it's searching
	 * 
	 * @return
	 */
	public int getAIDepth() {
		return depth;
	}

	/**
	 * 
	 * @return the remain total time of black/white
	 */
	public int getAITimeLeft() {
		if (virtualboard.isTurnBlack())
			return blackClock.getTime();
		else
			return whiteClock.getTime();
	}

	/**
	 * The move time: parameter used by AITask
	 * 
	 * @return
	 */
	public int getAIMoveTime() {
		return moveTime;
	}

	/**
	 * The increment time: parameter used by AI
	 * 
	 * @return increment time used by AITask
	 */
	public int getAIIcrementTime() {
		return increment;
	}

	/**
	 * Show the loose dialog when game over
	 * 
	 * @param time
	 *            : Time string represent the time used Override/rewrite if you
	 *            want
	 */
	public void showGameOver(int message) { // Soo shit
		thinking.setVisibility(View.INVISIBLE);
		String msg = "";
		if (virtualboard.getGameState() == VirtualBoard.BLACK_MATES
				|| message == GE_BLACKMATES) {
			topResult.addView(winningImage);
			msg = strPlayer1 + " is more intellegent than " + strPlayer2;
		} else if (virtualboard.getGameState() == VirtualBoard.WHITE_MATES
				|| message == GE_WHITEMATES) {
			bottomResult.addView(winningImage);
			msg = strPlayer2 + " is more intellegent than " + strPlayer1;
		} else {
			msg = strPlayer1 + " is more intellegent than " + strPlayer2;
		}

		new AlertDialog.Builder(this)
				.setIcon(R.drawable.loose)
				.setTitle("Game over")
				.setMessage(msg)
				.setPositiveButton("New Game",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								newGame();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								//
							}
						}).show();
	}

	/**
	 * Show the draw dialog when game over
	 * 
	 * @param time
	 *            : Time string represent the time used Override/rewrite if you
	 *            want
	 */
	public void showYouDraw(String time) { //
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.draw)
				.setTitle("Game over")
				.setMessage(
						"Draw!\nMoves: " + history.getNumberOfNodes()
								+ "\nTotal time: " + time)
				.setPositiveButton("New Game",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								newGame();
							}
						})
				.setNegativeButton("Hide",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								//
							}
						}).show();
	}

	/**
	 * Get time used by local user
	 * 
	 * @return used time
	 */
	public int getUserTime() {
		if (blackIsUser == !whiteIsUser) {
			if (blackIsUser)
				return blackClock.getMaxTime() - blackClock.getTime();
			else
				return whiteClock.getMaxTime() - whiteClock.getTime();
		} else
			return whiteClock.getMaxTime();
	}

	/**
	 * Cancel Ai if it running
	 * 
	 * @param mayInterruptIfRunning
	 * @return if canceled
	 */
	public boolean cancelAI(boolean mayInterruptIfRunning) {
		if (aiTask == null)
			return true;
		else
			aiTask.cancel(mayInterruptIfRunning);
		if (aiTask.isCancelled())
			return true;
		return false;
	}

	public void saveGame() {
		// TODO: start SaveGameActivity
		if (players == 1) {
			event = "play vs Android";
		} else if (players == 2) {
			event = "together";
		}
		round = 0;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
		String date = formatter.format(new Date());
		whiteName = strPlayer1;
		blackName = strPlayer2;
		String gameNotation = getLog();
		String result;

		if (currentState == STATE.WHITEWIN)
			result = "1-0";
		else if (currentState == STATE.BLACKWIN)
			result = "0-1";
		else if (currentState == STATE.DRAW)
			result = "1/2-1/2";
		else
			result = "*";
		String fen = virtualboard.getFen();
		int plyCount = virtualboard.movesFull;
		if (players == 1) {
			if ((virtualboard.isTurnWhite() && !whiteIsUser)
					|| (virtualboard.isTurnBlack() && !blackIsUser)) {
				Toast.makeText(this, "Wait for Android turn!", 100).show();
			} else {
				ChessDBTransact dbTransact = new ChessDBTransact(this);
				dbTransact.insertGameToDatabase(event, site, date, round,
						whiteName, blackName, whiteClock.getTime(),
						blackClock.getTime(), Preference.getMoveTime(this),
						result, "" + Preference.getMoveTime(this), plyCount,
						fen, gameNotation);
				dbTransact.safeClose();
				Toast.makeText(this, "Success", 100).show();
			}
		} else {
			ChessDBTransact dbTransact = new ChessDBTransact(this);
			dbTransact.insertGameToDatabase(event, site, date, round,
					whiteName, blackName, whiteClock.getTime(),
					blackClock.getTime(), Preference.getMoveTime(this), result,
					"" + Preference.getMoveTime(this), plyCount, fen,
					gameNotation);
			dbTransact.safeClose();
			Toast.makeText(this, "Success", 100).show();
		}

	}

	/**
	 * ECO: rewrite it
	 * 
	 * @return
	 */
	private String getECO() {
		// TODO Auto-generated method stub
		return "ECO";
	}

	private void getPrefs() {
		// Get the xml/preferences.xml preferences
		canGoBack = Preference.getCanUndo(this);
		maxTime = Preference.getTotalTime(this);
		try {
			blackClock.setMaxTime(maxTime);
			whiteClock.setMaxTime(maxTime);
		} catch (NumberFormatException nfe) {
			whiteClock.setMaxTime(0);
			blackClock.setMaxTime(0);
		} catch (Exception e) {

		}
		try {
			depth = Preference.getDifficult(this);
		} catch (NumberFormatException e) {
			depth = 1;
		} catch (Exception e) {
			// TODO: handle exception
		}
		moveTime = Preference.getMoveTime(this);
		disableScreenLock = Preference.getScreenOn(this);
		switch (Preference.getHandicrap(this)) {
		case 0:
			handicap = '0';
			break;
		case 1:
			handicap = 'p';
			break;
		case 2:
			handicap = 'n';
			break;
		case 3:
			handicap = 'r';
			break;
		case 4:
			handicap = 'q';
			break;

		default:
			break;
		}
	}

	public void turnOnPauseButton() {
		paused = false;
		pauseresumeButton.setImageBitmap(NavigateButton.pause);
	}

	public void updateNotationTreeView() {
		/*
		 * if (listView != null) { adapter = new SimpleAdapter(this, allMoves,
		 * R.layout.move_item, new String[] { "move", "comment" }, new int[] {
		 * R.id.move_string, R.id.move_comment }); listView.setAdapter(adapter);
		 * //
		 * listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL); }
		 */
	}

	/**
	 * This function is not good enough.
	 * 
	 * @param i
	 */
	public void editNotation(int i) {
		final HashMap<String, String> move = allMoves.get(i);
		final Dialog d = new Dialog(this);
		d.setTitle("Edit comment");
		d.setCanceledOnTouchOutside(true);
		final EditText editText = new EditText(this);
		editText.setText(move.get("comment"));
		editText.setLines(8);
		editText.setLayoutParams(new LinearLayout.LayoutParams(240, 200));
		Button okButton = new Button(this);
		okButton.setText("OK");
		okButton.setMinWidth(120);
		okButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String comment = editText.getText().toString();
				move.put("comment", comment);
				updateNotationTreeView();
				d.dismiss();
			}
		});
		Button cancelButton = new Button(this);
		cancelButton.setText("Hide");
		cancelButton.setMinWidth(120);
		cancelButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				d.dismiss();
			}
		});
		LinearLayout buttonLayout = new LinearLayout(this);
		buttonLayout.setOrientation(Gravity.CENTER_VERTICAL);
		buttonLayout.addView(cancelButton, new LinearLayout.LayoutParams(120,
				40));
		buttonLayout.addView(okButton, new LinearLayout.LayoutParams(120, 40));
		LinearLayout editCommentlayout = new LinearLayout(this);
		editCommentlayout.setLayoutParams(new LinearLayout.LayoutParams(240,
				240));
		editCommentlayout.setOrientation(Gravity.CENTER_HORIZONTAL);
		editCommentlayout.addView(editText/*
										 * ,new LinearLayout.LayoutParams(
										 * LinearLayout
										 * .LayoutParams.FILL_PARENT,
										 * LinearLayout
										 * .LayoutParams.WRAP_CONTENT)
										 */);
		editCommentlayout.addView(buttonLayout, new LinearLayout.LayoutParams(
		/* LinearLayout.LayoutParams.WRAP_CONTENT */280,
		/* LinearLayout.LayoutParams.WRAP_CONTENT */40));
		d.setContentView(editCommentlayout);
		d.show();
		// d.setContentView(view)
	}

	// WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW

	public LinearLayout getDefaultLayout() {
		whiteText = new TextView(this);
		whiteText.setText("WHITE");
		whiteText.setTextSize(12);
		whiteText.setGravity(Gravity.CENTER);
		whiteText.setTextColor(Color.WHITE);
		// whiteTextLayout.addView(whiteText,48,16); ///
		blackText = new TextView(this);
		blackText.setText("BLACK");
		blackText.setTextSize(12);
		blackText.setGravity(Gravity.CENTER);
		blackText.setTextColor(Color.WHITE);

		bottomLayout = new LinearLayout(this);
		bottomLayout.setBackgroundColor(Color.BLACK);

		controlButtonLayout = new LinearLayout(this);
		controlButtonLayout.setOrientation(LinearLayout.VERTICAL);
		controlButtonLayout.setLayoutParams(new LinearLayout.LayoutParams(64,
				112));
		controlButtonLayout.setBackgroundColor(Color.BLACK);
		controlButtonLayout.setGravity(Gravity.CENTER);

		bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
		// bottomLayout.setGravity(Gravity.CENTER);
		bottomLayout.setBackgroundColor(Color.BLACK);
		bottomLayout.addView(controlButtonLayout, 64, 112);
		// ==================for top bar
		topBar = new LinearLayout(this);
		topBar.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, optionBarHeight));
		topBar.setGravity(Gravity.CENTER);
		LinearLayout ll = new LinearLayout(this);
		ll.setGravity(Gravity.CENTER);
		ll.setLayoutParams(new LinearLayout.LayoutParams(
				(int) (convertDpToPixel((float) 320, this)), optionBarHeight));

		LayoutInflater inflater = getLayoutInflater();
		View contentTop = inflater.inflate(R.layout.optionbar_with_progressbar,
				null);
		blackClock = (ChessClock) contentTop.findViewById(R.id.id_time);
		TextView textviewPlayer1 = (TextView) contentTop
				.findViewById(R.id.id_name);
		textviewPlayer1.setText(strPlayer1);
		topResult = (LinearLayout) contentTop.findViewById(R.id.id_result);
		ll.addView(contentTop);
		thinking = (ProgressBar) contentTop.findViewById(R.id.id_progress);
		topBar.addView(ll);
		thinking.setVisibility(View.INVISIBLE);
		Animation topBarAnimation = AnimationUtils.loadAnimation(this,
				R.anim.slide_left);
		topBarAnimation.setDuration(500);
		topBar.setAnimation(topBarAnimation);

		// //title
		titleBar = new LinearLayout(this);
		titleBar.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, titleHeight));
		titleBar.setGravity(Gravity.CENTER);
		ImageView img = new ImageView(this);
		img.setBackgroundResource(R.drawable.title);
		img.setLayoutParams(new LayoutParams((int) (convertDpToPixel(
				(float) 320, this)), LayoutParams.FILL_PARENT));
		titleBar.addView(img);
		titleBar.setAnimation(topBarAnimation);
		// /
		// //bottom bar
		bottomBar = new LinearLayout(this);
		bottomBar.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, optionBarHeight));
		bottomBar.setGravity(Gravity.CENTER);
		View contentBottom = inflater.inflate(R.layout.optionbar, null);
		undo = (Button) contentBottom.findViewById(R.id.id_undo);
		bottomResult = (LinearLayout) contentBottom
				.findViewById(R.id.id_result);
		undo.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!virtualboard.isGameOver()) {
					mHandler.removeCallbacks(makeAIMoveRunnable);
					mHandler.postAtFrontOfQueue(backRunnable);
				}
			}
		});

		whiteClock = (ChessClock) contentBottom.findViewById(R.id.id_time);
		LinearLayout ll2 = new LinearLayout(this);
		ll2.setGravity(Gravity.CENTER);
		ll2.setLayoutParams(new LinearLayout.LayoutParams(
				(int) (convertDpToPixel((float) 320, this)), optionBarHeight));
		ll2.addView(contentBottom);
		bottomBar.addView(ll2);
		TextView textviewPlayer2 = (TextView) contentBottom
				.findViewById(R.id.id_name);
		textviewPlayer2.setText(strPlayer2);
		Animation bottomBarAnimation = AnimationUtils.loadAnimation(this,
				R.anim.slide_right);
		topBarAnimation.setDuration(500);
		bottomBar.setAnimation(bottomBarAnimation);
		// /for calculate the board

		boardLayout = new LinearLayout(this);
		boardLayout.setLayoutParams(new LinearLayout.LayoutParams(boardDimen,
				boardDimen));
		boardLayout.setOrientation(LinearLayout.HORIZONTAL);
		// topLayout.setGravity(Gravity.CENTER);
		boardLayout.setBackgroundColor(Color.BLACK);
		boardLayout.addView(visualboard, boardDimen, boardDimen);

		playLayout = new LinearLayout(this);
		playLayout.setLayoutParams(new LinearLayout.LayoutParams(playWidth,
				playHeight));
		playLayout.setOrientation(LinearLayout.VERTICAL);
		playLayout.setGravity(Gravity.CENTER);
		playLayout.addView(boardLayout);

		mainLayout = new LinearLayout(this);
		mainLayout.setBackgroundResource(R.drawable.bg);
		// mainLayout.setGravity(Gravity.CENTER);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		// //addview
		// mainLayout.addView(blackClock);

		/*
		 * init clock
		 */
		{
			if (!newgame) {
				blackTime = Integer.parseInt(getIntent().getExtras().getString(
						"BlackTime"));
				whiteTime = Integer.parseInt(getIntent().getExtras().getString(
						"WhiteTime"));
				if (blackTime > 0) {
					blackClock.setTime(blackTime);
				} else {
					blackClock.setTime(0);
				}
				if (whiteTime > 0) {
					whiteClock.setTime(whiteTime);
				} else {
					whiteClock.setTime(0);
				}
			}
		}
		mainLayout.addView(titleBar);
		mainLayout.addView(topBar);
		mainLayout.addView(playLayout);
		mainLayout.addView(bottomBar);
		// mainLayout.addView(bottomLayout, 320, 112);
		return mainLayout;
	}

	public String getResult() {
		if (currentState == STATE.NOTEND)
			return "*";
		if (currentState == STATE.WHITEWIN)
			return "1-0";
		if (currentState == STATE.BLACKWIN)
			return "0-1";
		if (currentState == STATE.DRAW)
			return "1/2-1/2";
		return "";
	}

	/*
	 * calculate board
	 */
	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	private void calculateBoard() {

		if (height >= 800 || width >= 800 && (height < 1024 && width < 1024)) {
			titleHeight = (int) convertDpToPixel(50, this);
			optionBarHeight = 65;
			optionBarHeight = (int) convertDpToPixel(optionBarHeight, this);
			playHeight = height - titleHeight - 2 * optionBarHeight
					- statusBarHeight;
			playWidth = width;
			if (playHeight >= playWidth) {
				boardDimen = playWidth;
			} else {
				boardDimen = playHeight;
			}
			barWidth = 320;
		} else if (height < 800 || width < 800) {
			optionBarHeight = (int) convertDpToPixel(optionBarHeight, this);
			titleHeight = (int) convertDpToPixel(titleHeight, this);
			playHeight = height - titleHeight - 2 * optionBarHeight
					- statusBarHeight;
			playWidth = width;
			if (playHeight >= playWidth) {
				boardDimen = playWidth;
			} else {
				boardDimen = playHeight;
			}
			barWidth = 320;
		} else {
			titleHeight = (int) convertDpToPixel(70, this);
			optionBarHeight = 90;
			optionBarHeight = (int) convertDpToPixel(optionBarHeight, this);
			playHeight = height - titleHeight - 2 * optionBarHeight
					- statusBarHeight;
			playWidth = width;
			if (playHeight >= playWidth) {
				boardDimen = playWidth;
			} else {
				boardDimen = playHeight;
			}
			barWidth = 480;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			showExitDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showExitDialog() {
		final Dialog exitDlg = new Dialog(this);
		LayoutInflater inflater = getLayoutInflater();
		exitDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(R.layout.exitgamedialog, null);
		exitDlg.setContentView(view);
		view.findViewById(R.id.id_quit).setOnClickListener(
				new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						virtualboard = null;
						visualboard = null;
						exitDlg.dismiss();
						finish();
					}
				});
		view.findViewById(R.id.id_save_quit).setOnClickListener(
				new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						// Do something here
						// StartActivity
						if ((virtualboard.isTurnWhite() && !whiteIsUser)
								|| (virtualboard.isTurnBlack() && !blackIsUser)) {
							Toast.makeText(PlayActivity.this,
									"Wait for Android turn!", 100).show();
						} else {
							mHandler.post(new Runnable() {
								public void run() {
									saveGame();
								}
							});
							exitDlg.dismiss();
							finish();
						}

					}
				});
		view.findViewById(R.id.id_cancel).setOnClickListener(
				new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						exitDlg.dismiss();
					}
				});
		exitDlg.show();

	}
}
