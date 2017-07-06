package com.roundflat.musclecard.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.roundflat.musclecard.R;
import com.roundflat.musclecard.model.TutorialModel;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CardFragment extends Fragment {

	private String imageName;

//	public CardFragment(List<TutorialModel> tutorialList) {
//		super();
//		this.tutorialList = tutorialList;
//	}
//	
	public CardFragment() {}

    public static CardFragment newInstance(String name) {
    	CardFragment frag = new CardFragment();
      Bundle args = new Bundle();
      args.putString("name", name);
      frag.setArguments(args);
      return frag;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		imageName = args.getString("name");
		System.out.println(imageName);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.pager_item_front, container,
				false);
		ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
		imageView.setImageDrawable(getImage(imageName));


		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("dummy", true);
	}

	private Drawable getImage(String name) {
		try {
			InputStream ims = getActivity().getAssets().open("images/" + name);
			Drawable drawable = Drawable.createFromStream(ims, null);

			return drawable;
		} catch (IOException ex) {
			return null;
		}

	}
}
