package com.interview.twitterauth;

import com.google.api.client.auth.oauth.*;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.IOException;
import java.util.Scanner;

public class TwitterSecretAndTokenGenerator {

    private final String consumerKey;
    private final String consumerSecret;

    private HttpRequestFactory factory;

    private static final String CONSUMER_KEY = "RLSrphihyR4G2UxvA0XBkLAdl";
    private static final String CONSUMER_SECRET = "FTz2KcP1y3pcLw0XXMX5Jy3GTobqUweITIFy4QefullmpPnKm4";
    private static final HttpTransport TRANSPORT = new NetHttpTransport();
    private static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
    private static final String ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
    private static final String REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";

    public static void main(String[] args) throws TwitterAuthenticationException {
        TwitterSecretAndTokenGenerator twitterAuthenticator = new TwitterSecretAndTokenGenerator(CONSUMER_KEY, CONSUMER_SECRET);
        twitterAuthenticator.getAuthorizedHttpRequestFactory();
    }

    /**
     * Create a new TwitterAuthenticator
     *
     * @param consumerKey The OAuth consumer key
     * @param consumerSecret The OAuth consumer secret
     */
    public TwitterSecretAndTokenGenerator(final String consumerKey, final String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    /**
     * Lazily initialize an HTTP request factory which embeds the OAuth tokens required by the Twitter APIs
     *
     * @return The authenticated HTTP request factory
     */
    public synchronized HttpRequestFactory getAuthorizedHttpRequestFactory() throws TwitterAuthenticationException {
        if (factory != null) {
            return factory;
        }

        factory = createRequestFactory();
        return factory;
    }

    /**
     * Create a new authenticated HTTP request factory which embeds the OAuth tokens required by the Twitter APIs
     *
     * @return The authenticated HTTP request factory
     */
    private HttpRequestFactory createRequestFactory() throws TwitterAuthenticationException {
        OAuthHmacSigner signer = new OAuthHmacSigner();
        signer.clientSharedSecret = consumerSecret;

        OAuthCredentialsResponse requestTokenResponse = getTemporaryToken(signer);
        signer.tokenSharedSecret = requestTokenResponse.tokenSecret;

        OAuthAuthorizeTemporaryTokenUrl authorizeUrl = new OAuthAuthorizeTemporaryTokenUrl(AUTHORIZE_URL);
        authorizeUrl.temporaryToken = requestTokenResponse.token;


        String providedPin = retrievePin(authorizeUrl);

        OAuthCredentialsResponse accessTokenResponse = retrieveAccessTokens(providedPin, signer, requestTokenResponse.token);
        signer.tokenSharedSecret = accessTokenResponse.tokenSecret;

        System.out.println("Secret : " + accessTokenResponse.tokenSecret);
        System.out.println("Token : " + accessTokenResponse.token);

        OAuthParameters parameters = new OAuthParameters();
        parameters.consumerKey = consumerKey;
        parameters.token = accessTokenResponse.token;
        parameters.signer = signer;


        return TRANSPORT.createRequestFactory(parameters);
    }

    /**
     * Retrieve the initial temporary tokens required to obtain the acces token
     *
     * @param signer The HMAC signer used to cryptographically sign requests to Twitter
     * @return The response containing the temporary tokens
     */
    private OAuthCredentialsResponse getTemporaryToken(final OAuthHmacSigner signer) throws TwitterAuthenticationException {
        OAuthGetTemporaryToken requestToken = new OAuthGetTemporaryToken(REQUEST_TOKEN_URL);
        requestToken.consumerKey = consumerKey;
        requestToken.transport = TRANSPORT;
        requestToken.signer = signer;

        OAuthCredentialsResponse requestTokenResponse;
        try {
            requestTokenResponse = requestToken.execute();
        } catch (IOException e) {
            throw new TwitterAuthenticationException("Unable to aquire temporary token: " + e.getMessage(), e);
        }

        System.out.println("Aquired temporary token...\n");

        return requestTokenResponse;
    }

    /**
     * Guide the user to obtain a PIN from twitter to authorize the requests
     *
     * @param authorizeUrl The URL embedding the temporary tokens to be used to request the PIN
     * @return The PIN as it is entered by the user after following the Twitter OAuth wizard
     */
    private String retrievePin(final OAuthAuthorizeTemporaryTokenUrl authorizeUrl) throws TwitterAuthenticationException {
        String providedPin;
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Go to the following link in your browser:\n" + authorizeUrl.build());
            System.out.println("\nPlease enter the retrieved PIN:");
            providedPin = scanner.nextLine();
        } finally {
            scanner.close();
        }

        if (providedPin == null) {
            throw new TwitterAuthenticationException("Unable to read entered PIN");
        }

        return providedPin;
    }

    /**
     * Exchange the temporary token and the PIN for an access token that can be used to invoke Twitter APIs
     *
     * @param providedPin The PIN that the user obtained when following the Twitter OAuth wizard
     * @param signer The HMAC signer used to cryptographically sign requests to Twitter
     * @param token The temporary token to be exchanged for the access token
     * @return The access token that can be used to invoke Twitter APIs
     */
    private OAuthCredentialsResponse retrieveAccessTokens(final String providedPin, final OAuthHmacSigner signer, final String token) throws TwitterAuthenticationException {
        OAuthGetAccessToken accessToken = new OAuthGetAccessToken(ACCESS_TOKEN_URL);
        accessToken.verifier = providedPin;
        accessToken.consumerKey = consumerSecret;
        accessToken.signer = signer;
        accessToken.transport = TRANSPORT;
        accessToken.temporaryToken = token;

        OAuthCredentialsResponse accessTokenResponse;
        try {
            accessTokenResponse = accessToken.execute();
        } catch (IOException e) {
            throw new TwitterAuthenticationException("Unable to authorize access: " + e.getMessage(), e);
        }
        System.out.println("\nAuthorization was successful");

        return accessTokenResponse;
    }
}
