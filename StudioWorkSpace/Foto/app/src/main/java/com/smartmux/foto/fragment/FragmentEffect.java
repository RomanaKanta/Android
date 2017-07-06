package com.smartmux.foto.fragment;

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
import android.widget.SeekBar;
import android.widget.TextView;

import com.ortiz.touch.TouchImageView;
import com.smartmux.foto.R;
import com.smartmux.foto.adapter.RecycleView_FilterAdapter;
import com.smartmux.foto.adapter.RecycleView_FilterAdapter.OnItemClickListener;
import com.smartmux.foto.modelclass.BitmapHolder;
import com.smartmux.foto.modelclass.ListData;
import com.smartmux.foto.utils.AddItems;
import com.smartmux.foto.utils.FotoLibsConstant;

import java.util.ArrayList;

import cn.Ragnarok.LomoFilter;
import cn.Ragnarok.OilFilter;
import cn.Ragnarok.PixelateFilter;
import cn.Ragnarok.SoftGlowFilter;
public class FragmentEffect extends Fragment implements OnClickListener {

	private ArrayList<ListData> mEffectListData = new ArrayList<ListData>();
	RecycleView_FilterAdapter filterAdapter;
	private static RecyclerView effectRecyclerView;

	String strtext = "";
	Animation downDrawerOpen, downDrawerClose = null;
	ImageView a_imageview_done, a_imageview_close, a_imageview_logo;
	TextView a_textview_title, a_textview_done;
	RelativeLayout a_footer = null;
	SeekBar effectSeekBar = null;
	BitmapHolder mBitmapHolder = null;
	Bitmap originBitmap, changeBitmap = null;
	private TouchImageView main_imageview = null;
	boolean edit = false;
	Bitmap bm = null;

	public static FragmentEffect newInstance() {
		FragmentEffect fragment = new FragmentEffect();
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
		mEffectListData = AddItems.getEffectListItems(getActivity());

		originBitmap = mBitmapHolder.getBm();

		changeBitmap = originBitmap;

		effectSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar_effect);

		effectRecyclerView = (RecyclerView) rootView
				.findViewById(R.id.fragment_horizontal_listview);
		effectRecyclerView.startAnimation(downDrawerOpen);
		effectRecyclerView.setHasFixedSize(true);
		effectRecyclerView.setLayoutManager(new LinearLayoutManager(
				getActivity(), LinearLayoutManager.HORIZONTAL, false));

		filterAdapter = new RecycleView_FilterAdapter(getActivity(),
				mEffectListData);
		effectRecyclerView.setAdapter(filterAdapter);// set adapter on
														// recyclerview
		filterAdapter.notifyDataSetChanged();

		filterAdapter.SetOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {

				if (originBitmap != null) {
					edit = true;
					filterAdapter.setSelected(position);

//					applyEffect(position);
					new ApplyEffect(position).execute();

				}

			}
		});

		return rootView;
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

		a_imageview_logo = (ImageView) getActivity().findViewById(
				R.id.imageview_logo);
		a_textview_title = (TextView) getActivity().findViewById(
				R.id.textview_header);
		a_textview_done = (TextView) getActivity().findViewById(
				R.id.textview_done);

		a_textview_title.setText(R.string.effect);
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
			effectSeekBar.setVisibility(View.GONE);
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
			effectSeekBar.setVisibility(View.GONE);
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

	private void applyEffect(int styleNo) {
		switch (styleNo) {

		case FotoLibsConstant.NONE:

			changeBitmap = originBitmap;
			break;

		case FotoLibsConstant.SPOT:

			double radius = (originBitmap.getWidth() / 3) * 95 / 100;
			changeBitmap = LomoFilter.changeToLomo(originBitmap, radius);
			break;

		case FotoLibsConstant.SOFTGLOW:
			changeBitmap = SoftGlowFilter.softGlowFilter(originBitmap, 0.6);
			break;
		case FotoLibsConstant.POSTERIZE:
			changeBitmap = OilFilter.changeToOil(originBitmap, 2);
			break;

		case FotoLibsConstant.PIXELATE:

			changeBitmap = PixelateFilter.changeToPixelate(originBitmap, 10);
			break;
		default:
			break;
		}

	

	}

	
	public class ApplyEffect extends AsyncTask<Void, Void, Void> {

		private ProgressDialog progress = null;
		int position;

		public ApplyEffect(int pos) {
			
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
			
			applyEffect(position);
			
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
