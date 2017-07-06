package com.smartmux.weatherapp.utils;

/**
 * Constant values reused in this sample.
 */
public final class Constants {
	public static int SPLASH_TIME_OUT = 2000;
    public static final int SUCCESS_RESULT = 0;

    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME =
      "com.google.android.gms.location.sample.locationaddress";

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    
    public static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    public static String IMG_URL = "http://openweathermap.org/img/w/";

	
    public static String BASE_FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?mode=json&q=";

    public static final String TAG = "fetch-address-intent-service";
    
    public static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    public static final String LOCATION_ADDRESS_KEY = "location-address";
    public static final String PREFS_NAME = "Weather_App";
    public static final String TODAY_PREFS_KEY = "weather";
    public static final String WEEK_PREFS_KEY = "weatherforecast";
    public static final String ADDRESS_PREFS_KEY = "address";
    
}
