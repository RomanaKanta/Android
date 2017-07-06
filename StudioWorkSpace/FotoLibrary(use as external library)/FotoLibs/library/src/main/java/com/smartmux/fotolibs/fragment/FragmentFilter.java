package com.smartmux.fotolibs.fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.Ragnarok.BitmapFilter;

import com.ortiz.touch.TouchImageView;
import com.smartmux.fotolibs.MainEditorActivity;
import com.smartmux.fotolibs.R;
import com.smartmux.fotolibs.MainEditorActivity.LoadImage;
import com.smartmux.fotolibs.adapter.RecycleView_FilterAdapter;
import com.smartmux.fotolibs.adapter.RecycleView_FilterAdapter.OnItemClickListener;
import com.smartmux.fotolibs.modelclass.BitmapHolder;
import com.smartmux.fotolibs.modelclass.ListData;
import com.smartmux.fotolibs.utils.AddItems;
import com.smartmux.fotolibs.utils.FotoLibsConstant;

public class FragmentFilter extends Fragment implements OnClickListener {

	private ArrayList<ListData> mFilterListData = new ArrayList<ListData>();

	RecycleView_FilterAdapter filterAdapter;
	private static RecyclerView filterRecyclerView;

	String strtext = "";
	Animation downDrawerOpen, downDrawerClose = null;
	ImageView a_imageview_done, a_imageview_close;
	TextView a_textview_title, a_textview_done, a_imageview_logo;
	RelativeLayout a_footer = null;
	Bitmap originBitmap, changeBitmap = null;
	private TouchImageView main_imageview = null;
	boolean edit = false;
	BitmapHolder mBitmapHolder = null;

	public static FragmentFilter newInstance() {
		FragmentFilter fragment = new FragmentFilter();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_footer, container,
				false);

		downDrawerOpen = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_up);
		downDrawerClose = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_down);

		mBitmapHolder = new BitmapHolder();

		initActivityItem();
		initFragItem(rootView);

		originBitmap = mBitmapHolder.getBm();
		changeBitmap = originBitmap;

		return rootView;
	}

	private void initFragItem(View rootView) {

		mFilterListData = AddItems.getFilterItems(getActivity());

		filterRecyclerView = (RecyclerView) rootView
				.findViewById(R.id.fragment_horizontal_listview);
		filterRecyclerView.startAnimation(downDrawerOpen);
		filterRecyclerView.setHasFixedSize(true);
		filterRecyclerView.setLayoutManager(new LinearLayoutManager(
				getActivity(), LinearLayoutManager.HORIZONTAL, false));

		filterAdapter = new RecycleView_FilterAdapter(getActivity(),
				mFilterListData);
		filterRecyclerView.setAdapter(filterAdapter);// set adapter on
														// recyclerview
		filterAdapter.notifyDataSetChanged();

		filterAdapter.SetOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {

				if (originBitmap != null) {
					edit = true;
					filterAdapter.setSelected(position);
					
					new ApplyFilter(position).execute();
					
//					if (position == 0) {
//						changeBitmap = originBitmap;
//					} else {
//						applyStyle(position);
//					}
//					
//					main_imageview.setImageBitmap(changeBitmap);
				}
				
			}
		});

	}

	private void initActivityItem() {

		main_imageview = (TouchImageView) getActivity().findViewById(
				R.id.editor_imageView);

		a_imageview_done = (ImageView) getActivity().findViewById(
				R.id.imageview_done);
		a_imageview_close = (ImageView) getActivity().findViewById(
				R.id.imageview_close);
		a_footer = (RelativeLayout) getActivity().findViewById(
				R.id.horizontalLayout);

		a_imageview_logo = (TextView) getActivity().findViewById(
				R.id.imageview_logo);
		a_textview_title = (TextView) getActivity().findViewById(
				R.id.textview_header);
		a_textview_done = (TextView) getActivity().findViewById(
				R.id.textview_done);

		a_textview_title.setText(R.string.filter);
		a_imageview_logo.setVisibility(View.GONE);
		a_textview_done.setVisibility(View.GONE);
		a_imageview_close.setVisibility(View.VISIBLE);
		a_imageview_done.setVisibility(View.VISIBLE);

		a_imageview_done.setOnClickListener(this);
		a_imageview_close.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.imageview_done) {
			mBitmapHolder.setBm(changeBitmap);
			setHeaderContent();
			originBitmap = null;
			changeBitmap = null;
			if (changeBitmap != null) {
				originBitmap.recycle();
				originBitmap = null;
				changeBitmap.recycle();
				changeBitmap = null;
			}
		} else if (id == R.id.imageview_close) {
			originBitmap = null;
			changeBitmap = null;
			setHeaderContent();
			if (changeBitmap != null) {
				originBitmap.recycle();
				originBitmap = null;
				changeBitmap.recycle();
				changeBitmap = null;
			}
		} else {
		}
	}

	private void setHeaderContent() {

		a_footer.setVisibility(View.VISIBLE);
		a_footer.startAnimation(downDrawerOpen);

		Bundle bundle = new Bundle();
		bundle.putString("message", "From Fragment");

		FragmentTransparent frag = new FragmentTransparent();
		frag.setArguments(bundle);
		replaceFrag(frag, FotoLibsConstant.MAIN);
	}

	private void replaceFrag(Fragment frag, String tag) {

		FragmentTransaction fragTransaction = getFragmentManager()
				.beginTransaction();
		fragTransaction.replace(R.id.fragment_main, frag, tag);
		fragTransaction.addToBackStack(tag);
		fragTransaction.commit();

	}

	private void applyStyle(int styleNo) {
		switch (styleNo) {

		case 0:
			
			changeBitmap = originBitmap;
			break;
			
		case BitmapFilter.LIGHT_STYLE:
			int width = originBitmap.getWidth();
			int height = originBitmap.getHeight();
			changeBitmap = BitmapFilter.changeStyle(originBitmap,
					BitmapFilter.LIGHT_STYLE, width / 3, height / 2, width / 2);
			break;

		case BitmapFilter.NEON_STYLE:
			changeBitmap = BitmapFilter.changeStyle(originBitmap,
					BitmapFilter.NEON_STYLE, 200, 100, 50);
			break;

		default:
			changeBitmap = BitmapFilter.changeStyle(originBitmap, styleNo);
			break;
		}
	}

	public class ApplyFilter extends AsyncTask<Void, Void, Void> {

		private ProgressDialog progress = null;
		int position;

		public ApplyFilter(int pos) {
			
			position = pos;
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			progress = ProgressDialog.show(getActivity(), null,
					"Filtering");

		}

		@Override
		protected Void doInBackground(Void... params) {
			applyStyle(position);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progress.dismiss();

			main_imageview.setImageBitmap(changeBitmap);

		}
	}
	
}
