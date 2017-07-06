package com.smartmux.pos.dialogfragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.smartmux.pos.R;
import com.smartmux.pos.fragment.CashFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DiscountDialog extends DialogFragment {

    private float charge,total;
    Bundle mArgs;

    @Bind(R.id.tv_charge)
    TextView txtcharge;
    @Bind(R.id.tv_total)
    TextView txttotal;
    @Bind(R.id.tv_discount)
    TextView txtdiacount;
    @Bind(R.id.et_discount)
    EditText etDiscount;
    float disc =0;

    @OnClick(R.id.add_discount)
    public void addDiscount(View view) {
        CashFragment cashFragment = (CashFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.fragment_cash));

        cashFragment.addDiscount(total,Integer.parseInt(etDiscount.getText().toString()));
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

		View rootView = inflater.inflate(R.layout.dialog_fragment_add_discount, container,
				false);
        ButterKnife.bind(this, rootView);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//		getDialog().getWindow().setBackgroundDrawableResource(
//				android.R.color.transparent);
		getDialog().getWindow().setGravity(Gravity.BOTTOM);
//		getDialog().getWindow().getAttributes().windowAnimations = R.style.AnimationBottomUpDown;

        mArgs = getArguments();
        charge = mArgs.getFloat("charge");


        txtcharge.setText(String.format(getString(R.string.total_amount), charge));



        etDiscount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {

                if(!cs.equals("")) {
                    float d = Float.parseFloat(cs.toString());

                    disc = (d / 100) * charge;
                    txtdiacount.setText(String.format(getString(R.string.total_amount), disc));

                    total = (charge - disc);
                    txttotal.setText(String.format(getString(R.string.total_amount), total));
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });




		return rootView;
	}

	
}
