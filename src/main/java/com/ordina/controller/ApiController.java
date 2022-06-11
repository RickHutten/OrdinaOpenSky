package com.ordina.controller;

import com.ordina.data.SharedStorage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ApiController {

    private final SharedStorage storage = SharedStorage.getInstance();

    /**
     * Gets the top 3 countries of origin
     */
    @GetMapping(value = "/count")
    public Map<String, Integer> countryCount() {
        return storage.getCountryCount().entrySet().stream()
                .sorted(Comparator.comparingInt(setEntry -> -setEntry.getValue().size()))
                .limit(3)
                .collect(Collectors.toMap(Map.Entry::getKey, setEntry -> setEntry.getValue().size()));
    }

    /**
     * Get the altitude slices and their corresponding plane IDs
     */
    @GetMapping(value = "/altitude")
    public Map<Integer, Set<String>> altitude() {
        return storage.getAltitudeSlices();
    }

    /**
     * Gets the plane IDs which are likely to change altitude slice before the next set of data is received
     */
    @GetMapping(value = "/warnings")
    public List<String> warnings() {
        return storage.getSliceWarnings();
    }

    /**
     * Gets the hourly number of planes over the netherlands
     */
    @GetMapping(value = "/nl")
    public Integer planesOverNL() {
        return storage.getLastMessageAboveLocation(storage.netherlands).size();
    }

    /**
     * Combines all the data of the above endpoints in a single JSON.
     */
    @GetMapping(value = "/all")
    public Map<String, Object> getAll() {
        Map<String, Integer> locationCount = new HashMap<>();
        locationCount.put("netherlands", planesOverNL());

        Map<String, Object> data = new HashMap<>();
        data.put("location_count", locationCount);
        data.put("altitudes", altitude());
        data.put("origin_count", countryCount());
        data.put("warnings", warnings());

        return data;
    }
}
