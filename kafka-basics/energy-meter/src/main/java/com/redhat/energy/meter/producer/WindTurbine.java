package com.redhat.energy.meter.producer;

import com.redhat.energy.meter.common.Config;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class WindTurbine extends Config {
    private static final int[] energyProductionSequence = { 300, 400, 500, 600, 700 };

    private static void printRecord(ProducerRecord<Void, Integer> record) {
        System.out.println("Sent record:");
        System.out.println("\tTopic = " + record.topic());
        System.out.println("\tPartition = " + record.partition());
        System.out.println("\tKey = " + record.key());
        System.out.println("\tValue = " + record.value());
    }

    private static Properties configureProperties() {
        Properties props = new Properties();

        configureProducer(props);
        configureConnectionSecurity(props);

        return props;
    }

    private static void configureProducer(Properties props) {
        // TODO: configure the bootstrap server
        // props.setProperty(Kafka, null)

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "my-cluster-kafka-bootstrap-gyvrve-kafka-cluster.apps.ap46a.prod.ole.redhat.com:443");
        // TODO: configure the key serializer

        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.IntegerSerializer");
        // TODO: configure the value serializer
        //org.apache.kafka.common.serialization.IntegerSerializer
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringSerializer");

        
    }

    public static void main(String[] args) {
        // TODO: implement the business logic
        KafkaProducer<Void,Integer>  producer = new KafkaProducer<>(configureProperties());
        
        for(int enery: energyProductionSequence){
            ProducerRecord<Void,Integer> record = new ProducerRecord<Void,Integer>( "wind-turbine-production",enery);
            producer.send(record);
            printRecord(record);
        }

        producer.close();
        
    }
}
