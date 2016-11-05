package tpi.unq.bondimaps.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import tpi.unq.bondimaps.Bus;
import tpi.unq.bondimaps.MainActivity;
import tpi.unq.bondimaps.R;
import tpi.unq.bondimaps.model.CustomLatLng;
import tpi.unq.bondimaps.util.DistanceCalculator;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationGenerator {

    public static void launchNotification(MainActivity mainActivity, CustomLatLng customLatLng, Bus bus) {
        if(notificationIsActive(mainActivity) && busIsAtTheDesiredDistance(mainActivity, customLatLng, bus)) {
            Intent notIntent = new Intent(mainActivity, MainActivity.class);
            PendingIntent contIntent = PendingIntent.getActivity(mainActivity, 0, notIntent, 0);

            Notification notification = new NotificationCompat.Builder(mainActivity)
                    .setContentTitle("El colectivo ya se encuentra cerca.")
                    .setContentText("Distancia: " + getDistanceOfBus(mainActivity) + " cuadras.")
                    .setTicker("El colectivo esta cerca!!!")
                    .setSmallIcon(R.drawable.ic_bus)
                    .setContentIntent(contIntent)
                    .setPriority(Notification.PRIORITY_HIGH).build();

            NotificationManager notificationManager = (NotificationManager) mainActivity.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }
    }

    private static boolean busIsAtTheDesiredDistance(MainActivity mainActivity, CustomLatLng customLatLng, Bus bus) {
        return DistanceCalculator.blocksToMeters(getDistanceOfBus(mainActivity))
                >= DistanceCalculator.distanceInMeters(customLatLng, bus);
    }

    private static int getDistanceOfBus(MainActivity mainActivity) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        return Integer.valueOf(sharedPrefs.getString("config_distance_bus", "NULL"));
    }

    private static boolean notificationIsActive(MainActivity mainActivity) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        return sharedPrefs.getBoolean("notifications_new_message", false);
    }
}
