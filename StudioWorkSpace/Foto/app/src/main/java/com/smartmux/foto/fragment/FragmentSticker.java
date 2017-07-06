package com.smartmux.foto.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

import com.example.abner.stickerdemo.view.StickerView;
import com.smartmux.foto.R;
import com.smartmux.foto.adapter.RecyclerView_StickerAdapter;
import com.smartmux.foto.adapter.RecyclerView_StickerAdapter.OnItemClickListener;
import com.smartmux.foto.modelclass.BitmapHolder;
import com.smartmux.foto.modelclass.ListData;
import com.smartmux.foto.utils.AddItems;
import com.smartmux.foto.utils.FotoLibsConstant;

import java.util.ArrayList;
public class FragmentSticker extends Fragment implements OnClickListener {

	private ArrayList<ListData> mStickerArrayList = new ArrayList<ListData>();
//	HorizontalListView sticker_listview;
//	StickerAdapter mListAdapter;
	
	private static RecyclerView stickerRecyclerView;
	RecyclerView_StickerAdapter stickerAdapter;
	
	String strtext = "";
	Animation downDrawerOpen, downDrawerClose = null;
	ImageView a_imageview_done, a_imageview_close, a_imageview_logo;
	TextView a_textview_title, a_textview_done;
	RelativeLayout a_footer = null;
	ImageView sticker_image_view = null;
	Bitmap changed_bitmap = null;
	boolean edit = false;

	private StickerView mCurrentView;
	private ArrayList<View> mViews;
	private RelativeLayout mContentRootView;
	BitmapHolder mBitmapHolder = null;

	public static FragmentSticker newInstance() {
		FragmentSticker fragment = new FragmentSticker();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_sticker, container,
				false);

		mBitmapHolder = new BitmapHolder();

		mContentRootView = (RelativeLayout) rootView
				.findViewById(R.id.content_root);

		mViews = new ArrayList<View>();
		mStickerArrayList = AddItems.getStickerItems(getActivity());

		downDrawerOpen = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_up);
		downDrawerClose = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_down);

		initActivityItem();

		sticker_image_view = (ImageView) rootView
				.findViewById(R.id.sticker_Imageview);
		sticker_image_view.setImageBitmap(mBitmapHolder.getBm());
	
		stickerRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_sticker_listview);
		stickerRecyclerView.startAnimation(downDrawerOpen);
		stickerRecyclerView.setHasFixedSize(true);
		stickerRecyclerView.setLayoutManager(new LinearLayoutManager(
				getActivity(), LinearLayoutManager.HORIZONTAL,
				false));
		
		 stickerAdapter = new RecyclerView_StickerAdapter(
				getActivity(), mStickerArrayList);
		stickerRecyclerView.setAdapter(stickerAdapter);// set adapter on recyclerview
		stickerAdapter.notifyDataSetChanged();

		stickerAdapter.SetOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(View view, int position) {
			
				edit = true;

				
				addStickerView(mStickerArrayList.get(position).getmImage());
				
			}
		});
		
		return rootView;
	}

	private void addStickerView(int resID) {
		final StickerView stickerView = new StickerView(getActivity());
		stickerView.setImageResource(resID);
		stickerView.setOperationListener(new StickerView.OperationListener() {
			@Override
			public void onDeleteClick() {
				mViews.remove(stickerView);
				mContentRootView.removeView(stickerView);
			}

			@Override
			public void onEdit(StickerView stickerView) {
				// if (mCurrentEditTextView != null) {
				// mCurrentEditTextView.setInEdit(false);
				// }
				mCurrentView.setInEdit(false);
				mCurrentView = stickerView;
				mCurrentView.setInEdit(true);
			}

			@Override
			public void onTop(StickerView stickerView) {
				int position = mViews.indexOf(stickerView);
				if (position == mViews.size() - 1) {
					return;
				}
				StickerView stickerTemp = (StickerView) mViews.remove(position);
				mViews.add(mViews.size(), stickerTemp);
			}
		});
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		mContentRootView.addView(stickerView, lp);
		mViews.add(stickerView);
		setCurrentEdit(stickerView);
	}

	private void setCurrentEdit(StickerView stickerView) {
		if (mCurrentView != null) {
			mCurrentView.setInEdit(false);
		}
		// if (mCurrentEditTextView != null) {
		// mCurrentEditTextView.setInEdit(false);
		// }
		mCurrentView = stickerView;
		stickerView.setInEdit(true);
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

		a_textview_title.setText(R.string.sticker);
		a_imageview_logo.setVisibility(View.GONE);
		a_textview_done.setVisibility(View.GONE);
		a_imageview_close.setVisibility(View.VISIBLE);
		a_imageview_done.setVisibility(View.VISIBLE);

		a_imageview_done.setOnClickListener(this);
		a_imageview_close.setOnClickListener(this);
	}

	private Bitmap generateBitmap() {

		changed_bitmap = Bitmap.createBitmap(mContentRootView.getWidth(),
				mContentRootView.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(changed_bitmap);
		mContentRootView.draw(canvas);
		return changed_bitmap;

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.imageview_done) {
			if (edit) {
				mCurrentView.setInEdit(false);
				mBitmapHolder.setBm(generateBitmap());
			}
			changed_bitmap = null;
			setHeaderContent();
			if (changed_bitmap != null) {
				changed_bitmap.recycle();
				changed_bitmap = null;
			}
		} else if (id == R.id.imageview_close) {
			changed_bitmap = null;
			setHeaderContent();
			if (changed_bitmap != null) {
				changed_bitmap.recycle();
				changed_bitmap = null;
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
