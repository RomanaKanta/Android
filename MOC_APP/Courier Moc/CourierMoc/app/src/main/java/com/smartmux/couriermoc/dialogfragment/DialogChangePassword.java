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
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.modelclass.UserInfo;
import com.smartmux.couriermoc.utils.PreferenceUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogChangePassword extends DialogFragment {


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

    @Bind(R.id.old_pdw)
    EditText oldPassword;
    @Bind(R.id.new_pwd)
    EditText newPassword;
    @Bind(R.id.retype_new_pwd)
    EditText retypePassword;
    UserInfo obj;

    @OnClick(R.id.btn_save)
    public void savePassword() {

        if (checkValidate()) {

            obj.setPassword(newPassword.getText().toString());
            Gson gson = new Gson();
            String newJson = gson.toJson(obj);
            PreferenceUtils.setUserInfo(getActivity(), newJson);
            Toast.makeText(getActivity(), "Password Changed", Toast.LENGTH_SHORT).show();
            dismiss();

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_change_password, container,
                false);
        ButterKnife.bind(this, rootView);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(
                android.R.color.white);
        getDialog().getWindow().setGravity(Gravity.CENTER);
//		getDialog().getWindow().getAttributes().windowAnimations = R.style.AnimationBottomUpDown;

        Gson gson = new Gson();
        obj = gson.fromJson(PreferenceUtils.getUserInfo(getActivity()), UserInfo.class);

        return rootView;
    }


    private boolean checkValidate() {
        boolean valid = true;

        String newpassword = newPassword.getText().toString();
        String retype = retypePassword.getText().toString();
        String oldpassword = oldPassword.getText().toString();

        if (newpassword.isEmpty() || newpassword.length() < 4 || newpassword.length() > 10) {
            newPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            newPassword.setError(null);
        }
        if (oldpassword.isEmpty() || !oldpassword.equals(obj.getPassword())) {
            oldPassword.setError("password doesn't match");
            valid = false;
        } else {
            oldPassword.setError(null);
        }
        if (retype.isEmpty() || !retype.equals(newpassword)) {
            retypePassword.setError("new password doesn't match");
            valid = false;
        } else {
            retypePassword.setError(null);
        }

        return valid;
    }

}
