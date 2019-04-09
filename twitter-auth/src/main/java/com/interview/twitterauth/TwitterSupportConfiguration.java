package com.interview.twitterauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwitterSupportConfiguration {

    @Bean
    public TwitterProperties twitterProperties() {
        return new TwitterProperties();
    }

    @Bean TwitterOAuth twitterAuth(TwitterProperties twitterProperties) {
        return new TwitterOAuth(twitterProperties);
    }

}
