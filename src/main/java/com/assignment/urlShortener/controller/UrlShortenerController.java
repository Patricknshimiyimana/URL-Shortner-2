package com.assignment.urlShortener.controller;

import com.assignment.urlShortener.dto.UrlRequestDto;
import com.assignment.urlShortener.model.UrlMapping;
import com.assignment.urlShortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerController {
    private final UrlShortenerService service;

    @GetMapping("/")
    public String index() {
        return "Welcome to URL - Shortener Service!";
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlMapping> createShortUrl(@Valid @RequestBody UrlRequestDto request) {
        log.info("Received request to shorten URL: {}", request.getUrl());
        UrlMapping urlMapping = service.createShortUrl(
                request.getUrl(),
                request.getCustomId(),
                request.getTtlHours()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(urlMapping);
    }

    @GetMapping("/{shortId}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortId) {
        log.info("Received request to redirect short URL: {}", shortId);
        String longUrl = service.getLongUrl(shortId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }

    @DeleteMapping("/{shortId}")
    public ResponseEntity<Void> deleteShortUrl(@PathVariable String shortId) {
        log.info("Received request to delete short URL: {}", shortId);
        service.deleteUrl(shortId);
        return ResponseEntity.noContent().build();
    }
}