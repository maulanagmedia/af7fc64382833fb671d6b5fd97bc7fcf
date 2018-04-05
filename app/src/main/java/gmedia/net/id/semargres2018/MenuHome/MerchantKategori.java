package gmedia.net.id.semargres2018.MenuHome;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.support.v7.widget.SearchView;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gmedia.net.id.semargres2018.MenuNearby.Adapter.ListMerchantAdapter;
import gmedia.net.id.semargres2018.MenuNearby.SystemUtils.SuggestionPref;
import gmedia.net.id.semargres2018.R;
import gmedia.net.id.semargres2018.Utils.Inisialisasi;
import gmedia.net.id.semargres2018.Utils.ServerURL;

public class MerchantKategori extends AppCompatActivity implements LocationListener {

    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private MenuItem mSearchItem;
    private Menu menu;
    private ArrayList<String> suggestionList = new ArrayList<>();
    private SuggestionPref suggestionPref;
    private SimpleCursorAdapter mAdapter;
    private Gson gson;

    // Location
    private double latitude, longitude;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private Location location;
    private final int REQUEST_PERMISSION_COARSE_LOCATION = 2;
    private final int REQUEST_PERMISSION_FINE_LOCATION = 3;
    public boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    private String TAG = "Nearby";
    private String address0 = "";
    private boolean refreshMode = false;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private LocationSettingsRequest mLocationSettingsRequest;
    private SettingsClient mSettingsClient;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private Boolean mRequestingLocationUpdates;
    private Location mCurrentLocation;
    private ArrayList<HashMap<String, String>> listOffer;
    private String offerImage = "";
    private ProgressBar pbLoading;
    private ArrayList<CustomItem> merchantList;
    private String keyword = "";
    private int startIndex = 0;
    private final int count = 10;
    private int offerIndex = 0;
    private ListView lvNearbyMerchant;
    private View footerList;
    private ListMerchantAdapter adapter;
    private boolean isLoading = false;
    private ArrayList<CustomItem> moreList;
    private SearchView searchView;
    private Toolbar mToolbar;
    private String idKat = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_kategori);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_back_white));
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;

        // getLocation update by google
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mSettingsClient = LocationServices.getSettingsClient(context);
        mRequestingLocationUpdates = false;

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        initUI();

    }

    private void initUI() {

        lvNearbyMerchant = (ListView) findViewById(R.id.lv_nearby_merchant);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        final String[] from = new String[] {"merchant"};
        final int[] to = new int[] {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(context,
                R.layout.list_sugestion,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        suggestionList = new ArrayList<>();
        suggestionPref = new SuggestionPref(context);
        gson = new Gson();
        try {
            suggestionList = gson.fromJson(suggestionPref.getSuggestion(), new TypeToken<ArrayList<String>>(){}.getType());
        }catch (Exception e){
            e.printStackTrace();
            suggestionList = new ArrayList<>();
        }

        // inisialisasi index
        keyword = "";
        startIndex = 0;
        isLoading = false;

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            idKat = bundle.getString("id", "");
            String title = bundle.getString("title","");
            if(title.length() > 0 ) setTitle(title);
        }

        //get location
        initLocation();

        initEvent();
    }

    private void initEvent() {

        lvNearbyMerchant.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int count = lvNearbyMerchant.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvNearbyMerchant.getLastVisiblePosition() >= count - threshold && !isLoading) {

                        isLoading = true;
                        lvNearbyMerchant.addFooterView(footerList);
                        startIndex += count;
                        getMoreData();
                        //Log.i(TAG, "onScroll: last ");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                onLocationChanged(mCurrentLocation);
            }
        };
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener((Activity)context, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener((Activity) context, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        mRequestingLocationUpdates = true;
                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location clocation) {

                                        if (clocation != null) {

                                            location = clocation;
                                            refreshMode = true;
                                            onLocationChanged(location);
                                        }else{
                                            location = getLocation();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener((Activity) context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult((Activity) context, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                                //refreshMode = false;
                        }

                        //get Location
                        location = getLocation();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CHECK_SETTINGS){

            if(resultCode == Activity.RESULT_CANCELED){

                mRequestingLocationUpdates = false;
            }else if(resultCode == Activity.RESULT_OK){

                startLocationUpdates();
            }

        }
    }

    private void initLocation() {

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        setCriteria();
        latitude = 0;
        longitude = 0;
        location = new Location("set");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        refreshMode = true;
        updateAllLocation();

        /*btnResetPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refreshMode = true;
                //location = getLocation();
                updateAllLocation();
            }
        });*/
    }

    private void updateAllLocation(){
        mRequestingLocationUpdates = true;
        startLocationUpdates();
    }

    public Location getLocation() {

        try {

            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.v("isGPSEnabled", "=" + isGPSEnabled);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

            if (isGPSEnabled == false && isNetworkEnabled == false) {
                // no network provider is enabled
                Toast.makeText(context, "Cannot identify the location.\nPlease turn on GPS or turn on your data.",
                        Toast.LENGTH_LONG).show();

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    location = null;

                    // Granted the permission first
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_FINE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_FINE_LOCATION);
                        }
                        return null;
                    }

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            //onLocationChanged(location);
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");

                    if (locationManager != null) {
                        Location bufferLocation = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (bufferLocation != null) {

                            location = bufferLocation;
                        }
                    }
                }else{
                    //Toast.makeText(context, "Turn on your GPS for better accuracy", Toast.LENGTH_SHORT).show();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(location != null && location.getLongitude() != 0 && location.getLatitude() != 0){
            onLocationChanged(location);
        }

        return location;
    }

    public void setCriteria() {
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        provider = locationManager.getBestProvider(criteria, true);
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nearby_menu, menu);
        mSearchItem = menu.findItem(R.id.m_search);

        MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Called when SearchView is collapsing
                if (mSearchItem.isActionViewExpanded()) {
                    animateSearchToolbar(1, false, false);
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Called when SearchView is expanding
                animateSearchToolbar(1, true, true);
                return true;
            }
        });

        return true;
    }

    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {

        mToolbar.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        //mDrawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(context, R.color.quantum_grey_600));

        if (show) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int width = mToolbar.getWidth() -
                        (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                        ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar,
                        isRtl(context.getResources()) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2, 0.0f, (float) width);
                createCircularReveal.setDuration(250);
                createCircularReveal.start();
            } else {
                TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, (float) (-mToolbar.getHeight()), 0.0f);
                translateAnimation.setDuration(220);
                mToolbar.clearAnimation();
                mToolbar.startAnimation(translateAnimation);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int width = mToolbar.getWidth() -
                        (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                        ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar,
                        isRtl(getResources()) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2, (float) width, 0.0f);
                createCircularReveal.setDuration(250);
                createCircularReveal.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mToolbar.setBackgroundColor(getThemeColor(context, R.attr.colorPrimary));
                        //mDrawerLayout.setStatusBarBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimaryDark));
                    }
                });
                createCircularReveal.start();
            } else {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (-mToolbar.getHeight()));
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setDuration(220);
                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mToolbar.setBackgroundColor(getThemeColor(context, R.attr.colorPrimary));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mToolbar.startAnimation(animationSet);
            }
            //mDrawerLayout.setStatusBarBackgroundColor(ThemeUtils.getThemeColor(MainActivity.this, R.attr.colorPrimaryDark));
        }
    }

    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.m_search));
        searchView.setQueryHint(context.getResources().getString(R.string.search_merchant));
        searchView.setSuggestionsAdapter(mAdapter);
        // Getting selected (clicked) item suggestion
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {

                Cursor c = mAdapter.getCursor();
                String key = "";
                if(c.moveToPosition(position)) {
                    key = c.getString(1);
                }

                //String key = suggestionList.get(position);
                searchView.setQuery(key, true);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {

                Cursor c = mAdapter.getCursor();
                String key = "";
                if(c.moveToPosition(position)) {
                    key = c.getString(1);
                }
                searchView.setQuery(key, true);
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                updateSuggestion(s);
                iv.hideSoftKey(context);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                if(s.length() == 0){
                    updateSuggestion(s);
                }else{
                    populateAdapter(s);
                }

                return false;
            }
        });

        return true;
    }

    private void populateAdapter(String query) {

        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "merchant" });

        if(suggestionList != null){

            for (int i = 0; i< suggestionList.size() ; i++) {
                if (suggestionList.get(i).toLowerCase().contains(query.toLowerCase()))
                    c.addRow(new Object[] {i, suggestionList.get(i)});
            }
        }

        mAdapter.changeCursor(c);
    }

    private void updateSuggestion(String key){

        if(key.length() > 0){
            boolean isExist = false;
            int x = 0;
            if(suggestionList != null){
                for(String s: suggestionList){

                    if(s.equals(key)) {
                        isExist = true;
                        break;
                    }
                    x++;
                }
            }else{
                suggestionList = new ArrayList<>();
            }

            if(isExist){

                suggestionList.remove(x);
            }

            suggestionList.add(0, key);
            String data = gson.toJson(suggestionList);
            suggestionPref.saveSuggestion(data);

            /*final String[] from = new String[] {"merchant"};
            final int[] to = new int[] {android.R.id.text1};
            mAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.list_sugestion,
                    null,
                    from,
                    to,
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            if(searchView != null)searchView.setSuggestionsAdapter(mAdapter);*/
        }

        keyword = key;
        refreshMode = true;
        updateAllLocation();
        //getOffer();
    }

    @Override
    public void onLocationChanged(Location location) {

        if(refreshMode){
            refreshMode = false;
            this.location = location;
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
            //setPointMap();
            getOffer();
        }
    }

    private void getOffer(){

        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();
        ApiVolley request = new ApiVolley(context, jBody, "GET", ServerURL.getIklanHome+idKat, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);
                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    offerImage = "";
                    listOffer = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            HashMap<String, String> sumer = new HashMap<>();
                            sumer.put("icon",item.getString("icon"));
                            sumer.put("link",item.getString("link"));
                            listOffer.add(sumer);
                        }
                    }

                    getLocatedMerchant();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
            }
        });
    }

    private void getLocatedMerchant(){

        isLoading = true;
        pbLoading.setVisibility(View.VISIBLE);
        startIndex = 0;

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("jarak", Inisialisasi.jarakMerchantAll);
            jBody.put("latitude", iv.doubleToStringFull(latitude));
            jBody.put("longitude", iv.doubleToStringFull(longitude));
            jBody.put("kategori", idKat);
            jBody.put("start", String.valueOf(startIndex));
            jBody.put("count", String.valueOf(count));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getNearbyMerchant, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);
                isLoading = false;
                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    merchantList = new ArrayList<>();
                    offerIndex = 0;

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            // 0. offer
                            // 1. item
                            merchantList.add(new CustomItem("1",
                                    item.getString("id_m"),
                                    item.getString("nama"),
                                    item.getString("deskripsi"),
                                    item.getString("foto"),
                                    item.getString("alamat"),
                                    round(item.getString("jarak"),2),
                                    idKat
                            ));

                            if(listOffer.size() > 0){
                                if(i % 8 == 0){

                                    if(offerIndex >= listOffer.size()){
                                        offerIndex = 0;
                                    }

                                    HashMap<String, String> offer =  listOffer.get(offerIndex);
                                    merchantList.add(new CustomItem("0", offer.get("icon"), offer.get("link")));
                                    offerIndex++;
                                }
                            }
                        }
                    }

                    setMerchantList(merchantList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                isLoading = false;
            }
        });
    }

    private String round(String valueString, int places) {

        double value = iv.parseNullDouble(valueString);
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        double hasil = (double) tmp / factor;
        return iv.doubleToStringFull(hasil);
    }

    private void setMerchantList(List<CustomItem> tableList) {

        lvNearbyMerchant.setAdapter(null);

        if(tableList != null && tableList.size() > 0){

            Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            try {
                // this is why the minimal sdk must be JB
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    display.getRealSize(size);
                }else {
                    display.getSize(size);
                }
            } catch (NoSuchMethodError err) {
                display.getSize(size);
            }

            int menuWidth = 0;
            menuWidth = size.x;

            adapter = new ListMerchantAdapter(((Activity)context), tableList, menuWidth);
            lvNearbyMerchant.addFooterView(footerList);
            lvNearbyMerchant.setAdapter(adapter);
            lvNearbyMerchant.removeFooterView(footerList);

            lvNearbyMerchant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    CustomItem selectedItem = (CustomItem) adapterView.getItemAtPosition(i);

                    /*Intent intent = new Intent(context, DetailComplaint.class);
                    intent.putExtra("id", selectedItem.getItem1());
                    startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                }
            });
        }
    }

    private void getMoreData() {

        isLoading = true;
        moreList = new ArrayList<>();
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("jarak", Inisialisasi.jarakMerchantAll);
            jBody.put("latitude", iv.doubleToStringFull(latitude));
            jBody.put("longitude", iv.doubleToStringFull(longitude));
            jBody.put("kategori", idKat);
            jBody.put("start", String.valueOf(startIndex));
            jBody.put("count", String.valueOf(count));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getNearbyMerchant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                lvNearbyMerchant.removeFooterView(footerList);
                isLoading = false;

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = response.getJSONArray("response");
                        moreList = new ArrayList<>();
                        offerIndex = (adapter!= null) ? (adapter.getCount() % listOffer.size()) : 0;

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            // 0. offer
                            // 1. item
                            moreList.add(new CustomItem("1",
                                    item.getString("id_m"),
                                    item.getString("nama"),
                                    item.getString("deskripsi"),
                                    item.getString("foto"),
                                    item.getString("alamat"),
                                    round(item.getString("jarak"),2),
                                    idKat
                            ));

                            int currentLength = 0;
                            if(adapter != null) currentLength = adapter.getCount();
                            if((i + currentLength) % 8 == 0){

                                if(offerIndex >= listOffer.size()){
                                    offerIndex = 0;
                                }

                                HashMap<String, String> offer =  listOffer.get(offerIndex);
                                moreList.add(new CustomItem("0", offer.get("icon"), offer.get("link")));
                                offerIndex++;
                            }
                        }

                        //lvNearbyMerchant.removeFooterView(footerList);
                        if(adapter != null) adapter.addMoreData(moreList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                isLoading = false;
                lvNearbyMerchant.removeFooterView(footerList);
            }
        });
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
