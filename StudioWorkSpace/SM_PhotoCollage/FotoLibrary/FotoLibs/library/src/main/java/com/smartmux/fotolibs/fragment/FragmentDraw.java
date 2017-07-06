package com.smartmux.fotolibs.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.smartmux.fotolibs.R;
import com.smartmux.fotolibs.modelclass.BitmapHolder;
import com.smartmux.fotolibs.utils.FotoLibsConstant;
import com.smartmux.fotolibs.widget.ColorSeekBar;
import com.smartmux.fotolibs.widget.DrawCircle;
import com.smartmux.fotolibs.widget.SimpleDrawingView;

public class FragmentDraw extends Fragment implements OnClickListener {

	RelativeLayout layout_draw = null;
	Animation downDrawerOpen, downDrawerClose = null;
	ImageView a_imageview_done, a_imageview_close = null;
	TextView a_textview_title, a_textview_done, a_imageview_logo;
	RelativeLayout a_footer = null;

	private Bitmap bmp_main, changed_bitmap;
	private ImageView draw_image;
	private FrameLayout flayout;
	private DrawCircle circle;
	private SeekBar strokeSeekBar;
	private SimpleDrawingView drawingView;
	private boolean isErase = false;
	private View viewadd;
	// private ColorPickerSeekBar colorPicker;
	private ColorSeekBar colorSeekBar;
	int prevcolor = Color.BLACK;
	BitmapHolder mBitmapHolder = null;
	ImageView mErase = null;


	public static FragmentDraw newInstance() {
		FragmentDraw fragment = new FragmentDraw();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_draw, container,
				false);
		downDrawerOpen = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_up);
		downDrawerClose = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_down);

		mBitmapHolder = new BitmapHolder();

		bmp_main = mBitmapHolder.getBm();

		initActivityItem();

		layout_draw = (RelativeLayout) rootView.findViewById(R.id.draw_layer);
		layout_draw.startAnimation(downDrawerOpen);

		draw_image = (ImageView) rootView.findViewById(R.id.import_imageview);
		draw_image.setImageBitmap(bmp_main);

		mErase = (ImageView) rootView.findViewById(R.id.image_erasr);

		setDrawFunction(rootView);

		return rootView;
	}

	private void setColor(SeekBar seekBarFont) {

		LinearGradient test = new LinearGradient(0.f, 0.f, 580.f, 0.0f,

		new int[] { 0xFF000000, 0xFF0000FF, 0xFF00FF00, 0xFF00FFFF, 0xFFFF0000,
				0xFFFF00FF, 0xFFFFFF00, 0xFFFFFFFF }, null, TileMode.CLAMP);
		ShapeDrawable shape = new ShapeDrawable(new RectShape());
		shape.getPaint().setShader(test);

		seekBarFont.setProgressDrawable((Drawable) shape);
		seekBarFont.setMax(256 * 7 - 1);
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

		a_textview_title.setText(R.string.draw);
		a_imageview_logo.setVisibility(View.GONE);
		a_textview_done.setVisibility(View.GONE);
		a_imageview_close.setVisibility(View.VISIBLE);
		a_imageview_done.setVisibility(View.VISIBLE);

		a_imageview_done.setOnClickListener(this);
		a_imageview_close.setOnClickListener(this);
	}

	private void setDrawFunction(View rootView) {

		flayout = (FrameLayout) rootView.findViewById(R.id.body_one);
		viewadd = getActivity().getLayoutInflater().inflate(R.layout.draw,
				flayout, false);
		drawingView = new SimpleDrawingView(getActivity());

		drawingView.setBitmap(bmp_main);
		flayout.addView(viewadd);

		// colorPicker = (ColorPickerSeekBar) rootView
		// .findViewById(R.id.colorPickerSeekBar);
        colorSeekBar = (ColorSeekBar) rootView.findViewById(R.id.colorPickerSeekBar);
//		setColor(colorPicker);
        colorSeekBar.setMaxValue(100);
//        colorSeekBar.setColors(R.array.material_colors); // material_colors is defalut included in res/color,just use it.
//        colorSeekBar.setColorBarValue(10); //0 - maxValue
//        colorSeekBar.setAlphaBarValue(10); //0-255
//        colorSeekBar.setShowAlphaBar(false);
//        colorSeekBar.setBarHeight(20); //5dpi
//        colorSeekBar.setThumbHeight(50); //30dpi

		circle = (DrawCircle) rootView.findViewById(R.id.drawCircle);
		circle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isErase) {
					drawingView.isErase = true;
					isErase = true;
					mErase.setVisibility(View.VISIBLE);
					circle.setColor(Color.WHITE);
					circle.invalidate();
				} else {
					drawingView.isErase = false;
					isErase = false;
					mErase.setVisibility(View.GONE);
					circle.setColor(prevcolor);
					circle.invalidate();
				}
			}

		});

		circle.setRadius(3);
		circle.invalidate();
		strokeSeekBar = (SeekBar) rootView.findViewById(R.id.stroke_seekbar);
        strokeSeekBar.setMax(100);
		strokeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// Log.d("progress", "" + progress);
				// int radiius = (int) (progress * .8);
				// circle.setRadius((radiius));
				circle.setRadius(progress);
				circle.invalidate();
				drawingView.setStroke((int) (progress * 2 * circle.red));
			}
		});

        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarValue, int alphaBarValue, int color) {
                prevcolor = color;
                if (drawingView.isErase == false && isErase == false) {
                    circle.setColor(color);
                    circle.invalidate();
                }
                drawingView.setColor(color);
            }
        });

