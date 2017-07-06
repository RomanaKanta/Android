package com.smartmux.musiclist.DialogFragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.smartmux.musiclist.R;
import com.smartmux.musiclist.dbhandler.Playlist;

import java.util.ArrayList;

public class DialogAddToPlaylist extends DialogFragment {

    ListView playlist;
    ArrayAdapter<String> adapter;
    ArrayList<String> playlistarray;

	public void onResume() {
		super.onResume();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;

		getDialog().getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
		int mrgnwt = (int) getActivity().getResources().getDimension(
				R.dimen.mrgn_left)
				+ (int) getActivity().getResources().getDimension(R.dimen.mrgn_left);
		lp.width = (int) (width - mrgnwt);
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getDialog().getWindow().setAttributes(lp);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.dialog_add_to_playlist, container,
				false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		getDialog().getWindow().setGravity(Gravity.CENTER);
//		getDialog().getWindow().getAttributes().windowAnimations = R.style.AnimationBottomUpDown;
        playlistarray = Playlist.getAllPlaylistsName(getActivity().getContentResolver());
        playlist = (ListView)rootView.findViewById(R.id.dialog_playlist);

        adapter = new ArrayAdapter<String>(
                getActivity(), R.layout.font_row, playlistarray);

        playlist.setAdapter(adapter);
        playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),""+position,Toast.LENGTH_SHORT).show();
            }
        });

		return rootView;
	}

}
