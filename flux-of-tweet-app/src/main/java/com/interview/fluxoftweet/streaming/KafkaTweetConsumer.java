package com.interview.fluxoftweet.streaming;

import com.interview.fluxoftweet.model.Tweet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.internals.DefaultKafkaReceiver;
import reactor.kafka.receiver.internals.DefaultKafkaReceiverClose;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaTweetConsumer {

    private static final String JSON_TRUSTED_PACKAGES = "com.interview.fluxoftweet.model";
    private static final String CONSUME_LATEST = "latest";

    private Flux<Tweet> tweetFlux;
    private KafkaReceiver<String, Tweet> kafkaReceiver;

    @Value("${kafka.bootstarp.server}")
    private String bootstrapServers;

    @Value("${kafka.topic.name}")
    private String topicName;

    @Value("${kafka.group-id.name}")
    private String groupName;

    /**
     * Create Kafka receiver and receive kafka events to pubkish the events with non-blocking way.
     */
    @PostConstruct
    public void init() {
        ReceiverOptions<String, Tweet> receiverOptions = getReceiverOptions(bootstrapServers,
                topicName, groupName);

        kafkaReceiver = KafkaReceiver.create(receiverOptions);

        tweetFlux = kafkaReceiver.receiveAutoAck().concatMap(r -> r)
                .map(receiverRecord -> receiverRecord.value())
                .onErrorContinue((exception, user) ->
                        log.error("Exception when reading tweet data from kafka : {}", user, exception))
                .publish().autoConnect();

        tweetFlux.publishOn(Schedulers.fromExecutor(Executors.newFixedThreadPool(10))).subscribe().dispose();
    }

    /**
     * Setup Kafka receiver options.
     * @param bootstrapServers
     * @param topic
     * @param groupId
     * @return
     */
    private ReceiverOptions<String, Tweet> getReceiverOptions(String bootstrapServers,
                                                              String topic, String groupId) {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, JSON_TRUSTED_PACKAGES);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, CONSUME_LATEST);

        return ReceiverOptions.<String, Tweet>create(consumerProps)
                .subscription(Collections.singleton(topic));
    }

    /**
     * @return Live stream tweets
     */
    public Flux<Tweet> getTweeterStream() {
        return this.tweetFlux;
    }

    /**
     *  When spring context is closed kafka receiver will close safe way.
     */
    @PreDestroy
    public void onShutdown() {
        DefaultKafkaReceiverClose defaultKafkaReceiverClose = new DefaultKafkaReceiverClose();
        log.info("Closing kafka twitter receiver");
        defaultKafkaReceiverClose.close((DefaultKafkaReceiver) kafkaReceiver);
    }


}
