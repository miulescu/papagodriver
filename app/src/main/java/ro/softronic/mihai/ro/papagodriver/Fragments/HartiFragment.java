package ro.softronic.mihai.ro.papagodriver.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import ro.softronic.mihai.ro.papagodriver.Activities.TheApp;
import ro.softronic.mihai.ro.papagodriver.Model.Order;
import ro.softronic.mihai.ro.papagodriver.R;

;

public class HartiFragment extends Fragment  implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener
//        GoogleMap.OnMarkerClickListener
{

public  static int MY_PERMISSION_ACCESS_FINE_LOCATION = 11;
public  static int MY_PERMISSION_ACCESS_COURSE_LOCATION = 11;

private static final String ARG_LOCATION = "arg.location";


// ListView stuff
private View mTransparentHeaderView;
private View mSpaceView;
private View mTransparentView;
private View mWhiteSpaceView;


private LatLng mLocation;
private Marker mLocationMarker;

private SupportMapFragment mMapFragment;

private GoogleMap mMap;
private boolean mIsNeedLocationUpdate = true;

private GoogleApiClient mGoogleApiClient;
private LocationRequest mLocationRequest;

private Activity activity;

public static int MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 11;

private IntentFilter intentFilter;

        MyBroadcastReceiver receiver;
    private DelPinBroadcastReceiver delpinMessageReceiver;

    private ArrayList<Marker> markers;

private Realm myRealm;
//    private OrderAdapter mAdapter;

    public static final int USE_ADDRESS_NAME = 1;
    public static final int USE_ADDRESS_LOCATION = 2;

    int fetchType = USE_ADDRESS_LOCATION;

    private static final String TAG = "MAIN_ACTIVITY_ASYNC";
    private TextView adresa_livrare;



public HartiFragment() {
        }

//public static HartiFragment newInstance(LatLng location) {
//        HartiFragment f = new HartiFragment();
//        Bundle args = new Bundle();
//        args.putParcelable(ARG_LOCATION, location);
//        f.setArguments(args);
//        return f;
//        }


private class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String longitude = extras.getString("longitude");
        String latitude = extras.getString("latitude");
        String status = extras.getString("id1");

        myRealm.beginTransaction();
        Order order1 = myRealm.createObject(Order.class);

        // Set its fields
//            order1.setId(5);
        order1.setRestaurant("Temple Bar");
        order1.setStatus(status);
        order1.setClient_latitude(latitude);
        order1.setClient_longitude(longitude);

        order1.setExpirationTime(System.currentTimeMillis() + 100000);

        myRealm.commitTransaction();

        //put marker on map
        LatLng client = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        final  Marker marker = mMap.addMarker(new MarkerOptions().position(client)
                .title("Client calling"));

        new GeocodeAsyncTask().execute(marker.getPosition());

        markers.add(marker);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker m : markers) {
            builder.include(m.getPosition());
        }
        builder.include(mLocationMarker.getPosition());
        LatLngBounds bounds = builder.build();
        int padding = 150; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.moveCamera(cu);


//        final Handler handler = new Handler();
//
//        final long startTime = SystemClock.uptimeMillis();
//        final long duration = 2000;
//
//        Projection proj = mMap.getProjection();
//        final LatLng markerLatLng = marker.getPosition();
//        Point startPoint = proj.toScreenLocation(markerLatLng);
//        startPoint.offset(0, -100);
//        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
//
//        final Interpolator interpolator = new BounceInterpolator();
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - startTime;
//                float t = interpolator.getInterpolation((float) elapsed / duration);
//                double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
//                double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
//                marker.setPosition(new LatLng(lat, lng));
//
//                if (t < 1.0) {
//                    // Post again 16ms later.
//                    handler.postDelayed(this, 16);
//                }
//            }
//        });

        // update your textView in the main layout
//        Toast.makeText(getActivity(),longitude,Toast.LENGTH_LONG)
//                .show();
    }
}

    private class DelPinBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extras = intent.getExtras();
            if (extras.getString("clear_all") == "OK"){
                for (int i = 0; i < markers.size(); i++) {
                        markers.get(i).remove();
                        markers.remove(i);
                }
                //delete from realm
                myRealm.beginTransaction();
                RealmResults<Order> results = myRealm.where(Order.class).findAll();
                results.clear();
                myRealm.commitTransaction();
            } else {
                String longitude = extras.getString("longitude");
                String latitude = extras.getString("latitude");
                LatLng position_to_del = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                for (int i = 0; i < markers.size(); i++) {
                    if (markers.get(i).getPosition().equals(position_to_del)) {
                        markers.get(i).remove();
                        markers.remove(i);
                    }
                }
            }
            //de selectat ultimul tag
            if (markers.size() == 1){
                adresa_livrare.setText("");
            }
    }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_harti, container, false);
        this.activity = getActivity();
        adresa_livrare = (TextView)rootView.findViewById(R.id.adresa_livrare);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        mLocation = this.getArguments().getParcelable(ARG_LOCATION);
