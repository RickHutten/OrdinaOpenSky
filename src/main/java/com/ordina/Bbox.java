package com.ordina;

import java.util.Objects;

public class Bbox {

    private final Double latMin, latMax, lonMin, lonMax;

    /**
     * Bounding box for a given range of latitude and longitude coordinates.
     */
    public Bbox(Double latMin, Double latMax, Double lonMin, Double lonMax) {
        this.latMin = latMin;
        this.latMax = latMax;
        this.lonMin = lonMin;
        this.lonMax = lonMax;
    }

    /**
     * Returns whether the given coordinate is in the bounding box.
     */
    public boolean contains(Double lat, Double lon) {
        return lat != null &&  lon != null &&
                latMin < lat && lat < latMax &&
                lonMin < lon && lon < lonMax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bbox bbox = (Bbox) o;
        return latMin.equals(bbox.latMin) && latMax.equals(bbox.latMax) && lonMin.equals(bbox.lonMin) && lonMax.equals(bbox.lonMax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latMin, latMax, lonMin, lonMax);
    }

    @Override
    public String toString() {
        return "Bbox{" +
                "latMin=" + latMin +
                ", latMax=" + latMax +
                ", lonMin=" + lonMin +
                ", lonMax=" + lonMax +
                '}';
    }
}
