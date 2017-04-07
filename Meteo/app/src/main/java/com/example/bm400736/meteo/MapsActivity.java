package com.example.bm400736.meteo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bm400736.meteo.data.Meteo;
import com.example.bm400736.meteo.forecastdata.Forecast5Days3Hours;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

//base :http://android-er.blogspot.fr/2015/10/android-query-current-weather-using.html

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private GoogleMap mMap;
    private ArrayList<Marker> markers;

    private Meteo meteo;
    //LatLng actualCoords = new LatLng(0.0, 0.0);

    final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    //private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markers = new ArrayList<Marker>();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (savedInstanceState != null) {

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(this);//rajout
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, DetailsActivity.class);
                //intent.putExtra(EXTRA_LOGIN, login.getText().toString());
                //intent.putExtra(EXTRA_PASSWORD, pass.getText().toString());
                //intent.putExtra("json", (new Gson()).toJson(meteo));
                intent.putExtra("lat", marker.getPosition().latitude);
                intent.putExtra("lon", marker.getPosition().longitude);
                startActivity(intent);
            }
        });
        final LatLng HAMBURG = new LatLng(53.558, 9.927);

        configureGPSDialog();

        enableMyLocation(mMap);

        //markers.add(mMap.addMarker(new MarkerOptions().position(HAMBURG).title("Marker in Sydney")));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(HAMBURG));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                for (Marker maker : markers) {
                    maker.remove();
                }


                OpenWeatherMapTask weatherTask = new OpenWeatherMapTask(arg0);
                weatherTask.execute();
                /*try {
                    weatherTask.sendQuery("");
                } catch (IOException e) {
                    e.printStackTrace();
                };*/

                //actualCoords = arg0;

                //Log.println(Log.INFO, "tag", "marker added");
            }
        });
    }

    private void configureGPSDialog(){
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Aciver la localisation ?");
            dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            dialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    finish();
                }
            });
            dialog.show();
        }
    }

    //
    private void enableMyLocation(final GoogleMap googleMap){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    for (Marker maker : markers) {
                        maker.remove();
                    }

                    OpenWeatherMapTask weatherTask = new OpenWeatherMapTask(googleMap.getMyLocation());
                    weatherTask.execute();

                    //actualCoords = new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude());
                    return false;
                }
            });
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    enableMyLocation(mMap);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        //return null;
        return prepareInfoView(marker);
    }

    private View prepareInfoView(Marker marker){
        Meteo meteo = (Meteo) marker.getTag();
        //prepare InfoView programmatically
        LinearLayout infoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setLayoutParams(infoViewParams);

        LinearLayout subInfoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        subInfoView.setLayoutParams(subInfoViewParams);

        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add(new TextView(MapsActivity.this));
        textViews.add(new TextView(MapsActivity.this));
        textViews.add(new TextView(MapsActivity.this));
        textViews.add(new TextView(MapsActivity.this));

        textViews.get(0).setText(meteo.getName());
        textViews.get(0).setTypeface(null, Typeface.BOLD);
        textViews.get(1).setText(meteo.getWeather()[0].getDescription());
        textViews.get(2).setText(Double.toString(meteo.getMain().getTemp()).toString() + "°C");
        textViews.get(3).setText("Humdidité : " + Integer.toString(meteo.getMain().getHumidity()) + "%");

        for(TextView tv : textViews)
        {
            tv.setTextColor(Color.BLACK);
            tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);//centré horiz et vert
            subInfoView.addView(tv);
        }

        //Button btnPlusDInfos = new Button(MapsActivity.this);
        //btnPlusDInfos.setCompoundDrawablesWithIntrinsicBounds(R.drawable., 0, 0, 0);

        //rajout de 20 px de marge entre le texte et le bouton
        //ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)subInfoView.getLayoutParams();
        //params.setMargins(params.leftMargin, params.topMargin, params.rightMargin + 20, params.bottomMargin);

        //btnPlusDInfos.setText("Plus");


        infoView.addView(subInfoView);
        //infoView.addView(btnPlusDInfos);

        return infoView;
    }

    private class OpenWeatherMapTask extends AsyncTask<Void, Void, String> {

        LatLng coordinates;

        String dummyAppid = "ede8ed629c321c8345a55d2879bc0104";
        //String queryWeather = "http://api.openweathermap.org/data/2.5/weather?q=";
        String queryDummyKey = "&appid=" + dummyAppid;

        String queryWeatherCoordinates = "http://api.openweathermap.org/data/2.5/weather?";


        OpenWeatherMapTask(LatLng latLng) {
            this.coordinates = latLng;
        }
        OpenWeatherMapTask(Location location) {
            this.coordinates = new LatLng(location.getLatitude(), location.getLongitude());
        }



        @Override
        protected String doInBackground(Void... params) {

            String queryReturn = null;
            String query = null;
            try {
                //query = queryWeather + URLEncoder.encode(cityName, "UTF-8") + queryDummyKey;
                query = queryWeatherCoordinates
                        + "lat="
                        + URLEncoder.encode(Double.toString((coordinates.latitude)), "UTF-8")
                        + "&lon="
                        + URLEncoder.encode(Double.toString((coordinates.longitude)), "UTF-8")
                        + queryDummyKey
                        + "&lang=fr"
                        + "&units=metric";
                queryReturn = sendQuery(query);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return queryReturn;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                Gson gson = new GsonBuilder().create();
                Meteo meteo = gson.fromJson(s, Meteo.class);//update global object
                //Log.d("onPostExecute", "meteo : " + meteo.getWeather()[0].getDescription());
                //mark.setTitle(meteo.getName() + ": " + meteo.getWeather()[0].getDescription());

                Marker marker = mMap.addMarker(new MarkerOptions().position(coordinates).title("Marker"));
                marker.setTag(meteo);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                markers.add(marker);
            }
        }

        private String sendQuery(String query) throws IOException {
            String result = "";

            URL searchURL = new URL(query);

            HttpURLConnection httpURLConnection = (HttpURLConnection) searchURL.openConnection();

            //can't be in the main thread
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader,
                        8192);

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
            }

            return result;
        }
    }
}
