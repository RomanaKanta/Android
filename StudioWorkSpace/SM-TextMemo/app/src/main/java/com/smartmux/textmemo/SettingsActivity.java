package com.smartmux.textmemo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.smartmux.textmemo.adapter.ColorListAdapter;
import com.smartmux.textmemo.adapter.ImagePagerAdapter;
import com.smartmux.textmemo.adapter.TextSizeAdapter;
import com.smartmux.textmemo.modelclass.BannerItem;
import com.smartmux.textmemo.modelclass.ColorItem;
import com.smartmux.textmemo.modelclass.TextSizeModel;
import com.smartmux.textmemo.util.PBPreferenceUtils;
import com.smartmux.textmemo.utils.AppExtra;
import com.smartmux.textmemo.utils.FileManager;
import com.smartmux.textmemo.utils.JSONParser;
import com.smartmux.textmemo.widget.HorizontalListView;
import com.smartmux.textmemo.wifitransfer.ServerControlActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class SettingsActivity extends AppMainActivity implements OnClickListener {

	private int fontSize;
	public int fontColor;
	public int backColor;
	private String fontStyle;
	private int fontStyleID;
	private boolean isLineNumberShow;

	private ToggleButton btnShowLineNumber;
	
	private View txtColor,bgColor = null;
	private TextView txtSize;
	private TextView txtStyle;

	RelativeLayout textColorLayer, bgColorLayer = null;
	HorizontalListView textColorListView, bgColorListView = null;
	ArrayList<String> textarray = new ArrayList<String>();
	ArrayList<ColorItem> array = new ArrayList<ColorItem>();
	ArrayList<TextSizeModel> txtSizeList = new ArrayList<TextSizeModel>();
	ColorListAdapter colorAdapter = null;
	boolean textColor = false;
	Animation animViewOpen, animViewClose = null;
	private FileManager fileManager;
	public static boolean isInPause = false;
	
	GradientDrawable txt_secdrawable, bg_secdrawable;
	Typeface tf ;

    RelativeLayout serverLayer;
    private AutoScrollViewPager viewPager;
    private ArrayList<BannerItem> bannerList = new ArrayList<>();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

        new SM_AsyncTaskForGetBanner().execute();

		animViewOpen = AnimationUtils.loadAnimation(this,
				R.anim.buttom_up_bounce);
		animViewClose = AnimationUtils.loadAnimation(this, R.anim.push_up_out);
		
		 tf = Typeface.createFromAsset(getAssets(),
				AppExtra.AVENIRLSTD_BLACK);

		// AppActionBar.changeActionBarFont(getApplicationContext(),
		// SettingsActivity.this);
		// AppActionBar.updateAppActionBar(getActionBar(), this, false, true);
		// getActionBar().setDisplayHomeAsUpEnabled(true);
		createCustomActionBarTitle(R.string.app_name);

		fileManager = new FileManager();

		fontSize = PBPreferenceUtils.getIntPref(getApplicationContext(),
				"TextMemo", "font_size", 14);
		
		fontStyleID = PBPreferenceUtils.getIntPref(
				getApplicationContext(), "TextMemo",
				"font_style_id", 0);

		fontStyle = PBPreferenceUtils.getStringPref(getApplicationContext(),
				"TextMemo", "font_style", "DEFAULT");

		fontColor = PBPreferenceUtils.getIntPref(getApplicationContext(),
				"TextMemo", "color_value", Color.BLACK);
		backColor = PBPreferenceUtils.getIntPref(getApplicationContext(),
				"TextMemo", "background_color", Color.parseColor("#f7f6b6"));
		isLineNumberShow = PBPreferenceUtils.getBoolPref(
				getApplicationContext(), "TextMemo", "line_show", true);

		textColorLayer = (RelativeLayout) findViewById(R.id.text_color_list_layout);
		bgColorLayer = (RelativeLayout) findViewById(R.id.bg_color_list_layout);

		btnShowLineNumber = (ToggleButton) findViewById(R.id.button_line_no);
//		btnTxtColor = (Button) findViewById(R.id.button_txt_color);
//		btnBackgroundColor = (Button) findViewById(R.id.button_background_color);
		
		txtColor= (View) findViewById(R.id.view_txt_color);
		bgColor = (View) findViewById(R.id.view_background_color);

//		btnTxtColor.setClickable(false);
//		btnBackgroundColor.setClickable(false);

		btnShowLineNumber.setChecked(isLineNumberShow);
//		btnBackgroundColor.setBackgroundColor(backColor);
//		btnTxtColor.setBackgroundColor(fontColor);
		
		txtColor.setBackgroundResource(R.drawable.circuler_background);
		bgColor.setBackgroundResource(R.drawable.circuler_background);

		 txt_secdrawable = (GradientDrawable) txtColor.getBackground();
		 bg_secdrawable = (GradientDrawable) bgColor.getBackground(); 
		 
		txt_secdrawable.setColor(fontColor);
		bg_secdrawable.setColor(backColor);
		

		findViewById(R.id.txtsize_content).setOnClickListener(this);
		findViewById(R.id.txtcolor_content).setOnClickListener(this);
		findViewById(R.id.backgroundcolor_content).setOnClickListener(this);
		findViewById(R.id.txtfont_content).setOnClickListener(this);
		findViewById(R.id.change_password).setOnClickListener(this);
		findViewById(R.id.wifi_transfer).setOnClickListener(this);
		btnShowLineNumber.setOnClickListener(this);

		txtSize = (TextView) findViewById(R.id.textView_textsize);
		txtSize.setText("" + fontSize);

		txtStyle = (TextView) findViewById(R.id.textView_textfont);
		txtStyle.setText(fontStyle);
//		txtStyle.setTypeface(Typeface.createFromAsset(getAssets(),
//				fontStyle));

		if (fontStyle.equals("DEFAULT")) {

			txtStyle.setTypeface(Typeface.DEFAULT);

		} else if (fontStyle.equals("MONOSPACE")) {

			txtStyle.setTypeface(Typeface.MONOSPACE);

		} else if (fontStyle.equals("SANS_SERIF")) {

			txtStyle.setTypeface(Typeface.SANS_SERIF);

		} else if (fontStyle.equals("SERIF")) {

			txtStyle.setTypeface(Typeface.SERIF);

		}else if (fontStyleID > 3 && fontStyleID <= 10) {

			tf = Typeface.createFromAsset(getAssets(),
					"fonts/" + fontStyle + ".ttf");
			txtStyle.setTypeface(tf);
		}else if (fontStyleID > 10) {

			tf = Typeface.createFromAsset(getAssets(),
					"fonts/" + fontStyle + ".otf");
			txtStyle.setTypeface(tf);
		}

		setList();


	}

    private void setAutoScrollBanner(ArrayList<BannerItem> bannerItemList){

        serverLayer = (RelativeLayout)findViewById(R.id.server_layer);
        serverLayer.setVisibility(View.VISIBLE);
        viewPager = (AutoScrollViewPager)findViewById(R.id.view_pager);

        viewPager.setAdapter(new ImagePagerAdapter(SettingsActivity.this, bannerItemList).setInfiniteLoop(false));
//        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());


        viewPager.setInterval(4000);
        viewPager.startAutoScroll();
//        viewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % bannerList.size());
    }

