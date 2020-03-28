package com.example.trackyourpath;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Nearby_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Nearby_Adapter nearbyAdapter;
    private ArrayList<Nearby_Value> value_List;
    String lat,lon;
    //TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        value_List=new ArrayList<>();
       // tv=findViewById(R.id.tv1);
        Bundle bundle = getIntent().getExtras();
        lat=bundle.getString("lat");
        lon=bundle.getString("lon");
        bundle.clear();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lon+"&radius=4000&type=restaurant&keyword=cruise&key=Your Maps API Key";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                       // tv.setText("Response is: "+ response.substring(0,500));

                        String folder="Near_By";
                        File f=new File(Environment.getExternalStorageDirectory(),folder);
                        if(!f.exists())
                        {
                            f.mkdirs();
                        }
                        File mypath=new File(f,"Restaurants.json");
                        if(!mypath.exists())
                        {
                            try {
                                FileWriter file=new FileWriter(mypath);
                                file.write(response);
                                file.flush();
                                file.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        else{
                            mypath.delete();
                            try {
                                mypath.createNewFile();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            FileWriter fw;

                            try {
                                fw=new FileWriter(mypath);
                                fw.write(response);
                                fw.flush();
                                fw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        File mypath1 = new File(f, "Restaurants.json");
                        JSONObject objHIstory = null;
                        if (mypath1.exists()) {
                            try {
                                FileInputStream is = new FileInputStream(mypath1);
                                int size = is.available();
                                byte[] buffer = new byte[size];
                                is.read(buffer);
                                is.close();
                                String OldHistory = new String(buffer);
                                try {

                                    objHIstory=new JSONObject(response);
                                   if(objHIstory.getString("status").equals("ZERO_RESULTS"))
                                    {
                                        Toast.makeText(getApplicationContext(),"No Restaurents in 4km radius",Toast.LENGTH_SHORT).show();
                                    }
                                    else if(objHIstory.getString("status").equals("OVER_QUERY_LIMIT"))
                                   {
                                       Toast.makeText(getApplicationContext(),"Your nearby places request limit is over",Toast.LENGTH_SHORT).show();
                                   }
                                   else{
                                       //String s ="";
                                       JSONArray ja=objHIstory.getJSONArray("results");
                                       for(int i=0;i<ja.length();i++)
                                       {
                                           objHIstory=ja.getJSONObject(i);
                                           String name=objHIstory.getString("name");
                                           String rating=objHIstory.getString("rating");
                                           //String image=objHIstory.getString("icon");
                                           value_List.add(new Nearby_Value(name,rating));//,image));

                                          // s=s+"icon:"+objHIstory.getString("icon")+"\nname:"+ objHIstory.getString("name")+"\nrating:"+objHIstory.getString("rating")+"\n";
                                       }

                                       nearbyAdapter =new Nearby_Adapter(Nearby_Activity.this,value_List);
                                       recyclerView.setAdapter(nearbyAdapter);

                                      // tv.setText(ja.toString());
                                        //objHIstory=objHIstory.getJSONObject("icon");
                                   }

                                }
                                catch (Throwable t) {
                                    Log.e("My App", "Could not parse malformed JSON: \"" + OldHistory + "\"" + t);
                                }
                            }
                            catch (IOException e) {
                                Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
                            }

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

              //  tv.setText("That didn't work!");
                Toast.makeText(getApplicationContext(),"Your are Offline!",Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);

    }

}
