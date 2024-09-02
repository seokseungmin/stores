package com.springboot.stores.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DistanceCalculator {

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // 지구 반경(km)

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon1 - lon2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // 두 지점 간 거리(km)
    }
}
