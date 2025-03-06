package com.assignment.urlShortener.service;

import com.assignment.urlShortener.model.UrlMapping;

public interface UrlShortenerService {
    public UrlMapping createShortUrl(String longUrl, String customId, Long ttlHours);

    public String getLongUrl(String shortId);

    public void deleteUrl(String shortId);

    public String generateShortId();
} 