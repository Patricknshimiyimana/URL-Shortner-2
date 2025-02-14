package com.assignment.urlShortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UrlRequestDto {
    @NotBlank(message = "URL cannot be empty")
    private String url;
    private String customId;
    private Long ttlHours;
}