package com.ordina.data;

import com.ordina.Bbox;
import com.ordina.data.opensky.OpenSky;

import java.util.*;

public class SharedStorage
{
    /**
     * Definition of the bounding box surrounding the netherlands.
     */
    public final Bbox netherlands = new Bbox(50.80372, 53.51040, 3.31497, 7.09205);

    /**
     * The OpenSky data getter instance.
     */
    private DataGetter<OpenSky> getter;

    /**
     * Saves the last message per plane over a given location.
     * {locationHash: {"ICAO24": lastContact, ...}, ...}
     */
    private final Map<Integer, Map<String, Integer>> lastMessageAboveLocation = new HashMap<>();

    /**
     * Plane IDs seen grouped by their country of origin.
     * {"country": ("ICAO24", ...), ...}
     */
    private Map<String, Set<String>> countryCount = new HashMap<>();

    /**
     * Planes are divided into 1000 m altitude slices.
     * {0: ("ICAO24", ...), 1: ("ICAO24", ...), ...}
     */
    private Map<Integer, Set<String>> altitudeSlices = new HashMap<>();

    /**
     * List of plane IDs of planes which will are likely to change to the next slice
     * before the next data is received.
     */
    private List<String> sliceWarnings = new ArrayList<>();

    /**
     * Singleton-style class to provide data shared between multiple classes in the application.
     */
    private SharedStorage() {}

    private static class Singleton {
        private static final SharedStorage INSTANCE = new SharedStorage();
    }

    public static SharedStorage getInstance() {
        return Singleton.INSTANCE;
    }

    /**
     * Set the lastMessageAboveLocation of the given location.
     * @param lastMessageAboveLocation Map of plane IDs and their last contact time
     * @param location the bounding box of the corresponding location
     */
    public synchronized void setLastMessageAboveLocation(Map<String, Integer> lastMessageAboveLocation, Bbox location) {
        this.lastMessageAboveLocation.put(location.hashCode(), lastMessageAboveLocation);
    }

    /**
     * Get a map of plane IDs and their last contact time of the given location. If nothing
     * has been previously saved for the given location, an empty map is returned.
     * @param location the location for which the data is requested.
     */
    public synchronized Map<String, Integer> getLastMessageAboveLocation(Bbox location) {
        return lastMessageAboveLocation.getOrDefault(location.hashCode(), new HashMap<>());
    }

    /**
     * Get a map of countries of origin and the plane IDs that belong to these countries.
     */
    public synchronized Map<String, Set<String>> getCountryCount() {
        return countryCount;
    }

    /**
     * Sets the country of origin map.
     */
    public synchronized void setCountryCount(Map<String, Set<String>> countryCount) {
        this.countryCount = countryCount;
    }

    /**
     * Sets the plane IDs belonging to their most recent altitude slice.
     */
    public synchronized void setAltitudeSlices(Map<Integer, Set<String>> slices) {
        this.altitudeSlices = slices;
    }

    /**
     * Gets the altitude slices with their corresponding most recent plane IDs.
     */
    public synchronized Map<Integer, Set<String>> getAltitudeSlices() {
        return this.altitudeSlices;
    }

    /**
     * Get which planes are likely to switch slice before next data is received.
     */
    public synchronized List<String> getSliceWarnings() {
        return sliceWarnings;
    }

    /**
     * Set which planes are likely to switch slice before the next data is received.
     */
    public synchronized void setSliceWarnings(List<String> sliceWarnings) {
        this.sliceWarnings = sliceWarnings;
    }

    /**
     * Sets the OpenSky DataGetter instance.
     */
    public synchronized void setOpenSky(DataGetter<OpenSky> getter) {
        this.getter = getter;
    }

    /**
     * Get the DataGetter for the OpenSky data.
     */
    public synchronized DataGetter<OpenSky> getOpenSky() {
        return getter;
    }
}
