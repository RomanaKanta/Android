package com.smartmux.foto.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;
import com.smartmux.foto.R;
import com.smartmux.foto.adapter.RecycleView_ResizeListAdapter;
import com.smartmux.foto.adapter.RecycleView_ResizeListAdapter.OnItemClickListener;
import com.smartmux.foto.modelclass.BitmapHolder;
import com.smartmux.foto.modelclass.ListData;
import com.smartmux.foto.utils.AddItems;
import com.smartmux.foto.utils.FotoLibsConstant;

import java.util.ArrayList;
public class FragmentResize extends Fragment implements OnClickListener {

	RelativeLayout base, dimention = null;
	LinearLayout resizeDialog, bottomlayer;
	View fakeview;
	private ArrayList<ListData> mTextListData = new ArrayList<ListData>();
//	HorizontalListView resize_listview;
//	ResizeListAdapter mListAdapter;
	private static RecyclerView resizeRecyclerView;
	RecycleView_ResizeListAdapter  resizeAdapter;
	
	String strtext = "";
	ImageView imageview_attach, imageview_attach_close,
			imageview_axis_selector;
	boolean y_axis, attach_close = false;
	Animation downDrawerOpen, downDrawerClose = null;
	ImageView a_imageview_done, a_imageview_close, a_imageview_logo;
	TextView a_textview_title, a_textview_done;
	RelativeLayout a_footer = null;
	CropImageView cropImageView;
	TextView textview_original_ht, textview_original_wt;
	EditText edittext_new_ht, edittext_new_wt;
	Bitmap originalImagebitmap;
	int original_ht, original_wt, new_ht, new_wt;
	Bitmap newBitmap = null;
	BitmapHolder mBitmapHolder = null;

	public static FragmentResize newInstance() {
		FragmentResize fragment = new FragmentResize();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_crop, container,
				false);

		downDrawerOpen = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_up);
		downDrawerClose = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_down);

		mBitmapHolder = new BitmapHolder();

		originalImagebitmap = mBitmapHolder.getBm();

		initActivityItem();
		initFragItem(rootView);
		getOriginalSize();

