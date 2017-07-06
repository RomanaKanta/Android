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
import com.ksproject.krishop.dialogfragment.DialogFrogotPassword;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentLogin extends Fragment {

    @OnClick(R.id.image_back)
    public void back(){
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);

         }

    @Bind(R.id.action_title)
    TextView title;

    @Bind(R.id.input_email)
    EditText etEmail;
    @Bind(R.id.input_password)
    EditText etPassword;

    @OnClick(R.id.txt_forgot)
    public void resetPassword(){

        DialogFrogotPassword frogotPassword = new DialogFrogotPassword();
        frogotPassword.show(getActivity().getFragmentManager(), "Frogot_Password");

    }

    @OnClick(R.id.btn_signup)
    public void signUp(){

//        if (!validate()) {
//            Toast.makeText(LoginActivity.this, "login failed", Toast.LENGTH_LONG).show();
//            return;
//        }

        ((SingUpActivity)getActivity()).setFragment(new FragmentRegister(),R.anim.push_left_in,
                R.anim.push_left_out);


    }

    @OnClick(R.id.btn_login)
    public void logIn(){

            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myFragmentView = inflater.inflate(R.layout.fragment_login,
                container, false);

        ButterKnife.bind(this, myFragmentView);
        title.setText(R.string.login);
        return myFragmentView;
    }

    public boolean validate() {
        boolean valid = true;

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

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

        return valid;
    }
}
