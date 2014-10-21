/**
 * 
 */
package com.ducv.fff.chess.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.ducv.fff.chess.R;

public abstract class LogView extends ScrollView {

	/**
	 * @param context
	 */
	public LogView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public LogView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public LogView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		whitePieces[KING] = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.wk);
		whitePieces[QUEEN] = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.wq);
		whitePieces[ROOK] = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.wr);
		whitePieces[BISHOP] = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.wb);
		whitePieces[KNIGHT] = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.wn);

		blackPieces[KING] = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.bk);
		blackPieces[QUEEN] = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.bq);
		blackPieces[ROOK] = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.br);
		blackPieces[BISHOP] = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.bb);
		blackPieces[KNIGHT] = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.bn);
		this.invalidate();
	}

	public void reSizeIcon(int newSize) {
		iconSize = newSize;
		Matrix m = new Matrix();
		// TODO: resize all the icons here
	}

	protected OnClickListener onClickListener;
	protected static final Bitmap[] whitePieces = new Bitmap[5];
	protected static final Bitmap[] blackPieces = new Bitmap[5];
	public static final int KING = 0;
	public static final int QUEEN = 1;
	public static final int ROOK = 2;
	public static final int BISHOP = 3;
	public static final int KNIGHT = 4;
	private int iconSize = 32;
	private int textSize = 32;

}
