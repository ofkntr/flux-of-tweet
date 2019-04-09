package com.interview.fluxoftweet.controller;

import com.interview.fluxoftweet.model.Tweet;
import com.interview.fluxoftweet.streaming.KafkaTweetConsumer;
import com.interview.fluxoftweet.service.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Controller
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;

    /**
     * @return home view
     */
    @GetMapping("/")
    public Mono<String> home() {
        return Mono.just("home");
    }

    /**
     * @return group-by-user view
     */
    @GetMapping("/user-tweets")
    public Mono<String> userTweets() {
        return Mono.just("group-by-user");
    }

    /**
     * @return order-by-tweets view
     */
    @GetMapping("/order-tweets")
    public Mono<String> orderTweets() {
        return Mono.just("order-by-tweets");
    }

    /**
     * @return current live stream tweet from kafka
     */
    @GetMapping(value = "/live-tweets", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<Tweet> tweets() {
        return tweetService.getCurrentLiveStreamTweet();
    }

    /**
     * @return The messages grouped by user (users sorted chronologically, ascending)
     */
    @GetMapping(value = "/groupByUserTweets", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<Tweet> groupByUserTweets() {
        return tweetService.groupByUserTweetAssendingOrderByCreatedAt()
                .delayElements(Duration.ofMillis(1000));
    }

    /**
     * @return The messages per user should also be sorted chronologically, ascending
     */
    @GetMapping(value = "/getTweetsOrderByCreatedDate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<Tweet> getTweetsOrderByCreatedDate() {
        return tweetService.getTweetsAssendingOrderByCreatedAt()
                .delayElements(Duration.ofMillis(1000));
    }

}
