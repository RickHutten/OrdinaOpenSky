package com.ordina.data.opensky;

import com.ordina.data.DataPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

@Component
public class OpenSkyDataSubscriber implements Subscriber<OpenSky> {

    private Subscription subscription;
    Logger logger = LoggerFactory.getLogger(OpenSkyDataSubscriber.class);

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void init() {
        // Get instance of the DataPublisher that publishes OpenSky data and set itself as subscriber
        DataPublisher publisher = context.getBean(DataPublisher.class);
        publisher.setSubscriber(this);
    }

    // Instance is created by spring boot
    private OpenSkyDataSubscriber() {}

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(1); // Request first data from publisher
    }

    /**
     * Called when a new data is received
     */
    @Override
    public void onNext(OpenSky item) {
        // Print that new data is received for debugging purposes
        logger.info("Data received");

        // Process the data and request new
        OpenSkyDataProcessor.process(item);
        this.subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        Arrays.stream(throwable.getStackTrace()).forEach(error -> logger.error(error.toString()));
    }

    @Override
    public void onComplete() {
        logger.info("OpenSkyDataSubscriber completed!");
    }
}
