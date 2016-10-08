package tpi.unq.bondimaps;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Locale;

import tpi.unq.bondimaps.adapter.PlaceAutocompleteAdapter;
import tpi.unq.bondimaps.database.PlaceManagerDB;
import tpi.unq.bondimaps.model.CustomLatLng;
import tpi.unq.bondimaps.model.Place;

public class AddPlaceActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener {

    private PlaceManagerDB placeManagerDB;
    private GoogleMap mMap;
    private Geocoder geoCoder;
    private Address location;
    private String address;
    private LatLng latLng;
    private static final String TAG = AddPlaceActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geoCoder = new Geocoder(this, Locale.getDefault());
        placeManagerDB = new PlaceManagerDB(this);
        FloatingActionButton myPlacesButton = (FloatingActionButton) findViewById(R.id.confirm_place);
        myPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPlaceActivity.this);
                builder.setTitle("Nombre del lugar");
                final EditText input = new EditText(AddPlaceActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CustomLatLng customLatLng = new CustomLatLng(latLng.latitude, latLng.longitude);
                        Place place = new Place(input.getText().toString(), address, customLatLng);
                        placeManagerDB.addPlace(place);
                        NavUtils.navigateUpTo(AddPlaceActivity.this, new Intent(AddPlaceActivity.this, PlaceListActivity.class));
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
        FloatingActionButton cancelButton = (FloatingActionButton) findViewById(R.id.cancel_place);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        AutoCompleteTextView autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete_place);
        autocompleteView.setAdapter(new PlaceAutocompleteAdapter(AddPlaceActivity.this, R.layout.list_item));
        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                address = (String) parent.getItemAtPosition(position);
                Toast.makeText(AddPlaceActivity.this, address, Toast.LENGTH_SHORT).show();
                addMarkerInMap();
            }
        });
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
    }

    public void addMarkerInMap() {
        mMap.clear();
        generateLatLngByAddress();
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    private void generateLatLngByAddress() {
        try {
            location = geoCoder.getFromLocationName(address, 1).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        String str = (String) parent.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
