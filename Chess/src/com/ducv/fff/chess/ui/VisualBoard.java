/**
 * 
 */
package com.ducv.fff.chess.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.ducv.fff.chess.R;
import com.ducv.fff.preference.Preference;

public class VisualBoard extends View {

	public static final int PLAYER1 = 0;
	public static final int PLAYER2 = 1;

	private int boardSize;
	private int cellSize;
	private Bitmap blackpieces[] = new Bitmap[6];
	private Bitmap whitepieces[] = new Bitmap[6];

	private Bitmap blackpieces_rotation[] = new Bitmap[6];
	private Bitmap whitepieces_rotation[] = new Bitmap[6];

	private Bitmap[] rawBlackIcons = new Bitmap[6];
	private Bitmap[] rawWhiteicons = new Bitmap[6];

	private Bitmap hightLightLegal, hightLightLastMove, boardBlackWhite,
			boardRedWhite, boardWood;
	public static final String TAG = "ducv.Board";
	// /
	private boolean mutable = false;
	private boolean flip = false;
	// Array for save background Board Color color
	private byte[][] bgBoardColor = { // ?
			{ 1, -1, 1, -1, 1, -1, 1, -1 }, // 1: White background cells
			{ -1, 1, -1, 1, -1, 1, -1, 1 }, // -1: Black background cells
			{ 1, -1, 1, -1, 1, -1, 1, -1 }, { -1, 1, -1, 1, -1, 1, -1, 1 },
			{ 1, -1, 1, -1, 1, -1, 1, -1 }, { -1, 1, -1, 1, -1, 1, -1, 1 },
			{ 1, -1, 1, -1, 1, -1, 1, -1 }, { -1, 1, -1, 1, -1, 1, -1, 1 } };

	private Paint blackPaint; // painter for black cells
	private Paint whitePaint; // painter for white cells
	private Paint highlightedPaint; // for highlight cells
	private Paint imageIconPaint; // for painting images icon
	private int n_HighlightedCells;
	private int[][] cells = new int[8][8];

	private Context context;
	private int curPlayer = PLAYER2;
	private int players = 1;

	/**
	 * @param context
	 */
	public VisualBoard(Context context, int boardSize, int playesr) {
		super(context);
		// TODO Auto-generated constructor stub
		this.boardSize = boardSize;
		setupDefault();
		this.context = context;
		this.players = playesr;
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public VisualBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setupDefault();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public VisualBoard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		setupDefault();
	}

	public void setupDefault() {
		// this.boardSize = DEFAULT_BOARD_SIZE;
		this.cellSize = boardSize / 8;
		init();
		this.postInvalidate();
	}

	public void setArray(int[][] array) {
		//
		for (int i = 0; i < 8; i++) {
			System.arraycopy(array[i], 0, this.cells[i], 0, 8);
		}
		this.postInvalidate();
	}

	/**
	 * Is it necessary?
	 * 
	 * @param array
	 */
	public void updateByArray(int[][] array) {
		setArray(array);

	}

	private void init() {
		// TODO Auto-generated method stub
		mutable = true;
		blackPaint = new Paint();
		blackPaint.setColor(Color.GRAY);

		whitePaint = new Paint();
		whitePaint.setColor(Color.WHITE);

		highlightedPaint = new Paint();
		highlightedPaint.setColor(Color.BLUE);
		n_HighlightedCells = 0;
		imageIconPaint = new Paint();
		loadIcons();
	}

	private void loadIcons() {
		rawBlackIcons[0] = BitmapFactory.decodeResource(getResources(),
				R.drawable.bk3_);
		rawBlackIcons[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.bq3_);
		rawBlackIcons[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.br3_);
		rawBlackIcons[3] = BitmapFactory.decodeResource(getResources(),
				R.drawable.bb3_);
		rawBlackIcons[4] = BitmapFactory.decodeResource(getResources(),
				R.drawable.bn3_);
		rawBlackIcons[5] = BitmapFactory.decodeResource(getResources(),
				R.drawable.bp3_);

		rawWhiteicons[0] = BitmapFactory.decodeResource(getResources(),
				R.drawable.wk3_);
		rawWhiteicons[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.wq3_);
		rawWhiteicons[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.wr3_);
		rawWhiteicons[3] = BitmapFactory.decodeResource(getResources(),
				R.drawable.wb3_);
		rawWhiteicons[4] = BitmapFactory.decodeResource(getResources(),
				R.drawable.wn3_);
		rawWhiteicons[5] = BitmapFactory.decodeResource(getResources(),
				R.drawable.wp3_);

		hightLightLegal = BitmapFactory.decodeResource(getResources(),
				R.drawable.highlight_legal_move);
		hightLightLastMove = BitmapFactory.decodeResource(getResources(),
				R.drawable.highlight_last_move);

		boardBlackWhite = BitmapFactory.decodeResource(getResources(),
				R.drawable.board1);
		boardRedWhite = BitmapFactory.decodeResource(getResources(),
				R.drawable.board2);
		boardWood = BitmapFactory.decodeResource(getResources(),
				R.drawable.board3);
		reSizeIcons();
	}

