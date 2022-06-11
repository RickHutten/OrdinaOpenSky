package com.ordina;

import com.ordina.data.DataGetter;
import com.ordina.data.SharedStorage;
import com.ordina.data.opensky.OpenSky;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.io.IOException;
import java.net.URL;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class App {

    // Url to the OpenSky API
    private static final String OPEN_SKY_API_URL = "https://opensky-network.org/api/states/all";

    public static void main(String[] args) throws IOException {
        // Create instance able to receive data from the OpenSky API
        URL url = new URL(OPEN_SKY_API_URL);
        DataGetter<OpenSky> getter = new DataGetter<>(url, OpenSky.class);

        // Save DataGetter to storage
        SharedStorage storage = SharedStorage.getInstance();
        storage.setOpenSky(getter);

        // Start the application
        SpringApplication.run(App.class, args);
    }
}
