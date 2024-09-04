package ru.easycraft.bff.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("api")
@CrossOrigin(originPatterns = "http://localhost:[*]", allowCredentials = "true") // Только для DEV
@Slf4j
public class BffController {


    @GetMapping(value = "/bff/manifest", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> manifest(@CookieValue(value = "access_token", required = false) String accessToken) {
        log.info("GET manifest. Cookie access_token: '{}'", accessToken);

        if (!StringUtils.hasText(accessToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return Mono.just("""
                {
                  "mfe" : "http://localhost:4201/remoteEntry.json"
                }""");
    }

    @GetMapping(value = "/bff/login")
    public Mono<Void> login(@RequestParam(name = "redirect_uri") String redirectUri, ServerHttpResponse response) {
        log.info("GET login. redirect_uri: '{}'", redirectUri);

        response.addCookie(
                ResponseCookie.from("access_token", "123")
                        .httpOnly(true)
                        .path("/")
                        .build());
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create(redirectUri));
        return response.setComplete();
    }

    @GetMapping(value = "/sso/userinfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> userinfo(@CookieValue(value = "access_token", required = false) String accessToken) {
        log.info("GET userinfo. Cookie access_token: '{}'", accessToken);

        if (!StringUtils.hasText(accessToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return Mono.just("""
                {
                  "id" : "777",
                  "name" : "Анастасия"
                }""");
    }


}
