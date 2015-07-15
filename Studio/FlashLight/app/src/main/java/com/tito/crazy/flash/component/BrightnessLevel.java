package com.tito.crazy.flash.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tito.crazy.flash.R;

@SuppressLint("NewApi")
public class BrightnessLevel extends LinearLayout {

	private View view1, view2, view3;

	public BrightnessLevel(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.brightness_level, this, true);
		init();
	}

	public BrightnessLevel(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.brightness_level, this, true);
		init();
	}

	public BrightnessLevel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.brightness_level, this, true);
		init();
	}

	private void init() {
		view1 = findViewById(R.id.myViewBrightness1);
		view2 = findViewById(R.id.myViewBrightness2);
		view3 = findViewById(R.id.myViewBrightness3);
	}

	public void setLevel(int level) {
		switch (level) {
		case 0:
			view1.setBackgroundResource(R.drawable.back_border_black);
			view2.setBackgroundResource(R.drawable.back_border_black);
			view3.setBackgroundResource(R.drawable.back_border_black);
			break;

		case 1:
			view1.setBackgroundResource(R.drawable.back_border_white);
			view2.setBackgroundResource(R.drawable.back_border_black);
			view3.setBackgroundResource(R.drawable.back_border_black);
			break;
		case 2:
			view1.setBackgroundResource(R.drawable.back_border_white);
			view2.setBackgroundResource(R.drawable.back_border_white);
			view3.setBackgroundResource(R.drawable.back_border_black);
			break;
		case 3:
			view1.setBackgroundResource(R.drawable.back_border_white);
			view2.setBackgroundResource(R.drawable.back_border_white);
			view3.setBackgroundResource(R.drawable.back_border_white);
			break;
		default:
			break;
		}
		invalidate();
	}

}
