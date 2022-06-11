package com.ordina.data.opensky;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OpenSky {

    @JsonProperty("time") private Integer time;
    public Integer getTime() { return time; }

    @JsonProperty("states") private List<State> states;
    public List<State> getStates() { return states; }

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record State(String icao24, String callsign, String originCountry, Integer timePosition,
                 Integer lastContact, Double longitude, Double latitude, Double baroAltitude,
                 Boolean onGround, Double velocity, Double trueTrack, Double verticalRate,
                 List<Integer> sensors, Double geoAltitude, String squawk, Boolean spi,
                 Integer positionSource) {

        /**
         * Whether the plane is expected to change from altitude slice after the given amount of seconds.
         */
        public boolean warnSliceChange(int sliceSize, int seconds) {
            int currentSlice = (int) (geoAltitude() / sliceSize);
            int nextSlice = (int) ((geoAltitude() + verticalRate() * seconds) / sliceSize);
            return currentSlice != nextSlice;
        }
    }
}
