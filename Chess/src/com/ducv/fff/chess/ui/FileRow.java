package com.ducv.fff.chess.ui;

import java.io.File;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
public class FileRow extends LinearLayout {

	private String filename = null;

	public FileRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public FileRow(Context context) {
		super(context, null);
		// TODO Auto-generated constructor stub
		init();
	}

	public FileRow(Context context, String filename) {
		super(context, null);
		// TODO Auto-generated constructor stub
		this.filename = filename;
		init();
	}

	protected void init() {
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setLayoutParams(new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		if (filename != null) {
			File file = new File(filename);
			View icon = getIcon(filename);
			View details = getDetails(filename);
			this.addView(icon);
			this.addView(details);
		}
	}

	View getIcon(String filename) {
		View v = new View(this.getContext());
		// TODO: Add process code for icon

		return v;
	}

	View getDetails(String filename) {
		View v = new View(this.getContext());
		// TODO: Add process code for detail

		return v;
	}
}