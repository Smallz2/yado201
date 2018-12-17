package app.yado.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import app.yado.Adapters.PlaceAutocompleteAdapter;
import app.yado.Models.PlaceInfo;
import app.yado.R;

public class Map_Activity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    // Globals
    private static final String TAG = Map_Activity.class.getSimpleName();
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    final static int REQUEST_LOCATION = 199;
    private static final float DEFAULT_ZOOM = 16f;
    private static final LatLngBounds LAT_LNG_BOUNDS_MONTSERRAT = new LatLngBounds(new LatLng(16.735383899132405, -62.17560096116756),
            new LatLng(16.735383899132405, -62.2401309165611));

    // vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mPlaceAutoCompleteAdapter;
    private AutocompleteFilter mAutoCompleteFilter;
    private PlaceInfo mPlace;

    //widget
    private AutoCompleteTextView mSearchTextInput;
    private RelativeLayout mMyLocationButton;
    private RelativeLayout mChangeMapType;
    private TextView mTaskLocationText;
    private TextView mTaskLocationLatLong;
    private Button mLocationSelectionButton;


    // The last known geographical location of device
    private Location mLastKnownLocation;


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getFusedDeviceLocation(); // SEE FUNCTION DESCRIPTION FOR ITS USE

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true); // set marker on user's position
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .draggable(true));

                    moveCamera(latLng, 18f, "No_Title");
                }
            });

            init();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_);

        // find views
        mSearchTextInput = findViewById(R.id.id_locationSearchInput);
        mMyLocationButton = findViewById(R.id.id_myLocationButton);
        mChangeMapType = findViewById(R.id.id_changeMapType);
        mTaskLocationText = findViewById(R.id.id_taskLocationText);
        mTaskLocationLatLong = findViewById(R.id.id_taskLocationLatLong);
        mLocationSelectionButton = findViewById(R.id.id_selectLocationButton);

        getLocationPermission();

    }

    private void init() {
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                //.enableAutoManage(this, this)
                .build();

        // GeoDataClient
        GeoDataClient mGeoDataClient = Places.getGeoDataClient(this, null);


        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("MS").build();

        // try catch not need here but .builder complaining about something
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(typeFilter).build(Map_Activity.this);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        mPlaceAutoCompleteAdapter = new PlaceAutocompleteAdapter(getApplicationContext(), mGeoDataClient,
                LAT_LNG_BOUNDS_MONTSERRAT, typeFilter);

        mSearchTextInput.setAdapter(mPlaceAutoCompleteAdapter);
        mSearchTextInput.setOnItemClickListener(mAutoCompleteClickListener);

        mSearchTextInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute search method

                    geoLocate();
                    return true;
                }
                return false;
            }
        });

        mMyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: myLocation button");

                getFusedDeviceLocation();
            }
        });

        mChangeMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: change map type to satellite");

                if (!(mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID)) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

        mLocationSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: selecting location");

                finish();
            }
        });

        // hide keyboard
        hideSoftKeyboard();
    }

    private void updateTaskLocationCard (LatLng latLng) {
        String mLatLong = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
        String taskLocationAddress;

        // Set location on information card
        taskLocationAddress = getLocationFromLatLong(new LatLng(latLng.latitude, latLng.longitude));
        mTaskLocationText.setText(taskLocationAddress);

        // Send location information back in intent
        Intent intent = new Intent();
        intent.putExtra("taskLocationAddress", taskLocationAddress);
        intent.putExtra("taskLocationLatLong", latLng.toString());
        setResult(1, intent);

        mTaskLocationLatLong.setText(mLatLong);

        //todo: do more here
    }

    private String getLocationFromLatLong(LatLng latLng){
        Geocoder geocoder;

        List<Address> addresses;

        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            return addresses.get(0).getAddressLine(0);

        } catch (IOException e) {
            Log.e(TAG, "getLocationFromLatLong: " + e.getMessage());
        }

        return null;
    }

    // todo: find a cleaner way to to this
    private String getCountryCodeFromLatLong(LatLng latLng){
        Geocoder geocoder;

        List<Address> addresses;

        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            return addresses.get(0).getCountryCode();

        } catch (IOException e) {
            Log.e(TAG, "getCountryCodeFromLatLong: " + e.getMessage());
        }

        return null;
    }


    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchTextInput.getText().toString();

        Geocoder mGeoCoder = new Geocoder(this);

        List<Address> list = new ArrayList<>();

        try {
            list = mGeoCoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    /**
     * This function gets location if it was stored in another app eg. google maps.
     * otherwise it returns null
     * this is used to save power
     */
    private void getFusedDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting device current location");

        FusedLocationProviderClient mFuseLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available
         */

        try {
            if (mLocationPermissionGranted) {
                mFuseLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(final Location location) {
                                if (location != null) {
                                    Log.d(TAG, "onSuccess: location found");
                                    mLastKnownLocation = location;

                                    moveCamera(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                                } else {
                                    Log.d(TAG, "onSuccess: location is null : get location manually from provider");
                                    // mLastKnowLocation is Null so ask user to enable location service again
                                    handleEnableLocationService();

                                    //todo: mLastKnownLocation is null if user turns on device location
                                    //todo: need to get location from gps here or set a default location

                                    // moveCamera(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM);
                                }
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "onComplete: current location is null");
            Toast.makeText(this, "unable to get current location", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        String countryCode = getCountryCodeFromLatLong(latLng);
        mMap.clear();

        if (countryCode.equals("MS") ) {
            Log.d(TAG, "moveCamera: moving camera to : lat: " + latLng + ", lng: " + latLng.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            Marker marker = null;



            if (!title.equals("My Location")) {

                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .draggable(true));

            }

            // update information card on location change
            updateTaskLocationCard(latLng);
        }


        // Hide Keyboard
        hideSoftKeyboard();
    }

    private void handleEnableLocationService () {
        Log.d(TAG, "handleEnableLocationService: handle enabling device location service");

        final LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        // check if gps enabled
        if (!(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getApplicationContext()))) {
            Log.d(TAG, "handleEnableLocationService: GPS not enabled");
            enableLocation();
        }
    }

    private boolean hasGPSDevice(Context context) {
        Log.d(TAG, "hasGPSDevice: checking if device has gps");

        final LocationManager mgr = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (mgr == null) {
            return false;
        }
        final List<String> providers = mgr.getAllProviders();
        return providers != null && providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLocation() {
        Log.d (TAG, "enableLocation: enabling location, prompt user to enable service");

        if (mGoogleApiClient == null) { // check if googleApiClient is initialize
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            mGoogleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.d(TAG, "Location Error: " + connectionResult.getErrorMessage());
                        }
                    }).build();

            mGoogleApiClient.connect();
            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30 * 1000);
            mLocationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            builder.setAlwaysShow(true);

            final PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    final Status status = locationSettingsResult.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling the startResolutionForResult(),
                                // and check the result in onActivityResult
                                status.startResolutionForResult(Map_Activity.this, REQUEST_LOCATION);
                            //    finish();
                            } catch (IntentSender.SendIntentException e) {
                                // ignore error
                            }
                            break;
                    }
                }
            });
        }
    }

    private void initMap() { // todo: fix problem here
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(Map_Activity.this);
    }

    /**
     * Prompt the user for permission to use the device location
     */

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        /*
         * Request location permission, so that we can get the location of
         * device. the result of the permission request is handled by a callback,
         * onRequestPermissionResult.
         */

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            // INIT Map
            initMap();
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // if the request is cancelled, the result arrays are empty
                if(grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionResult: permission granted");
                    mLocationPermissionGranted = true;
                    // Initialize our map : location permission is granted
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchTextInput.getWindowToken(), 0);
    }

    /*
    ------------------------------------ Google places API autocomplete Suggestions ----------------------------------
     */

    private AdapterView.OnItemClickListener mAutoCompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutoCompleteAdapter.getItem(position);
            final String mPlaceId = item.getPlaceId();

            PendingResult<PlaceBuffer> mPlaceResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, mPlaceId);

            mPlaceResult.setResultCallback(mUpdatePlaceDetailsCallback);

        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release(); // to prevent memory leak
                return;
            }

            final Place place = places.get(0);

            try {
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setLatLng(place.getLatLng());

                Log.d(TAG, "onResult: place details: " + mPlace.toString());
            } catch (NullPointerException e) {
                Log.e(TAG, "onResult NullPointerException: " + e.getMessage());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace.getName());

            places.release(); // to prevent memory leak
        }
    };
}
