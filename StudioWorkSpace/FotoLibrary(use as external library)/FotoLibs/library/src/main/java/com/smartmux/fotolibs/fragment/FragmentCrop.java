package com.smartmux.fotolibs.fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;
import com.smartmux.fotolibs.R;
import com.smartmux.fotolibs.adapter.RecycleView_CropAdapter;
import com.smartmux.fotolibs.adapter.RecycleView_CropAdapter.OnItemClickListener;
import com.smartmux.fotolibs.modelclass.BitmapHolder;
import com.smartmux.fotolibs.modelclass.ListData;
import com.smartmux.fotolibs.utils.AddItems;
import com.smartmux.fotolibs.utils.FotoLibsConstant;

public class FragmentCrop extends Fragment implements OnClickListener {

	private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;

	LinearLayout resizeDialog, bottomlayer = null;
	RelativeLayout base, dimention = null;
	View fakeview = null;
	private ArrayList<ListData> mCropListData = new ArrayList<ListData>();
	private static RecyclerView cropRecyclerView;
	RecycleView_CropAdapter  cropAdapter;
	
	Animation downAnimOpen, downAnimClose = null;
	ImageView a_imageview_done, a_imageview_close = null;
	TextView a_textview_title, a_textview_done, a_imageview_logo = null;
	RelativeLayout a_footer = null;
	private ImageView imageview_crop_select = null;
	CropImageView cropImageView = null;
	Bitmap croppedImageBitmap = null;
	boolean y_axis;
	BitmapHolder mBitmapHolder = null;
	int original_ht, original_wt;
	

	public static FragmentCrop newInstance() {
		FragmentCrop fragment = new FragmentCrop();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_crop, container,
				false);

		downAnimOpen = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_up);
		downAnimClose = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_down);

		mBitmapHolder = new BitmapHolder();
		original_ht = mBitmapHolder.getBm().getHeight();
		original_wt = mBitmapHolder.getBm().getWidth();

		mCropListData = AddItems.getCropHorizontalListItems(getActivity());

		initFragItem(rootView);

		initActivityItem();

		return rootView;
	}

	private void initFragItem(View rootView) {

		// initialize Crop Image View
		cropImageView = (CropImageView) rootView
				.findViewById(R.id.cropImageview);
		cropImageView.setVisibility(View.VISIBLE);
		cropImageView.setImageBitmap(mBitmapHolder.getBm());
		cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES,
				DEFAULT_ASPECT_RATIO_VALUES);
		cropImageView.setGuidelines(2);
		// 0 = off(no guideline),1= show guideline in on touch, 2 = show
		// guideline always

		// initialize layout
		base = (RelativeLayout) rootView.findViewById(R.id.crop_base_layout);
		base.setBackgroundColor(Color.parseColor("#000000"));

		dimention = (RelativeLayout) rootView
				.findViewById(R.id.crop_select_protion);

		resizeDialog = (LinearLayout) rootView.findViewById(R.id.layout_resize);
		fakeview = (View) rootView.findViewById(R.id.fake_view);
		resizeDialog.setVisibility(View.GONE);
		fakeview.setVisibility(View.GONE);

		bottomlayer = (LinearLayout) rootView.findViewById(R.id.bottom_layout);
		bottomlayer.startAnimation(downAnimOpen);

		// initialize image view
		imageview_crop_select = (ImageView) rootView
				.findViewById(R.id.crop_select_image);
		imageview_crop_select.setImageResource(R.drawable.btn_rotate);
		imageview_crop_select.setColorFilter(Color.parseColor("#FFFFFF"),
				PorterDuff.Mode.SRC_ATOP);

		dimention.setOnClickListener(this);

		// set List
		cropRecyclerView = (RecyclerView) rootView.findViewById(R.id.crop_listview);
		cropRecyclerView.startAnimation(downAnimOpen);
		cropRecyclerView.setHasFixedSize(true);
		cropRecyclerView.setLayoutManager(new LinearLayoutManager(
				getActivity(), LinearLayoutManager.HORIZONTAL,
				false));
		
		cropAdapter = new RecycleView_CropAdapter(
				getActivity(), mCropListData);
		 cropRecyclerView.setAdapter(cropAdapter);// set adapter on recyclerview
		 cropAdapter.notifyDataSetChanged();

		 cropAdapter.SetOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(View view, int position) {

				cropAdapter.setSelected(position);

				if (position == 0) {

					cropImageView.setFixedAspectRatio(false);

				} else {
					cropImageView.setFixedAspectRatio(true);
					setAspectRatio(position);
				}

			}
		});

	}

	private void setAspectRatio(int position) {

		if (y_axis) {

			switch (position) {

			case 1:
				cropImageView.setAspectRatio(1, 1);
				break;

			case 2:
				cropImageView.setAspectRatio(3, 4);
				break;

			case 3:
				cropImageView.setAspectRatio(2, 3);
				break;

			case 4:
				cropImageView.setAspectRatio(9, 16);
				break;

			default:
				break;
			}
		} else {
			switch (position) {

			case 1:

				cropImageView.setAspectRatio(1, 1);

				break;

			case 2:
				cropImageView.setAspectRatio(4, 3);
				break;

			case 3:
				cropImageView.setAspectRatio(3, 2);
				break;

			case 4:
				cropImageView.setAspectRatio(16, 9);
				break;

			default:
				break;
			}
		}

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

		a_textview_title.setText(R.string.crop);
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
			croppedImageBitmap = cropImageView.getCroppedImage();
			mBitmapHolder.setBm(croppedImageBitmap);
			setHeaderContent();
			croppedImageBitmap = null;
			if (croppedImageBitmap != null) {
				croppedImageBitmap.recycle();
				croppedImageBitmap = null;
			}
		} else if (id == R.id.imageview_close) {
			setHeaderContent();
			croppedImageBitmap = null;
			if (croppedImageBitmap != null) {
				croppedImageBitmap.recycle();
				croppedImageBitmap = null;
			}
		} else if (id == R.id.crop_select_protion) {
			if (y_axis) {

				y_axis = false;
				mCropListData = AddItems
						.getCropHorizontalListItems(getActivity());

				cropAdapter.setValue(mCropListData);

			} else {

				y_axis = true;
				mCropListData = AddItems
						.getCropVarticalListItems(getActivity());

				cropAdapter.setValue(mCropListData);

			}
		} else {
		}
	}

	private void setHeaderContent() {
		a_footer.setVisibility(View.VISIBLE);
		a_footer.startAnimation(downAnimOpen);

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
