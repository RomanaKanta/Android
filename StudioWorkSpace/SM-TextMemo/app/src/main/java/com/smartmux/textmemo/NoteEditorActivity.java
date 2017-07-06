package com.smartmux.textmemo;

import java.io.File;
import java.io.FileNotFoundException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.textmemo.util.PBPreferenceUtils;
import com.smartmux.textmemo.utils.AppExtra;
import com.smartmux.textmemo.utils.AppToast;
import com.smartmux.textmemo.utils.FileManager;
import com.smartmux.textmemo.utils.FileUtil;
import com.smartmux.textmemo.utils.GeneralUtils;
import com.smartmux.textmemo.widget.AdvancedEditText;

@SuppressLint({ "SimpleDateFormat", "NewApi" })
public class NoteEditorActivity extends AppMainActivity {

	public static int numTitle = 1;
	public static String curDate = "";
	public static String curText = "";
	private EditText mTitleText;
	private AdvancedEditText mBodyText;
	private TextView mDateText;
	private String noteMode;
	private String noteFileName = null;
	private String justName = null;
	Typeface tf = null;

	private FileManager fileManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.note_editor);

		fileManager = new FileManager();

		// AppActionBar.changeActionBarFont(getApplicationContext(),NoteEditorActivity.this);
		// AppActionBar.updateAppActionBar(getActionBar(), this, false, true);
		// getActionBar().setDisplayHomeAsUpEnabled(true);

		createCutomActionBarTitle(getString(R.string.note_create_title));

		Bundle bundle = getIntent().getExtras();

		noteFileName = bundle.getString(AppExtra.NOTE_FILENAME);

		noteMode = bundle.getString(AppExtra.NOTE_MODE);

		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (AdvancedEditText) findViewById(R.id.body);
		mDateText = (TextView) findViewById(R.id.notelist_date);

		int fontSize = PBPreferenceUtils.getIntPref(getApplicationContext(),
				"TextMemo", "font_size", 14);

		int fontColor = PBPreferenceUtils.getIntPref(getApplicationContext(),
				"TextMemo", "color_value", Color.BLACK);
		int backColor = PBPreferenceUtils.getIntPref(getApplicationContext(),
				"TextMemo", "background_color", Color.parseColor("#f7f6b6"));

		String fontStyle = PBPreferenceUtils.getStringPref(
				getApplicationContext(), "TextMemo", "font_style", "DEFAULT");

		int fontStyleID = PBPreferenceUtils.getIntPref(getApplicationContext(),
				"TextMemo", "font_style_id", 0);

		if (fontStyle.equals("DEFAULT")) {

			mBodyText.setTypeface(Typeface.DEFAULT);
			mTitleText.setTypeface(Typeface.DEFAULT);

		} else if (fontStyle.equals("MONOSPACE")) {

			mBodyText.setTypeface(Typeface.MONOSPACE);
			mTitleText.setTypeface(Typeface.MONOSPACE);

		} else if (fontStyle.equals("SANS_SERIF")) {

			mBodyText.setTypeface(Typeface.SANS_SERIF);
			mTitleText.setTypeface(Typeface.SANS_SERIF);

		} else if (fontStyle.equals("SERIF")) {

			mBodyText.setTypeface(Typeface.SERIF);
			mTitleText.setTypeface(Typeface.SERIF);

		} else if (fontStyleID > 3 && fontStyleID <= 10) {

			tf = Typeface.createFromAsset(getAssets(), "fonts/" + fontStyle
					+ ".ttf");
			mBodyText.setTypeface(tf);
			mTitleText.setTypeface(tf);
		} else if (fontStyleID > 10) {

			tf = Typeface.createFromAsset(getAssets(), "fonts/" + fontStyle
					+ ".otf");
			mBodyText.setTypeface(tf);
			mTitleText.setTypeface(tf);
		}

		mBodyText.setBackgroundColor(backColor);
		findViewById(R.id.notEditor_root).setBackgroundColor(backColor);

		mBodyText.setTextSize(fontSize);
		mBodyText.setTextColor(fontColor);

		mTitleText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				String str = mTitleText.getText().toString();
				if (str.length() > 0) {
					// getActionBar().setTitle(str);
					createCutomActionBarTitle(str);
				} else {
					// getActionBar().setTitle("Text Memo");
					createCutomActionBarTitle(getString(R.string.note_create_title));
				}
				// if (str.length() > 0) {
				// getActionBar().setTitle(str);
				// }
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		mDateText.setText(FileUtil.getJustCurrentDateTime());

		if (noteFileName != null) {
			int pos = noteFileName.lastIndexOf(".");
			justName = pos > 0 ? noteFileName.substring(0, pos) : noteFileName;

			mTitleText.setText(justName);
			mDateText.setText(fileManager.getNoteDateTime(noteFileName));
			try {
				mBodyText.setText(fileManager.getNoteContent(noteFileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			// getActionBar().setTitle(GeneralUtils.removeExtension(noteFileName));
			createCutomActionBarTitle(GeneralUtils
					.removeExtension(noteFileName));
		}

	}

	private void createCutomActionBarTitle(String text) {
		this.getActionBar().setDisplayShowCustomEnabled(true);
		this.getActionBar().setDisplayShowTitleEnabled(false);
		this.getActionBar().setDisplayShowHomeEnabled(false);

		LayoutInflater inflator = LayoutInflater.from(this);
		View v = inflator.inflate(R.layout.custom_action_bar_sec, null);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				AppExtra.AVENIRLSTD_BLACK);
		TextView title = (TextView) v.findViewById(R.id.textView_tit);
		title.setTypeface(tf);
		title.setText(text);

		ImageView back = (ImageView) v.findViewById(R.id.imageView_back);
		ImageView save = (ImageView) v.findViewById(R.id.imageView_save);
		save.setVisibility(View.VISIBLE);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				fileManager.setBackCode(getApplicationContext());
				finish();
				overridePendingTransition(R.anim.push_right_out,
						R.anim.push_right_in);
			}
		});

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (GeneralUtils.checkSdcard()) {
					
					String title = mTitleText.getText().toString();
					String body = mBodyText.getText().toString();
					if ((title.equals(null) || "".equals(title))
							&& (body.equals(null) || "".equals(body))) {
						AppToast.show(getApplicationContext(),
								getString(R.string.note_toast_1));
					} else if (title.equals(null) || "".equals(title)) {
						AppToast.show(getApplicationContext(),
								getString(R.string.note_toast_2));
					} else if (body.equals(null) || "".equals(body)) {
						AppToast.show(getApplicationContext(),
								getString(R.string.note_toast_3));
					} else {

						saveNote(noteMode.equals(AppExtra.NOTE_MODE_EDIT));

						if (noteMode.equals(AppExtra.NOTE_MODE_EDIT)) {
							if (!title.equals(justName)) {
								String fileFullPath = AppExtra.APP_ROOT_FOLDER
										+ "/" + noteFileName;
								File file = new File(fileFullPath);

								file.renameTo(new File(AppExtra.APP_ROOT_FOLDER
										+ "/", title + ".txt"));

								saveNote(noteMode
										.equals(AppExtra.NOTE_MODE_EDIT));
							}
						}

					}
				} else {
					Toast.makeText(getApplicationContext(),
							"sd card not available", Toast.LENGTH_SHORT).show();
				}

			}
		});

		// assign the view to the actionbar
		this.getActionBar().setCustomView(v);
	}

	private void saveNote(boolean isNoteModeEdit) {
		String title = mTitleText.getText().toString();

		String body = mBodyText.getText().toString();
		if (fileManager.saveNote(getApplicationContext(), title, body,
				isNoteModeEdit)) {
			fileManager.setBackCode(getApplicationContext());
			AppToast.show(getApplicationContext(),
					getString(R.string.Note_Saved_successfully));

			this.finish();
		} else {

			AppToast.show(getApplicationContext(),
					getString(R.string.file_exists));
		}
	}

	@Override
	protected void onUserLeaveHint() {
		fileManager.setHomeCode(getApplicationContext());
		super.onUserLeaveHint();
	}

	@Override
	protected void onResume() {
		super.onResume();

		int event_code = fileManager.getReturnCode(getApplicationContext());
		if (event_code == AppExtra.HOME_CODE) {
			Intent i = new Intent(NoteEditorActivity.this,
					LoginWindowActivity.class);
			startActivity(i);
		}

	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			fileManager.setBackCode(getApplicationContext());
//			finish();
//			overridePendingTransition(R.anim.push_right_out,
//					R.anim.push_right_in);
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

	@Override
	public void onBackPressed() {
		fileManager.setBackCode(getApplicationContext());
		finish();
		overridePendingTransition(R.anim.push_right_out,
				R.anim.push_right_in);
	}

	
	
}
