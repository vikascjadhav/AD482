package com.redhat.vehicles.movement.tracker;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.redhat.vehicles.movement.events.VehicleMoved;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;

@ApplicationScoped
public class VehicleMovementTracker {

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // Event Key SerDe (all events use the vehicle id as the Kafka record key)
        Serde<Integer> intSerde = Serdes.Integer();

        // Event Value SerDes
        ObjectMapperSerde<Vehicle> vehicleSerde = new ObjectMapperSerde<>(
                Vehicle.class);
        ObjectMapperSerde<VehicleMetrics> vehicleMetricsSerde = new ObjectMapperSerde<>(
                VehicleMetrics.class);
        ObjectMapperSerde<VehicleMoved> vehicleMovedSerde = new ObjectMapperSerde<>(
                VehicleMoved.class);

        // TODO: Create GlobalKTable from "vehicles"

        GlobalKTable<Integer, Vehicle> ktable = builder.globalTable("vehicles",
                Materialized.<Integer, Vehicle, KeyValueStore<Bytes, byte[]>>as("vehicles-store").withKeySerde(intSerde)
                        .withValueSerde(vehicleSerde));

        // TODO: Create KStream from "vehicle-movements"

        KStream<Integer, VehicleMoved> kStream = builder.stream("vehicle-movements",
                Consumed.with(Serdes.Integer(), vehicleMovedSerde));

        // TODO: join

        KStream<Integer, VehicleStatus> joinresult = kStream.join(ktable, (vehicleId, vehicleMoved) -> vehicleId,
                (vehicleMoved, vehicle) -> new VehicleStatus(vehicle, vehicleMoved.latitude, vehicleMoved.longitude,
                        vehicleMoved.elevation));
        // TODO: print the enriched stream

        joinresult.foreach((key, vehicleStatus) -> System.out.println(vehicleStatus));

        // TODO: group by, aggregate, and materialize

        joinresult.groupByKey().aggregate(VehicleMetrics::new,
                (vehicleId, vehicleStatsus, metrics) -> metrics.update(vehicleStatsus),
                Materialized.<Integer, VehicleMetrics, KeyValueStore<Bytes, byte[]>>as("vehicle-metrics-store")
                        .withKeySerde(intSerde).withValueSerde(vehicleMetricsSerde));

        return builder.build();
    }
}
