package com.ducv.fff.chess.ui;

import android.os.AsyncTask;

import com.ducv.fff.chess.VirtualBoard;

public class BoardPreload extends
		AsyncTask<VirtualBoard, BoardScroller, Integer> {

	public BoardPreload() {

	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(BoardScroller... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	@Override
	protected Integer doInBackground(VirtualBoard... params) {
		// TODO Auto-generated method stub
		return null;
	}

}
