package tpi.unq.bondimaps.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import tpi.unq.bondimaps.model.CustomLatLng;
import tpi.unq.bondimaps.model.Place;

public class PlaceManagerDB {

    private static final String TAG = PlaceManagerDB.class.getSimpleName();
    private DatabaseHelper databaseHelper;

    public PlaceManagerDB(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void addPlace(Place place) {
        SQLiteDatabase connection = databaseHelper.getWritableDatabase();
        ContentValues valuesLatLng = new ContentValues();
        valuesLatLng.put("LAT", place.getCoordinate().getLat());
        valuesLatLng.put("LNG", place.getCoordinate().getLng());
        long idLatLng = connection.insert("LATLNG", null, valuesLatLng);
        ContentValues placeValues = new ContentValues();
        placeValues.put("NAME", place.getName());
        placeValues.put("ADDRESS", place.getAddress());
        placeValues.put("LATLNGID", idLatLng);
        connection.insert("PLACES", null, placeValues);
        connection.close();
    }

    public List<Place> getPlaces() {
        List<Place> places = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        String query = "SELECT * FROM PLACES p, LATLNG ll WHERE p.LATLNGID = ll.ID";
        Cursor cursor = database.rawQuery(query, null);
        while(cursor.moveToNext()) {
            Place place = new Place();
            place.setId(cursor.getLong(0));
            place.setName(cursor.getString(1));
            place.setAddress(cursor.getString(2));
            CustomLatLng latLng = new CustomLatLng(cursor.getDouble(4), cursor.getDouble(5));
            latLng.setId(cursor.getLong(3));
            place.setCoordinate(latLng);
            places.add(place);
        }
        database.close();
        return places;
    }
}
