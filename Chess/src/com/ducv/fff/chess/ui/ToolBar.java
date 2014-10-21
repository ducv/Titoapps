
/**
 * The toolbar for place many things like: icon button, clock, navigate button, menu button
 */
package com.ducv.fff.chess.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class ToolBar extends View {

	private LinearLayout toolbarLayout;
	private TableLayout maintable;
	private TableLayout lefttable;
	private TableLayout centertable;
	private TableLayout righttable;
	private TextView black;
	private TextView white;
	private ChessClock blackClock;
	private ChessClock whiteClock;
	private PlayerImageButton blackImageButton;
	private PlayerImageButton whiteImageButton;
	private NavigateButton goBackButton;
	private NavigateButton goForwardButton;
	private NavigateButton menuButton;
	private NavigateButton pauseresumeButton;

	/**
	 * @param context
	 */
	public ToolBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ToolBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ToolBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	private void init() {
		int width = 320;
		int height = 160;
		maintable = new TableLayout(this.getContext());
		lefttable = new TableLayout(this.getContext());
		centertable = new TableLayout(this.getContext());
		righttable = new TableLayout(this.getContext());
		black = new TextView(this.getContext());
		black.setText("BLACK");
		lefttable.addView(black, width / 4, height / 4);
		lefttable.addView(blackImageButton, width / 4, height / 2);
		lefttable.addView(blackClock, width / 4, height / 4);

		white = new TextView(this.getContext());
		white.setText("WHITE");
		righttable.addView(white, width / 4, height / 4);
		righttable.addView(whiteImageButton, width / 4, height / 2);
		righttable.addView(blackClock, width / 4, height / 4);

	}
}
