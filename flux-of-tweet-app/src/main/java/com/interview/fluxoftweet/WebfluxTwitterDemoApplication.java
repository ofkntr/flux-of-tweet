package com.interview.fluxoftweet;

import com.interview.fluxoftweet.model.Tweet;
import com.interview.fluxoftweet.utils.TwitterWebClient;
import com.interview.fluxoftweet.service.TweetService;
import com.interview.fluxoftweet.streaming.KafkaTweetProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class WebfluxTwitterDemoApplication {

    private final TwitterWebClient twitterWebClient;
    private final KafkaTweetProducer kafkaTweetProducer;
    private final TweetService tweetService;

    @Value("${twitter.stream.api.url}")
    private String twitterStreamApiUrl;

    public static void main(String[] args) {
        SpringApplication.run(WebfluxTwitterDemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner tweetTrackInitializer() {
        return args -> {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

            String tracks = "#trump";
            if (args.length > 0) {
                log.info("Using arguments as tracks");
                tracks = String.join(",", args);
            }

            log.info("Filter tracks [{}]", tracks);
            body.add("track", tracks);

            Flux<Tweet> tweets =
                    twitterWebClient.getFromTwitterStreamAPI(twitterStreamApiUrl, body, Tweet.class)
                            .onErrorContinue((throwable, tweet) -> {
                                log.error("Exception when getting data from Twitter, tweet : {}", tweet, throwable);
                            });

            tweets.flatMap(tweet -> {
                Mono<Tweet> kafkaMono = kafkaTweetProducer.send(tweet).map(a -> tweet);
                Mono<Tweet> h2Mono = tweetService.saveTweet(tweet);
                return Mono.zip(kafkaMono, h2Mono).map(a -> a.getT2());
            }).subscribe(System.out::println);

        };
    }

}
