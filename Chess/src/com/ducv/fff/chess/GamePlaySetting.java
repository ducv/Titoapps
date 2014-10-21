package com.ducv.fff.chess;

import com.ducv.fff.preference.Preference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class GamePlaySetting extends Activity implements OnClickListener {
	private Button play, back;
	private EditText player1, player2;
	private int players;
	private ImageButton diffiButtons[];
	private ImageButton handicap[];
	private ImageButton gameTimers[];
	private ImageButton moveTimer[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newgamesettings);
		play = (Button) findViewById(R.id.id_play);
		back = (Button) findViewById(R.id.id_back);
		play.setOnClickListener(this);
		back.setOnClickListener(this);
		player1 = (EditText) findViewById(R.id.id_edit_player1);
		player2 = (EditText) findViewById(R.id.id_edit_player2);
		players = getIntent().getIntExtra("PLAYERS", 1);
		if (players == 1) {
			player1.setText("Android");
			player1.setEnabled(false);
		}
		/*
		 * difficult
		 */
		diffiButtons = new ImageButton[10];
		for (int i = 0; i < 10; i++) {
			diffiButtons[i] = (ImageButton) findViewById(R.id.Difficulty1 + i);
		}
		for (int i = 0; i < 10; i++) {
			diffiButtons[i].setAlpha(150);
		}
		if (players == 1) {
			diffiButtons[Preference.getDifficult(this) - 1].setAlpha(255);
			for (int i = 0; i < 10; i++) {
				final int index = i;
				diffiButtons[i].setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						diffiButtons[index].setAlpha(255);
						Preference
								.setDifficult(GamePlaySetting.this, index + 1);
						for (int j = 0; j < 10; j++) {
							if (index != j)
								diffiButtons[j].setAlpha(150);
						}
						return true;
					}
				});
			}
		}

		// ===========
		/*
		 * handicap
		 */
		handicap = new ImageButton[5];
		for (int i = 0; i < 5; i++) {
			handicap[i] = (ImageButton) findViewById(R.id.Handicap0 + i);
		}
		for (int i = 0; i < 5; i++) {
			handicap[i].setAlpha(150);
		}
		handicap[Preference.getHandicrap(this)].setAlpha(255);
		for (int i = 0; i < 5; i++) {
			final int index = i;
			handicap[i].setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					handicap[index].setAlpha(255);
					Preference.setHandicrap(GamePlaySetting.this, index);
					for (int j = 0; j < 5; j++) {
						if (index != j)
							handicap[j].setAlpha(150);
					}
					return true;
				}
			});
		}
		// ================
		/*
		 * game timers
		 */
		gameTimers = new ImageButton[5];
		for (int i = 0; i < 5; i++) {
			gameTimers[i] = (ImageButton) findViewById(R.id.GameTimer1 + i);
		}
		moveTimer = new ImageButton[5];
		for (int i = 0; i < 5; i++) {
			moveTimer[i] = (ImageButton) findViewById(R.id.MoveTimer1 + i);
		}
		for (int i = 0; i < 5; i++) {
			gameTimers[i].setAlpha(150);
		}
		switch (Preference.getTotalTime(this)) {
		case 0:
			gameTimers[0].setAlpha(255);
			for (int j = 0; j < 5; j++) {
				moveTimer[j].setAlpha(150);
			}
			break;
		case 300000:
			gameTimers[1].setAlpha(255);
			break;
		case 600000:
			gameTimers[2].setAlpha(255);
			break;
		case 1800000:
			gameTimers[3].setAlpha(255);
			break;
		case 3600000:
			gameTimers[4].setAlpha(255);
			break;

		default:
			break;
		}

		for (int i = 0; i < 5; i++) {
			final int index = i;
			gameTimers[i].setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					gameTimers[index].setAlpha(255);
					switch (index) {
					case 0:
						Preference.setTotalTime(GamePlaySetting.this, 0);
						for (int j = 0; j < 5; j++) {
							moveTimer[j].setAlpha(150);
						}
						break;
					case 1:
						if (Preference.getTotalTime(GamePlaySetting.this) == 0) {
							Preference.setMoveTime(GamePlaySetting.this, 5000);
							moveTimer[1].setAlpha(255);
						}
						Preference.setTotalTime(GamePlaySetting.this, 300000);
						break;
					case 2:
						if (Preference.getTotalTime(GamePlaySetting.this) == 0) {
							Preference.setMoveTime(GamePlaySetting.this, 5000);
							moveTimer[1].setAlpha(255);
						}
						Preference.setTotalTime(GamePlaySetting.this, 600000);
						break;
					case 3:
						if (Preference.getTotalTime(GamePlaySetting.this) == 0) {
							Preference.setMoveTime(GamePlaySetting.this, 5000);
							moveTimer[1].setAlpha(255);
						}
						Preference.setTotalTime(GamePlaySetting.this, 1800000);
						break;
					case 4:
						if (Preference.getTotalTime(GamePlaySetting.this) == 0) {
							Preference.setMoveTime(GamePlaySetting.this, 5000);
							moveTimer[1].setAlpha(255);
						}
						Preference.setTotalTime(GamePlaySetting.this, 3600000);
						break;

					default:
						break;
					}

					for (int j = 0; j < 5; j++) {
						if (index != j)
							gameTimers[j].setAlpha(150);
					}
					return true;
				}
			});
		}
		// ================
		/*
		 * move timers
		 */

		for (int i = 0; i < 5; i++) {
			moveTimer[i].setAlpha(150);
		}
		if (Preference.getTotalTime(this) > 0) {
			switch (Preference.getMoveTime(this)) {
			case 0:
				moveTimer[0].setAlpha(255);
				break;
			case 5000:
				moveTimer[1].setAlpha(255);
				break;
			case 10000:
				moveTimer[2].setAlpha(255);
				break;
			case 30000:
				moveTimer[3].setAlpha(255);
				break;
			case 60000:
				moveTimer[4].setAlpha(255);
				break;

			default:
				break;
			}
		}

		for (int i = 0; i < 5; i++) {
			final int index = i;
			moveTimer[i].setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if (Preference.getTotalTime(GamePlaySetting.this) > 0) {
						moveTimer[index].setAlpha(255);

						switch (index) {
						case 0:
							Preference.setMoveTime(GamePlaySetting.this, 0);
							break;
						case 1:
							Preference.setMoveTime(GamePlaySetting.this, 5000);
							break;
						case 2:
							Preference.setMoveTime(GamePlaySetting.this, 10000);
							break;
						case 3:
							Preference.setMoveTime(GamePlaySetting.this, 30000);
							break;
						case 4:
							Preference.setMoveTime(GamePlaySetting.this, 60000);
							break;

						default:
							break;
						}
					}
					for (int j = 0; j < 5; j++) {
						if (index != j)
							moveTimer[j].setAlpha(150);
					}
					return true;
				}
			});
		}
		// ================
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.id_play:
			Intent play = new Intent(GamePlaySetting.this, PlayActivity.class);
			play.putExtra("new_game", true);
			play.putExtra("PLAYERS", players);
			if (players == 1) {
				play.putExtra("PLAYER1", player1.getText().toString() + "("
						+ Preference.getDifficult(this) + ")");
			} else {
				play.putExtra("PLAYER1", player1.getText().toString());
			}

			play.putExtra("PLAYER2", player2.getText().toString());
			startActivity(play);
			finish();
			break;
		case R.id.id_back:
			finish();
			break;
		default:
			break;
		}
	}
}
