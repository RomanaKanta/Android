package com.smartmux.fotolibs.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
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

import com.ortiz.touch.TouchImageView;
import com.smartmux.fotolibs.R;
import com.smartmux.fotolibs.modelclass.BitmapHolder;
import com.smartmux.fotolibs.utils.FotoLibsConstant;

public class FragmentAdjustment extends Fragment implements OnClickListener {

	View falseView = null;
	ImageView a_imageview_done, a_imageview_close;
	TextView a_textview_title, a_textview_done, a_imageview_logo;
	RelativeLayout a_footer = null;
	private TouchImageView main_imageview = null;
	Animation downDrawerOpen, downDrawerClose = null;
	private SeekBar seekB, seekC, seekS = null;
	Bitmap main_bm, new_bm = null;
	float progressB, progressC, progressS;
	BitmapHolder mBitmapHolder = null;

	public static FragmentAdjustment newInstance() {
		FragmentAdjustment fragment = new FragmentAdjustment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_adjustment,
				container, false);

		downDrawerOpen = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_up);
		downDrawerClose = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_down);

		mBitmapHolder = new BitmapHolder();
		 
		 main_bm = mBitmapHolder.getBm();

		initActivityItem();

		falseView = (View) rootView.findViewById(R.id.false_adjust);
		falseView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				falseView.setVisibility(View.VISIBLE);
			}
		});

		seekB = (SeekBar) rootView.findViewById(R.id.seekBar_bright);
		seekC = (SeekBar) rootView.findViewById(R.id.seekBar_contrast);
		seekS = (SeekBar) rootView.findViewById(R.id.seekBar_saturation);

		seekB.setMax(510);
		seekB.setProgress((242));
		seekC.setMax(10);
		seekC.setProgress((1));
		seekS.setMax(10);
		seekS.setProgress((1));
		progressB = 242 - 255;
		progressC = 1;
		progressS = 1;

		seekB.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				new_bm = null;
				new_bm = changeBitmapContrastBrightness(main_bm, progressC,
						(float) (progress - 255), progressS);
				main_imageview.setImageBitmap(new_bm);
				progressB = progress - 255;

				// Log.e("BRIGHT", "" + progress);
			}
		});

		seekC.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				if (progress <= 1) {
					progress = 1;
				}
				new_bm = null;
				new_bm = changeBitmapContrastBrightness(main_bm,
						(float) (progress), progressB, progressS);
				main_imageview.setImageBitmap(new_bm);
				progressC = progress;
			}
		});

		seekS.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// new_bm = drawAlteredImage(largeIcon , seekC.getProgress(),
				// seekB.getProgress(), progress);
				new_bm = null;
				new_bm = changeBitmapContrastBrightness(main_bm, progressC,
						progressB, (float) (progress));
				main_imageview.setImageBitmap(new_bm);
				progressS = progress;
			}
		});

		return rootView;
	}

	public static Bitmap changeBitmapContrastBrightness(Bitmap bmp,
			float contrast, float brightness, float satu) {
		ColorMatrix cm = new ColorMatrix(new float[] { contrast, 0, 0, 0,
				brightness, 0, contrast, 0, 0, brightness, 0, 0, contrast, 0,
				brightness, 0, 0, 0, 1, 0 });

		Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
				bmp.getConfig());

		Canvas canvas = new Canvas(ret);

		Paint paint = new Paint();

		cm.setSaturation(satu);

		final float m[] = cm.getArray();
		final float c = contrast;
		cm.set(new float[] { m[0] * c, m[1] * c, m[2] * c, m[3] * c,
				m[4] * c + brightness, m[5] * c, m[6] * c, m[7] * c, m[8] * c,
				m[9] * c + brightness, m[10] * c, m[11] * c, m[12] * c,
				m[13] * c, m[14] * c + brightness, m[15], m[16], m[17], m[18],
				m[19] });

		paint.setColorFilter(new ColorMatrixColorFilter(cm));
		canvas.drawBitmap(bmp, 0, 0, paint);

		return ret;
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

		a_textview_title.setText(R.string.adjust);
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
//			mBitmapManager.addBitmapToMemoryCache(cacheKey,new_bm);
			mBitmapHolder.setBm(new_bm);
			setHeaderContent();
			main_bm = null;
			new_bm = null;
			if (new_bm != null) {
				main_bm.recycle();
				main_bm = null;
				new_bm.recycle();
				new_bm = null;
			}
		} else if (id == R.id.imageview_close) {
			setHeaderContent();
			main_bm = null;
			new_bm = null;
			if (new_bm != null) {
				main_bm.recycle();
				main_bm = null;
				new_bm.recycle();
				new_bm = null;
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
