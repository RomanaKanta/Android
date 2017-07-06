package com.smartmux.couriermoc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.AddressComponent;
import com.seatgeek.placesautocomplete.model.AddressComponentType;
import com.seatgeek.placesautocomplete.model.Place;
import com.seatgeek.placesautocomplete.model.PlaceDetails;
import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.dialogfragment.PhotoSelectionDialog;
import com.smartmux.couriermoc.modelclass.UserInfo;
import com.smartmux.couriermoc.utils.Constant;
import com.smartmux.couriermoc.utils.PreferenceUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name)
    EditText _nameText;
    @Bind(R.id.input_phone)
    EditText _phoneText;
    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.input_country)
    EditText _countryText;
    @Bind(R.id.input_zipcode)
    EditText _zipcodeText;
    @Bind(R.id.input_state)
    EditText _stateText;
    @Bind(R.id.input_city)
    EditText _cityText;
    @Bind(R.id.input_street)
    EditText _streetText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;
    @Bind(R.id.image_selected)
    ImageView selectedImage;
    String path = "";
    @Bind(R.id.autocomplete)
    PlacesAutocompleteTextView mAutocomplete;

    @OnClick(R.id.set_image)
    public void pickImage() {
        new PhotoSelectionDialog().show(getSupportFragmentManager(), "Select");
    }
//    @OnClick(R.id.image_from_camera)
//    public void pickImageFromCamera(){
//
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ButterKnife.bind(this);
        setAddress();
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();

            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String phone = _phoneText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String country = _countryText.getText().toString();
        String zipCode = _zipcodeText.getText().toString();
        String state = _stateText.getText().toString();
        String city = _cityText.getText().toString();
        String address = _streetText.getText().toString();

        // TODO: Implement your own save logic here.

        UserInfo userInfo = new UserInfo(path, name, phone, email, password, country, zipCode, state, city, address);

        Gson gson = new Gson();
        String json = gson.toJson(userInfo);
        PreferenceUtils.setUserInfo(SignupActivity.this, json);


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                        Constant.mCurrentPhotoPath = null;
                    }
                }, 3000);
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String phone = _phoneText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String country = _countryText.getText().toString();
        String zipCode = _zipcodeText.getText().toString();
        String state = _stateText.getText().toString();
        String city = _cityText.getText().toString();
        String address = _streetText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (phone.isEmpty() || !isValidMobile(phone)) {
            _phoneText.setError("invalid phone number");
            valid = false;
        } else {
            _phoneText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        if (country.isEmpty()) {
            _countryText.setError("country can't be empty");
            valid = false;
        } else {
            _countryText.setError(null);
        }
        if (zipCode.isEmpty()) {
            _zipcodeText.setError("zip code can't be empty");
            valid = false;
        } else {
            _zipcodeText.setError(null);
        }
        if (city.isEmpty()) {
            _cityText.setError("city can't be empty");
            valid = false;
        } else {
            _cityText.setError(null);
        }
        if (state.isEmpty()) {
            _stateText.setError("zip code can't be empty");
            valid = false;
        } else {
            _stateText.setError(null);
        }
        if (address.isEmpty()) {
            _streetText.setError("address can't be empty");
            valid = false;
        } else {
            _streetText.setError(null);
        }

        return valid;
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
//        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.GALLERY_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

                Uri imageFileUri = data.getData();

                if (imageFileUri != null) {

                    path = getPath(imageFileUri);
                    setPhoto(path);
                }
            }
        } else if (requestCode == Constant.ACTION_TAKE_PHOTO_B) {

            if (resultCode == RESULT_OK) {
                if (Constant.mCurrentPhotoPath != null) {
                    path = Constant.mCurrentPhotoPath;
                    setPhoto(path);
                }
            }

        }
    }

    public String getPath(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null,
                null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        return cursor.getString(columnIndex);
    }

    private void setPhoto(String path) {

        if (!TextUtils.isEmpty(path)) {

            Glide.with(SignupActivity.this)
                    .load(path)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .into(new BitmapImageViewTarget(selectedImage) {
                        @Override
                        protected void setResource(Bitmap resource) {

                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(SignupActivity.this.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            selectedImage.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }

    }

    private void setAddress(){
        mAutocomplete.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                mAutocomplete.getDetailsFor(place, new DetailsCallback() {
                    @Override
                    public void onSuccess(final PlaceDetails details) {
                        Log.d("test", "details " + details);
                        _streetText.setText(details.name);
                        for (AddressComponent component : details.address_components) {
                            for (AddressComponentType type : component.types) {
                                switch (type) {
                                    case STREET_NUMBER:
                                        break;
                                    case ROUTE:
                                        break;
                                    case NEIGHBORHOOD:
                                        break;
                                    case SUBLOCALITY_LEVEL_1:
                                        break;
                                    case SUBLOCALITY:
                                        break;
                                    case LOCALITY:
                                        _cityText.setText(component.long_name);
                                        break;
                                    case ADMINISTRATIVE_AREA_LEVEL_1:
                                        _stateText.setText(component.short_name);
                                        break;
                                    case ADMINISTRATIVE_AREA_LEVEL_2:
                                        break;
                                    case COUNTRY:
                                        _countryText.setText(component.long_name);
                                        break;
                                    case POSTAL_CODE:
                                        _zipcodeText.setText(component.long_name);
                                        break;
                                    case POLITICAL:
                                        break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(final Throwable failure) {
                        Log.d("test", "failure " + failure);
                    }
                });
            }
        });
    }
}
