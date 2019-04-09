package com.interview.twitterauth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "twitter")
public class TwitterProperties {

    /**
     * Consumer Key (API Key)
     */
    private String consumerKey;

    /**
     * Consumer Secret (API Secret)
     */
    private String consumerSecret;

    /**
     * Access Token
     */
    private String token;

    /**
     * Access Token Secret
     */
    private String secret;

}
