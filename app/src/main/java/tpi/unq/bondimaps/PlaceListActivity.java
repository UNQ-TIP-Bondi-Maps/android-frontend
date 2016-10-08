package tpi.unq.bondimaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import tpi.unq.bondimaps.adapter.PlaceAdapter;
import tpi.unq.bondimaps.database.PlaceManagerDB;
import tpi.unq.bondimaps.model.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceListActivity extends AppCompatActivity {

    private boolean mTwoPane;
    private List<Place> places = new ArrayList<>();
    private PlaceManagerDB placeManagerDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        placeManagerDB = new PlaceManagerDB(this);
        updatePlaces();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_place_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceListActivity.this, AddPlaceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        View recyclerView = findViewById(R.id.place_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        if (findViewById(R.id.place_detail_container) != null) {
            mTwoPane = true;
        }
    }

    @Override
    protected void onResume() {
        updatePlaces();
        super.onResume();
    }

    private void updatePlaces() {
        places = placeManagerDB.getPlaces();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new PlaceAdapter(places, mTwoPane, getSupportFragmentManager()));
    }
}