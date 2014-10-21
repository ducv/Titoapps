package com.ducv.fff.chess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class FileSaveActivity extends Activity {

	public static final int SAVE_FILE_ERROR = 10;
	private Intent mIntent = null;
	private String content = "";
	public static String TAG = "com.pluto.FileSaveActivity";
	protected ListView listView;
	protected Button okButton;
	protected EditText filenameEditText;
	protected SimpleAdapter mAdapter;
	protected SimpleAdapter notes;
	protected String currentDir;
	private boolean showFileDetails = true;
	private boolean onlyShowPGNFiles = false;
	private ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mIntent = this.getIntent();
		content = mIntent.getStringExtra("content");
		Log.i(TAG, content);

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
				height - 112));

		LinearLayout bottomToolbarLayout = new LinearLayout(this);
		bottomToolbarLayout.setOrientation(LinearLayout.HORIZONTAL);
		bottomToolbarLayout.setLayoutParams(new LinearLayout.LayoutParams(
				width, 80));
		bottomToolbarLayout.setGravity(Gravity.CENTER_HORIZONTAL);

		listView = new ListView(this);
		topFrameLayout.addView(listView);

		okButton = new Button(this);
		// cancelButton = new Button(this);
		filenameEditText = new EditText(this);

		okButton.setText("OK");
		// cancelButton.setText("Cancel");
		filenameEditText.setHint("Type file name here");
		filenameEditText.setLines(1);

		LinearLayout.LayoutParams editTextParmas = new LinearLayout.LayoutParams(
				width * 3 / 4, 64);
		LinearLayout.LayoutParams buttonParmas = new LinearLayout.LayoutParams(
				width / 4, 64);
		bottomToolbarLayout.addView(filenameEditText, editTextParmas);
		bottomToolbarLayout.addView(okButton, buttonParmas);
		// bottomToolbarLayout.addView(cancelButton,buttonParmas);
		bottomToolbarLayout.setBackgroundColor(Color.GRAY);
		// adapter = new MyAdapter();

		notes = new SimpleAdapter(this, list, R.layout.file_item, new String[] {
				"line1", "line2" }, new int[] { R.id.file_name,
				R.id.file_attributes });
		listView.setAdapter(notes);

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String filename = list.get(arg2).get("line1");
				File file = new File(filename);
				if (file.isFile()) {
					filenameEditText.setText(file.getName());
				} else if (file.isDirectory()) {
					list.clear();
					notes.notifyDataSetChanged();
					listView.setAdapter(notes);
					currentDir = file.getAbsolutePath();
					for (File f : file.listFiles()) {
						addItem(f);
					}
					listView.setAdapter(notes);
				}
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		okButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String filename = filenameEditText.getText().toString();
				if (filename == null)
					return;
				if (filename.length() > 0) {
					try {
						if (!filename.toLowerCase().endsWith(".pgn"))
							filename += ".pgn";
						save(filename, content);
						finish();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.gc();
						showDialog(SAVE_FILE_ERROR);
					}
				}
			}
		});
		mainLayout.addView(topFrameLayout);
		mainLayout.addView(bottomToolbarLayout);
		this.setContentView(mainLayout);
		currentDir = "/";
		for (File file : new File("/").listFiles()) {
			if (onlyShowPGNFiles) {
				if (file.getName().toLowerCase().endsWith(".pgn"))
					addItem(file);
				else if (file.isDirectory())
					addItem(file);
			} else
				addItem(file);
		}
		listView.setAdapter(notes);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SAVE_FILE_ERROR:
			return new AlertDialog.Builder(this).setIcon(R.drawable.stop_alert)
					.setTitle("Save file error!").setMessage(
							"Can access this file!").setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// finish();
								}
							}).create();
		}
		return null/* super.onCreateDialog(id) */;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		return super.onMenuItemSelected(featureId, item);
	}

	private void addItem(File file) {
		HashMap<String, String> item = new HashMap<String, String>();
		Date date = new Date(file.lastModified());
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy.MM.dd hh:mm aaa");
		String dateString = formatter.format(date);
		String filesize = "";
		long file_size = file.length();
		if (file_size < 1024) {
			filesize = file_size + " bytes";
		} else if (file_size > 1024 && file_size < 1024 * 1024) {
			filesize = file_size / 1024 + " KBs";
		} else
			filesize = file_size / (1024 * 1024) + " MBs";

		item.put("line1", file.getAbsolutePath());
		if (showFileDetails) {
			item.put("line2", "Last modified: " + dateString + " File size: "
					+ filesize);
		} else {
			item.put("line2", "");
		}

		list.add(item);
		// Log.i(TAG, "New");
		// Log.i(TAG, "size: " + list.size());
		/*
		 * SimpleAdapter notes = new SimpleAdapter(this, list,
		 * R.layout.file_item, new String[] { "line1", "line2" }, new int[] {
		 * R.id.file_name, R.id.file_attributes });
		 */
		notes.notifyDataSetChanged();
	}

	public void save(String filename, String content) throws IOException {
		FileWriter fw = new FileWriter(new File(currentDir, filename));
		BufferedWriter bf = new BufferedWriter(fw);
		bf.write(content);
		try {
			bf.flush();
			fw.flush();
			bf.close();
			fw.close();
		} catch (Exception e) {
			// Log.e(TAG,"save: Exception when save file: " + filename);
		}
		return;
	}

	private void getPrefs() {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		showFileDetails = prefs.getBoolean(
				"show_details_in_file_browser_chooser", true);
		onlyShowPGNFiles = prefs.getBoolean("only_show_pgn_files_chooser",
				false);
	}

}
