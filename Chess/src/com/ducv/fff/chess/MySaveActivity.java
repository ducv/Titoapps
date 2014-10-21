package com.ducv.fff.chess;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MySaveActivity extends Activity {

	protected ListView listView;
	protected Button okButton;
	protected Button cancelButton;
	protected EditText filenameEditText;
	protected SimpleAdapter mAdapter;
	protected BaseAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		int width = disp.getWidth();
		int height = disp.getHeight();

		LinearLayout mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		LinearLayout topFrameLayout = new LinearLayout(this);
		topFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(width,
				height - 64));

		LinearLayout bottomToolbarLayout = new LinearLayout(this);
		bottomToolbarLayout.setOrientation(LinearLayout.HORIZONTAL);
		bottomToolbarLayout.setLayoutParams(new LinearLayout.LayoutParams(
				width, 40));

		listView = new ListView(this);
		topFrameLayout.addView(listView, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		okButton = new Button(this);
		cancelButton = new Button(this);
		filenameEditText = new EditText(this);

		okButton.setText("OK");
		cancelButton.setText("Cancel");
		filenameEditText.setHint("File's name here");

		LinearLayout.LayoutParams mParmas = new LinearLayout.LayoutParams(
				width / 3 - 10, 4);
		bottomToolbarLayout.addView(filenameEditText, mParmas);
		bottomToolbarLayout.addView(okButton, mParmas);
		bottomToolbarLayout.addView(cancelButton, mParmas);
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		adapter.notifyDataSetChanged();
	}

	public class MyAdapter extends BaseAdapter {

		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
