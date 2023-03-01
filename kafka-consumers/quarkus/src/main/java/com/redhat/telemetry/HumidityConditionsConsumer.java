package com.redhat.telemetry;

import javax.inject.Singleton;

import org.eclipse.microprofile.reactive.messaging.Incoming;


@Singleton
public class HumidityConditionsConsumer {

    public HumidityStats stats = new HumidityStats();

    // TODO: Implement the consumer method

    
    @Incoming("humidityConditions")
    public void consume(int humidityValue) {
        stats.add(humidityValue);
        System.out.println("Humidity Value: "+humidityValue);
    }

}
