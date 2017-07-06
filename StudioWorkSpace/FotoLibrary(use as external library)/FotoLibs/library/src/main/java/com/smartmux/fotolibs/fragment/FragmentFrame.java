package com.smartmux.fotolibs.fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.smartmux.fotolibs.R;
import com.smartmux.fotolibs.adapter.RecyclerView_FrameAdapter;
import com.smartmux.fotolibs.adapter.RecyclerView_FrameAdapter.OnItemClickListener;
import com.smartmux.fotolibs.modelclass.BitmapHolder;
import com.smartmux.fotolibs.modelclass.FrameModel;
import com.smartmux.fotolibs.utils.AddItems;
import com.smartmux.fotolibs.utils.FotoLibsConstant;

public class FragmentFrame extends Fragment implements OnClickListener {

	private static RecyclerView frameRecyclerView;
	RecyclerView_FrameAdapter  frameAdapter;
	
	private ImageView frameImage;
	private ArrayList<FrameModel> frameArray;
	Bitmap mainImageBitmap, changedBitmap = null;
	Animation downDrawerOpen, downDrawerClose = null;
	ImageView a_imageview_done, a_imageview_close;
	TextView a_textview_title, a_textview_done, a_imageview_logo;
	RelativeLayout a_footer = null;
	private Bitmap frame, out = null;
	boolean isClicked = false;
	BitmapHolder mBitmapHolder = null;

	public static FragmentFrame newInstance() {
		FragmentFrame fragment = new FragmentFrame();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_frame, container,
				false);

		downDrawerOpen = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_up);
		downDrawerClose = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_down);

		mBitmapHolder = new BitmapHolder();

		initActivityItem();
		frameArray = AddItems.getFrameItems(getActivity());

		mainImageBitmap = mBitmapHolder.getBm();

		changedBitmap = mainImageBitmap;

		frameImage = (ImageView) rootView.findViewById(R.id.frame_imageView);
		frameImage.setImageBitmap(mainImageBitmap);
		
		frameRecyclerView = (RecyclerView) rootView.findViewById(R.id.frame_listview);
		frameRecyclerView.startAnimation(downDrawerOpen);
		frameRecyclerView.setHasFixedSize(true);
		frameRecyclerView.setLayoutManager(new LinearLayoutManager(
				getActivity(), LinearLayoutManager.HORIZONTAL,
				false));
		
		 frameAdapter = new RecyclerView_FrameAdapter(getActivity(), frameArray, 
					mainImageBitmap);
		frameRecyclerView.setAdapter(frameAdapter);// set adapter on recyclerview
		frameAdapter.notifyDataSetChanged();

		frameAdapter.SetOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(View view, int position) {
			
				if (!isClicked) {

					isClicked = true;
					setFramedImage(position);

				}
			}
		});
		
		
		return rootView;
	}

	public void setFramedImage(int position) {
		// Log.d("Clicked", "frame clicked");

		if (frame != null) {
			frame.recycle();
			frame = null;
			// System.gc();
		}

		if (out != null) {
			out.recycle();
			out = null;
			// System.gc();
		}

		frame = decodeSampledBitmapFromResource(getResources(),
				frameArray.get(position).getFrame(), frameImage.getWidth(),
				frameImage.getHeight());

		out = combineImages(frame, mainImageBitmap);
		frameImage.setImageBitmap(out);
		changedBitmap = out;

		isClicked = false;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// BEGIN_INCLUDE (calculate_sample_size)
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 2;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger
			// inSampleSize).

			long totalPixels = width * height / inSampleSize;

			// Anything more than 2x the requested pixels we'll sample down
			// further
			final long totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels > totalReqPixelsCap) {
				inSampleSize *= 2;
				totalPixels /= 2;
			}
		}
		return inSampleSize;
		// END_INCLUDE (calculate_sample_size)
	}

	static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeResource(res, resId, options);
	}

	public Bitmap combineImages(Bitmap frame, Bitmap image) {

		Bitmap cs = null;
		Bitmap rs = null;

		rs = Bitmap.createScaledBitmap(frame, image.getWidth(),
				image.getHeight(), true);

		cs = Bitmap.createBitmap(rs.getWidth(), rs.getHeight(),
				Bitmap.Config.RGB_565);

		Canvas comboImage = new Canvas(cs);

		comboImage.drawBitmap(image, 0, 0, null);
		comboImage.drawBitmap(rs, 0, 0, null);

		if (rs != null) {
			rs.recycle();
			rs = null;
		}
		Runtime.getRuntime().gc();

		return cs;
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

		a_textview_title.setText(R.string.frame);
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
			mBitmapHolder.setBm(changedBitmap);
			setHeaderContent();
			mainImageBitmap = null;
			changedBitmap = null;
			if (changedBitmap != null) {
				mainImageBitmap.recycle();
				mainImageBitmap = null;
				changedBitmap.recycle();
				changedBitmap = null;
			}
		} else if (id == R.id.imageview_close) {
			setHeaderContent();
			mainImageBitmap = null;
			changedBitmap = null;
			if (changedBitmap != null) {
				mainImageBitmap.recycle();
				mainImageBitmap = null;
				changedBitmap.recycle();
				changedBitmap = null;
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