//        if (mLocation == null) {
//            mLocation = getLastKnownLocation(false);
//        }
//        ArrayList<Marker> markers = new ArrayList<Marker>();
        mLocation = getLastKnownLocation(false);
        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapContainer, mMapFragment, "map");
        fragmentTransaction.commit();

        //test
//        ArrayList<String> testData = new ArrayList<String>(100);
//        for (int i = 0; i < 100; i++) {
//            testData.add("Item " + i);
//        }
        // show white bg if there are not too many items


//        mHeaderAdapter = new HeaderAdapter(getActivity(), testData, this);
//        mListView.setItemAnimator(null);
//        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mListView.setLayoutManager(layoutManager);
//        mListView.setAdapter(mHeaderAdapter);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMapFragment.getMapAsync(this);
            // Check if we were successful in obtaining the map.
//            if (mMap != null) {
//                mMap.setMyLocationEnabled(false);
//                mMap.getUiSettings().setCompassEnabled(false);
//                mMap.getUiSettings().setZoomControlsEnabled(false);
//                mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                LatLng update = getLastKnownLocation();
//                if (update != null) {
//                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(update, 11.0f)));
//                }
//                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                    @Override
//                    public void onMapClick(LatLng latLng) {
//                        mIsNeedLocationUpdate = false;
//                        moveToLocation(latLng, false);
//                    }
//                });
//            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // In case Google Play services has since become available.
        setUpMapIfNeeded();
        //register broadcastreceiver
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.my.app.onMessageReceived");
        receiver = new MyBroadcastReceiver();
        getActivity().registerReceiver(receiver, intentFilter);
        delpinMessageReceiver = new DelPinBroadcastReceiver();
        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(delpinMessageReceiver,
                new IntentFilter("delpin_IntentFilter_string"));

//        BroadcastReceiver delpinMessageReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent != null) {
//                    String latitude = intent.getStringExtra("latitude");
//                    String longitude = intent.getStringExtra("longitude");
//                    LatLng position_to_del = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
//                    for (int i = 0; i < markers.size(); i++) {
//                        if (markers.get(i).getPosition().equals(position_to_del)) {
//                            markers.remove(i);
//                        }
//
//                        //Get all your data from intent and do what you want
//                    }
//                }
//            }
//        };




//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this.getActivity())
//                .name(Realm.DEFAULT_REALM_NAME)
//                .schemaVersion(0) 44.341361, 23.775308
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        try {
//            myRealm = getInstance(realmConfiguration);
//        } catch (RealmMigrationNeededException e){
//            try {
//                Realm.deleteRealm(realmConfiguration);
//                //Realm file has been deleted.
//                myRealm =  getInstance(realmConfiguration);
//            } catch (Exception ex){
//                throw ex;
//                //No Realm file to remove.
//            }
//        }
        myRealm = Realm.getDefaultInstance();
//        try {
//            myRealm = Realm.getDefaultInstance();
//        } catch (RealmMigrationNeededException e){
//            try {
//                Realm.deleteRealm();
//                //Realm file has been deleted.
//                myRealm =  getInstance(realmConfiguration);
//            } catch (Exception ex){
//                throw ex;
//                //No Realm file to remove.
//            }
//        }
//        myRealm = getInstance(getActivity());
//        myRealm.beginTransaction();
//        RealmResults<Order> results = myRealm.where(Order.class).findAll();
//        results.clear();
//        // Create an object
////        Order order1 = myRealm.createObject(Order.class);
////
////        // Set its fields
////        order1.setId(4);
////        order1.setRestaurant("Temple Bar");
////        order1.setStatus("cooking");
//
//        myRealm.commitTransaction();
//
//        if(mAdapter == null) {
//            List<School> schools = null;
//            try {
//                schools = loadSchools();
//                RealmPath =  realm.getPath();
//                //Log.d( tag, String.valueOf(RealmPath));
//                Log.d( tag, String.valueOf(schools));
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }

