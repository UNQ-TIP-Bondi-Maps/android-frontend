package tpi.unq.bondimaps.util;

import tpi.unq.bondimaps.Bus;
import tpi.unq.bondimaps.model.CustomLatLng;

public class DistanceCalculator {

    public static double blocksToMeters(int blocks) {
        return blocks * 100;
    }

    public static double distanceInMeters(CustomLatLng myPosition, Bus bus) {
        double radiusEarth = 6378137; //Earth radius in meters
        double distanceLat = rad(bus.getLat()- myPosition.getLat());
        double distanceLong = rad(bus.getLng() - myPosition.getLng());
        double a = Math.sin(distanceLat / 2) * Math.sin(distanceLat / 2) +
                Math.cos(rad(myPosition.getLat())) * Math.cos(rad(bus.getLat())) *
                        Math.sin(distanceLong / 2) * Math.sin(distanceLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = radiusEarth * c;
        return Math.round(d);
    }

    private static double rad(double x) {
        return x * Math.PI / 180;
    }

}
