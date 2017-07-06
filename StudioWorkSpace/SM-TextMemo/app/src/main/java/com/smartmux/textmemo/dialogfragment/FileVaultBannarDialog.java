package com.smartmux.textmemo.dialogfragment;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.smartmux.textmemo.R;

public class FileVaultBannarDialog extends DialogFragment {

	ImageView install, delete;
	RelativeLayout banar_root;

	String FileVault_Link = "https://play.google.com/store/apps/details?id=com.smartmux.filevaultfree";

	public void onResume() {
		super.onResume();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;

		getDialog().getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
		lp.width = (int) (width - 80);
		// lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.height = (int) (((width * 964) / 1172) - 45);
		getDialog().getWindow().setAttributes(lp);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.filevault_banar, container,
				false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		getDialog().getWindow().setGravity(Gravity.CENTER);
		// getDialog().getWindow().getAttributes().windowAnimations =
		// R.style.AnimationBottomUpDown;

		install = (ImageView) rootView.findViewById(R.id.file_vault_install);

		install.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				((NoteEditorActivity) getActivity()).ad_Show = true;
				startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse(FileVault_Link)));
				dismiss();
			}
		});

		delete = (ImageView) rootView.findViewById(R.id.file_vault_ad_delete);

		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				((NoteEditorActivity) getActivity()).ad_Show = true;
				dismiss();
			}
		});

		return rootView;
	}

}