//		resize_listview = (HorizontalListView) rootView
//				.findViewById(R.id.crop_listview);
//		mTextListData = AddItems.getResizeListItems(getActivity());
//		// resize_listview.startAnimation(downDrawerOpen);
//		mListAdapter = new ResizeListAdapter(getActivity(), mTextListData);
//		resize_listview.setAdapter(mListAdapter);
//		resize_listview.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//
//				int item = Integer.parseInt(mTextListData.get(position)
//						.getmText().toString());
//
//				setNewSize(item, false, false);
//
//			}
//		});
		mTextListData = AddItems.getResizeListItems(getActivity());
		resizeRecyclerView = (RecyclerView) rootView.findViewById(R.id.crop_listview);
		resizeRecyclerView.startAnimation(downDrawerOpen);
		resizeRecyclerView.setHasFixedSize(true);
		resizeRecyclerView.setLayoutManager(new LinearLayoutManager(
				getActivity(), LinearLayoutManager.HORIZONTAL,
				false));
		
		resizeAdapter = new RecycleView_ResizeListAdapter(
				getActivity(), mTextListData);
		resizeRecyclerView.setAdapter(resizeAdapter);// set adapter on recyclerview
		 resizeAdapter.notifyDataSetChanged();

		 resizeAdapter.SetOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(View view, int position) {

				int item = Integer.parseInt(mTextListData.get(position)
						.getmText().toString());

				setNewSize(item, false, false);

			}
		});
		

		return rootView;
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

		a_textview_title.setText(R.string.resize);
		a_imageview_logo.setVisibility(View.GONE);
		a_textview_done.setVisibility(View.GONE);
		a_imageview_close.setVisibility(View.VISIBLE);
		a_imageview_done.setVisibility(View.VISIBLE);

		a_imageview_done.setOnClickListener(this);
		a_imageview_close.setOnClickListener(this);
	}

	private void initFragItem(View rootView) {

		// initialize Layout
		base = (RelativeLayout) rootView.findViewById(R.id.crop_base_layout);
		base.setBackgroundColor(Color.parseColor("#00000000"));

		dimention = (RelativeLayout) rootView
				.findViewById(R.id.crop_select_protion);

		resizeDialog = (LinearLayout) rootView.findViewById(R.id.layout_resize);
		fakeview = (View) rootView.findViewById(R.id.fake_view);
		resizeDialog.setVisibility(View.VISIBLE);
		fakeview.setVisibility(View.VISIBLE);

		bottomlayer = (LinearLayout) rootView.findViewById(R.id.bottom_layout);
		bottomlayer.startAnimation(downDrawerOpen);

		// initialize Image view
		cropImageView = (CropImageView) rootView
				.findViewById(R.id.cropImageview);
		cropImageView.setVisibility(View.GONE);

		imageview_attach = (ImageView) rootView
				.findViewById(R.id.resize_attach_image);
		imageview_attach.setColorFilter(Color.parseColor("#FFFFFF"),
				PorterDuff.Mode.SRC_ATOP);

		imageview_attach_close = (ImageView) rootView
				.findViewById(R.id.resize_attach_close_image);
		imageview_attach_close.setColorFilter(Color.parseColor("#FFFFFF"),
				PorterDuff.Mode.SRC_ATOP);
		imageview_attach_close.setVisibility(View.GONE);

		imageview_axis_selector = (ImageView) rootView
				.findViewById(R.id.crop_select_image);
		imageview_axis_selector.setImageResource(R.drawable.dimension_width);
		imageview_axis_selector.setColorFilter(Color.parseColor("#FFFFFF"),
				PorterDuff.Mode.SRC_ATOP);

		// initialize Text & Edit view
		textview_original_ht = (TextView) rootView
				.findViewById(R.id.resize_original_ht);
		textview_original_wt = (TextView) rootView
				.findViewById(R.id.resize_original_wt);

		edittext_new_ht = (EditText) rootView.findViewById(R.id.resize_new_ht);
		edittext_new_wt = (EditText) rootView.findViewById(R.id.resize_new_wt);

		// set OnClickListener
		fakeview.setOnClickListener(this);
		imageview_attach.setOnClickListener(this);
		dimention.setOnClickListener(this);
		edittext_new_ht.setOnClickListener(this);
		edittext_new_wt.setOnClickListener(this);
		// editTextChangedListener(edittext_new_ht, true);
		// editTextChangedListener(edittext_new_wt,false);

	}

	private void getOriginalSize() {

		original_ht = originalImagebitmap.getHeight();
		original_wt = originalImagebitmap.getWidth();

		textview_original_ht.setText("" + original_ht);
		textview_original_wt.setText("" + original_wt);
		edittext_new_ht.setText("" + original_ht);
		edittext_new_wt.setText("" + original_wt);
	}

	private void setNewSize(int resize, boolean edit, boolean edit_ht) {

		if (attach_close) {
			if (edit) {

			} else {
				if (y_axis) {

					edittext_new_ht.setText("" + resize);
					// edittext_new_wt.setText("" + wt);
				} else {

					// edittext_new_ht.setText("" + ht);
					edittext_new_wt.setText("" + resize);
				}
			}

		} else {
			if (edit) {

				if (edit_ht) {

					int wt = (int) Math.ceil((original_wt * resize)
							/ original_ht);

					edittext_new_wt.setText("" + wt);

				} else {

					int ht = (int) Math.ceil((original_ht * resize)
							/ original_wt);

					edittext_new_ht.setText("" + ht);
				}

			} else {
				if (y_axis) {

					int wt = (int) Math.ceil((original_wt * resize)
							/ original_ht);

					edittext_new_ht.setText("" + resize);
					edittext_new_wt.setText("" + wt);
				} else {

					int ht = (int) Math.ceil((original_ht * resize)
							/ original_wt);

					edittext_new_ht.setText("" + ht);
					edittext_new_wt.setText("" + resize);
				}
			}
		}

	}

	private void editTextChangedListener(EditText ed, final boolean edit_ht) {

		ed.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {

				if (cs.toString().equals("")) {
					setNewSize(0, true, edit_ht);
				} else {
					setNewSize(Integer.parseInt(cs.toString()), true, edit_ht);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

	}

	public void scaleDown(Bitmap realImage, boolean filter) {

		if (newBitmap != null) {

			newBitmap.recycle();
			newBitmap = null;
		}

		if (!(edittext_new_wt.getText().equals(""))
				|| !(edittext_new_ht.getText().equals(""))) {
			int width = Integer.parseInt(edittext_new_wt.getText().toString());
			int height = Integer.parseInt(edittext_new_ht.getText().toString());

			if (width == 0 || height == 0) {
				newBitmap = Bitmap.createScaledBitmap(realImage, original_wt,
						original_ht, filter);
			} else {
				newBitmap = Bitmap.createScaledBitmap(realImage, width, height,
						filter);
			}

			mBitmapHolder.setBm(newBitmap);
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.imageview_done) {
			scaleDown(originalImagebitmap, true);
			setHeaderContent();
			newBitmap = null;
			if (newBitmap != null) {
				newBitmap.recycle();
				newBitmap = null;
			}
		} else if (id == R.id.imageview_close) {
			setHeaderContent();
			newBitmap = null;
			if (newBitmap != null) {
				newBitmap.recycle();
				newBitmap = null;
			}
		} else if (id == R.id.resize_new_ht) {
			editTextChangedListener(edittext_new_ht, true);
		} else if (id == R.id.resize_new_wt) {
			editTextChangedListener(edittext_new_wt, false);
		} else if (id == R.id.fake_view) {
			resizeDialog.setVisibility(View.VISIBLE);
			fakeview.setVisibility(View.VISIBLE);
		} else if (id == R.id.resize_attach_image) {
			if (imageview_attach_close.getVisibility() == View.GONE) {

				imageview_attach_close.setVisibility(View.VISIBLE);
				attach_close = true;

			} else {
				imageview_attach_close.setVisibility(View.GONE);
				attach_close = false;
			}
		} else if (id == R.id.crop_select_protion) {
			if (y_axis) {
				imageview_axis_selector
						.setImageResource(R.drawable.dimension_width);

				y_axis = false;

			} else {

				imageview_axis_selector
						.setImageResource(R.drawable.dimension_height);
				y_axis = true;

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