	/**
	 * resize icon, the cellSize must be change before calling this func the raw
	 * icons must be load first
	 */
	public void reSizeIcons() {
		Matrix mscaleMatrix = new Matrix();
		Matrix rectMatrix = new Matrix();
		Matrix boardMatrix = new Matrix();
		Matrix rotation = new Matrix();

		rotation.postRotate(180);
		rectMatrix.postScale((float) cellSize / hightLightLastMove.getWidth(),
				(float) cellSize / hightLightLastMove.getHeight());
		boardMatrix.postScale(
				(float) (cellSize * 8) / boardBlackWhite.getWidth(),
				(float) (cellSize * 8) / boardBlackWhite.getHeight());

		mscaleMatrix.postScale((float) cellSize / rawWhiteicons[0].getWidth(),
				(float) cellSize / rawWhiteicons[0].getHeight());

		for (int i = 0; i < 6; i++) {
			blackpieces[i] = Bitmap.createBitmap(rawBlackIcons[i], 0, 0,
					rawBlackIcons[i].getWidth(), rawBlackIcons[i].getHeight(),
					mscaleMatrix, true);

			whitepieces[i] = Bitmap.createBitmap(rawWhiteicons[i], 0, 0,
					rawWhiteicons[i].getWidth(), rawWhiteicons[i].getHeight(),
					mscaleMatrix, true);
		}

		// /rotation
		for (int i = 0; i < 6; i++) {
			blackpieces_rotation[i] = Bitmap.createBitmap(blackpieces[i], 0, 0,
					blackpieces[i].getWidth(), blackpieces[i].getHeight(),
					rotation, true);

			whitepieces_rotation[i] = Bitmap.createBitmap(whitepieces[i], 0, 0,
					whitepieces[i].getWidth(), whitepieces[i].getHeight(),
					rotation, true);
		}
		// /

		hightLightLastMove = Bitmap.createBitmap(hightLightLastMove, 0, 0,
				hightLightLastMove.getWidth(), hightLightLastMove.getHeight(),
				rectMatrix, true);
		hightLightLegal = Bitmap.createBitmap(hightLightLegal, 0, 0,
				hightLightLegal.getWidth(), hightLightLegal.getHeight(),
				rectMatrix, true);
		boardBlackWhite = Bitmap.createBitmap(boardBlackWhite, 0, 0,
				boardBlackWhite.getWidth(), boardBlackWhite.getHeight(),
				boardMatrix, true);
		boardRedWhite = Bitmap.createBitmap(boardRedWhite, 0, 0,
				boardRedWhite.getWidth(), boardRedWhite.getHeight(),
				boardMatrix, true);
		boardWood = Bitmap.createBitmap(boardWood, 0, 0, boardWood.getWidth(),
				boardWood.getHeight(), boardMatrix, true);

		/*
		 * free mem
		 */
		{
			for (int i = 0; i < 6; i++) {
				rawBlackIcons[i] = null;
				rawWhiteicons[i] = null;
			}
			mscaleMatrix = null;
			rectMatrix = null;
			rotation = null;
			boardMatrix = null;
		}

	}

