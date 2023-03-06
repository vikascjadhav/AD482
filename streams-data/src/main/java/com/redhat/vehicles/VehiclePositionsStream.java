package com.redhat.vehicles;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;

@ApplicationScoped
public class VehiclePositionsStream {

    // Deserializer for NULL keys.
    private final Serde<String> stringSerde = Serdes.String();

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // TODO: create serde to deserialize VehiclePosition messages
        ObjectMapperSerde<VehiclePosition> vehiclePositionSerde = new ObjectMapperSerde<>(VehiclePosition.class);

        // TODO: Create the stream from the "vehicle-positions" topic

        KStream<String, VehiclePosition> stream = builder.stream("vehicle-positions",
                Consumed.with(stringSerde, vehiclePositionSerde));

        // TODO: print stream values

        stream.foreach((kv, v) -> System.out.println("Received vehicle position: " + v));

        // TODO: map positions to elevations in feet
        // and send the stream to "vehicle-feet-elevations" topic

        stream.map((k, v) -> {
            Double feet = v.elevation * 3.28084;
            return KeyValue.pair(v.vehicleId, feet);
        }).to("vehicle-feet-elevations", Produced.with(Serdes.Integer(), Serdes.Double()));

        // TODO: group positions by vehicle id

        KGroupedStream<Integer, VehiclePosition> positionsBYVehicle = stream.groupBy((k, v) -> v.vehicleId,
                Grouped.with(Serdes.Integer(), vehiclePositionSerde));

        // TODO: count positions by vehicle
        KTable<Integer, Long> countByVehicle = positionsBYVehicle.count();
        // TODO: print the count values
        countByVehicle.toStream().foreach((vehicleId, count) -> {
            System.out.println(
                    "Vehicle: " + vehicleId + " Positions count: " + count + "\n");
        });

        return builder.build();
    }
}
