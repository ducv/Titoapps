package com.ducv.fff.chess;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Rules extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rules);
		findViewById(R.id.Help_ExitButton).setOnClickListener(this);
	}
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.Help_ExitButton){
			finish();
		}
	}

}