	/**
	 * Draw/redraw the board, there are 2 action: background(included
	 * highlighted cells) and pieces
	 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Draw the board
		int cellLeft = 0, cellTop = 0;
		if (!flip) {
			if (Preference.getBoardType(context).equals("1")) {
				canvas.drawBitmap(boardBlackWhite, 0, 0, null);
			} else if (Preference.getBoardType(context).equals("2")) {
				canvas.drawBitmap(boardRedWhite, 0, 0, null);
			} else {
				canvas.drawBitmap(boardWood, 0, 0, null);
			}

			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					cellLeft = Math.round(col * cellSize);
					cellTop = Math.round(row * cellSize);

					if (Math.abs(bgBoardColor[row][col]) == 2) // highlighted
																// cell
						canvas.drawRect(cellLeft, cellTop, cellLeft + cellSize,
								cellTop + cellSize, highlightedPaint);
					else if (Math.abs(bgBoardColor[row][col]) == 3) // highlighted
						// cell
						canvas.drawBitmap(hightLightLegal, cellLeft, cellTop,
								null);
					else if (Math.abs(bgBoardColor[row][col]) == 4) // highlighted
						// cell
						canvas.drawBitmap(hightLightLastMove, cellLeft,
								cellTop, null);
					else if (Math.abs(bgBoardColor[row][col]) == 10) { // highlighted
						// cell
						canvas.drawBitmap(hightLightLastMove, cellLeft,
								cellTop, null);
						canvas.drawBitmap(hightLightLegal, cellLeft, cellTop,
								null);
					}
				}
			}
			// Draw the pieces on that board
			if (players == 1) {
				for (int row = 0; row < 8; row++) {
					for (int col = 0; col < 8; col++) {
						cellLeft = Math.round(col * cellSize);
						cellTop = Math.round(row * cellSize);
						int val = cells[row][col]; // / Important: val is the
													// value
						// represent the pieces
						if (val > 0) // White pieces
							canvas.drawBitmap(whitepieces[Math.abs(val) - 1],
									cellLeft, cellTop, imageIconPaint);
						else if (val < 0) // Black pieces
							canvas.drawBitmap(blackpieces[Math.abs(val) - 1],
									cellLeft, cellTop, imageIconPaint);
					}
				}
			} else if (players == 2) {
				if (curPlayer == PLAYER2) {
					for (int row = 0; row < 8; row++) {
						for (int col = 0; col < 8; col++) {
							cellLeft = Math.round(col * cellSize);
							cellTop = Math.round(row * cellSize);
							int val = cells[row][col]; // / Important: val is
														// the value
							// represent the pieces
							if (val > 0) // White pieces
								canvas.drawBitmap(
										whitepieces[Math.abs(val) - 1],
										cellLeft, cellTop, imageIconPaint);
							else if (val < 0) // Black pieces
								canvas.drawBitmap(
										blackpieces[Math.abs(val) - 1],
										cellLeft, cellTop, imageIconPaint);
						}
					}
				} else {
					for (int row = 0; row < 8; row++) {
						for (int col = 0; col < 8; col++) {
							cellLeft = Math.round(col * cellSize);
							cellTop = Math.round(row * cellSize);
							int val = cells[row][col]; // / Important: val is
														// the value
							// represent the pieces
							if (val > 0) // White pieces
								canvas.drawBitmap(
										whitepieces_rotation[Math.abs(val) - 1],
										cellLeft, cellTop, imageIconPaint);
							else if (val < 0) // Black pieces
								canvas.drawBitmap(
										blackpieces_rotation[Math.abs(val) - 1],
										cellLeft, cellTop, imageIconPaint);
						}
					}
				}
			}

		} else {
			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {

					cellLeft = Math.round(col * cellSize);
					cellTop = Math.round(row * cellSize);
					if (Math.abs(bgBoardColor[7 - row][7 - col]) == 2) // highlighted
																		// cell
						canvas.drawRect(cellLeft, cellTop, cellLeft + cellSize,
								cellTop + cellSize, highlightedPaint);
					else if (bgBoardColor[7 - row][7 - col] == -1) // black cell
						canvas.drawRect(cellLeft, cellTop, cellLeft + cellSize,
								cellTop + cellSize, blackPaint);
					else if (bgBoardColor[7 - row][7 - col] == 1) // white cell
						canvas.drawRect(cellLeft, cellTop, cellLeft + cellSize,
								cellTop + cellSize, whitePaint);
				}
			}
			// Draw the pieces on that board
			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					cellLeft = Math.round(col * cellSize);
					cellTop = Math.round(row * cellSize);
					int val = cells[7 - row][7 - col]; // / Important: val is
														// the value
					// represent the pieces
					if (val > 0) // White pieces
						canvas.drawBitmap(whitepieces[Math.abs(val) - 1],
								cellLeft, cellTop, imageIconPaint);
					else if (val < 0) // Black pieces
						canvas.drawBitmap(blackpieces[Math.abs(val) - 1],
								cellLeft, cellTop, imageIconPaint);
				}
			}
		}
		return;
	}

	public void setMutable(boolean b) {
		this.mutable = b;
	}

	public boolean isMutable() {
		return this.mutable;
	}

	/*
	 * @author Ducv set board size here
	 */
	public void reSize(int newBoardSize) {
		this.boardSize = newBoardSize;
		this.cellSize = this.boardSize / 8;
		reSizeIcons();
		this.postInvalidate();
	}

