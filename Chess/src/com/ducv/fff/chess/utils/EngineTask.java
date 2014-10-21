package com.ducv.fff.chess.utils;

import java.io.IOException;

import mediocre.board.Board;
import mediocre.board.Move;
import mediocre.engine.Engine;
import mediocre.engine.Engine.LineEval;
import android.os.AsyncTask;
import android.util.Log;

import com.ducv.fff.chess.PlayActivity;
import com.ducv.fff.chess.VirtualBoard;
import com.ducv.fff.chess.ui.VisualBoard;

public class EngineTask extends AsyncTask<VirtualBoard, VisualBoard, Integer> {
	private PlayActivity playActivity;
	private VisualBoard visualboard;
	private VirtualBoard virtualboard;
	private int move = 0;

	public EngineTask(PlayActivity play, VisualBoard vb) {
		super();
		this.playActivity = play;
		this.visualboard = vb;
	}

	@Override
	protected Integer doInBackground(VirtualBoard... arg0) {
		virtualboard = arg0[0];
		Board board = arg0[0].clone();
		Engine engine = new Engine();
		int depth = playActivity.getAIDepth();
		boolean uci = false;
		int timeLeft = playActivity.getAITimeLeft();
		int increment = playActivity.getAIIcrementTime();
		int movetime = playActivity.getAIMoveTime();
		boolean leftOpening = false;
		try {
			LineEval line = engine.search(board, depth, uci, timeLeft,
					increment, movetime, leftOpening);
			if (line.line[0] != 0) {
				move = line.line[0];
				return line.line[0];
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	protected void onPostExecute(Integer result) {
		String notation = Move.notation(move, virtualboard);
		String inputNotation = Move.inputNotation(move);
		try {
			virtualboard.makeMove(move);
			playActivity.decide();
			String fen = virtualboard.getFen();
			
			Node node = new Node(move, notation, inputNotation, fen);
			playActivity.addNodeToTree(node);
			visualboard.updateByArray(virtualboard.get8x8Array());
			///history
			visualboard.clearHistory();
			visualboard.markHistory(8 - (inputNotation.charAt(1) - '0'),
					inputNotation.charAt(0) - 'a');
			visualboard.markHistory(8 - (inputNotation.charAt(3) - '0'),
					inputNotation.charAt(2) - 'a');
			////
			playActivity.turnOnPauseButton();
			// playActivity.decide();
		} catch (IndexOutOfBoundsException e) {
			//
		} catch (Exception e) {
			//
		}

	}

}
