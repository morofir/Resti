package com.example.Resti;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.geojson.GeoJsonLayer;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    TextView titleTV,phoneTV,addressTV, openingTV,getPhoneTV;
    MapView mapView;
    MarkerObject passObject;
    GoogleMap map;
    ImageButton wazebtn;
    String baseWazeUrl = "https://waze.com/ul?ll=";
    MarkerObject markerObject;
    String searchString;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        LoadData();

        titleTV=findViewById(R.id.title_text);
        mapView=(MapView)findViewById(R.id.map_view);
        addressTV=findViewById(R.id.address_tv);
        openingTV =findViewById(R.id.opening_h);
        mapView.onCreate(savedInstanceState);
        wazebtn=findViewById(R.id.waze_btn);
        getPhoneTV= findViewById(R.id.phone);

        mapView.getMapAsync(this);
        String name = getIntent().getStringExtra("restName");
        String restCity = getIntent().getStringExtra("restCity");
        String restStreet = getIntent().getStringExtra("restStreet");
        String restStreetNumber = getIntent().getStringExtra("restStreetNumber");
        String restOpening = getIntent().getStringExtra("restOpening");
        String restPhone = getIntent().getStringExtra("restPhone");
        String lon = getIntent().getStringExtra("lon");
        String lat = getIntent().getStringExtra("lat");
        addressTV.setText(restCity+": "+restStreet+ " "+restStreetNumber);
        openingTV.setText(restOpening);
        getPhoneTV.setText(restPhone);

        titleTV.setText(name);


        wazebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchString = baseWazeUrl+lat+", "+lon+"&z=10";
                try
                {
                    // Launch Waze to look for our address:
                    Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( searchString ) );
                    startActivity( intent );
                }
                catch ( ActivityNotFoundException ex  )
                {
                    // If Waze is not installed, open it in Google Play:
                    Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                    startActivity(intent);
                }

            }
        });
    }

    public void SaveData(){
        //SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson gson=new Gson();
        String json = gson.toJson(passObject);
        editor.putString("markerObject",json);
        editor.apply();
    }
    public void LoadData(){
        SharedPreferences sharedPreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
        Gson gson=new Gson();
        String json = sharedPreferences.getString("markerObject",null);
        Type type = new TypeToken<MarkerObject>() {}.getType();
        passObject=gson.fromJson(json,type);
        if(passObject==null)
        {
            passObject = new MarkerObject();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        String lon = getIntent().getStringExtra("lon");
        String lat = getIntent().getStringExtra("lat");
        double d1=Double.parseDouble(lon);
        double d2=Double.parseDouble(lat);

//        LatLng SelectedMarker = new LatLng(32.017136,34.745441);
        LatLng SelectedMarker = new LatLng(d2,d1);


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SelectedMarker,15f));



        //get data from geojson
        GeoJsonLayer layer = null;
        try {
            layer = new GeoJsonLayer(map, R.raw.restgeo , getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        layer.addLayerToMap();


        mapView.onResume();
//        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.moveCamera(CameraUpdateFactory.newLatLng(SelectedMarker));
    }

}