//    public class MyOnPageChangeListener implements OnPageChangeListener {
//
//        @Override
//        public void onPageSelected(int position) {
//            Toast.makeText(SettingsActivity.this,""+ position,Toast.LENGTH_SHORT).show();
//
//        }
//
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
//
//        @Override
//        public void onPageScrollStateChanged(int arg0) {}
//    }


    private void createCustomActionBarTitle(int text) {
		this.getActionBar().setDisplayShowCustomEnabled(true);
		this.getActionBar().setDisplayShowTitleEnabled(false);
		this.getActionBar().setDisplayShowHomeEnabled(false);

		LayoutInflater inflator = LayoutInflater.from(this);
		View v = inflator.inflate(R.layout.custom_action_bar_sec, null);

		
		TextView title = (TextView) v.findViewById(R.id.textView_tit);
		title.setTypeface(tf);
		title.setText(text);

		ImageView save = (ImageView) v.findViewById(R.id.imageView_save);
		save.setVisibility(View.GONE);

		ImageView back = (ImageView) v.findViewById(R.id.imageView_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_down_in,
						R.anim.push_down_out);
			}
		});

		// assign the view to the actionbar
		this.getActionBar().setCustomView(v);
	}

	void setList() {
		String[] text = getResources().getStringArray(R.array.color_array);

		textarray.addAll(Arrays.asList(text));
		Collections.shuffle(textarray);
		for (int i = 0; i < textarray.size(); i = i + 2) {
			array.add(new ColorItem(textarray.get(i), textarray.get(i + 1)));
		}

		textColorListView = (HorizontalListView) findViewById(R.id.text_color_list);
		bgColorListView = (HorizontalListView) findViewById(R.id.bg_color_list);

	}

	// call from adapter
	public void setTextColor(int color) {
//		btnTxtColor.setBackgroundColor(color);
		
		txt_secdrawable.setColor(color);
		
		PBPreferenceUtils.saveIntPref(getApplicationContext(), "TextMemo",
				"color_value", color);

		fontColor = PBPreferenceUtils.getIntPref(getApplicationContext(),
				"TextMemo", "color_value", Color.BLACK);

		// textColorLayer.startAnimation(animListClose);
		textColorLayer.setVisibility(View.GONE);
	}

	// call from adapter
	public void setBackgroundColor(int color) {
//		btnBackgroundColor.setBackgroundColor(color);
		
		bg_secdrawable.setColor(color);
		
		PBPreferenceUtils.saveIntPref(getApplicationContext(), "TextMemo",
				"background_color", color);

		backColor = PBPreferenceUtils.getIntPref(getApplicationContext(),
				"TextMemo", "background_color", Color.parseColor("#f7f6b6"));

		// bgColorLayer.startAnimation(animListClose);
		bgColorLayer.setVisibility(View.GONE);

	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.push_down_in,
				R.anim.push_down_out);
	}

	@Override
	protected void onUserLeaveHint() {
		fileManager.setHomeCode(getApplicationContext());
		super.onUserLeaveHint();
	}

	@Override
	protected void onResume() {
		super.onResume();

        if(viewPager!=null) {
            viewPager.startAutoScroll();
        }

		if (!isInPause) {
			int event_code = fileManager.getReturnCode(getApplicationContext());
			if (event_code == AppExtra.HOME_CODE) {
				Intent i = new Intent(SettingsActivity.this,
						LoginWindowActivity.class);
				startActivity(i);
			}
		}
		if (isInPause) {

			isInPause = false;
		}

	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

		case R.id.txtsize_content:

			LoadDataForTextSize();

			CustomizeDialog dialog1 = new CustomizeDialog(
					SettingsActivity.this, R.string.text_size_title,false);
			dialog1.show();
			break;

		case R.id.txtfont_content:

			LoadDataForTextStyle();

			CustomizeDialog dialog2 = new CustomizeDialog(
					SettingsActivity.this, R.string.text_style_title,true);
			dialog2.show();
			break;

		case R.id.txtcolor_content:

			if (textColorLayer.getVisibility() == View.GONE) {
				textColor = true;
				colorAdapter = new ColorListAdapter(this, array, textColor);

				textColorListView.setAdapter(colorAdapter);

				textColorLayer.startAnimation(animViewOpen);
				textColorLayer.setVisibility(View.VISIBLE);
				bgColorLayer.setVisibility(View.GONE);
			} else {
				// textColorLayer.startAnimation(animListClose);
				textColorLayer.setVisibility(View.GONE);
			}

			break;
		case R.id.backgroundcolor_content:

			if (bgColorLayer.getVisibility() == View.GONE) {

				textColor = false;
				colorAdapter = new ColorListAdapter(this, array, textColor);

				bgColorListView.setAdapter(colorAdapter);
				bgColorLayer.startAnimation(animViewOpen);
				bgColorLayer.setVisibility(View.VISIBLE);
				textColorLayer.setVisibility(View.GONE);
			} else {
				// bgColorLayer.startAnimation(animListClose);
				bgColorLayer.setVisibility(View.GONE);
			}

			break;
		case R.id.button_line_no:

			if (btnShowLineNumber.isChecked()) {

				PBPreferenceUtils.saveBoolPref(getApplicationContext(),
						"TextMemo", "line_show", true);
			} else {

				PBPreferenceUtils.saveBoolPref(getApplicationContext(),
						"TextMemo", "line_show", false);
			}

			break;

		case R.id.change_password:
			isInPause = true;
			Intent intent = new Intent(SettingsActivity.this,
					ChangePasswordAvtivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;

		case R.id.wifi_transfer:
			isInPause = true;
			Intent wifiintent = new Intent(SettingsActivity.this,
					ServerControlActivity.class);
			startActivity(wifiintent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
			
			default:
				break;
		}

	}

	public class CustomizeDialog extends Dialog implements OnClickListener {

		Button okButton;
		TextView mTitle = null;
		ListView listview;
		View v = null;
		int id;

		public CustomizeDialog(Context context, int id, boolean font) {
			super(context);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.dialog_list);
			v = getWindow().getDecorView();
			v.setBackgroundResource(android.R.color.transparent);
			mTitle = (TextView) findViewById(R.id.dig_title);
			listview = (ListView) findViewById(R.id.listView_textsize);
			okButton = (Button) findViewById(R.id.OkButton);
			okButton.setTypeface(tf);
			okButton.setOnClickListener(this);

			this.id = id;
			mTitle.setText(getString(id));

			listview.setAdapter(new TextSizeAdapter(SettingsActivity.this,
					txtSizeList,font));

		}

		@Override
		public void onClick(View v) {
			/** When OK Button is clicked, dismiss the dialog */
			if (v == okButton) {

				if (id == R.string.text_style_title) {

					Typeface tf = null;
					
					for (int j = 0; j < txtSizeList.size(); j++) {
						if (txtSizeList.get(j).ismValue()) {

							PBPreferenceUtils.saveStringPref(
									getApplicationContext(), "TextMemo",
									"font_style", txtSizeList.get(j)
											.getmTitle());
							
							PBPreferenceUtils.saveIntPref(
									getApplicationContext(), "TextMemo",
									"font_style_id", txtSizeList.get(j)
											.getmID());

							txtStyle.setText(txtSizeList.get(j).getmTitle());

							fontStyle = PBPreferenceUtils.getStringPref(
									getApplicationContext(), "TextMemo",
									"font_style", "DEFAULT");
							
							fontStyleID = PBPreferenceUtils.getIntPref(
									getApplicationContext(), "TextMemo",
									"font_style_id", 0);

							if (fontStyle.equals("DEFAULT")) {

								txtStyle.setTypeface(Typeface.DEFAULT);

							} else if (fontStyle.equals("MONOSPACE")) {

								txtStyle.setTypeface(Typeface.MONOSPACE);

							} else if (fontStyle.equals("SANS_SERIF")) {

								txtStyle.setTypeface(Typeface.SANS_SERIF);

							} else if (fontStyle.equals("SERIF")) {

								txtStyle.setTypeface(Typeface.SERIF);

							}else if (fontStyleID > 3 && fontStyleID <= 10) {

								tf = Typeface.createFromAsset(getAssets(),
										"fonts/" + fontStyle + ".ttf");
								txtStyle.setTypeface(tf);
							}else if (fontStyleID > 10) {

								tf = Typeface.createFromAsset(getAssets(),
										"fonts/" + fontStyle + ".otf");
								txtStyle.setTypeface(tf);
							}
						}

					}

				} else if (id == R.string.text_size_title) {

					for (int j = 0; j < txtSizeList.size(); j++) {
						if (txtSizeList.get(j).ismValue()) {

							PBPreferenceUtils.saveIntPref(
									getApplicationContext(), "TextMemo",
									"font_size", Integer.parseInt(txtSizeList
											.get(j).getmTitle()));

							txtSize.setText(txtSizeList.get(j).getmTitle());

							fontSize = PBPreferenceUtils.getIntPref(
									getApplicationContext(), "TextMemo",
									"font_size", 15);

						}
					}

				}

				dismiss();

			}
		}

	}

	private void LoadDataForTextSize() {
		if (txtSizeList.size() > 0) {

			txtSizeList.clear();
		}
		
		for (int i = 10; i < 52; i=i+2) {
			if (fontSize == i) {
				txtSizeList.add(new TextSizeModel(i,String.valueOf(i), true));
			}else{
			txtSizeList.add(new TextSizeModel(i,String.valueOf(i), false));
			}
		}

	}

	private void LoadDataForTextStyle() {
		if (txtSizeList.size() > 0) {

			txtSizeList.clear();
		}
		
		ArrayList<String> array = new ArrayList<String>();
		String[] text = getResources().getStringArray(R.array.text_font);
		array.addAll(Arrays.asList(text));
		
		for (int i = 0; i < array.size(); i++) {
			if (fontStyle.equals(array.get(i))) {
				txtSizeList.add(new TextSizeModel(i,array.get(i), true));
			}else{
				txtSizeList.add(new TextSizeModel(i,array.get(i), false));
			}
		}

	}

    @Override
    protected void onPause() {
        super.onPause();
        // stop auto scroll when onPause
        if(viewPager!=null) {
            viewPager.stopAutoScroll();
        }
    }


        class SM_AsyncTaskForGetBanner extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Log.e("onPreExecute", "call");

        }

        @Override
        protected JSONObject doInBackground(String... params) {
//            Log.e("doInBackground", "call");

            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("client_identifier",getApplicationContext().getPackageName());
            postDataParams.put("platform","android");

            // Creating new JSON Parser
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getApiResult(AppExtra.BANNER_LIST_URL,postDataParams);


            return json;
        }

        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            if(result != null){

                try {

                    if (result.has("status")) {

                        if (result.getString("status").equals("OK")) {
                            Log.e("onPostExecute", "call");
                            setDataFromJson(result);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
    }


    private void setDataFromJson(JSONObject json) {

        if(bannerList!=null && bannerList.size()>0){
            bannerList.clear();
        }

        try {
            // Getting JSON Array
            JSONArray bannerArray = json.getJSONArray("message");
//            Log.e("Length", ""+ bannerArray.length() );
            for (int i = 0; i < bannerArray.length(); i++) {
                JSONObject c = bannerArray.getJSONObject(i);
//                Log.e("JSONObject", ""+ c );

                JSONObject obj  = c.getJSONObject("AppBanner");
//
                    Log.e("obj", ""+ obj );

                    String title = obj.getString("title");

                    String thumbUrl = obj.getString("thumb_url");

                    String actionUrl = obj.getString("action_url");

                    BannerItem bannerItem = new BannerItem(title,thumbUrl,actionUrl,false);

                    bannerList.add(bannerItem);

            }

//            Log.e("setDataFromJson", ""+ bannerList.size() );
            setAutoScrollBanner(bannerList);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
