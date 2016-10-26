package tpi.unq.bondimaps;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient googleApiClient;
    public static final String TAG = MainActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private GoogleMap googleMap;
    private Marker currLocationMarker = null;
    private Location lastLocation;
    public ServiceManager serviceManager;
    public int linesToDestiny;
    public BusesLocator locator;
    public String serverIp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serverIp = (String) getIntent().getExtras().get("ipBack");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // Create the LocationRequest object
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        linesToDestiny = 0;
        serviceManager = new ServiceManager(this);
        locator = new BusesLocator();
        locator.execute();
        //My places button
        FloatingActionButton myPlacesButton = (FloatingActionButton) findViewById(R.id.my_places_button);
        myPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlaceListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        //My destiny button
        FloatingActionButton myDestinyButton = (FloatingActionButton) findViewById(R.id.my_destiny_button);
        myDestinyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectDestinyActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("ipBack", serverIp);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            handleNewLocation(location);
        }
        Log.i(TAG, "Location service connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location service suspended, please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpIfNeeded();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        googleApiClient.disconnect();
        //locator.cancel(false);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!lastLocation.equals(location)) {
            handleNewLocation(location);
            lastLocation = location;
        }

        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }

        //stop location updates
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    private void handleNewLocation(Location location) {
        LatLng lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions()
                .position(lastLocation);
        googleMap.clear();
        googleMap.addMarker(options);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 16));
    }

    private class BusesLocator extends AsyncTask<Void, Void, Void> {

        private List<Bus> busesUpdated;
        private int busesToAdd = 0;

        @Override
        protected void onPreExecute() {
            this.busesUpdated = new ArrayList<Bus>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            this.busesUpdated.clear();
            serverIp = (String) getIntent().getExtras().get("ipBack");
            if(getIntent().getExtras().get("linesSize")!= null) {
                linesToDestiny = (int) getIntent().getExtras().get("linesSize");
                if (linesToDestiny > 0) {
                    String linesPath = "";
                    for(int i = 0; i < linesToDestiny; i++) {
                        String aLineString = (String) getIntent().getExtras().get("line"+i);
                        if (i==linesToDestiny-1) {
                            linesPath = linesPath + aLineString;
                        } else {
                            linesPath = linesPath + aLineString + "&";
                        }
                        Log.i("line pat elem: " , aLineString + " partial lp: " +linesPath);
                    }
                    while (!isCancelled()) {
                        Log.i("BusesLocator: ", "Start doInBackground");
                        String url = "http://" + serverIp + ":8080/backend/rest/busLines/lines/" + linesPath + "/buses";
                        Log.i("url locator: ", url);
                        JSONArray busArray;
                        try {
                            busArray = serviceManager.getListResource(url);
                            busesToAdd = busArray.length();
                            for (int i = 0; i < this.busesToAdd; i++) {
                                JSONObject buses = busArray.getJSONObject(i);
                                JSONObject position = buses.getJSONObject("position");
                                String dirOfTravel = buses.getString("directionOfTravel");
                                String routeWay = buses.getString("routeWay");
                                double busLat = position.getDouble("lat");
                                double busLng = position.getDouble("lng");

                                Bus busToAdd = new Bus();
                                busToAdd.setLat(busLat);
                                busToAdd.setLng(busLng);
                                busToAdd.setDirectionOfTravel(dirOfTravel);
                                busToAdd.setRouteWay(routeWay);
                                this.busesUpdated.add(busToAdd);
                                Log.i("busPosition ", " - lat: " + busLat + " lng: " + busLng + " .DirOfTravel :" + dirOfTravel);
                            }
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            publishProgress();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... params) {
            googleMap.clear();
            if(busesUpdated != null && busesUpdated.size() > 0) {
                for (int i = 0; i < this.busesUpdated.size(); i++) {
                    LatLng newPos = new LatLng(this.busesUpdated.get(i).getLat(), this.busesUpdated.get(i).getLng());
                    MarkerOptions aBus = new MarkerOptions()
                            .position(newPos)
                            .title(this.busesUpdated.get(i).getDirectionOfTravel());
                    googleMap.addMarker(aBus);
                    if(this.busesUpdated.get(i).getDirectionOfTravel().equals("Once - Wilde")) {
                        List<LatLng> routeCoordinates = PolyUtil.decode(this.busesUpdated.get(i).getRouteWay());
                        PolylineOptions routePolyline = new PolylineOptions()
                                .addAll(routeCoordinates)
                                .clickable(false);
                        googleMap.addPolyline(routePolyline);
                    }
                }
            }
            else{
                Log.i("onProgressUpdate - ", "finish with no results");
            }
            //TODO get my position in a different way
            MarkerOptions myPosition = new MarkerOptions()
                    .position(new LatLng(-34.706453, -58.278560));
            googleMap.addMarker(myPosition);
            Log.i("onProgressUpdate - ", "finish ");
        }

    }
}
