package com.assignment.urlShortener.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class UrlMapping {
    @Id
    private String shortId;
    private String longUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
} 
