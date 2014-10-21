/**
 * 
 */
package com.ducv.fff.chess.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ducv.fff.chess.R;

public class PlayerImageButton extends ImageView {
	public static Bitmap android_on;
	public static Bitmap android_off;
	public static Bitmap human_on;
	public static Bitmap human_off;
	private boolean isHuman;
	private boolean isOn;
	private boolean mutable;
	public static final String TAG = "com.fpt.chess.ui.PlayerIconButton";

	/**
	 * @param context
	 */
	public PlayerImageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public PlayerImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PlayerImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public void togleHuman() {
		// TODO Auto-generated method stub
		isHuman = !isHuman;
		update();
	}

	public void togleOn() {
		// TODO Auto-generated method stub
		isOn = !isOn;
		update();
	}

	private void update() {
		// TODO Auto-generated method stub
		if (isHuman) {
			if (isOn) {
				this.setImageBitmap(human_on);
			} else {
				this.setImageBitmap(human_off);
			}
		} else {
			if (isOn) {
				this.setImageBitmap(android_on);
			} else {
				this.setImageBitmap(android_off);
			}
		}
	}

	private void init() {
		// TODO Auto-generated method stub
		android_on = BitmapFactory.decodeResource(getResources(),
				R.drawable.android_on);
		android_off = BitmapFactory.decodeResource(getResources(),
				R.drawable.android_off);
		human_on = BitmapFactory.decodeResource(getResources(),
				R.drawable.human_on);
		human_off = BitmapFactory.decodeResource(getResources(),
				R.drawable.human_off);
		isHuman = true;
		isOn = false;
		mutable = true;
		update();
	}

	public boolean isOn() {
		// TODO Auto-generated method stub
		return this.isOn;
	}

	public boolean isHuman() {
		// TODO Auto-generated method stub
		return this.isHuman;
	}

	public void setMutable(boolean b) {
		// TODO Auto-generated method stub
		this.mutable = b;
	}

	public boolean isMutable() {
		// TODO Auto-generated method stub
		return this.mutable;
	}

	public void toHuman() {
		// TODO Auto-generated method stub
		isHuman = true;
		update();
	}

	public void toAndroid() {
		// TODO Auto-generated method stub
		isHuman = false;
		update();
	}

	public void toOn() {
		this.isOn = true;
		this.update();
	}

	public void toOff() {
		this.isOn = false;
		this.update();
	}
}
