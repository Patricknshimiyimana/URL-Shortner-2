package com.assignment.urlShortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UrlRequestDto {
    @NotBlank(message = "URL cannot be empty")
    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid URL format")
    @Schema(description = "The long URL to be shortened", example = "https://example.com/very/long/url")
    private String url;

    @Schema(description = "Optional custom ID for the shortened URL", example = "my-custom-id")
    private String customId;

    @Schema(description = "Optional time-to-live in hours", example = "24")
    private Long ttlHours;
}