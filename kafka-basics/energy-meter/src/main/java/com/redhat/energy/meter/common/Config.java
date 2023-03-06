package com.redhat.energy.meter.common;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;

import java.util.Properties;

public class Config {
    protected static void configureConnectionSecurity(Properties props) {
        // TODO: configure the connection protocol
         props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        // TODO: configure the path to the truststore file
        props.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "C:\\Users\\vikas\\AD482\\truststore.jks");
        props.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password");
        // TODO: configure the truststore password
    }
}
