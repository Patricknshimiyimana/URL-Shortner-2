package com.assignment.urlShortener.service;

import com.assignment.urlShortener.exception.UrlNotFoundException;
import com.assignment.urlShortener.exception.CustomIdAlreadyExistsException;
import com.assignment.urlShortener.model.UrlMapping;
import com.assignment.urlShortener.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlShortenerService {
    private final UrlMappingRepository repository;
    private static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_ID_LENGTH = 6;
    private final Random random = new Random();

    public UrlMapping createShortUrl(String longUrl, String customId, Long ttlHours) {
        String shortId = (customId != null && !customId.trim().isEmpty()) ? customId : generateShortId();
        
        if (customId != null && !customId.trim().isEmpty() && repository.existsById(customId)) {
            log.error("Custom ID {} already exists", customId);
            throw new CustomIdAlreadyExistsException("Custom ID already exists");
        }

        UrlMapping mapping = new UrlMapping();
        mapping.setShortId(shortId);
        mapping.setLongUrl(longUrl);
        mapping.setCreatedAt(LocalDateTime.now());
        
        if (ttlHours != null) {
            mapping.setExpiresAt(LocalDateTime.now().plusHours(ttlHours));
        }

        log.debug("Creating new URL mapping: {} -> {}", shortId, longUrl);
        return repository.save(mapping);
    }

    public String getLongUrl(String shortId) {
        UrlMapping mapping = repository.findById(shortId)
                .orElseThrow(() -> new UrlNotFoundException("URL not found"));

        if (mapping.getExpiresAt() != null && mapping.getExpiresAt().isBefore(LocalDateTime.now())) {
            repository.delete(mapping);
            throw new UrlNotFoundException("URL has expired");
        }

        return mapping.getLongUrl();
    }

    public void deleteUrl(String shortId) {
        if (!repository.existsById(shortId)) {
            throw new UrlNotFoundException("URL not found");
        }
        repository.deleteById(shortId);
        log.debug("Deleted URL mapping with ID: {}", shortId);
    }

    private String generateShortId() {
        StringBuilder shortId;
        do {
            shortId = new StringBuilder();
            for (int i = 0; i < SHORT_ID_LENGTH; i++) {
                shortId.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
            }
        } while (repository.existsById(shortId.toString()));
        
        return shortId.toString();
    }
} 