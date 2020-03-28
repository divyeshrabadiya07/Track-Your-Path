package com.example.trackyourpath;

import android.graphics.Color;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ImageView back_arrow;
    private TextView title;
    JSONArray jsonArrayOldHIstory;
    JSONObject jsonObj;
    String date,date1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        back_arrow=findViewById(R.id.back_arrow);
        title=findViewById(R.id.title);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        date= bundle.getString("D");
        date1=bundle.getString("D1");
        bundle.clear();
        title.setText("Tracking Day:-"+date1);

        readFile();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        if(jsonArrayOldHIstory!=null)
        {
            if(jsonArrayOldHIstory.length()>0)
            {
                String lt,lo;
                double lat,lon;
                LatLng b[]=new LatLng[jsonArrayOldHIstory.length()];
                for(int i=0;i<jsonArrayOldHIstory.length();i++)
                {
                    try {
                        JSONObject string = jsonArrayOldHIstory.getJSONObject(i);
                        lt=string.getString("lt");
                        lo=string.getString("lo");
                        lat=Double.parseDouble(lt);
                        lon=Double.parseDouble(lo);
                        b[i]=new LatLng(lat,lon);
                        if(i==0)
                        {
                          mMap.addMarker(new MarkerOptions().position(b[i]).title("Starting of Day").icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_maps)).snippet(date1));//defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));//icon(BitmapDescriptorFactory.fromResource(R.drawable.first));
                        }
                        else if(i==(jsonArrayOldHIstory.length()-1))
                        {
                           mMap.addMarker(new MarkerOptions().position(b[i]).title("End of Day").icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_maps)).snippet(date1));
                        }

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(b[i],20));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                       mMap.addPolyline(new PolylineOptions().clickable(true).add(b).color(Color.rgb(255,78,0)).width(18).startCap(new RoundCap()).endCap(new RoundCap()));



                mMap.setOnMarkerClickListener(this);

            }
        }


        //LatLng Anupam = new LatLng(21.213592,72.886165);
        //LatLng Amazia=new LatLng(21.194834, 72.865004);
        //mMap.addMarker(new MarkerOptions().position(Anupam).title("Marker in Surat")); //place marker at specific coordinate
        //mMap.addMarker(new MarkerOptions().position(Amazia).title("Marker in Amazia"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(Anupam));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Amazia,15));
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    private void readFile() {

        String folder_main = "GPS_Value";
        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }
        File mypath = new File(f, "Coordinates1.json");
        if (!mypath.exists()) {
            mypath.mkdirs();
        }
        JSONObject objHIstory = null;
        if (mypath.exists()) {
            try {
                FileInputStream is = new FileInputStream(mypath);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String OldHistory = new String(buffer);
                try {

                    objHIstory = new JSONObject(OldHistory);
                    jsonObj= objHIstory.getJSONObject("coordinates");
                    if(jsonObj.has(date))
                    {
                        jsonArrayOldHIstory= jsonObj.getJSONArray(date);
                    }
                    else{

                        Toast toast=Toast.makeText(getApplicationContext(),"No tracking found",Toast.LENGTH_SHORT);
                        toast.show();
                    }


                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + OldHistory + "\"");
                }
            } catch (IOException e) {
                Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
                Toast.makeText(getApplicationContext(),"No tracking found",Toast.LENGTH_SHORT).show();
            }

        }
    }


}

