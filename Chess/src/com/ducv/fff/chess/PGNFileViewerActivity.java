package com.ducv.fff.chess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ducv.fff.chess.ui.VisualBoard;
import com.ducv.fff.chess.utils.pgn.PGNGame;
import com.ducv.fff.chess.utils.pgn.PGNParser;

public class PGNFileViewerActivity extends DynamicListActivity {

	private static final String TAG = "com.fpt.chess.PGNFileViewer";
	private ArrayList<PGNGame> allGames;
	private ArrayList<VirtualBoard> allVirtualBoard;
	private GameListAdapter adapter;
	private ProgressDialog mProgressDialog;
	private BufferedReader pgnBuffered = null;
	private TextView textview = null;
	private String pgnFilename = null;
	private Intent mIntent = null;
	private Handler mHandler = null;
	private Runnable setAdapterRunnable = null;
	private int width;
	private int height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		width = disp.getWidth();
		height = disp.getHeight();
		textview = new TextView(this);
		mIntent = this.getIntent();
		pgnFilename = mIntent.getStringExtra("pgnfilename");
		mHandler = new Handler();
		setAdapterRunnable = new Runnable() {
			public void run() {
				if (adapter != null)
					setListAdapter(adapter);
			}
		};
		mProgressDialog = ProgressDialog.show(PGNFileViewerActivity.this,
				"Please wait...", "Retrieving data ...", true);
		allVirtualBoard = new ArrayList<VirtualBoard>();
		new Thread() {
			@Override
			public void run() {

				try {
					pgnBuffered = getPGNFileBuffer(pgnFilename);
					textview.setText(pgnBuffered.toString());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					Log.i(TAG, "File not found");
					new AlertDialog.Builder(PGNFileViewerActivity.this)
							.setIcon(R.drawable.stop_alert)
							.setTitle("Error!")
							.setMessage(
									"There are something wrong.\nSmart Chess cannot open it!")
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											finish();
										}
									}).show();
				}

				setContentView(R.layout.gamepreview);
				if (pgnBuffered != null)
					allGames = getAllGames(pgnBuffered);
				if (allGames == null) {
					allGames = new ArrayList<PGNGame>();
				}
				// allGames = new ArrayList<PGNGame>();
				adapter = new GameListAdapter(PGNFileViewerActivity.this,
						allGames);
				mHandler.post(setAdapterRunnable);
				mProgressDialog.dismiss();
			}
		}.start();
	}

	public ArrayList<PGNGame> getAllGames(BufferedReader pgnBuffered) {
		ArrayList<PGNGame> allGames = new ArrayList<PGNGame>();
		ArrayList<String> splitedStrings = PGNParser.splitPGNFile(pgnBuffered);
		int size = splitedStrings.size();
		Log.d(TAG, "Found: " + size + " games");
		for (int i = 0; i < size; i++) {
			String s = splitedStrings.get(i);
			// Log.i(TAG, "String: " + s);
			allGames.add(PGNParser.getPGNGame(s));
			// Log.i(TAG, "Parsing completed");

		}
		return allGames;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Log.i(TAG, "position: " + position);
		PGNGame pgnGame = allGames.get(position);
		try {
			int[] array = allVirtualBoard.get(position).getMoveArray();
			Log.i(TAG, "pgnGame: " + array.toString());
			Intent tIntent = new Intent(this, TrainingActivity.class);
			tIntent.putExtra("movesarray", array);
			startActivity(tIntent);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private BufferedReader getPGNFileBuffer(String pgnFilename)
			throws FileNotFoundException {
		File pgnFile = new File(pgnFilename);
		FileReader pgnFileReader = new FileReader(pgnFile);
		BufferedReader buffered = new BufferedReader(pgnFileReader);
		Log.i(TAG, "getBuffered Completed");
		return buffered;
	}

	/*
	 * public ListView createList() { LinearLayout mainpanel = new
	 * LinearLayout(this); ListView listview = new ListView(this); TextView
	 * child1 = new TextView(this); child1.setText("Ngon");
	 * listview.addView(child1); return listview; }
	 */

	public class GameListAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<PGNGame> mGamesList;

		public GameListAdapter(Context context, ArrayList<PGNGame> gamesList) {
			this.mContext = context;
			this.mGamesList = gamesList;
		}

		public int getCount() {
			return mGamesList.size();
		}

		public PGNGame getItem(int location) {
			return mGamesList.get(location);
		}

		public long getItemId(int arg0) {
			// TODO Rewrite
			return 0;
		}

		public void addGame(PGNGame pgnGame) {
			mGamesList.add(pgnGame);
		}

		public View getView(int location, View arg1, ViewGroup arg2) {
			// TODO I am not sure
			View v = arg1;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.pgn_game_row, null);
			}
			PGNGame pgnGame = mGamesList.get(location);
			if (pgnGame != null) {
				VisualBoard visualboard = (VisualBoard) v
						.findViewById(R.id.preview_board);
				TextView strText = (TextView) v.findViewById(R.id.str_text);
				VirtualBoard virtualboard = new VirtualBoard();
				virtualboard.setupStart('0');
				Log.d(TAG, "pgnGame: " + pgnGame.getHitsString());
				virtualboard.makeBatchMoves(pgnGame.getHitsArrayList());
				if (visualboard != null) {
					visualboard.setVisibility(View.VISIBLE);
					visualboard.reSize(140);
					visualboard.updateByArray(virtualboard.get8x8Array());
				}
				if (strText != null)
					strText.setText(pgnGame.getAttributes());
				allVirtualBoard.add(location, virtualboard);
			}
			return v;
		}
	}

	public class GameInfoView extends LinearLayout implements
			View.OnLongClickListener, View.OnClickListener {

		private PGNGame pgnGame;
		private VisualBoard visualboard;

		public GameInfoView(Context context, PGNGame pgnGame) {
			super(context);
			this.pgnGame = pgnGame;
			this.setLayoutParams(new LayoutParams(width, 32));
			// Add more Views here
			TextView textview = new TextView(context);
			textview.setText(pgnGame.getAttributes());
			this.setOrientation(Gravity.CENTER_HORIZONTAL);
			visualboard = getVisualBoard(pgnGame);
			this.addView(visualboard, 32, 32);
			// .addView(textview);
		}

		public VisualBoard getVisualBoard(PGNGame game) {
			VirtualBoard virtualboard = new VirtualBoard();
			// PGNHit hits = game.g
			//
			// TODO : makeBatchMoves();
			virtualboard.setupStart('0');
			// ###virtualboard.makeBatchMoves(game.getHitsArrayList());
			//
			VisualBoard visualboard = new VisualBoard(this.getContext(),320,1);
			visualboard.setupDefault();
			visualboard.reSize(32);
			visualboard.updateByArray(virtualboard.get8x8Array());
			return visualboard;
		}

		public boolean onLongClick(View v) {
			PreviewTask previewer = new PreviewTask(visualboard);
			previewer.execute(pgnGame);
			return false;
		}

		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void goBack() {
		// TODO Auto-generated method stub

	}

	@Override
	public void goForward() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load() {
		// TODO Auto-generated method stub

	}

	public class PreviewTask extends AsyncTask<PGNGame, Integer, Integer> {
		private VisualBoard visualboard;

		public PreviewTask(VisualBoard visualboard) {
			this.visualboard = visualboard;
		}

		@Override
		protected Integer doInBackground(PGNGame... arg0) {
			ArrayList<String> notations = arg0[0].getHitsArrayList();
			VirtualBoard virtualboard = new VirtualBoard();
			virtualboard.setupStart('0');
			visualboard.updateByArray(virtualboard.get8x8Array());
			for (int i = 0; i < notations.size(); i++) {
				virtualboard.makeNotationMove(notations.get(i));
				visualboard.updateByArray(virtualboard.get8x8Array());
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return 0;
		}

	}
}
