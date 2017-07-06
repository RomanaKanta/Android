package com.smartmux.pos.dialogfragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.smartmux.pos.R;
import com.smartmux.pos.fragment.CashFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class OptionDeleteDialog extends DialogFragment {

    Bundle mArgs;

    int itemPosition = 0;



    @OnClick(R.id.btn_yes)
    public void itemDelete(View view) {

        CashFragment cashFragment = (CashFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.fragment_cash));

        cashFragment.removeFromList(itemPosition);
        dismiss();

    }

    @OnClick(R.id.btn_no)
    public void dismiss(View view) {
        dismiss();
    }


	public void onResume() {
		super.onResume();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;

		getDialog().getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
		lp.width = (int) (width);
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getDialog().getWindow().setAttributes(lp);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.dialog_fragment_item_delete, container,
				false);
        ButterKnife.bind(this, rootView);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//		getDialog().getWindow().setBackgroundDrawableResource(
//				android.R.color.transparent);
		getDialog().getWindow().setGravity(Gravity.BOTTOM);
//		getDialog().getWindow().getAttributes().windowAnimations = R.style.AnimationBottomUpDown;

        mArgs = getArguments();
        itemPosition = mArgs.getInt("position");


		return rootView;
	}

	
}