//		colorPicker.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//
//			@Override
//			public void onStopTrackingTouch(SeekBar seekBar) {
//			}
//
//			@Override
//			public void onStartTrackingTouch(SeekBar seekBar) {
//			}
//
//			@Override
//			public void onProgressChanged(SeekBar seekBar, int progress,
//					boolean fromUser) {
//				// Log.d("color", "" + color);
//				int color = getColor(progress);
//
//				prevcolor = color;
//				if (drawingView.isErase == false && isErase == false) {
//					circle.setColor(color);
//					circle.invalidate();
//				}
//				drawingView.setColor(color);
//
//			}
//		});

	}

	private int getColor(int progress) {

		int r = 0;
		int g = 0;
		int b = 0;

		if (progress < 256) {
			b = progress;
		} else if (progress < 256 * 2) {
			g = progress % 256;
			b = 256 - progress % 256;
		} else if (progress < 256 * 3) {
			g = 255;
			b = progress % 256;
		} else if (progress < 256 * 4) {
			r = progress % 256;
			g = 256 - progress % 256;
			b = 256 - progress % 256;
		} else if (progress < 256 * 5) {
			r = 255;
			g = 0;
			b = progress % 256;
		} else if (progress < 256 * 6) {
			r = 255;
			g = progress % 256;
			b = 256 - progress % 256;
		} else if (progress < 256 * 7) {
			r = 255;
			g = 255;
			b = progress % 256;
		}
		return Color.argb(255, r, g, b);

	}

	private Bitmap generateBitmap() {

		flayout.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(flayout.getDrawingCache());
		flayout.setDrawingCacheEnabled(false);
		return bitmap;

	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.imageview_done) {
			mErase.setVisibility(View.GONE);
			drawingView.setStroke(1);
			drawingView.setColor(Color.BLACK);
			mBitmapHolder.setBm(generateBitmap());
			setHeaderContent();
			changed_bitmap = null;
			if (changed_bitmap != null) {

				bmp_main.recycle();
				bmp_main = null;
				changed_bitmap.recycle();
				changed_bitmap = null;
			}
		} else if (id == R.id.imageview_close) {
			mErase.setVisibility(View.GONE);
			drawingView.setStroke(1);
			drawingView.setColor(Color.BLACK);
			setHeaderContent();
			changed_bitmap = null;
			if (changed_bitmap != null) {

				bmp_main.recycle();
				bmp_main = null;
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
