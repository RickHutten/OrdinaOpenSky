package com.ordina.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

public class DataGetter<T> {

    // Object to map OpenSky API JSON to a OpenSky java object
    private final ObjectMapper mObjectMapper = new ObjectMapper();

    // Url of the API
    private final URL mUrl;

    // Class to which the received JSON must be mapped
    private final Class<T> mCls;

    public DataGetter(URL url, Class<T> cls) {
        this.mUrl = url;
        this.mCls = cls;
    }

    /**
     * Downloads the data from mUrl and returns an instance of type T representing the JSON data.
     * @return Instance of T representing the JSON data
     * @throws IOException exception raised by ObjectMapper.readValue()
     */
    public T get() throws IOException {
        return mObjectMapper.readValue(mUrl, mCls);
    }
}
