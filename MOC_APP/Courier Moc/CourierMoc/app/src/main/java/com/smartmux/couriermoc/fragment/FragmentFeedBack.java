package com.smartmux.couriermoc.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.modelclass.UserInfo;
import com.smartmux.couriermoc.utils.Constant;
import com.smartmux.couriermoc.utils.JSONParser;
import com.smartmux.couriermoc.utils.PreferenceUtils;
import com.smartmux.couriermoc.utils.SoftKeyboardHandledLinearLayout;
import com.smartmux.couriermoc.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class FragmentFeedBack extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public FragmentFeedBack() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentFeedBack newInstance(String param1, String param2) {
        FragmentFeedBack fragment = new FragmentFeedBack();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Bind(R.id.editText_email)
    EditText txtEmail;
    @Bind(R.id.editText_comment)
    EditText txtComment;

    @Bind(R.id.textView_number_of_character_left)
    TextView txtRemainingChars;

    @Bind(R.id.feedback_content)
    SoftKeyboardHandledLinearLayout mainView;

    @OnClick(R.id.button_feedback)
    public void actionFeedBack() {

        if (txtEmail.getText().toString().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches()) {
            txtEmail.setError("enter a valid email address");
        } else {
            txtEmail.setError(null);
            if (Utils.isNetworkAvailable(getActivity())) {

                new sendRequestToServer(Constant.SUB_API_FEEDBACK, txtEmail.getText().toString(), txtComment.getText().toString()).execute();
            } else {

                Toast.makeText(getActivity(),
                        getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        ButterKnife.bind(this, view);

        Gson gson = new Gson();
        UserInfo obj = gson.fromJson(PreferenceUtils.getUserInfo(getActivity()), UserInfo.class);

        txtRemainingChars.setText(String.format(
                getString(R.string.remaining_chars), 400));

        txtEmail.setText(obj.getEmail());

        txtEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {

                if (cs.toString().length() <= 0) {

//                    txtEmail.setHintTextColor(Utils.getColor(SMPublisherEBookFeedbackActivity.this, R.color.md_grey_500));

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

        txtComment.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {

                txtRemainingChars.setText(String.format(
                        getString(R.string.remaining_chars), 400 - cs
                                .toString().length()));
                if (cs.toString().length() <= 0) {

//                    txtComment.setHintTextColor(Utils.getColor(SMPublisherEBookFeedbackActivity.this, R.color.md_grey_500));

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

        mainView.setOnSoftKeyboardVisibilityChangeListener(new SoftKeyboardHandledLinearLayout.SoftKeyboardVisibilityChangeListener() {
            @Override
            public void onSoftKeyboardShow() {

                txtEmail.setCursorVisible(true);
                txtComment.setCursorVisible(true);
            }

            @Override
            public void onSoftKeyboardHide() {

                txtEmail.setCursorVisible(false);
                txtComment.setCursorVisible(false);
            }
        });

        return view;
    }


    private class sendRequestToServer extends
            AsyncTask<String, String, JSONObject> {

        String url;
        ProgressDialog progressDialog;
        String email;
        String comment;

        public sendRequestToServer(String url, String email, String comment) {
            super();
            this.url = url;
            this.email = email;
            this.comment = comment;
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            // Creating new JSON Parser
            JSONParser jParser = new JSONParser();
            String identifier = Utils
                    .getAppPackageName(getActivity());
            String uid = Utils.getUID(getActivity());
            String device = Utils.getDeviceModel();
            String version = Utils
                    .getAppVersionNumber(getActivity());
            String platform = Utils.getAppPlatfrom();

//            String pushId = EBookPreferenceUtils.getStringPref(
//                    getActivity(), Constant.PREF_NAME,
//                    Constant.pushId, "");


            HashMap<String, String> postData = new HashMap<>();

            postData.put("identifier", identifier);
            postData.put("uid", uid);
            postData.put("device", device);
            postData.put("version", version);
            postData.put("platform", platform);
//            postData.put("push_id", pushId);
            postData.put("country", Utils.getLocalCountryName());
            postData.put("language", Utils.getLocalLanguage());
            postData.put("debug", String.valueOf(Constant.debug));

            postData.put("email", email);
            postData.put("comment", comment);

            JSONObject json = jParser.getApiResult(
                    url, postData);


            return json;
        }

        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            if (progressDialog != null) {

                progressDialog.dismiss();
            }
            if (result != null) {

                try {

                    if (result.has("status")) {

                        Toast.makeText(getActivity(),
                                result.getString("message"), Toast.LENGTH_SHORT)
                                .show();
                        if (result.getString("status").equals("OK")) {
//                            finish();
//                            overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                        }

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }


}
