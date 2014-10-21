/**
 * 
 */
package com.ducv.fff.chess;

import android.content.Intent;
import android.os.Bundle;

public class TrainingActivity extends PlayActivity {

	private Intent tIntent = null;
	private int[] array = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		tIntent = this.getIntent();
		array = tIntent.getIntArrayExtra("movesarray");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void newGame() {
		super.newGame();
		if (array != null) {
			try {
				virtualboard.makeBatchMoves(array, array.length);
				visualboard.updateByArray(virtualboard.get8x8Array());
			} catch (IndexOutOfBoundsException i) {

			} catch (Exception e) {

			}
			array = null;
		}
	}

	/**
	 * 
	 */
}
