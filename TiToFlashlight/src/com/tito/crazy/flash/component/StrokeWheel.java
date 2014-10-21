package com.tito.crazy.flash.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.tito.crazy.flash.R;
import com.tito.crazy.flash.listener.StrokeIndexListener;
import com.tito.crazy.flash.ultils.SoundUltils;

public class StrokeWheel extends View implements OnTouchListener {
	private final int ITEMS = 14;
	private Resources resources;
	private float screenWidth;
	private float strokeHeight, strokeWidth;
	private float originalX;
	private Bitmap strokeItemBm;
	private Bitmap[] items = new Bitmap[ITEMS];
	private float delta = 0;
	private float adjustX = 0;
	private float oldX = 0;
	private float maxX;
	private int index = 0;
	private StrokeIndexListener strokeIndexListener;
	private SoundUltils soundUltils;
//	private int[] mBgColors = { 0xffa1a1a1, 0xffa1a1a1, 0xffffffff, 0xffa1a1a1, 0xffa1a1a1 };
//	private float[] mBgPositions = { 0, 0.2f, 0.5f, 0.8f, 1f };

	public StrokeWheel(Context context) {
		super(context);
		init(context);
	}

	public StrokeWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public StrokeWheel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	@SuppressLint("ClickableViewAccessibility")
	private void init(Context context) {
		soundUltils = SoundUltils.getInstance(context);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		screenWidth = display.getWidth();
		resources = getResources();
		strokeHeight = resources.getDimension(R.dimen.stroke_height);
		strokeItemBm = BitmapFactory.decodeResource(resources, R.drawable.stroke_item);
		scaleBitmap();
		originalX = screenWidth / 2 - (ITEMS - 3 + 0.5f) * strokeWidth;
		maxX = screenWidth / 2 - 2.5f * strokeWidth;
		this.setOnTouchListener(this);
//		setBackgroundDrawable(new LinearGradientDrawable(Orientation.LEFT_RIGHT, mBgColors, mBgPositions));
		setBackgroundResource(R.drawable.radio_tunner);
	}

	public void setIndexListener(StrokeIndexListener strokeIndexListener) {
		this.strokeIndexListener = strokeIndexListener;
		strokeIndexListener.onIndex(index);
	}

	private void scaleBitmap() {
		Matrix scaleMatrix = new Matrix();
		float scale = strokeHeight / strokeItemBm.getHeight();
		scaleMatrix.postScale(scale, scale);
		strokeItemBm = Bitmap.createBitmap(strokeItemBm, 0, 0, strokeItemBm.getWidth(), strokeItemBm.getHeight(), scaleMatrix, true);
		strokeWidth = strokeItemBm.getWidth();
		for (int i = 0; i < ITEMS; i++) {
			items[i] = strokeItemBm;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (int i = 0; i < ITEMS; i++) {
			canvas.drawBitmap(items[i], originalX + strokeWidth * i + adjustX, 0, null);
		}
		if (delta > 0) {
			if (originalX + adjustX < maxX) {
				adjustX += delta;
			} else {
				adjustX = maxX - originalX;
			}
		} else {
			if (adjustX > 0) {
				adjustX += delta;
			} else {
				adjustX = 0;
			}
		}
		delta = 0;
		super.onDraw(canvas);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			oldX = event.getX();
			delta = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			delta = event.getX() - oldX;
			oldX = event.getX();
			move();
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			autoMove();
			break;
		default:
			break;
		}
		return true;
	}

	private void move() {
		if (Math.abs(adjustX - index * strokeWidth) <= 0.5f * strokeWidth) {
			return;
		} else {
			if (adjustX - index * strokeWidth < 0) {
				index--;
			} else {
				index++;
			}
			soundUltils.playSound(true);
			if (index >= 0 && index <= 9) {
				strokeIndexListener.onIndex(index);
			}
			return;
		}
	}

	private void autoMove() {
		adjustX = index * strokeWidth;
		invalidate();
	}

}
