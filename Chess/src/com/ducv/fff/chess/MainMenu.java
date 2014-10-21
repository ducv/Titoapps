package com.ducv.fff.chess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.ducv.fff.chess.ui.Audio;
import com.ducv.fff.chess.utils.database.ChessDBTransact;
import com.ducv.fff.preference.Preference;

public class MainMenu extends Activity implements OnClickListener {

	public static final int SAVE_AND_QUITE = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.newgame);
		init();
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	private void init() {
		findViewById(R.id.id_single_button).setOnClickListener(this);
		findViewById(R.id.id_two_players_button).setOnClickListener(this);
		findViewById(R.id.id_load_game).setOnClickListener(this);
		findViewById(R.id.id_setting).setOnClickListener(this);
		findViewById(R.id.id_help).setOnClickListener(this);
		preInit();

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.id_single_button:
			Intent singglePlayer = new Intent(MainMenu.this,
					GamePlaySetting.class);
			singglePlayer.putExtra("PLAYERS", 1);
			startActivity(singglePlayer);
			break;
		case R.id.id_two_players_button:
			Intent twoPlayers = new Intent(MainMenu.this, GamePlaySetting.class);
			twoPlayers.putExtra("PLAYERS", 2);
			startActivity(twoPlayers);
			break;
		case R.id.id_load_game:
			Intent iIntent = new Intent();
			iIntent.setClass(this, DatabaseView.class);
			startActivity(iIntent);
			break;
		case R.id.id_setting:
			Intent optionIntent = new Intent(MainMenu.this,
					ChessPreferences.class);
			startActivity(optionIntent);
			break;
		case R.id.id_help:
			Intent helpIntent = new Intent(MainMenu.this, Help.class);
			startActivity(helpIntent);
			break;

		default:
			break;
		}

	}

	private void preInit() {
		if (!Preference.getFirstUse(this)) {
			Preference.setBoardType(this, "1");
			Preference.setScreenOn(this, false);
			Preference.setShowCpuThinhing(this, true);
			Preference.setShowCpuThinhing(this, true);
			Preference.setShowLegalAndLastMove(this, true);
			Preference.setShowTime(this, true);
			Preference.setSound(this, true);
			Preference.setStatusBar(this, false);
			Preference.setFirstUse(this, true);
		}
		getFirst();
	}

	public void getFirst() {
		ChessDBTransact dbTransact = new ChessDBTransact(this/*
															 * ,
															 * mDatabaseFileName
															 * , version
															 */);
		int currentGameId = dbTransact.getMaxGameId();
		Log.w("asd", "" + currentGameId);
		if (currentGameId == 0) {
			// ???
			dbTransact
					.insertGameToDatabase(
							"play vs Android",
							"Leiden NED",
							"1994.10.??",
							1,
							"Android(8)",
							"The King",
							0,
							0,
							0,
							"1-0",
							"0",
							34,
							"",
							"1.e4 e5 2.Nf3 f5 3.Nxe5 Qf6 4.Nc4 fxe4 5.Nc3 Qf7 6.Ne3 c6 7.Nxe4 d5 8.Ng5 Qf6 9.Nf3 d4 10.Nc4 b5 11.Qe2+ Kd8 12.Nce5 Bc5 13.Qe4 Ne7 14.Bxb5 Re8 15.b4 Bf5 16.Qh4 Ng6 17.Qxf6+ gxf6 18.O-O Nxe5 19.Nh4 Bxc2 20.Bxc6 Nbxc6 21.bxc5 Bd3 22.Re1 Nb4 23.Bb2 Nc2 24.Rec1 Nxa1 25.Bxa1 Nc6 26.Nf3 Re4 27.Ne1 Ba6 28.a3 Kc7 29.Nc2 Rd8 30.Ne1 Rde8  31.Nf3 Ne5 32.Nxd4 Nd3 33.Rf1 Re1 34.Nf3");
			dbTransact
					.insertGameToDatabase(
							"play vs Android",
							"Leiden NED",
							"1994.10.??",
							0,
							"Android(10)",
							"Bionic",
							0,
							0,
							0,
							"1-0",
							"0",
							45,
							"",
							"1.e4 d5 2.exd5 Nf6 3.Bb5+ Nbd7 4.d4 Nxd5 5.c4 N5f6 6.Nf3 c6 7.Ba4 Nb6 8.Bb3 Bg4 9.Nc3 Bxf3 10.Qxf3 Qxd4 11.Be3 Qd3 12.Rd1 Qg6 13.O-O e5 14.Qh3 Bb4 15.Na4 Nxa4 16.Bxa4 O-O 17.a3 Be7 18.f4 exf4 19.Rxf4 a6 20.Rd2 Qb1+ 21.Rf1 Qa2 22.Bd4 Qxc4 23.Bb3 Qb5 24.Qg3 Nh5 25.Qd3 Qxd3 26.Rxd3 Rad8 27.Rdd1 Rxd4 28.Rxd4 Bc5 29.Rfd1 Rd8 30.Rf1 Bxd4+ 31.Kh1 Rf8 32.Bd1 Nf6 33.b4 Bb2 34.a4 Nd5 35.b5 axb5 36.axb5 Ne3 37.Re1 Nxd1 38.Rxd1 cxb5 39.g4 Re8 40.Rd7 Re1+ 41.Kg2 f6 42.h4 b6 43.Rd6 Bc3 44.h5 Ba5 45.Kf2 Re8");
			dbTransact
					.insertGameToDatabase(
							"together",
							"Cairo EGY",
							"2009.07.12",
							5,
							"Kareim Wageih",
							"Mohamed Ezat",
							2253,
							2459,
							0,
							"1-0",
							"0",
							40,
							"",
							"1. d4 Nf6 2. Nf3 c5 3. d5 b5 4. Bg5 Ne4 5. Bf4 Bb7 6. Qd3 f5 7. Nbd2 c4 8. Qd4 Na6 9. Nxe4 fxe4 10. Qxe4 e6 11. c3 Nc7 12. Bg5 Qb8 13. Qh4 Nxd5 14. e4 Ne7 15. Rd1 Ng6 16. Qh5 Bxe4 17. Nh4 Bc5 18. f3 O-O 19. Rxd7 Qe5 20. Rd2 Bxf3");
			dbTransact.commit();
		}
	}

}