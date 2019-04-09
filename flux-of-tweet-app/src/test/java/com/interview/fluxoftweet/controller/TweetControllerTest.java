package com.interview.fluxoftweet.controller;

import com.interview.fluxoftweet.model.Tweet;
import com.interview.fluxoftweet.model.User;
import com.interview.fluxoftweet.service.TweetService;
import com.interview.fluxoftweet.streaming.KafkaTweetConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(value = MockitoJUnitRunner.class)
public class TweetControllerTest {

    @InjectMocks
    TweetController tweetController;

    @Mock
    TweetService tweetService;

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
    public void should_return_home_page() {

        final TestPublisher<String> testPublisher = TestPublisher.create();

        StepVerifier.create(tweetController.home())
                .then(() -> testPublisher.emit("home"))
                .expectNext("home")
                .verifyComplete();
    }

    @Test
    public void should_return_group_by_user_page() {

        final TestPublisher<String> testPublisher = TestPublisher.create();

        StepVerifier.create(tweetController.userTweets())
                .then(() -> testPublisher.emit("group-by-user"))
                .expectNext("group-by-user")
                .verifyComplete();
    }

    @Test
    public void should_return_order_tweets_page() {

        final TestPublisher<String> testPublisher = TestPublisher.create();

        StepVerifier.create(tweetController.orderTweets())
                .then(() -> testPublisher.emit("order-by-tweets"))
                .expectNext("order-by-tweets")
                .verifyComplete();
    }

    @Test
    public void should_get_live_tweets() {

        final TestPublisher<Tweet> testPublisher = TestPublisher.create();

        when(tweetService.getCurrentLiveStreamTweet()).thenReturn(tweets);

        Flux<Tweet> tweetFlux = tweetController.tweets();

        StepVerifier.create(tweetFlux)
                .then(() -> testPublisher.emit(tweet))
                .expectNext(tweet)
                .verifyComplete();
    }

    @Test
    public void should_groupBy_user_tweets() {
        final TestPublisher<List<Tweet>> testPublisher = TestPublisher.create();

        when(tweetService.groupByUserTweetAssendingOrderByCreatedAt()).thenReturn(tweets);

        Flux<Tweet> tweetFlux = tweetController.groupByUserTweets();

        StepVerifier.create(tweetFlux)
                .then(() -> testPublisher.emit(listOfTweet))
                .expectNext(tweet)
                .verifyComplete();
    }

    @Test
    public void should_get_tweets_orderBy_createdDate() {

        final TestPublisher<List<Tweet>> testPublisher = TestPublisher.create();

        when(tweetService.getTweetsAssendingOrderByCreatedAt()).thenReturn(tweets);

        Flux<Tweet> tweetFlux = tweetController.getTweetsOrderByCreatedDate();

        StepVerifier.create(tweetFlux)
                .then(() -> testPublisher.emit(listOfTweet))
                .expectNext(tweet)
                .verifyComplete();

    }







}