package com.smartmux.trackfriends.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.smartmux.trackfriends.R;

public class FragmentSetting extends Fragment implements OnClickListener {

	Button standardButton, hybridButton, satelliteButton, threeDmapButton,
			routeButton, settingButton = null;

	public static FragmentSetting newInstance() {
		FragmentSetting fragment = new FragmentSetting();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.right_footer_drawer_screen,
				container, false);

		standardButton = (Button) view.findViewById(R.id.button_standard);
		standardButton.setOnClickListener(this);
		
		hybridButton = (Button) view.findViewById(R.id.button_hybrid);
		hybridButton.setOnClickListener(this);
		
		satelliteButton = (Button) view.findViewById(R.id.button_satalite);
		satelliteButton.setOnClickListener(this);
		
		threeDmapButton = (Button) view.findViewById(R.id.button_show_map);
		routeButton = (Button) view.findViewById(R.id.button_show_route);
		settingButton = (Button) view.findViewById(R.id.button_setting);

		return view;
	}

	void setMapType(String text) {
		Bundle bundle = new Bundle();
		FragmentMap mapFrag = new FragmentMap();

		bundle.putString("message", text);

		mapFrag.setArguments(bundle);
		FragmentTransaction fragTransaction = getFragmentManager()
				.beginTransaction();
		fragTransaction.replace(R.id.map, mapFrag);
		fragTransaction.addToBackStack(null);
		fragTransaction.commit();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.button_standard:
			
			setMapType("standard");
			break;
			
		case R.id.button_hybrid:

			setMapType("hybrid");
			break;
			
		case R.id.button_satalite:
		
			setMapType("satellite");
			break;
			
		default:
			break;
		}
	}
}