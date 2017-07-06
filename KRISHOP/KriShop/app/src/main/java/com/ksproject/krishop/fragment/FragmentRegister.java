package com.ksproject.krishop.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ksproject.krishop.R;
import com.ksproject.krishop.activity.SingUpActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentRegister extends Fragment {

    @OnClick(R.id.image_back)
    public void back() {
        ((SingUpActivity) getActivity()).setFragment(new FragmentLogin(), R.anim.push_right_out, R.anim.push_right_in);
    }

    @Bind(R.id.action_title)
    TextView title;
    @Bind(R.id.input_user_name)
    EditText etUserName;
    @Bind(R.id.input_email)
    EditText etEmail;
    @Bind(R.id.input_password)
    EditText etPassword;
    @Bind(R.id.input_confirm_password)
    EditText etRetypePassword;


    @OnClick(R.id.btn_signup)
    public void sighIn() {

//        if (!validate()) {
//            Toast.makeText(RegisterActivity.this, "signup failed", Toast.LENGTH_LONG).show();
//            return;
//        }


            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myFragmentView = inflater.inflate(R.layout.fragment_register,
                container, false);

        ButterKnife.bind(this, myFragmentView);
        title.setText(R.string.register);
        return myFragmentView;
    }

    public boolean validate() {
        boolean valid = true;

        String lastName = etUserName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String reTypePassword = etRetypePassword.getText().toString();

        if (lastName.isEmpty() || lastName.length() < 3) {
            etUserName.setError("at least 3 characters");
            valid = false;
        } else {
            etUserName.setError(null);
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("enter a valid email address");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            etPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        if (reTypePassword.isEmpty() || reTypePassword.length() < 4 || reTypePassword.length() > 10) {
            etRetypePassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            etRetypePassword.setError(null);
        }
        if (!reTypePassword.equals(password)) {
            etRetypePassword.setError("password doesn't match");
            valid = false;
        } else {
            etRetypePassword.setError(null);
        }

        return valid;
    }
}
