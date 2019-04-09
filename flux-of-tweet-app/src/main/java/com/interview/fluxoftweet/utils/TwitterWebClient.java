package com.interview.fluxoftweet.utils;

import com.interview.twitterauth.TwitterOAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class TwitterWebClient {

    private WebClient webClient;
    private final TwitterOAuth twitterOAuth;

    /**
     * Reactive web client to consume twitter stream api
     * @param url
     * @param body
     * @param clazz
     * @param <T>
     * @return
     */
    public  <T> Flux<T> getFromTwitterStreamAPI(String url, MultiValueMap<String, String> body, Class<T> clazz) {

        webClientBuilder(body);

        return webClient
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .onStatus(HttpStatus::isError,
                        clientResponse -> {
                            log.error("Http Status : " + clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class).flatMap(text -> Mono.error(new RuntimeException(text)));
                        }
                )
                .bodyToFlux(clazz)
                .onErrorMap(t -> {
                    log.error("Exception when calling twitter api", t);
                    return t;
                });
    }

    /**
     * Reactive web client builder with oauth header.
     * @param body
     */
    private void webClientBuilder(MultiValueMap<String, String> body) {
        webClient = WebClient.builder()
                .filter((currentRequest, next) ->
                        next.exchange(ClientRequest.from(currentRequest)
                                .header(HttpHeaders.AUTHORIZATION, twitterOAuth.oAuth1Header(
                                        currentRequest.url(), currentRequest.method(), body.toSingleValueMap()))
                                .build())).build();
    }

}
