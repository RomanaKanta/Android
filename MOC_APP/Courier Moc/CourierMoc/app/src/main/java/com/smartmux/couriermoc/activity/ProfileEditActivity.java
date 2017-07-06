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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

/**
 * Created by tanvir-android on 7/25/16.
 */
public class ProfileEditActivity extends AppCompatActivity {

    private static final String TAG = "ProfileEditActivity";
    @Bind(R.id.user_photo)
    ImageView userImage;
    @Bind(R.id.input_new_name)
    EditText _nameText;
    @Bind(R.id.input_new_phone)
    EditText _phoneText;
    @Bind(R.id.input_new_email)
    EditText _emailText;
    @Bind(R.id.input_new_country)
    EditText _countryText;
    @Bind(R.id.input_new_zipcode)
    EditText _zipcodeText;
    @Bind(R.id.input_new_state)
    EditText _stateText;
    @Bind(R.id.input_new_city)
    EditText _cityText;
    @Bind(R.id.input_new_address)
    EditText _addressText;
//    @Bind(R.id.input_old_pdw)
//    EditText _oldPwdText;
//    @Bind(R.id.input_new_pwd)
//    EditText _newPwdText;

//    @Bind(R.id.password_edit)
//    LinearLayout password_edit;
//    @OnClick(R.id.expand_password)
//    public void expandPassword(){
//        if(password_edit.getVisibility()==View.GONE) {
//            password_edit.setVisibility(View.VISIBLE);
//        }else{
//            password_edit.setVisibility(View.GONE);
//        }
//    }

    String path = "";
    @Bind(R.id.address_detail)
    LinearLayout address_edit;

    @OnClick(R.id.expand_address)
    public void expandAddress() {
        if (address_edit.getVisibility() == View.GONE) {
            address_edit.setVisibility(View.VISIBLE);
        } else {
            address_edit.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.change_photo)
    public void changePhoto() {
        new PhotoSelectionDialog().show(getSupportFragmentManager(), "Select");
    }

    @OnClick(R.id.btn_edit_profile)
    public void saveChanges() {
        save();
    }

    String password;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.input_add_new)
    PlacesAutocompleteTextView mAutocomplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Edit Profile");


        Gson gson = new Gson();
        UserInfo obj = gson.fromJson(PreferenceUtils.getUserInfo(ProfileEditActivity.this), UserInfo.class);

        setAddress();

        password = obj.getPassword();
        path = obj.getImageUrl();
        _nameText.setText(obj.getName());
        _phoneText.setText(obj.getPhone());
        _emailText.setText(obj.getEmail());
        _countryText.setText(obj.getCountry());
        _zipcodeText.setText(obj.getZipCode());
        _stateText.setText(obj.getState());
        _cityText.setText(obj.getCity());
        _addressText.setText(obj.getAddress());
        mAutocomplete.setText(obj.getAddress() +", " + obj.getCity() +", " + obj.getState() +", " + obj.getCountry());
        setPhoto(obj.getImageUrl());

    }

    public void save() {
        Log.d(TAG, "SAVE");

        if (!validate()) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(ProfileEditActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Changing Information...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String phone = _phoneText.getText().toString();
        String email = _emailText.getText().toString();
//        String password = _newPwdText.getText().toString();
        String country = _countryText.getText().toString();
        String zipCode = _zipcodeText.getText().toString();
        String state = _stateText.getText().toString();
        String city = _cityText.getText().toString();
        String address = _addressText.getText().toString();

        // TODO: Implement your own save logic here.

        UserInfo newUserInfo = new UserInfo(path, name, phone, email, password, country, zipCode, state, city, address);

        Gson gson = new Gson();
        String newJson = gson.toJson(newUserInfo);
        PreferenceUtils.setUserInfo(ProfileEditActivity.this, newJson);


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        finish();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    private void setPhoto(String path) {

        if (!TextUtils.isEmpty(path)) {

            Glide.with(ProfileEditActivity.this).load(path).asBitmap().centerCrop()
                    .placeholder(R.drawable.loading)
                    .into(new BitmapImageViewTarget(userImage) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(ProfileEditActivity.this.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            userImage.setImageDrawable(circularBitmapDrawable);
                        }
                    });


        } else {
            userImage.setImageResource(R.drawable.illust_029);
        }

    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String phone = _phoneText.getText().toString();
        String email = _emailText.getText().toString();
//        String newpassword = _newPwdText.getText().toString();
//        String oldpassword = _oldPwdText.getText().toString();
        String country = _countryText.getText().toString();
        String zipCode = _zipcodeText.getText().toString();
        String state = _stateText.getText().toString();
        String city = _cityText.getText().toString();
        String address = _addressText.getText().toString();

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

//        if (newpassword.isEmpty() || newpassword.length() < 4 || newpassword.length() > 10) {
//            _newPwdText.setError("between 4 and 10 alphanumeric characters");
//            valid = false;
//        } else {
//            _newPwdText.setError(null);
//        }
//        if (oldpassword.isEmpty() || !oldpassword.equals(password)) {
//            _oldPwdText.setError("password doesn't match");
//            valid = false;
//        } else {
//            _oldPwdText.setError(null);
//        }
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
            _addressText.setError("address can't be empty");
            valid = false;
        } else {
            _addressText.setError(null);
        }

        return valid;
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
//        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();

        }
        return (super.onOptionsItemSelected(menuItem));
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

    private void setAddress(){
        mAutocomplete.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                mAutocomplete.getDetailsFor(place, new DetailsCallback() {
                    @Override
                    public void onSuccess(final PlaceDetails details) {
                        Log.d("test", "details " + details);
                        _addressText.setText(details.name);
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
