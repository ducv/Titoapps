package com.tito.crazy.flash.component;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewCustom extends TextView {

	public TextViewCustom(Context context) {
		super(context);
		Typeface typeFace = Typeface.createFromAsset(context.getAssets(),
				"font/Droid.ttf");
		this.setTypeface(typeFace);
	}

	public TextViewCustom(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		Typeface typeFace = Typeface.createFromAsset(context.getAssets(),
				"font/Droid.ttf");
		this.setTypeface(typeFace);
	}

	public TextViewCustom(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Typeface typeFace = Typeface.createFromAsset(context.getAssets(),
				"font/Droid.ttf");
		this.setTypeface(typeFace);
	}

}
