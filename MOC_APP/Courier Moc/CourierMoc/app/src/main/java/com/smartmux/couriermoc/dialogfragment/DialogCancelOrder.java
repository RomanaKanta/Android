package com.smartmux.couriermoc.dialogfragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.smartmux.couriermoc.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogCancelOrder extends DialogFragment {


    public void onResume() {
        super.onResume();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        getDialog().getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.width = (int) (width) - (int) getActivity().getResources().getDimension(
                R.dimen.dialog_margin);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(lp);

    }


    @OnClick(R.id.btn_yes)
    public void cancelOrder() {

        Toast.makeText(getActivity(),"order Cancel",Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @OnClick(R.id.btn_no)
    public void doNothing() {
        dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_cancel_order, container,
                false);
        ButterKnife.bind(this, rootView);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(
                android.R.color.white);
        getDialog().getWindow().setGravity(Gravity.CENTER);
//		getDialog().getWindow().getAttributes().windowAnimations = R.style.AnimationBottomUpDown;



        return rootView;
    }
}
