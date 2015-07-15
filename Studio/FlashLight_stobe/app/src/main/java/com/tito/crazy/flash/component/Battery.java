package com.tito.crazy.flash.component;

import com.tito.crazy.flash.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

@SuppressLint("NewApi")
public class Battery extends LinearLayout {
	private View view1, view2, view3, view4;

	public Battery(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.battery_level, this, true);
		init();
	}

	public Battery(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.battery_level, this, true);
		init();
	}

	public Battery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.battery_level, this, true);
		init();
	}

	private void init() {
		view1 = findViewById(R.id.myViewBattery1);
		view2 = findViewById(R.id.myViewBattery2);
		view3 = findViewById(R.id.myViewBattery3);
		view4 = findViewById(R.id.myViewBattery4);
	}

	public void updateLevel(int level) {
		if (level <= 25) {
			view1.setBackgroundResource(R.drawable.back_border_red);
			view2.setBackgroundResource(R.drawable.back_border_black_rect);
			view3.setBackgroundResource(R.drawable.back_border_black_rect);
			view4.setBackgroundResource(R.drawable.back_border_black_rect);

		} else if (level <= 50) {
			view1.setBackgroundResource(R.drawable.back_border_yellow);
			view2.setBackgroundResource(R.drawable.back_border_yellow);
			view3.setBackgroundResource(R.drawable.back_border_black_rect);
			view4.setBackgroundResource(R.drawable.back_border_black_rect);
		} else if (level <= 75) {
			view1.setBackgroundResource(R.drawable.back_border_green);
			view2.setBackgroundResource(R.drawable.back_border_green);
			view3.setBackgroundResource(R.drawable.back_border_green);
			view4.setBackgroundResource(R.drawable.back_border_black_rect);
		} else {
			view1.setBackgroundResource(R.drawable.back_border_green);
			view2.setBackgroundResource(R.drawable.back_border_green);
			view3.setBackgroundResource(R.drawable.back_border_green);
			view4.setBackgroundResource(R.drawable.back_border_green);
		}
		invalidate();
	}
}
