package com.interview.fluxoftweet.service;

import com.interview.fluxoftweet.exceptions.MissingParametterException;
import com.interview.fluxoftweet.exceptions.TweetNotFoundException;
import com.interview.fluxoftweet.model.Tweet;
import com.interview.fluxoftweet.repository.TweetRepository;
import com.interview.fluxoftweet.streaming.KafkaTweetConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TweetService {

    private final TweetRepository tweetRepository;
    private final KafkaTweetConsumer kafkaTweetConsumer;

    /**
     * @return current live stream tweet from kafka
     */
    public Flux<Tweet> getCurrentLiveStreamTweet() {
        return kafkaTweetConsumer.getTweeterStream();
    }

    /**
     * Save live stream to h2 database with non-blocking way
     * @param tweet
     * @return
     */
    public Mono<Tweet> saveTweet(Tweet tweet) {
        return Mono.defer(() -> Mono.just(tweetRepository.save(tweet)))
                .subscribeOn(Schedulers.fromExecutor(Executors.newFixedThreadPool(10)));
    }

    /**
     * Get saved tweet from h2 by tweet id
     * @param id
     * @return
     */
    public Mono<Tweet> getTweet(String id) {
        if (!Optional.ofNullable(id).isPresent()) return Mono.error(MissingParametterException::new);

        return Mono.defer(() -> Mono.justOrEmpty(tweetRepository.findByTweetId(id).orElseThrow(TweetNotFoundException::new)))
                .timeout(Duration.ofMillis(1000))
                .subscribeOn(Schedulers.fromExecutor(Executors.newFixedThreadPool(10)));

    }

    /**
     * @return The messages grouped by user (users sorted chronologically, ascending)
     */
    public Flux<Tweet> groupByUserTweetAssendingOrderByCreatedAt() {
        return Flux.defer(() -> Flux.fromIterable(tweetRepository.groupByUserTweetAssendingOrderByCreatedAt()
                .orElseThrow(TweetNotFoundException::new)))
                .subscribeOn(Schedulers.fromExecutor(Executors.newFixedThreadPool(10)));
    }

    /**
     * @return The messages per user should also be sorted chronologically, ascending
     */
    public Flux<Tweet> getTweetsAssendingOrderByCreatedAt() {
        return Flux.defer(() -> Flux.fromIterable(tweetRepository.getTweetsAssendingOrderByCreatedAt()
                .orElseThrow(TweetNotFoundException::new)))
                .subscribeOn(Schedulers.fromExecutor(Executors.newFixedThreadPool(10)));
    }

}

