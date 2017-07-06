package com.smartmux.trackfriends.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.smartmux.trackfriends.EventCreationActivity;
import com.smartmux.trackfriends.R;

public class FragmentEvent extends Fragment implements OnClickListener {
	Button mCreateEvent = null;

	public static FragmentEvent newInstance() {
		FragmentEvent fragment = new FragmentEvent();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left_footer_drawer_screen,
				container, false);

		mCreateEvent = (Button) view.findViewById(R.id.button_create_event);

		mCreateEvent.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.button_create_event:

			Intent mIntent = new Intent(getActivity(),
					EventCreationActivity.class);
			startActivity(mIntent);
			getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
			break;

		default:
			break;

		}

	}

}
