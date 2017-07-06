package com.roundflat.musclecard;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView.OnStickyHeaderChangedListener;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView.OnStickyHeaderOffsetChangedListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.roundflat.musclecard.TutorialActivity.ClickHandlerReceiver;
import com.roundflat.musclecard.adapter.TitleAdapter;
import com.roundflat.musclecard.database.DatabaseHandler;
import com.roundflat.musclecard.model.TutorialModel;
import com.roundflat.musclecard.util.Constant;
import com.roundflat.musclecard.util.DataWrapper;
import com.roundflat.musclecard.util.PreferenceUtils;

public class RightMenuActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnStickyHeaderChangedListener,
		OnStickyHeaderOffsetChangedListener {

	StickyListHeadersListView listviewTitle;
	private TitleAdapter titleAdapter;
	private DatabaseHandler db;
	private boolean isRandom, IsFav;
	private List<TutorialModel> tutorialList = new ArrayList<TutorialModel>();
	String rootTitle;
	ClickHandlerReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tutorial_menu);

		db = new DatabaseHandler(this);

		IsFav = PreferenceUtils.getBoolPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.PREF_FAV, false);
		isRandom = PreferenceUtils.getBoolPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.PREF_RANDOM, false);

		if (getIntent().hasExtra(Constant.root_title)) {
			rootTitle = getIntent().getExtras().getString(Constant.root_title);
			// String queryForList = "SELECT DISTINCT * FROM "
			// + Constant.TABLE_TUTORIAL + " WHERE " + Constant.root_title
			// + " ='" + rootTitle + "' AND title != 'NIL'";
			//
			//
			//
			// if (rootTitle.equals("All")) {
			//
			// queryForList = "SELECT DISTINCT * FROM " +
			// Constant.TABLE_TUTORIAL
			// + " WHERE title != 'NIL'";
			//
			//
			// }else if (IsFav && !rootTitle.equals("All")) {
			//
			//
			// queryForList = "SELECT DISTINCT * FROM " +
			// Constant.TABLE_TUTORIAL
			// + " WHERE " + Constant.root_title + " ='" + rootTitle
			// + "' AND title != 'NIL'";
			//
			// }
			//
			// tutorialList = db.getAllTutorials(queryForList);

			tutorialList = new DataWrapper().getList();

			titleAdapter = new TitleAdapter(this, tutorialList, IsFav,
					rootTitle);

			listviewTitle = (StickyListHeadersListView) this
					.findViewById(R.id.listView_tutorial_title);
			listviewTitle.setOnStickyHeaderChangedListener(this);
			listviewTitle.setOnStickyHeaderOffsetChangedListener(this);
			listviewTitle.setEmptyView(findViewById(R.id.empty));
			listviewTitle.setDrawingListUnderStickyHeader(true);
			listviewTitle.setAreHeadersSticky(true);
			listviewTitle.setAdapter(titleAdapter);

			listviewTitle.setOnItemClickListener(this);
		}
		this.findViewById(R.id.textview_back).setOnClickListener(this);
		this.findViewById(R.id.textview_reset).setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {

		Log.d("pos", "" + position);

		Intent intent = new Intent("ListItemClick");

		if (IsFav) {
			boolean isExistTutorial = db.isFavoriteTutorialExists(tutorialList
					.get(position).getId());

			if (!isExistTutorial) {

				int itemPosition = position;
				for (int i = 0; i < itemPosition + 1; i++) {

					if (db.isFavoriteTutorialExists(tutorialList.get(i).getId())) {

						itemPosition = itemPosition - 1;
					}
				}
				intent.putExtra(Constant.position, itemPosition);

			} else {

				return;
			}
		} else {

			intent.putExtra(Constant.position, position);
		}

		sendBroadcast(intent);
		finish();
		overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		int id = v.getId();
		if (id == R.id.textview_reset) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					RightMenuActivity.this);

			builder.setMessage(R.string.reset_list);

			builder.setCancelable(false);
			// Add the buttons
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});

			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, int id) {

							for (int i = 0; i < tutorialList.size(); i++) {
								Intent intent = new Intent("ButtonClick");
								if (db.isFavoriteTutorialExists(tutorialList
										.get(i).getId())) {

									db.updateFavoriteTutorial(
											tutorialList.get(i).getId(), "0");

									intent.putExtra(Constant.position, i);
									intent.putExtra("item", true);
									sendBroadcast(intent);
								}

							}

							titleAdapter.notifyDataSetChanged();

							dialog.dismiss();

							// Intent intent = new Intent(
							// "reset");
							// intent.putExtra("item_id", id);
							// sendBroadcast(intent);
							//
							// Log.d("sendBroadcast", "sendBroadcast");

						}
					});

			// Create the AlertDialog
			builder.show();

		} else if (id == R.id.textview_back) {

			finish();
			overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}

		db.close();

	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
	}

	@Override
	public void onStickyHeaderChanged(StickyListHeadersListView l, View header,
			int itemPosition, long headerId) {
		header.setAlpha(1);

	}

	@Override
	public void onStickyHeaderOffsetChanged(StickyListHeadersListView l,
			View header, int offset) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			header.setAlpha(1 - (offset / (float) header.getMeasuredHeight()));
		}
	}

}
