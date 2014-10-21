/**
 * 
 */
package com.ducv.fff.chess.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ducv.fff.chess.VirtualBoard;

public class NotationView extends LogView {

	/**
	 * @param context
	 */
	public NotationView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public NotationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public NotationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onDraw(Canvas canvas) {
		// TODO redraw
		int nodes = historyTree.getNumberOfNodes();
		while (nodes > 0) {
			Node n = historyTree.getRoot();
			int i = 1;
			while (n != null) {
				String s = n.getNotation();
				if (i % 2 == 1) { // White moves:

				}
			}
		}
	}

	public void update() {
		int nodes = historyTree.getNumberOfNodes();
		this.setLayoutParams(new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		while (nodes > 0) {
			Node n = historyTree.getRoot();
			int index = 1;
			TextView textView = getNodeView(n.getNotation(), index,
					index % 2 == 1);
			textView.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					// scVirtualBoard.inputFen(n.getFEN());
				}
			});
			addView(textView);
			nodes--;
			n = n.getFirstChildNode();
			index++;
			if (n == null)
				break;
		}
	}

	public TextView getNodeView(String notation, int index, boolean isWhite) {
		TextView textView = new TextView(this.getContext());
		if (isWhite)
			textView.setText("" + (index + 1) / 2 + ". " + notation);
		else
			textView.setText(" " + notation);
		return textView;
	}

	private HistoryTree historyTree;
	private VirtualBoard scVirtualBoard;
}
