package com.interview.fluxoftweet.service;

import com.interview.fluxoftweet.model.Tweet;
import com.interview.fluxoftweet.model.User;
import com.interview.fluxoftweet.repository.TweetRepository;
import com.interview.fluxoftweet.streaming.KafkaTweetConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(value = MockitoJUnitRunner.class)
public class TweetServiceTest {

    @InjectMocks
    TweetService tweetService;

    @Mock
    TweetRepository tweetRepository;

    @Mock
    KafkaTweetConsumer kafkaTweetConsumer;

    User user;

    Tweet tweet;

    List<Tweet> listOfTweet;

    Flux<Tweet> tweets;

    @Before
    public void setUp() throws Exception {
        user = User
                .builder()
                .id(1L)
                .twitterUserId("917367105896570880")
                .createdAt(new Date())
                .name("Kunter")
                .screenName("@Kunter")
                .location("Delft")
                .build();

        tweet = Tweet
                .builder()
                .id(2L)
                .tweetId("1114904893322604551")
                .createdAt(new Date())
                .text("Hoihoi")
                .user(user)
                .build();

        listOfTweet = Arrays.asList(tweet);

        tweets = Flux.fromIterable(listOfTweet);
    }

    @Test
    public void should_get_live_stream_tweet() {
        final TestPublisher<List<Tweet>> testPublisher = TestPublisher.create();

        when(kafkaTweetConsumer.getTweeterStream()).thenReturn(tweets);

        Flux<Tweet> tweetFlux = tweetService.getCurrentLiveStreamTweet();

        StepVerifier.create(tweetFlux)
                .then(() -> testPublisher.emit(listOfTweet))
                .expectNext(tweet)
                .verifyComplete();

    }

    @Test
    public void should_save_tweet() {
        final TestPublisher<Tweet> testPublisher = TestPublisher.create();

        when(tweetRepository.save(tweet)).thenReturn(tweet);

        Mono<Tweet> tweetMono = tweetService.saveTweet(tweet);

        StepVerifier.create(tweetMono)
                .then(() -> testPublisher.emit(tweet))
                .expectNext(tweet)
                .verifyComplete();
    }

    @Test
    public void should_get_tweet() {

        final TestPublisher<Tweet> testPublisher = TestPublisher.create();

        when(tweetRepository.findByTweetId("1114904893322604551")).thenReturn(Optional.ofNullable(tweet));

        Mono<Tweet> tweetMono = tweetService.getTweet("1114904893322604551");

        StepVerifier.create(tweetMono)
                .then(() -> testPublisher.emit(tweet))
                .expectNext(tweet)
                .verifyComplete();

    }

    @Test
    public void should_groupBy_user_tweet_assending_orderBy_createdAt() {

        final TestPublisher<List<Tweet>> testPublisher = TestPublisher.create();

        when(tweetRepository.groupByUserTweetAssendingOrderByCreatedAt()).thenReturn(Optional.ofNullable(listOfTweet));

        Flux<Tweet> tweetFlux = tweetService.groupByUserTweetAssendingOrderByCreatedAt();

        StepVerifier.create(tweetFlux)
                .then(() -> testPublisher.emit(listOfTweet))
                .expectNext(tweet)
                .verifyComplete();

    }

    @Test
    public void should_get_tweets_assending_orderBy_createdAt() {

        final TestPublisher<List<Tweet>> testPublisher = TestPublisher.create();

        when(tweetRepository.getTweetsAssendingOrderByCreatedAt()).thenReturn(Optional.ofNullable(listOfTweet));

        Flux<Tweet> tweetFlux = tweetService.getTweetsAssendingOrderByCreatedAt();

        StepVerifier.create(tweetFlux)
                .then(() -> testPublisher.emit(listOfTweet))
                .expectNext(tweet)
                .verifyComplete();

    }

}