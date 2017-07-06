package com.smartmux.foto.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.smartmux.foto.R;
import com.smartmux.foto.adapter.HorizontalListAdapter;
import com.smartmux.foto.modelclass.BitmapHolder;
import com.smartmux.foto.modelclass.ListData;
import com.smartmux.foto.utils.AddItems;
import com.smartmux.foto.utils.FotoLibsConstant;
import com.smartmux.foto.widget.HorizontalListView;

import java.util.ArrayList;
public class FragmentRotate extends Fragment implements OnClickListener {

	private ArrayList<ListData> mRotateListData = new ArrayList<ListData>();
	HorizontalListView rotate_listview;
	HorizontalListAdapter mListAdapter;
	String strtext = "";
	Animation downDrawerOpen, downDrawerClose = null;
	ImageView a_imageview_done, a_imageview_close, a_imageview_logo;
	TextView a_textview_title, a_textview_done;
	RelativeLayout a_footer = null;
	Bitmap mainImageBitmap, src_image, changeBitmap = null;
	ImageView rotateImageView = null;
	SeekBar rotateSeekBar = null;
	BitmapHolder mBitmapHolder = null;

	public static FragmentRotate newInstance() {
		FragmentRotate fragment = new FragmentRotate();
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

		mainImageBitmap = mBitmapHolder.getBm();

		initActivityItem();

		rotateImageView = (ImageView) rootView
				.findViewById(R.id.fixed_imageView);
		rotateImageView.setImageBitmap(mainImageBitmap);

		rotateSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar_blur);
		rotateSeekBar.setVisibility(View.GONE);

		rotate_listview = (HorizontalListView) rootView
				.findViewById(R.id.bottom_listview);
		rotate_listview.startAnimation(downDrawerOpen);

		mRotateListData = AddItems.getRotateListItems(getActivity());

		mListAdapter = new HorizontalListAdapter(getActivity(),
				mRotateListData, false);
		rotate_listview.setAdapter(mListAdapter);
		rotate_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				setRotateFunction(position);

			}
		});
		return rootView;
	}

	private void setRotateFunction(int pos) {

		switch (pos) {
		case 0:

			Matrix rotateRight = new Matrix();
			rotateRight.preRotate(90);
			Bitmap rotateBitmap = Bitmap.createBitmap(mainImageBitmap, 0, 0,
					mainImageBitmap.getWidth(), mainImageBitmap.getHeight(),
					rotateRight, true);

			mainImageBitmap = rotateBitmap;
			rotateImageView.setImageBitmap(mainImageBitmap);

			break;
		case 1:

			Matrix matrixVartical = new Matrix();
			matrixVartical.preScale(-1, 1);
			Bitmap flipVirtical = Bitmap.createBitmap(mainImageBitmap, 0, 0,
					mainImageBitmap.getWidth(), mainImageBitmap.getHeight(),
					matrixVartical, false);
			flipVirtical.setDensity(DisplayMetrics.DENSITY_DEFAULT);
			mainImageBitmap = flipVirtical;
			rotateImageView.setImageBitmap(mainImageBitmap);

			break;
		case 2:

			Matrix matrixHorizontal = new Matrix();
			matrixHorizontal.preScale(1, -1);
			Bitmap flipHorizontal = Bitmap.createBitmap(mainImageBitmap, 0, 0,
					mainImageBitmap.getWidth(), mainImageBitmap.getHeight(),
					matrixHorizontal, false);
			flipHorizontal.setDensity(DisplayMetrics.DENSITY_DEFAULT);
			mainImageBitmap = flipHorizontal;
			rotateImageView.setImageBitmap(mainImageBitmap);

			break;
		default:

			break;
		}
	}

	private void initActivityItem() {

		a_imageview_done = (ImageView) getActivity().findViewById(
				R.id.imageview_done);
		a_imageview_close = (ImageView) getActivity().findViewById(
				R.id.imageview_close);
		a_footer = (RelativeLayout) getActivity().findViewById(
				R.id.horizontalLayout);

		a_imageview_logo = (ImageView) getActivity().findViewById(
				R.id.imageview_logo);
		a_textview_title = (TextView) getActivity().findViewById(
				R.id.textview_header);
		a_textview_done = (TextView) getActivity().findViewById(
				R.id.textview_done);

		a_textview_title.setText(R.string.rotate);
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
			mBitmapHolder.setBm(mainImageBitmap);
			setHeaderContent();
			mainImageBitmap = null;
			if (mainImageBitmap != null) {
				mainImageBitmap.recycle();
				mainImageBitmap = null;
			}
		} else if (id == R.id.imageview_close) {
			setHeaderContent();
			mainImageBitmap = null;
			if (mainImageBitmap != null) {
				mainImageBitmap.recycle();
				mainImageBitmap = null;
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
