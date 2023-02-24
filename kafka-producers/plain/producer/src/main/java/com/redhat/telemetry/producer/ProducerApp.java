package com.redhat.telemetry.producer;

import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;

public class ProducerApp {
    private final  static Random random = new Random();
    public static Properties configureProperties() {

        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, " my-cluster-kafka-bootstrap-gyvrve-kafka-cluster.apps.ap46a.prod.ole.redhat.com:443");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, props);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, props);
        props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG , "SSL");

        props.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "C:\\Users\\vikas\\AD482\\truststore.jks");
        props.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password");
        return props;

    }


    public static void main(String[] args) {
        Producer<Void,Integer> producer = new KafkaProducer<>(configureProperties());

        for(int i=0 ; i < 10; i++) {
            ProducerRecord<Void,Integer> record = new ProducerRecord<Void,Integer>("total-connected-devices", random.nextInt(100));
            producer.send(record);
            printRecord(record);
        }

        producer.close();
    }

    private static void printRecord(ProducerRecord record) {
        System.out.println("Sent record:");
        System.out.println("\tTopic = " + record.topic());
        System.out.println("\tPartition = " + record.partition());
        System.out.println("\tKey = " + record.key());
        System.out.println("\tValue = " + record.value());
    }
}
