package org.irs.util;

public class GeoUtils {
    private static final double EARTH_RADIUS_METERS = 6371008.8;

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Haversine formula
        double dlon = lon2Rad - lon1Rad;
        double dlat = lat2Rad - lat1Rad;
        double a = Math.pow(Math.sin(dlat / 2), 2) + 
                  Math.cos(lat1Rad) * Math.cos(lat2Rad) * 
                  Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // Calculate the distance in kilometers
        return Math.round(EARTH_RADIUS_METERS * c * 1000.0) / 1000.0; // rounded to 3 decimal places
    }
} 