	public void clearHighlightedCells() {
		// TODO Auto-generated method stub
		n_HighlightedCells = 0;
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				if (bgBoardColor[r][c] == -2) // -2 means: black cell color
					// highlighted
					bgBoardColor[r][c] = -1;
				if (bgBoardColor[r][c] == 2) // 2 means: white cell color
					// highlighted
					bgBoardColor[r][c] = 1;
			}
		}
		this.invalidate();
	}

	public void markHighlighted(int r, int c) {
		// TODO Auto-generated method stub
		n_HighlightedCells++;
		if (bgBoardColor[r][c] == -1)
			bgBoardColor[r][c] = -2; // -2 means: black cell color highlighted
		if (bgBoardColor[r][c] == 1)
			bgBoardColor[r][c] = 2; // 2 means: white cell color highlighted
		this.postInvalidate();
	}

	public void markCanMove(int r, int c) {
		if (bgBoardColor[r][c] == -1)
			bgBoardColor[r][c] = -3; // -3 means: black cell color highlighted
		if (bgBoardColor[r][c] == 1)
			bgBoardColor[r][c] = 3; // 3 means: white cell color highlighted

		if (bgBoardColor[r][c] == -4)
			bgBoardColor[r][c] = -10; // both can move and history
		if (bgBoardColor[r][c] == 4)
			bgBoardColor[r][c] = 10; // both can move and history

		this.postInvalidate();
	}

	public void clearCanMoveHightLight() {
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				if (bgBoardColor[r][c] == -3) // -3 means: black cell color
					// highlighted
					bgBoardColor[r][c] = -1;
				if (bgBoardColor[r][c] == 3) // 3 means: white cell color
					// highlighted
					bgBoardColor[r][c] = 1;

				if (bgBoardColor[r][c] == -10) // -3 means: black cell color
					// highlighted
					bgBoardColor[r][c] = -4;
				if (bgBoardColor[r][c] == 10) // 3 means: white cell color
					// highlighted
					bgBoardColor[r][c] = 4;
			}
		}
		this.invalidate();
	}

	public void markHistory(int r, int c) {
		if (bgBoardColor[r][c] == -1)
			bgBoardColor[r][c] = -4; // -4 means: black cell color highlighted
		if (bgBoardColor[r][c] == 1)
			bgBoardColor[r][c] = 4; // 4 means: white cell color highlighted
		this.postInvalidate();
	}

	public void clearHistory() {
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				if (bgBoardColor[r][c] == -4) // -4 means: black cell color
					// highlighted
					bgBoardColor[r][c] = -1;
				if (bgBoardColor[r][c] == 4) // 4 means: white cell color
					// highlighted
					bgBoardColor[r][c] = 1;
			}
		}
		this.invalidate();
	}

	public int getHighlightedCells() {
		return this.n_HighlightedCells;
	}

	public int getCellSize() {
		// TODO Auto-generated method stub
		return this.cellSize;
	}

	public float getBoardSize() {
		// TODO Auto-generated method stub
		return this.boardSize;
	}

	public void saveState(Bundle outState) {
		// Save the states of all the cells which are in use.
		outState.putInt("n_HighlightedCells", n_HighlightedCells);
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				String key = "cell" + x + "," + y;
				outState.putInt(key, cells[x][y]);
			}
		}
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				String key = "bgBoardColor" + x + "," + y;
				outState.putByte(key, bgBoardColor[x][y]);
			}
		}
	}

	public void restoreState(Bundle savedState) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				String key = "cell" + x + "," + y;
				cells[x][y] = savedState.getInt(key, cells[x][y]);
			}
		}
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				String key = "bgBoardColor" + x + "," + y;
				bgBoardColor[x][y] = savedState
						.getByte(key, bgBoardColor[x][y]);
			}
		}
		n_HighlightedCells = savedState.getInt("n_HighlightedCells",
				n_HighlightedCells);
		this.postInvalidate();
	}

	public void toggleFlip() {
		flip = !flip;
		this.postInvalidate();
	}

	public boolean isFlip() {
		return this.flip;
	}

	public void setCurPlayer(int curPlayer) {
		this.curPlayer = curPlayer;
		this.postInvalidate();
	}

	public void free() {
		for (int i = 0; i < 6; i++) {
			if (blackpieces[i] != null)
				blackpieces[i] = null;
			if (whitepieces[i] != null)
				whitepieces[i] = null;
			if (blackpieces_rotation[i] != null)
				blackpieces_rotation[i] = null;
			if (whitepieces_rotation[i] != null)
				whitepieces_rotation[i] = null;
		}
		if (highlightedPaint != null)
			highlightedPaint = null;
		if (hightLightLastMove != null)
			hightLightLastMove = null;
		if (hightLightLegal != null)
			hightLightLegal = null;
	}

}
