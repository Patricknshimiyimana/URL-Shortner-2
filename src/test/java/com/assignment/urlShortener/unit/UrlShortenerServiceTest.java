package com.assignment.urlShortener.unit;

import com.assignment.urlShortener.exception.CustomIdAlreadyExistsException;
import com.assignment.urlShortener.exception.UrlNotFoundException;
import com.assignment.urlShortener.model.UrlMapping;
import com.assignment.urlShortener.repository.UrlMappingRepository;
import com.assignment.urlShortener.service.UrlShortenerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlShortenerServiceTest {

    @Mock
    private UrlMappingRepository repository;

    private UrlShortenerServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UrlShortenerServiceImpl(repository);
    }

    @Test
    void createShortUrl_WithValidUrl_ShouldCreateMapping() {
        String longUrl = "https://example.com/long";
        UrlMapping expectedMapping = new UrlMapping();
        expectedMapping.setLongUrl(longUrl);
        when(repository.save(any(UrlMapping.class))).thenReturn(expectedMapping);

        UrlMapping result = service.createShortUrl(longUrl, null, null);

        assertNotNull(result);
        assertEquals(longUrl, result.getLongUrl());
        verify(repository).save(any(UrlMapping.class));
    }

    @Test
    void createShortUrl_WithCustomId_ShouldUseCustomId() {
        String longUrl = "https://example.com/long";
        String customId = "custom";
        when(repository.existsById(customId)).thenReturn(false);

        service.createShortUrl(longUrl, customId, null);

        ArgumentCaptor<UrlMapping> mappingCaptor = ArgumentCaptor.forClass(UrlMapping.class);
        verify(repository).save(mappingCaptor.capture());
        assertEquals(customId, mappingCaptor.getValue().getShortId());
    }

    @Test
    void createShortUrl_WithExistingCustomId_ShouldThrowException() {
        String customId = "existing";
        when(repository.existsById(customId)).thenReturn(true);

        assertThrows(CustomIdAlreadyExistsException.class, 
            () -> service.createShortUrl("https://example.com", customId, null));
    }

    @Test
    void createShortUrl_WithTTL_ShouldSetExpirationTime() {
        Long ttlHours = 24L;

        service.createShortUrl("https://example.com", null, ttlHours);

        ArgumentCaptor<UrlMapping> mappingCaptor = ArgumentCaptor.forClass(UrlMapping.class);
        verify(repository).save(mappingCaptor.capture());
        assertNotNull(mappingCaptor.getValue().getExpiresAt());
    }

    @Test
    void getLongUrl_WithValidId_ShouldReturnUrl() {
        String shortId = "abc123";
        UrlMapping mapping = new UrlMapping();
        mapping.setLongUrl("https://example.com");
        when(repository.findById(shortId)).thenReturn(Optional.of(mapping));

        String result = service.getLongUrl(shortId);

        assertEquals(mapping.getLongUrl(), result);
    }

    @Test
    void getLongUrl_WithNonexistentId_ShouldThrowException() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, 
            () -> service.getLongUrl("nonexistent"));
    }

    @Test
    void getLongUrl_WithExpiredUrl_ShouldThrowException() {
        String shortId = "expired";
        UrlMapping mapping = new UrlMapping();
        mapping.setExpiresAt(LocalDateTime.now().minusHours(1));
        when(repository.findById(shortId)).thenReturn(Optional.of(mapping));

        assertThrows(UrlNotFoundException.class, () -> service.getLongUrl(shortId));
        verify(repository).delete(mapping);
    }

    @Test
    void deleteUrl_WithValidId_ShouldDelete() {
        String shortId = "abc123";
        when(repository.existsById(shortId)).thenReturn(true);

        service.deleteUrl(shortId);

        verify(repository).deleteById(shortId);
    }

    @Test
    void deleteUrl_WithNonexistentId_ShouldThrowException() {
        String shortId = "nonexistent";
        when(repository.existsById(shortId)).thenReturn(false);

        assertThrows(UrlNotFoundException.class, () -> service.deleteUrl(shortId));
        verify(repository, never()).deleteById(anyString());
    }
} 