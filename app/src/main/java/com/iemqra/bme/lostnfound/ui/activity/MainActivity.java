package com.iemqra.bme.lostnfound.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.iemqra.bme.lostnfound.R;
import com.iemqra.bme.lostnfound.helper.LocationHandler;
import com.iemqra.bme.lostnfound.helper.SQLiteHandler;
import com.iemqra.bme.lostnfound.helper.SessionManager;
import com.iemqra.bme.lostnfound.manager.DataManager;
import com.iemqra.bme.lostnfound.ui.fragment.PagerFragment;

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static SessionManager session;
    public static Context baseContext;
    public static Activity baseActivty;
    private static Location mLastLocation;
    // Google client to interact with Google API
    private static GoogleApiClient mGoogleApiClient;
    Locale myLocale;
    private AlertDialog.Builder builder;
    private SQLiteHandler db;

    public static LocationHandler getLocation() {
        if (ContextCompat.checkSelfPermission(baseContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(baseActivty, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        LocationHandler loc = new LocationHandler();

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            loc.setLatitude(latitude);
            loc.setLongitude(longitude);
        }
        return loc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        baseContext = getBaseContext();
        baseActivty = this;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_main_tab);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_search));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_items));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_new_item));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_followings));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final CharSequence[] items = {getString(R.string.lang_en), getString(R.string.lang_hu)};
        builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.language_selector));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.lang_en))) {
                    setLocale("");
                } else if (items[item].equals(getString(R.string.lang_hu))) {
                    setLocale("hu");
                }
            }
        });

        final ViewPager viewPager = (ViewPager) findViewById(R.id.activity_main_pager);
        final PagerFragment adapter = new PagerFragment
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        DataManager.INSTANCE.setUser(db.getUserDetails());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sync:
//                builder.show();
                return true;
            case R.id.menu_language:
                builder.show();
                return true;
            case R.id.menu_logout:
                logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setLocale(String lang) {

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (!lang.isEmpty())
            conf.locale = new Locale(lang);
        else
            conf.locale = Locale.getDefault();
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    public boolean checkInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    private void logoutUser() {
        session.setLogin(false);
        LoginManager.getInstance().logOut();

        db.deleteTables();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}