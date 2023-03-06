package com.redhat.training.bank.stream;

import com.redhat.training.bank.event.AmountWasWithdrawn;
import com.redhat.training.bank.event.HighRiskWithdrawnWasDetected;
import com.redhat.training.bank.event.LowRiskWithdrawnWasDetected;
import com.redhat.training.bank.event.ModerateRiskWithdrawnWasDetected;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class AmountWasWithdrawnPipeline extends StreamProcessor {
    private static final Logger LOGGER = Logger.getLogger(AmountWasWithdrawnPipeline.class);

    // Reading topics
    static final String AMOUNT_WAS_WITHDRAWN_TOPIC = "bank-account-withdrawn";

    // Writing topics
    static final String LOW_RISK_WITHDRAWN_TOPIC = "low-risk-withdrawn-alert";
    static final String MODERATE_RISK_WITHDRAWN_TOPIC = "moderate-risk-withdrawn-alert";
    static final String HIGH_RISK_WITHDRAWN_TOPIC = "high-risk-withdrawn-alert";

    private KafkaStreams streams;

    private ObjectMapperSerde<LowRiskWithdrawnWasDetected> lowRiskEventSerde;
    private ObjectMapperSerde<ModerateRiskWithdrawnWasDetected> moderateRiskEventSerde;
    private ObjectMapperSerde<HighRiskWithdrawnWasDetected> highRiskEventSerde;

    void onStart(@Observes StartupEvent startupEvent) {
        StreamsBuilder builder = new StreamsBuilder();

        ObjectMapperSerde<AmountWasWithdrawn> withdrawalEventSerde
                = new ObjectMapperSerde<>(AmountWasWithdrawn.class);

        lowRiskEventSerde = new ObjectMapperSerde<>(LowRiskWithdrawnWasDetected.class);
        moderateRiskEventSerde = new ObjectMapperSerde<>(ModerateRiskWithdrawnWasDetected.class);
        highRiskEventSerde = new ObjectMapperSerde<>(HighRiskWithdrawnWasDetected.class);

        // TODO: Add inverse filter

        KStream<Long,AmountWasWithdrawn> mainStream = builder.stream(AMOUNT_WAS_WITHDRAWN_TOPIC,
        Consumed.with(Serdes.Long(), withdrawalEventSerde)).filterNot((k,v)->v.amount<50);


        // TODO: Split the stream
        mainStream.split().
        branch((key,widrawal) -> widrawal.amount > 50 && widrawal.amount <=1000, Branched.withConsumer(this::processLowAmountEvents)).
        branch((key,widrawal) -> widrawal.amount >1000 && widrawal.amount <=3000, Branched.withConsumer(this::processModerateAmountEvents)).
        branch((key,widrawal) -> true, Branched.withConsumer(this::processHighAmountEvents));

        // TODO: Create a Kafka streams and start it

        KafkaStreams streams = new KafkaStreams(builder.build(), generateStreamConfig());
        streams.start();
    }

    private void processLowAmountEvents(KStream<Long, AmountWasWithdrawn> stream) {
        // TODO: process the low amount branch
        stream.map((k,v) -> {
            logLowRiskWithdrawn(v.bankAccountId, v.amount);
            return new KeyValue<>(v.bankAccountId,new LowRiskWithdrawnWasDetected(v.bankAccountId,v.amount));
        } ).to(LOW_RISK_WITHDRAWN_TOPIC, Produced.with(Serdes.Long(), lowRiskEventSerde));;

    }

    private void processModerateAmountEvents(KStream<Long, AmountWasWithdrawn> stream) {
        // TODO: process the moderate amount branch
        stream.map((k,v) -> {
            logModerateRiskWithdrawn(v.bankAccountId, v.amount);
            return new KeyValue<>(v.bankAccountId, new ModerateRiskWithdrawnWasDetected(v.bankAccountId,v.amount));
        }).to(MODERATE_RISK_WITHDRAWN_TOPIC, Produced.with(Serdes.Long(), moderateRiskEventSerde));
    }

    private void processHighAmountEvents(KStream<Long, AmountWasWithdrawn> stream) {
        // TODO: process the high amount branch
        stream.map((k,v)->{
            logHighRiskWithdrawn(v.bankAccountId, v.amount);
            return new KeyValue<>(v.bankAccountId,  new HighRiskWithdrawnWasDetected(v.bankAccountId, v.amount));
        }).to(HIGH_RISK_WITHDRAWN_TOPIC, Produced.with(Serdes.Long(), highRiskEventSerde));

    }

    void onStop(@Observes ShutdownEvent shutdownEvent) {
        // TODO: Close the stream on shutdown
        streams.close();
    }

    // Helper methods
    private void logLowRiskWithdrawn(Long bankAccountId, Long amount) {
        LOGGER.infov(
                "Low Risk Withdrawn - Account ID: {0} Amount: {1}",
                bankAccountId,
                amount);
    }

    private void logModerateRiskWithdrawn(Long bankAccountId, Long amount) {
        LOGGER.infov(
                "Moderate Risk Withdrawn - Account ID: {0} Amount: {1}",
                bankAccountId,
                amount);
    }

    private void logHighRiskWithdrawn(Long bankAccountId, Long amount) {
        LOGGER.infov(
                "High Risk Withdrawn - Account ID: {0} Amount: {1}",
                bankAccountId,
                amount);
    }
}
