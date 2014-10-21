/**
 * 
 */
package com.ducv.fff.chess.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.ducv.fff.chess.VirtualBoard;

public class TreeView extends LogView {
	public TreeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public TreeView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public TreeView(Context context) {
		super(context, null, 0);
		// TODO Auto-generated constructor stub
	}

	private void init() {

	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Node n = historyTree.getRoot();
		while (n != null) {
			// TODO: Really draw the HistoryTree in

		}
	}

	private VirtualBoard scVirtualBoard;
	private HistoryTree historyTree;
	/**
	 * 
	 */

}
