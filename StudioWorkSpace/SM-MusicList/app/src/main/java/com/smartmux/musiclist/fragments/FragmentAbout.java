/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.smartmux.musiclist.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartmux.musiclist.R;
import com.smartmux.musiclist.uicomponent.AdvancedWebView;

public class FragmentAbout extends Fragment {


	public FragmentAbout() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootview = inflater.inflate(R.layout.fragment_about, null);
		setupInitialViews(rootview);
		return rootview;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void setupInitialViews(View inflatreView) {
        AdvancedWebView webView = (AdvancedWebView) inflatreView.findViewById(R.id.webview);
        webView.loadUrl("http://smartmux.com/");


	}
}
