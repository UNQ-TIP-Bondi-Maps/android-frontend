package tpi.unq.bondimaps.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bondi-maps.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer queryLatLng = new StringBuffer();
        queryLatLng.append("CREATE TABLE LATLNG (ID INTEGER PRIMARY KEY AUTOINCREMENT, ");
        queryLatLng.append("LAT REAL NOT NULL, ");
        queryLatLng.append("LNG REAL NOT NULL)");
        db.execSQL(queryLatLng.toString());
        StringBuffer queryPlace = new StringBuffer();
        queryPlace.append("CREATE TABLE PLACES (ID INTEGER PRIMARY KEY AUTOINCREMENT, ");
        queryPlace.append("NAME TEXT NOT NULL, ");
        queryPlace.append("ADDRESS TEXT NOT_NULL, ");
        queryPlace.append("LATLNGID INTEGER NOT NULL)");
        db.execSQL(queryPlace.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE LATLNG");
        db.execSQL("DROP TABLE PLACES");
        onCreate(db);
    }
}
