package com.ksproject.krishop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.ksproject.krishop.R;

public class SplashScreen extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
//        new SM_AsyncTaskForSetData().execute();

        new Handler().postDelayed(new Runnable() {



            @Override
            public void run() {

                Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

//    class SM_AsyncTaskForSetData extends AsyncTask<String, String, Boolean> {
//
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//
//            try {
//
//                JSONObject obj = new JSONObject(new JSONLoader().loadJSONFromAsset(SplashScreen.this, "json/main.json"));
//
//
//                if (obj != null && obj.has(Constant.JSON_ARRAY_NAME)) {
//
//                    if (AppData.allPlace != null && AppData.allPlace.size() > 0) {
//                        AppData.allPlace.clear();
//                    }
//                    if (AppData.allAttraction != null && AppData.allAttraction.size() > 0) {
//                        AppData.allAttraction.clear();
//                    }
//                    if (AppData.allHotel != null && AppData.allHotel.size() > 0) {
//                        AppData.allHotel.clear();
//                    }
//
//                    AppData.allPlace = JsonParser.getPlaces(obj.getString(Constant.JSON_ARRAY_NAME));
//
//                    for (int i = 0; i < AppData.allPlace.size(); i++) {
//
//                        Places product = AppData.allPlace.get(i);
//                        AppData.allAttraction.addAll(product.getAttractions());
//                        AppData.allHotel.addAll(product.getHotels());
//                    }
//
//                    return true;
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            return false;
//        }
//
//        protected void onPostExecute(Boolean result) {
//            super.onPostExecute(result);
//
//            if (result != null && result) {
//
//                        new Handler().postDelayed(new Runnable() {
//
//
//
//            @Override
//            public void run() {
//
//                Intent i = new Intent(SplashScreen.this, MainActivity.class);
//                startActivity(i);
//                overridePendingTransition(R.anim.push_left_in,
//                        R.anim.push_left_out);
//                finish();
//            }
//        }, SPLASH_TIME_OUT);
//            }
//
//        }
//    }
}
