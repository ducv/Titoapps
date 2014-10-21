package com.ducv.fff.chess.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ducv.fff.chess.R;

public class NavigateButton extends ImageView {
	public static Bitmap back;
	public static Bitmap forward;
	public static Bitmap menu;
	public static Bitmap pause;
	public static Bitmap resume;
	public static Bitmap logview;
	private boolean mutable;
	private boolean isOn;

	/**
	 * @param context
	 */
	public NavigateButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public NavigateButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public NavigateButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		back = BitmapFactory.decodeResource(getResources(), R.drawable.back);
		forward = BitmapFactory.decodeResource(getResources(),
				R.drawable.forward);
		pause = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
		resume = BitmapFactory
				.decodeResource(getResources(), R.drawable.resume);
		menu = BitmapFactory.decodeResource(getResources(), R.drawable.menu);
		logview = BitmapFactory.decodeResource(getResources(),
				R.drawable.log_view);
	}

	public void togleOn() {
		// TODO Auto-generated method stub
		isOn = !isOn;
		this.setClickable(isOn);
	}

	public void update() {
		// TODO Auto-generated method stub
	}

	public static NavigateButton createMenuButton(Context context) {
		// TODO Auto-generated method stub
		NavigateButton menuButton = new NavigateButton(context);
		int minWidth = 160;
		int minHeight = 80;
		menuButton.setMinimumWidth(minWidth);
		menuButton.setMinimumHeight(minHeight);
		menuButton.setImageBitmap(menu);
		menuButton.setClickable(true);
		return menuButton;
	}

}
