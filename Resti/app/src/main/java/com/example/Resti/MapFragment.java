package com.example.Resti;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.kml.KmlLayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    KmlLayer layer;
    GeoJsonLayer geoJsonLayer = null;
    GoogleApiClient googleApiClient;
    GoogleMap MyMap;
    Context Mcontext;
    MarkerObject passObject;
    Fragment fragment;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    LocationManager locationMana;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationCallback locationCallback;
    FusedLocationProviderClient mFusedLocationClient;
    String mLongitude, mLatitude;
    ArrayList<MarkerObject> markersArray = new ArrayList<MarkerObject>();
    ArrayList<LatLng> arrayList = new ArrayList<LatLng>();

    ArrayList<restObject> restArray;
    ArrayList<restObject> restArrayFull;

    String mFilter;

    SearchView searchView;

    public MapFragment(String longitude, String latitude, String filter) {
        mLongitude = longitude;
        mLatitude = latitude;
        mFilter = filter;
    }

    @Override
    public void onStart() {
//        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
//        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Mcontext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Mcontext = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        searchView = view.findViewById(R.id.sEXearch_view);
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.setQueryHint("What would you like to eat?");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                System.out.println(s);
                getFilter(s);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {

                return true;
            }
        });

        // Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        Mcontext = this.getContext();
        //Async Map
        supportMapFragment.getMapAsync(this::onMapReady);
        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        //When map is loaded
        MyMap = googleMap;
        MyMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        try {
            geoJsonLayer = new GeoJsonLayer(MyMap, R.raw.restgeo, getContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        geoJsonLayer.addLayerToMap();
        int counter= 0;

        if (restArray==null) {
            restArray = new ArrayList<>();
            for (GeoJsonFeature feature : geoJsonLayer.getFeatures()) {

                restObject restObject = new restObject();
                restObject.setStreet(String.valueOf(feature.getProperty("רחוב")));
                restObject.setCity(String.valueOf(feature.getProperty("עיר")));
                restObject.setStreetNumber(String.valueOf(feature.getProperty("מספר")));
                restObject.setIdNumber(String.valueOf(feature.getProperty("מספר סידויר מסעדה")));
                restObject.setRestType(String.valueOf(feature.getProperty("סוג המסעדה")));
                //System.out.println("type: " + (feature.getProperty("סוג המסעדה")));
                restObject.setOpeningHours(String.valueOf(feature.getProperty("שעות פתיחה")));
                restObject.setLon(String.valueOf(feature.getProperty("long")));
                restObject.setPrices(String.valueOf(feature.getProperty("טווח מחירים")));
                restObject.setName(String.valueOf(feature.getProperty("שם מסעדה")));
                restObject.setPhone(String.valueOf(feature.getProperty("טלפון")));
                restObject.setLat(String.valueOf(feature.getProperty("lat")));
                restObject.setLatLng(feature.getProperty("נ״צ"));
                restObject.setIndex(counter);
                counter++;

                restArray.add(restObject);
            }
            restArrayFull = new ArrayList<>(restArray);
            System.out.println("restArray.size():"+restArray.size());
        }
        else {
            System.out.println("restArray.size():"+restArray.size());
            for (restObject rs : restArray) {
                LatLng rest = new LatLng(Double.parseDouble(rs.getLat()), (Double.parseDouble(rs.getLon())));
                googleMap.addMarker(new MarkerOptions().position(rest).title("Marker in Holon").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        }
        Log.e("counter", String.valueOf(counter));

        LatLng Holon = new LatLng(32.01576999922713, 34.78718540409265);
        googleMap.addMarker(new MarkerOptions().position(Holon).title("Marker in Holon").icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Holon, 15f));

        Marker marker = MyMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(32.01576999922713, 34.78718540409265))
                        .title("HI")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.rsz_desti)));

            MyMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.setTitle(marker.getPosition().toString());

                    Intent intent = new Intent(getContext(),DetailsActivity.class);

                    double lon = marker.getPosition().longitude;
                    double lat =marker.getPosition().latitude;

                    Log.e("lon1", String.valueOf(lon));
                    Log.e("lat1", String.valueOf(lat));

                    for(restObject a: restArray) {
                            if(String.valueOf(lon).equals(a.getLat()) && String.valueOf(lat).equals(a.getLon())){
                                intent.putExtra("restName",a.getName());
                                intent.putExtra("restCity",a.getCity());
                                intent.putExtra("restStreet",a.getStreet());
                                intent.putExtra("restStreetNumber",a.getStreetNumber());
                                intent.putExtra("restOpening",a.getOpeningHours());
                                intent.putExtra("restPhone",a.getPhone());
                                intent.putExtra("lon",a.getLat()); //reversed
                                intent.putExtra("lat",a.getLon());

                            }
                    }
                    startActivity(intent);

                    return true;
                }

            });
        }
    /**
     * Parses the properties of a GeoJSON feature into a hashmap
     *
     * @param properties GeoJSON properties member
     * @return hashmap containing property values
     * @throws JSONException if the properties could not be parsed
     */
    private static HashMap<String, String> parseProperties(JSONObject properties)
            throws JSONException {
        HashMap<String, String> propertiesMap = new HashMap<String, String>();
        Iterator propertyKeys = properties.keys();
        while (propertyKeys.hasNext()) {
            String key = (String) propertyKeys.next();
            propertiesMap.put(key, properties.isNull(key) ? null : properties.getString(key));
        }
        return propertiesMap;
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationMana = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            locationMana.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, (LocationListener) getContext());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(getContext(), "" + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT);
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
                        MyMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void SaveData(){
        //SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences=this.getActivity().getSharedPreferences("sharedpreferences",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson gson=new Gson();
        String json = gson.toJson(passObject);
        editor.putString("markerObject",json);
        editor.apply();
    }
    public void LoadData(){
        SharedPreferences sharedPreferences=this.getActivity().getSharedPreferences("sharedpreferences",MODE_PRIVATE);
        Gson gson=new Gson();
        String json = sharedPreferences.getString("markerObject",null);
        Type type = new TypeToken<MarkerObject>() {}.getType();
        passObject=gson.fromJson(json,type);
        if(passObject==null)
        {
            passObject = new MarkerObject();
        }
    }

     MarkerObject findObject(LatLng location) {
        for(MarkerObject markerObject : markersArray) {
            if(markerObject.getLatLng().equals(location)) {
                return markerObject;
            }
        }
        return null;
    }
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void getFilter(String s) {
        String results = "";
        LatLng desiredPlace = null;
        List<restObject> filteredList = new ArrayList<>();

        if (s == null || s.length() == 0) {
            filteredList.addAll(restArrayFull);
        } else {
            String filterPattern = s.toLowerCase().trim();
            String type = null;
            String name = null;


            for (restObject rs : restArray) {
                if (s.equals(rs.getName())) {
                    name = s;
                    break;
                }
            }

            if (name == null) {
                switch (filterPattern) {
                    case "hamburger":
                        type = "המבורגר";
                        break;
                    case "humus":
                        type = "חומוסיה";
                        break;
                    case "pizza":
                        type = "פיצה";
                        break;
                    case "fast":
                    case "fast food":
                        type = "מזון מהיר";
                        break;
                    case "chinese":
                    case "asian":
                        type = "סיני";
                        break;
                    case "sushi":
                        type = "סושי";
                        break;
                    case "grill":
                        type = "גריל בר";
                        break;
                    case "meat":
                        type = "בשר";
                        break;
                    case "toast":
                        type = "טוסט נקניק";
                        break;
                    case "home":
                    case "like home":
                        type = "אוכל ביתי";
                        break;
                    case "coffee":
                    case "breakfast":
                        type = "קפה מסעדה";
                        break;
                    case "salad":
                        type = "סלטיה";
                        break;
                    case "schnitzel":
                        type = "שניצליה";
                        break;
                    case "borax":
                        type = "בורקסיה";
                        break;
                    case "juice":
                        type = "מיצים";
                        break;
                    case "south america":
                        type = "דרום אמריקאי";
                        break;
                    case "falafel":
                        type = "פלאפליה";
                        break;
                    case "italian":
                        type = "איטלקי";
                        break;
                    case "shawarma":
                        type = "שוארמיה";
                        break;
                    case "sandwich":
                        type = "סנדוויץ בר";
                        break;
                }

                System.out.println("restArrayFull.size(): " + restArrayFull.size());
                for (restObject rs : restArrayFull) {
                    if (rs.getRestType().equals(type)) {
                        filteredList.add(rs);
                    }
                }

                for (restObject rs : filteredList) {
                    results = results + rs.getName() + "\n";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(results)
                        .setTitle("Search results");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                results = name;
                for (restObject rs : restArray) {
                    if (results.equals(rs.getName())) {
                        desiredPlace = new LatLng(Double.parseDouble(rs.getLon()), Double.parseDouble(rs.getLat()));
                    }
                }

                //googleMap.addMarker(new MarkerOptions().position(Holon).title("Marker in Holon").icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
                if (desiredPlace != null) {
                    MyMap.moveCamera(CameraUpdateFactory.newLatLngZoom(desiredPlace, 30f));
                }
            }
        }
    }
}