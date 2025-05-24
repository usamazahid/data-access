package org.irs.util;
import smile.math.distance.Distance;

public class HaversineDistance implements Distance<double[]> {
    private static final double EARTH_RADIUS_KM = 6371.0088;

    @Override
    public double d(double[] a, double[] b) {
        double lat1 = Math.toRadians(a[0]);
        double lon1 = Math.toRadians(a[1]);
        double lat2 = Math.toRadians(b[0]);
        double lon2 = Math.toRadians(b[1]);

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double hav = Math.pow(Math.sin(dLat / 2), 2) +
                     Math.cos(lat1) * Math.cos(lat2) *
                     Math.pow(Math.sin(dLon / 2), 2);

        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(hav));
    }
}