//        RealmResults<Order> orders = myRealm.where(Order.class).findAll();
//        ArrayList<Order> listOrders = new ArrayList(myRealm.where(Order.class).findAll());
//        System.out.println("==> For Loop Example.");
//        for (int i = 0; i < listOrders.size(); i++) {
//            LatLng client = new LatLng(Double.parseDouble(listOrders.get(i).getClient_latitude()),
//                                       Double.parseDouble(listOrders.get(i).getClient_longitude()));
//            final  Marker marker = mMap.addMarker(new MarkerOptions().position(client)
//                    .title("Client calling"));
//
//        }

    }

    @Override
    public void onPause(){
        super.onPause();
        this.activity.unregisterReceiver(receiver);
//        this.activity.unregisterReceiver(delpinMessageReceiver);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
        markers = new ArrayList<Marker>();
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
//        myRealm.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        myRealm.close();
    }

    private LatLng getLastKnownLocation() {
        return getLastKnownLocation(true);
    }

    private LatLng getLastKnownLocation(boolean isMoveMarker) {
        LocationManager lm = (LocationManager) TheApp.getAppContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        String provider = lm.getBestProvider(criteria, true);
        if (provider == null) {
            return null;
        }
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
        }
        Location loc = lm.getLastKnownLocation(provider);
        if (loc != null) {
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            if (isMoveMarker) {
                moveMarker(latLng);
            }
            return latLng;
        }
        return null;
    }

    private void moveMarker(LatLng latLng) {
        if (mLocationMarker != null) {
            mLocationMarker.remove();
        }
        mLocationMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_my_location))
                .position(latLng).anchor(0.5f, 0.5f));
//        markers.add(mLocationMarker);

    }

//    @Override
//    public boolean onMarkerClick(final Marker marker) {
//        for (Marker m : markers) {
//           if (m.equals(marker)){
//                // put address on textview
////                   new GeocodeAsyncTask().execute(m.getPosition());
////               marker.showInfoWindow();
//
//              break;
//            }
//
//        }
//        return true;
//    }

    private void moveToLocation(Location location) {
        if (location == null) {
            return;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveToLocation(latLng);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, 14.0f)));
    }

    private void moveToLocation(LatLng latLng) {
        moveToLocation(latLng, true);
    }

    private void moveToLocation(LatLng latLng, final boolean moveCamera) {
        if (latLng == null) {
            return;
        }
        moveMarker(latLng);
        mLocation = latLng;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mIsNeedLocationUpdate) {
            moveToLocation(location);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(1);
        if ( ContextCompat.checkSelfPermission( this.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this.getActivity(), new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }

        if ( ContextCompat.checkSelfPermission( this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

//                checkSelfPermission("You have to accept to enjoy the most hings in this app");
            } else {


                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);

            }
        }
        mMap.setMyLocationEnabled(false);
//        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getActivity());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getActivity());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getActivity());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        LatLng update = getLastKnownLocation();
        if (update != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(update, 11.0f)));
        }


//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                mIsNeedLocationUpdate = false;
//                moveToLocation(latLng, false);
//            }
//        });
    }


    class GeocodeAsyncTask extends AsyncTask<LatLng, Void, Address> {

        String errorMessage = "";



        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Address doInBackground(LatLng...  latlngs) {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = null;

//            if(fetchType == USE_ADDRESS_NAME) {
//                String name = addressEdit.getText().toString();
//                try {
//                    addresses = geocoder.getFromLocationName(name, 1);
//                } catch (IOException e) {
//                    errorMessage = "Service not available";
//                    Log.e(TAG, errorMessage, e);
//                }
//            }
            if(fetchType == USE_ADDRESS_LOCATION) {
                LatLng loc = latlngs[0];
                double latitude = loc.latitude;
                double longitude = loc.longitude;

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException ioException) {
                    errorMessage = "Service Not Available";
                    Log.e(TAG, errorMessage, ioException);
                } catch (IllegalArgumentException illegalArgumentException) {
                    errorMessage = "Invalid Latitude or Longitude Used";
                    Log.e(TAG, errorMessage + ". " +
                            "Latitude = " + latitude + ", Longitude = " +
                            longitude, illegalArgumentException);
                }
            }
            else {
                errorMessage = "Unknown Type";
                Log.e(TAG, errorMessage);
            }

            if(addresses != null && addresses.size() > 0)
                return addresses.get(0);

            return null;
        }

        protected void onPostExecute(Address address) {
            if(address == null) {
            // scrie fara adresa
                adresa_livrare.setText("Latitude: " + address.getLatitude() + "\n" +
                        "Longitude: " + address.getLongitude() + "\n" +
                        "Address: " + "nu am gasit-o");
            }
            else {
                String addressName = "";
                for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressName += " --- " + address.getAddressLine(i);
                }
                adresa_livrare.setText("Address: " + addressName);
                markers.get(markers.size() -1).setSnippet(addressName + "\n" + "PAPAMIA");

            }
        }
    }
}