package com.orvba.app.util;

public class DistanceUtil {

    public static double calculateDistance(
            double latitude1,
            double longitude1,
            double latitude2,
            double longitude2) {

        final double EARTH_RADIUS_KM = 6371.0;

        double latDifference = Math.toRadians(latitude2 - latitude1);
        double lonDifference = Math.toRadians(longitude2 - longitude1);

        double a = Math.sin(latDifference / 2) * Math.sin(latDifference / 2)
                + Math.cos(Math.toRadians(latitude1))
                * Math.cos(Math.toRadians(latitude2))
                * Math.sin(lonDifference / 2)
                * Math.sin(lonDifference / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}