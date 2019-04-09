package com.interview.fluxoftweet.streaming;

import com.interview.fluxoftweet.model.Tweet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaTweetProducer {

    @Value("${kafka.bootstarp.server}")
    private String bootstrapServers;

    @Value("${kafka.topic.name}")
    private String topicName;

    private KafkaSender<String, Tweet> tweetKafkaSender;

    /**
     * Setup Kafka producer options.
     */
    @PostConstruct
    public void init() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        SenderOptions<String, Tweet> senderOptions = SenderOptions.create(producerProps);
        tweetKafkaSender = KafkaSender.create(senderOptions);
    }

    /**
     * Create kafka event with tweet id.
     * @param tweet
     * @return
     */
    public Mono<Tweet> send(Tweet tweet) {
        SenderRecord<String, Tweet, String> senderRecord
                = SenderRecord.create(
                        new ProducerRecord<>(topicName, tweet.getTweetId(), tweet),
                tweet.getTweetId());
        return tweetKafkaSender.send(Mono.just(senderRecord)).next().map(any -> tweet);
    }

    /**
     * When spring context is closed kafka sender will close safe way.
     */
    @PreDestroy
    public void onShutdown() {
        if (Optional.ofNullable(tweetKafkaSender).isPresent()) {
            log.info("Closing kafka twitter Sender");
            tweetKafkaSender.close();
        }
    }

}
