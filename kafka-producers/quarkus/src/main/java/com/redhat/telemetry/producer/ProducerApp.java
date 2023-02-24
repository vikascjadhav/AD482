package com.redhat.telemetry.producer;

import java.time.Duration;
import java.util.Properties;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProducerApp {

    private static final Logger LOG = Logger.getLogger(ProducerApp.class);

    private final  static Random random = new Random();

    public static Properties configuProperties() {

        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, " my-cluster-kafka-bootstrap-gyvrve-kafka-cluster.apps.ap46a.prod.ole.redhat.com:443");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, props);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, props);
        props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG , "SSL");

        props.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "C:\\Users\\vikas\\AD482\\truststore.jks");
        props.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password");
        return props;

    }

    public static void printRecord(ProducerRecord<Void,Integer> record) {
        System.out.println(record.value());
    }

 @Outgoing("device-temperatures") 
public Multi<Record<String, Integer>> generate() {
    return Multi.createFrom().ticks().every(Duration.ofSeconds(1)) 
            .onOverflow().drop()
            .map(tick -> {
                String currentDevice = "device-" + random.nextInt(10);
                int currentMeasure = random.nextInt(100);

                LOG.infov("Device ID: {0}, measure: {1}",
                        currentDevice,
                        currentMeasure
                );

                return Record.of(currentDevice, currentMeasure); 
            });
}
    public static void main(String[] args) {
       
    }

}
