package com.smartmux.foto.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ortiz.touch.TouchImageView;
import com.smartmux.foto.R;
import com.smartmux.foto.modelclass.BitmapHolder;
public class FragmentTransparent extends Fragment {

	ImageView a_imageview_done, a_imageview_close, a_imageview_logo = null;
	TextView a_textview_title, a_textview_done = null;
	RelativeLayout a_footer = null;
	TouchImageView a_main_imageview = null;
	BitmapHolder mBitmapHolder = null;

	public static FragmentTransparent newInstance() {
		FragmentTransparent fragment = new FragmentTransparent();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_transparent,
				container, false);
		mBitmapHolder = new BitmapHolder();
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			initActivityItem();
			setHeaderContent();
		}

		return rootView;
	}

	private void initActivityItem() {

		a_main_imageview = (TouchImageView) getActivity().findViewById(
				R.id.editor_imageView);

		a_imageview_done = (ImageView) getActivity().findViewById(
				R.id.imageview_done);
		a_imageview_close = (ImageView) getActivity().findViewById(
				R.id.imageview_close);
		a_footer = (RelativeLayout) getActivity().findViewById(
				R.id.horizontalLayout);
		a_footer.setVisibility(View.VISIBLE);

		a_imageview_logo = (ImageView) getActivity().findViewById(
				R.id.imageview_logo);
		a_textview_title = (TextView) getActivity().findViewById(
				R.id.textview_header);
		a_textview_done = (TextView) getActivity().findViewById(
				R.id.textview_done);

	}

	private void setHeaderContent() {

		a_main_imageview.setImageBitmap(mBitmapHolder.getBm());

		a_textview_title.setText(R.string.header_title);
		a_imageview_logo.setVisibility(View.VISIBLE);
		a_textview_done.setVisibility(View.VISIBLE);
		a_imageview_close.setVisibility(View.GONE);
		a_imageview_done.setVisibility(View.GONE);

	}

}
