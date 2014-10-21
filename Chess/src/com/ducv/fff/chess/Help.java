package com.ducv.fff.chess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Help extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		findViewById(R.id.Help_ExitButton).setOnClickListener(this);
		findViewById(R.id.Help_RulesButton).setOnClickListener(this);
	}
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Help_RulesButton:
			Intent help = new Intent(Help.this, Rules.class);
			startActivity(help);
			break;
		case R.id.Help_ExitButton:
			finish();
			break;

		default:
			break;
		}
		
	}

}
