package com.assignment.urlShortener.controller;

import com.assignment.urlShortener.dto.UrlRequestDto;
import com.assignment.urlShortener.model.UrlMapping;
import com.assignment.urlShortener.service.UrlShortenerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@Tag(name = "URL Shortener", description = "URL Shortener API endpoints")
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerController {
    private final UrlShortenerService service;

    @GetMapping("/")
    public String index() {
        return "Welcome to URL - Shortener Service!";
    }

    @Operation(summary = "Create a short URL", description = "Creates a shortened URL from a long URL with optional custom ID and TTL")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "URL successfully shortened", content = @Content(schema = @Schema(implementation = UrlMapping.class))),
            @ApiResponse(responseCode = "409", description = "Custom ID already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/shorten")
    public ResponseEntity<UrlMapping> createShortUrl(
            @Parameter(description = "URL details", required = true) @Valid @RequestBody UrlRequestDto request) {
        log.info("Received request to shorten URL: {}", request.getUrl());
        UrlMapping urlMapping = service.createShortUrl(
                request.getUrl(),
                request.getCustomId(),
                request.getTtlHours());
        return ResponseEntity.status(HttpStatus.CREATED).body(urlMapping);
    }

    @Operation(summary = "Redirect to long URL", description = "Redirects to the original long URL using the short ID")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirect to long URL"),
            @ApiResponse(responseCode = "404", description = "URL not found")
    })
    @GetMapping("/{shortId}")
    public ResponseEntity<Void> redirectToLongUrl(
            @Parameter(description = "Short URL ID", required = true) @PathVariable String shortId) {
        log.info("Received request to redirect short URL: {}", shortId);
        String longUrl = service.getLongUrl(shortId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }

    @Operation(summary = "Delete a short URL", description = "Deletes a shortened URL by its shortId")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "URL successfully deleted"),
            @ApiResponse(responseCode = "404", description = "URL not found")
    })
    @DeleteMapping("/{shortId}")
    public ResponseEntity<Void> deleteShortUrl(
            @Parameter(description = "Short URL ID to delete", required = true) @PathVariable String shortId) {
        log.info("Received request to delete short URL: {}", shortId);
        service.deleteUrl(shortId);
        return ResponseEntity.noContent().build();
    }
}