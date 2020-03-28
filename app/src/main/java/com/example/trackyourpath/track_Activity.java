package com.example.trackyourpath;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class track_Activity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    boolean first_time;
    RelativeLayout start1,btn1,getmap1,near1;
    TextView Start1,Btn1,Getmap1,Near1;
    Calendar c;
    Calendar cal = Calendar.getInstance();
    int d1, m1, y1;
    String date, s;
    DatePickerDialog d;
    double latitude, longitude;
    Context mContext;
    LocationManager locationManager;
    JSONArray jsonArrayOldHIstory;
    JSONObject jsonObj;
    String Day1,Month1,Year1;
    String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    double lat1 = Double.NaN, lon1 = Double.NaN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        mContext = this;
        SharedPreferences popup=getSharedPreferences("Prefs",0);
        first_time=popup.getBoolean("first_time_start",true);
        if(first_time)
        {
            AlertDialog.Builder first=new AlertDialog.Builder(mContext);
            first.setTitle("App Info");
            first.setMessage("yesterday's tracking will be shown.");
            first.setPositiveButton("Ok,get it", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                }
            });
            SharedPreferences.Editor editor=popup.edit();
            editor.putBoolean("first_time_start",false);
            editor.commit();
            AlertDialog alert=first.create();
            alert.show();

        }

        start1=findViewById(R.id.start1);
        Start1=findViewById(R.id.Start1);
        btn1=findViewById(R.id.btn1);
        Btn1=findViewById(R.id.Btn1);
        getmap1 =findViewById(R.id.getmap1);
        Getmap1=findViewById(R.id.Getmap1);
        near1 =findViewById(R.id.near1);
        Near1=findViewById(R.id.Near1);



        c = Calendar.getInstance();
        Day1 = String.valueOf(c.get(Calendar.DAY_OF_MONTH) - 1);
        Month1 = String.valueOf(c.get(Calendar.MONTH) + 1);
        Year1 = String.valueOf(c.get(Calendar.YEAR));
        date = (Day1 + Month1 + Year1);


        d1 = cal.get(Calendar.DAY_OF_MONTH);
        m1 = cal.get(Calendar.MONTH);
        y1 = cal.get(Calendar.YEAR);

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        readFile();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



       start1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               LocationManager lm;
               boolean gpsProviderEnabled;
               lm = (LocationManager) getSystemService(LOCATION_SERVICE);
               gpsProviderEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
               if (!gpsProviderEnabled) {
                   Toast.makeText(getApplicationContext(), "Turn on GPS", Toast.LENGTH_SHORT).show();

                   AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
                   alertDialog.setTitle("Location");
                   alertDialog.setMessage("To continue , turn on device location");
                   alertDialog.setPositiveButton("Open", new DialogInterface.OnClickListener(){
                       public void onClick(DialogInterface dialog, int which){
                           Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                           startActivity(intent);
                       }
                   });
                   alertDialog.setNegativeButton("No thanks", new DialogInterface.OnClickListener(){
                       public void onClick(DialogInterface dialog, int which){
                           dialog.cancel();
                       }
                   });
                   AlertDialog alert=alertDialog.create();
                   alert.show();
               } else {
                   if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                           || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                           || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                       ActivityCompat.requestPermissions(track_Activity.this, permissions, 1);
                   }
                   else{
                       Start1.setText("On going");
                       locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 30, locationListenerGPS);
                   }

               }
           }
       });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                d=new DatePickerDialog(track_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        y1=year;
                        m1=month;
                        d1=dayOfMonth;
                        Btn1.setText(d1+"/"+ (m1+1) +"/"+y1);
                        Day1=String.valueOf(d1);
                        Month1=String.valueOf(m1+1);
                        Year1=String.valueOf(y1);
                        date = d1+String.valueOf(m1+1)+y1;
                        readFile();
                        map_refresh();
                    }
                }, y1,m1,d1);

                d.getDatePicker().setMaxDate(System.currentTimeMillis());
                d.show();

            }
        });


        getmap1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(jsonArrayOldHIstory==null)
                {
                    Toast.makeText(getApplicationContext(),"No track found",Toast.LENGTH_SHORT).show();
                }
                else if(Btn1.getText().toString().equals("Date"))
                {

                    Intent i=new Intent("MapsActivity");
                    Bundle bundle = new Bundle();
                    s= Day1+(Month1)+Year1;
                    bundle.putString("D",s);
                    bundle.putString("D1",Day1+"/"+Month1+"/"+Year1);
                    i.putExtras(bundle);
                    startActivity(i);
                    bundle.clear();
                }
                else{
                    Intent i=new Intent("MapsActivity");
                    Bundle bundle = new Bundle();
                    s= d1 +String.valueOf(m1+1)+ y1;
                    bundle.putString("D",s);
                    bundle.putString("D1",Btn1.getText().toString());
                    i.putExtras(bundle);
                    startActivity(i);
                    bundle.clear();
                }
            }
        });


        near1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Double.isNaN(lat1) && Double.isNaN(lon1))
                {
                    Toast.makeText(getApplicationContext(),
                            "First select any location on map", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i=new Intent(track_Activity.this,Nearby_Activity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("lat",String.valueOf(lat1));
                    bundle.putString("lon",String.valueOf(lon1));
                    i.putExtras(bundle);
                    startActivity(i);
                    bundle.clear();

                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        map_refresh();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        lat1=marker.getPosition().latitude;
        lon1=marker.getPosition().longitude;

        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            latitude=location.getLatitude();
            longitude=location.getLongitude();
            Start1.setText("On Going");
            Calendar c = Calendar.getInstance();
            String day,month,year;
            day=String.valueOf(c.get(Calendar.DAY_OF_MONTH));
            month = String.valueOf(c.get(Calendar.MONTH) + 1);
            year = String.valueOf(c.get(Calendar.YEAR));
            date = (day + month + year);
            json();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

            Start1.setText("Start Track");

        }
    };


    public void json(){

        String folder="GPS_Value";
        File f=new File(Environment.getExternalStorageDirectory(),folder);
        if(!f.exists())
        {
            f.mkdirs();
        }
        File mypath=new File(f,"Coordinates1.json");
        if(!mypath.exists())
        {
            JSONObject coordinates=new JSONObject();
            try{
                coordinates.put("lt",latitude);
                coordinates.put("lo",longitude);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            JSONArray Date_Value=new JSONArray();
            Date_Value.put(coordinates);

            JSONObject D=new JSONObject();
            try{

                D.put(date,Date_Value);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            JSONObject Coordinates=new JSONObject();
            try {
                Coordinates.put("coordinates",D);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            try{
                FileWriter file=new FileWriter(mypath);
                file.write(Coordinates.toString());
                file.flush();
                file.close();
            }
            catch (IOException e)
            {

                Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
            }
        }
        else {


            JSONObject obj_coordinates=null;

            try {
                FileInputStream is=new FileInputStream(mypath.getAbsoluteFile());
                int size=is.available();
                byte buffer[]=new byte[size];
                is.read(buffer);
                is.close();
                String Old_Coordinates=new String(buffer);
                try{

                    JSONObject coordinates=new JSONObject();
                    try{
                        coordinates.put("lt",latitude);
                        coordinates.put("lo",longitude);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    obj_coordinates=new JSONObject(Old_Coordinates);

                    JSONObject j=obj_coordinates.getJSONObject("coordinates");

                    if(j.has(date))
                    {
                        JSONArray ja=j.getJSONArray(date);
                        ja.put(coordinates) ;
                        j.remove(date);
                        j.put(date,ja);
                    }
                    else{
                        j.put(date,new JSONArray().put(coordinates));
                    }
                    obj_coordinates=new JSONObject();
                    obj_coordinates.put("coordinates",j);
                }
                catch (Throwable t){
                    Log.e("My App", "Could not parse malformed JSON: \"" + Old_Coordinates + "\"");
                }
            }
            catch (IOException e)
            {
                Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            }


            if(obj_coordinates.length()>0)
            {
                mypath.delete();
                try {
                    mypath.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileWriter fw;
                try
                {
                    fw=new FileWriter(mypath);
                    fw.write(obj_coordinates.toString());
                    fw.flush();
                    fw.close();

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }


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
                    jsonObj = objHIstory.getJSONObject("coordinates");
                    if(jsonObj.has(date))
                    {
                        jsonArrayOldHIstory = jsonObj.getJSONArray(date);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"No track found for "+ Day1 +"/"+ Month1 +"/"+ Year1,Toast.LENGTH_SHORT).show();
                        jsonArrayOldHIstory=null;
                    }


                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + OldHistory + "\"");
                }
            } catch (IOException e) {
                Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
                Toast.makeText(getApplicationContext(),"No track found for "+ Day1 +"/"+ Month1 +"/"+ Year1,Toast.LENGTH_SHORT).show();
            }

        }


    }


    private void map_refresh()
    {
        if(mMap!=null)
        {
            mMap.clear();

            if(jsonArrayOldHIstory!=null)
            {
                if(jsonArrayOldHIstory.length()>0)
                {
                    String lt,lo;
                    double lat,lon;
                    LatLng a;
                    for(int i=0;i<jsonArrayOldHIstory.length();i++)
                    {
                        try {
                            JSONObject string = jsonArrayOldHIstory.getJSONObject(i);
                            lt=string.getString("lt");
                            lo=string.getString("lo");
                            lat=Double.parseDouble(lt);
                            lon=Double.parseDouble(lo);
                            a=new LatLng(lat,lon);
                            mMap.addMarker(new MarkerOptions().position(a).title("Near By Restaurents").snippet(Day1 +"/"+ Month1 +"/"+ Year1).icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(a,15));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    mMap.setOnMarkerClickListener(this);
                }

            }

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    Intent i=new Intent(track_Activity.this,Nearby_Activity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("lat",String.valueOf(lat1));
                    bundle.putString("lon",String.valueOf(lon1));
                    i.putExtras(bundle);
                    startActivity(i);
                    bundle.clear();

                }
            });
           mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
                @Override
                public void onInfoWindowClose(Marker marker) {
                    lat1=Double.NaN;
                    lon1=Double.NaN;
                }
            });
        }

    }

}
