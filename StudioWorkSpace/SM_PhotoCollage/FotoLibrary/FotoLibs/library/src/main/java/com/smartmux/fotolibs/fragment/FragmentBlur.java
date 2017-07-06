package com.smartmux.fotolibs.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.smartmux.fotolibs.R;
import com.smartmux.fotolibs.adapter.HorizontalListAdapter;
import com.smartmux.fotolibs.modelclass.BitmapHolder;
import com.smartmux.fotolibs.modelclass.BitmapManager;
import com.smartmux.fotolibs.utils.BlurBuilder;
import com.smartmux.fotolibs.utils.FotoLibsConstant;
import com.smartmux.fotolibs.widget.HorizontalListView;

public class FragmentBlur extends Fragment implements OnClickListener {

	HorizontalListView blur_listview;
	HorizontalListAdapter mListAdapter;
	String strtext = "";
	Animation downDrawerOpen, downDrawerClose = null;
	ImageView a_imageview_done, a_imageview_close;
	TextView a_textview_title, a_textview_done, a_imageview_logo;
	RelativeLayout a_footer = null;
	SeekBar blurSeekBar = null;
	ImageView blurImage = null;
	Bitmap mainBitmap, blurredBitmap = null;
	BitmapHolder mBitmapHolder = null;

	
	public static FragmentBlur newInstance() {
		FragmentBlur fragment = new FragmentBlur();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_bottom, container,
				false);

		downDrawerOpen = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_up);
		downDrawerClose = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_down);

		mBitmapHolder = new BitmapHolder();

		 mainBitmap = mBitmapHolder.getBm();

		initActivityItem();

		blurImage = (ImageView) rootView.findViewById(R.id.fixed_imageView);
		blurImage.setImageBitmap(mainBitmap);

		blurSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar_blur);
		blurSeekBar.setVisibility(View.VISIBLE);
		blurSeekBar.setMax(15); // max=20

		blurSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				if (progress < 2) {
					blurImage.setImageBitmap(mainBitmap);
				} else {
					blurredBitmap = null;
					blurredBitmap = BlurBuilder.blur(getActivity(), mainBitmap,
							(float) progress);

					blurImage.setImageBitmap(blurredBitmap);
				}

			}
		});

		blur_listview = (HorizontalListView) rootView
				.findViewById(R.id.bottom_listview);

		blur_listview.setVisibility(View.GONE);

		return rootView;
	}

	private void initActivityItem() {

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

		a_textview_title.setText(R.string.blur);

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
			
//			 mBitmapManager.addBitmapToMemoryCache(cacheKey,blurredBitmap);
			mBitmapHolder.setBm(blurredBitmap);
			setHeaderContent();
			mainBitmap = null;
			blurredBitmap = null;
			if (blurredBitmap != null) {
				mainBitmap.recycle();
				mainBitmap = null;
				blurredBitmap.recycle();
				blurredBitmap = null;
			}
		} else if (id == R.id.imageview_close) {
			setHeaderContent();
			mainBitmap = null;
			blurredBitmap = null;
			if (blurredBitmap != null) {
				mainBitmap.recycle();
				mainBitmap = null;
				blurredBitmap.recycle();
				blurredBitmap = null;
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

}
