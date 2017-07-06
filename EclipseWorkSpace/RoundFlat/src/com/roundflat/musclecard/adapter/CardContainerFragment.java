package com.roundflat.musclecard.adapter;

import java.io.IOException;
import java.io.InputStream;

import com.roundflat.musclecard.R;
import com.roundflat.musclecard.model.TutorialModel;

import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CardContainerFragment extends Fragment implements
		GestureDetector.OnGestureListener {

	private static boolean cardFlipped = false;
	static String imagePatterName = "_A_000.png";
	static TutorialModel tutarial;
	GestureDetectorCompat mDetector;

	public CardContainerFragment(TutorialModel model) {

		tutarial = model;
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_card_container,
				container, false);

		mDetector = new GestureDetectorCompat(getActivity(), this);
		rootView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				mDetector.onTouchEvent(event);
				return true;
			}
		});
		getChildFragmentManager().beginTransaction()
				.add(R.id.container, new CardFrontFragment()).commit();

		return rootView;
	}

	@SuppressLint("NewApi")
	public void flipCard() {
		Fragment newFragment;
		if (cardFlipped) {
			newFragment = new CardFrontFragment();
		} else {
			newFragment = new CardBackFragment();
		}

		getChildFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.animator.card_flip_right_in,
						R.animator.card_flip_right_out,
						R.animator.card_flip_left_in,
						R.animator.card_flip_left_out)
				.replace(R.id.container, newFragment).commit();

		cardFlipped = !cardFlipped;
	}

	public static class CardFrontFragment extends Fragment {

		public CardFrontFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_card, container,
					false);
			ImageView imageView = (ImageView) rootView
					.findViewById(R.id.imageView);

			try {
				InputStream ims = getActivity().getAssets().open(
						"images/" + tutarial.getId() + imagePatterName);
				Drawable drawable = Drawable.createFromStream(ims, null);
				imageView.setImageDrawable(drawable);
			} catch (IOException ex) {
				return null;
			}

//			if (cardFlipped) {
//
//				rootView.setVisibility(View.INVISIBLE);
//
//			} else {
//
//				rootView.setVisibility(View.VISIBLE);
//			}

			return rootView;
		}
	}

	public static class CardBackFragment extends Fragment {

		public CardBackFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_card_back,
					container, false);

			TextView txtSubtitle = (TextView) rootView
					.findViewById(R.id.textView_subtitle);
			TextView txtTitle = (TextView) rootView
					.findViewById(R.id.textView_title);
			TextView txtJP = (TextView) rootView.findViewById(R.id.textView_jp);
			TextView txtEng = (TextView) rootView
					.findViewById(R.id.textView_eng);
			txtSubtitle.setText(tutarial.getSub_title());
			txtTitle.setText(tutarial.getTitle());
			txtJP.setText(tutarial.getLabel_japanese());
			txtEng.setText(tutarial.getLabel_english());

			if (tutarial.getTitle().equals("NIL")) {

				txtTitle.setText(tutarial.getSub_title());
			}

//			if (cardFlipped) {
//
//				rootView.setVisibility(View.INVISIBLE);
//
//			} else {
//
//				rootView.setVisibility(View.VISIBLE);
//			}

			return rootView;
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		flipCard();
		return true;
	}

	// private static Drawable getImage(String name) {
	//
	//
	// }

}