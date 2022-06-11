package com.ordina.data.opensky;

import com.ordina.Bbox;
import com.ordina.data.SharedStorage;

import java.util.*;
import java.util.stream.Collectors;

public class OpenSkyDataProcessor {

    // Show the number of flights per this time in seconds
    private static final int FLIGHTS_PER_TIME_S = 3600;

    // Altitude slice size in meters
    private static final int ALTITUDE_SLICE_SIZE_M = 1000;

    // The rate at which new data is received in seconds
    private static final int DATA_RECEIVING_RATE_S = 10;

    private static final SharedStorage storage = SharedStorage.getInstance();

    /**
     * Process the data obtained when a new set of data is received.
     * @param data OpenSky object received from the OpenSky API.
     */
    public static void process(OpenSky data) {
        // Update the number of flights over NL per hour
        updatePlanesOverLocation(data, storage.netherlands);

        // Count the countries of origin of unique plane IDs
        countCountriesOfOrigin(data);

        // Place current planes in their altitude slice
        makeAltitudeSlices(data);

        // List the planes likely to change altitude slice before next data is received
        makeAltitudeSliceWarnings(data);
    }

    /**
     * Saves the plane IDs that have flown over the given location in the last FLIGHTS_PER_TIME_S seconds.
     * @param data the new data that is received
     * @param location location over which to collect plane IDs
     */
    private static void updatePlanesOverLocation(OpenSky data, Bbox location) {
        // Get the plane ICAOs that are currently over the location together with their last contact time
        Map<String, Integer> locationICAO = data.getStates()
                .stream()
                .filter(state -> location.contains(state.latitude(), state.longitude()))
                .filter(state -> !state.onGround())
                .collect(Collectors.toMap(OpenSky.State::icao24, OpenSky.State::lastContact));

        // Update the stored ("ICAOs", last_contact) combinations for the given location
        Map<String, Integer> updatedLocationICAOs = storage.getLastMessageAboveLocation(location);
        updatedLocationICAOs.putAll(locationICAO);

        // Remove the planes which have to flown over the location in the last FLIGHTS_PER_TIME_S seconds
        Map<String, Integer> oldRemoved = updatedLocationICAOs
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > getCurrentTimeSeconds() - FLIGHTS_PER_TIME_S)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Update the newly obtained plane IDs over the given location
        storage.setLastMessageAboveLocation(oldRemoved, location);
    }

    /**
     * Count unique plane IDs per country of origin.
     */
    public static void countCountriesOfOrigin(OpenSky data) {
        // Create a set of the unique ICAOs per origin country
        Map<String, Set<String>> countryCount = data.getStates().stream()
                .filter(state -> !state.originCountry().isBlank())
                .collect(Collectors.groupingBy(
                        OpenSky.State::originCountry,
                        Collectors.mapping(OpenSky.State::icao24, Collectors.toSet())
                        )
                );

        // Merge the hashmaps together with the one from storage
        storage.getCountryCount().forEach(
                (key, value) -> countryCount.merge(
                        key, value, (oldVal, newVal) -> {
                            oldVal.addAll(newVal);
                            return oldVal;
                        })
        );

        // Update the stored unique planes per country of origin
        storage.setCountryCount(countryCount);
    }

    /**
     * Group plane IDs per altitude slice of ALTITUDE_SLICE_SIZE_M meters.
     */
    public static void makeAltitudeSlices(OpenSky data) {
        Map<Integer, Set<String>> slices = data.getStates().stream()
                .filter(state -> state.geoAltitude() != null)
                .collect(Collectors.groupingBy(
                        state -> ALTITUDE_SLICE_SIZE_M * (int) (state.geoAltitude() / ALTITUDE_SLICE_SIZE_M),
                        Collectors.mapping(OpenSky.State::icao24, Collectors.toSet())
                        )
                );
        // Save to storage
        storage.setAltitudeSlices(slices);
    }

    /**
     * Find the plane IDs that are likely to change slice before the next data is received.
     */
    public static void makeAltitudeSliceWarnings(OpenSky data) {
        List<String> warnings = data.getStates().stream()
                .filter(state -> state.geoAltitude() != null && state.verticalRate() != null)
                .filter(state -> state.warnSliceChange(ALTITUDE_SLICE_SIZE_M, DATA_RECEIVING_RATE_S))
                .map(OpenSky.State::icao24).toList();

        storage.setSliceWarnings(warnings);
    }

    /**
     * Get the current Unix time in seconds.
     */
    private static long getCurrentTimeSeconds() {
        return System.currentTimeMillis() / 1000L;
    }
}
