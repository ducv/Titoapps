package com.ducv.fff.chess;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;


/**
 * Activity with <b><< Go previous</b> at the top of list views and <b> Go next
 * >> </b>at the bottom of the list views This is a efficient way to dynamic
 * load content of list views Every screen should have from 15 to 40 views
 * exclude 2 views Go next and Go previous
 */
public abstract class DynamicListActivity extends ListActivity {

	protected static int MAX_ITEMS = 30;
	protected ArrayList<Object> items;
	protected Object goBack;
	protected Object goForward;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	public abstract void load();

	public abstract void goBack();

	public abstract void goForward();
}
