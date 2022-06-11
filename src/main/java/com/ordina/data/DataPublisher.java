package com.ordina.data;

import com.ordina.data.opensky.OpenSky;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
public class DataPublisher {

    // DataGetter for the OpenSky Data
    DataGetter<OpenSky> data;

    // Publishes the data whenever data from OpenSky is received
    SubmissionPublisher<OpenSky> publisher = new SubmissionPublisher<>();

    // Instance is created by spring boot
    private DataPublisher() {
        data = SharedStorage.getInstance().getOpenSky();
    }

    /**
     * Add the given subscriber to listen for when a new piece of data is received
     */
    public void setSubscriber(Subscriber<OpenSky> subscriber) {
        publisher.subscribe(subscriber);
    }

    /**
     * Gets the data from the DataGetter and publishes it.
     * This function is run at a fixed rate of once every 10 seconds. Subsequent calls
     * are blocked until the previous one is finished.
     */
    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    private void looped() throws IOException {
        OpenSky openSky = data.get();
        publisher.submit(openSky);
    }
}
