package com.learnandroid.myweatherapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

public class MainActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {
    private static final String TAG = "MainActivity";

    public static final int PERMISSION_ID = 44;

    private TextView textPlace, textTime, textDate, textTemperature,bottom_sheet_text;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private CoordinatorLayout coordinatorLayout;
    private RequestQueue requestQueue;
    private FusedLocationProviderClient mFusedLocationClient;
    private final List<WeatherData> dataListForAdapter = new ArrayList<>();
    private final WeatherData homeWeather = new WeatherData();
    BroadcastReceiver _broadcastReceiver;
    private ProgressBar progressBar;

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("HH:mm");


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("VisibleForTests")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        NetworkStateReceiver networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));


        coordinatorLayout = findViewById(R.id.coordinator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        textPlace = findViewById(R.id.place);
        textDate = findViewById(R.id.date);
        recyclerView = findViewById(R.id.recycler);
        textTime = findViewById(R.id.time);
        textTemperature = findViewById(R.id.centigrade);
        imageView = findViewById(R.id.image);
        bottom_sheet_text = findViewById(R.id.bottom_sheet_text);
        setBackground();

        View bottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        requestQueue = Volley.newRequestQueue(this);
        mFusedLocationClient = new FusedLocationProviderClient(this);
        getLastLocation();
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            String string = location.getLatitude() + "," + location.getLongitude();
                            Log.d(TAG, "onComplete: getData from ON_COMPLETE");
                            getData(string);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            String string = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            Log.d(TAG, "onResume: getLastLocation");
            getLastLocation();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        _broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    textTime.setText(_sdfWatchTime.format(new Date()));
            }
        };

        registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (_broadcastReceiver != null)
            unregisterReceiver(_broadcastReceiver);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getData(String query) {
        WeatherData weatherToday = new WeatherData();
        String url = "https://api.weatherapi.com/v1/forecast.json?key=275c811f025f448ca90180845211010%20&q=" + query + "&days=5&aqi=yes&alerts=yes";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "getData: Got Response: " + response.toString());
                    try {
                        parseJson(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.d(TAG, "getData: Error: " + error.getMessage());
                });
        requestQueue.add(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void parseJson(JSONObject response) throws JSONException {

        dataListForAdapter.clear();
        JSONObject localData = response.getJSONObject("location");
        JSONObject currentData = response.getJSONObject("current");
        JSONObject conditionData = currentData.getJSONObject("condition");
        JSONObject forecast = response.getJSONObject("forecast");
        JSONArray forecast_day = forecast.getJSONArray("forecastday");

        homeWeather.setPlace(localData.getString("name"));
        String text = conditionData.getString("text");
        homeWeather.setCondition(text);
        homeWeather.setIcon(conditionData.getString("icon").substring(39,42));

        homeWeather.setWind_kph(currentData.getString("wind_kph"));
        homeWeather.setPressure_in(currentData.getString("pressure_in"));
        homeWeather.setHumidity(currentData.getString("humidity"));
        homeWeather.setFeelsLike_c(currentData.getString("feelslike_c"));
        homeWeather.setDate((forecast_day.getJSONObject(0)).getString("date"));

        for (int i = 1; i < forecast_day.length(); i++) {

            JSONObject forecastDay = forecast_day.getJSONObject(i);

            JSONObject day = forecastDay.getJSONObject("day");
            JSONObject condition = day.getJSONObject("condition");
            String date = forecastDay.getString("date");
            Log.d(TAG, "parseJson: Date is " + date);
            Log.d(TAG, "parseJson: value of i is " + i + " $ totle length " + forecast_day.length());
            String max = day.getString("maxtemp_c");
            String min = day.getString("mintemp_c");
            String report = condition.getString("text");
            String icon = "a_"+condition.getString("icon").substring(39,42);
            Log.d(TAG, "parseJson: icon_name: "+icon);
            WeatherData listData = new WeatherData();

            listData.setMin_temp(min);
            listData.setMx_temp(max);
            listData.setCondition(report);
            listData.setDate(date);
            listData.setIcon("drawable/"+icon);
            dataListForAdapter.add(listData);
            Log.d(TAG, "parseJson: Size of list " + dataListForAdapter.size());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        ForecastAdapter adapter = new ForecastAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
        adapter.setList(dataListForAdapter);
        setUpHome(homeWeather);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setUpHome(WeatherData weatherData) {

        textDate.setText(weatherData.getDate());
        textPlace.setText(weatherData.getPlace());
        textTemperature.setText(weatherData.getFeelsLike_c() + "\u00B0"+"\n"+weatherData.getCondition());
        String name = "drawable/a_"+weatherData.getIcon();
        int id = getResources().getIdentifier(name,"id",this.getPackageName());
       imageView.setImageResource(id);
       String bottom = "\nLocation:  "+weatherData.getPlace()+
               "\n\nDate:  "+weatherData.getDate()+
               "\n\nFeels Like:  "+weatherData.getFeelsLike_c()+"\u00B0"+
               "\n\nReport:  "+weatherData.getCondition()+
               "\n\nPressure:  "+weatherData.getPressure_in()+
               "\n\nWind Speed:  "+weatherData.getWind_kph()+
               "\n\nHumidity: "+weatherData.getHumidity();
       bottom_sheet_text.setText(bottom);
       progressBar.setVisibility(View.GONE);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    public void setBackground() {
        LocalTime localTime = LocalTime.now(ZoneId.systemDefault());
        String time = localTime.toString().substring(0, 5);
        textTime.setText(time);
        int value = Integer.parseInt(time.substring(0, 2));
        if (value <= 12) {
            coordinatorLayout.setBackground(getDrawable(R.drawable.sunny_background));
        } else if (value <= 18) {
            coordinatorLayout.setBackground(getDrawable(R.drawable.day_background));
        } else {
            coordinatorLayout.setBackground(getDrawable(R.drawable.night_background));
        }
    }

    public void windowSettings() {
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().
                setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    @Override
    public void networkAvailable() {
        Toast.makeText(this, "Online...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void networkUnavailable() {
        Toast.makeText(this, "Connect to the Internet", Toast.LENGTH_LONG).show();
    }
}