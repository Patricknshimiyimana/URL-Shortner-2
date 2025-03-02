package com.assignment.urlShortener.integration;

import com.assignment.urlShortener.dto.UrlRequestDto;
import com.assignment.urlShortener.model.UrlMapping;
import com.assignment.urlShortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UrlShortenerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlMappingRepository urlRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenCreateShortUrl_thenReturnShortUrl() throws Exception {
        UrlRequestDto request = new UrlRequestDto();
        request.setUrl("https://www.example.com/very/long/url");

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortId").exists());
    }

    @Test
    void whenRedirectWithValidShortUrl_thenRedirectToLongUrl() throws Exception {
        UrlMapping mapping = new UrlMapping();
        mapping.setLongUrl("https://www.example.com");
        mapping.setShortId("abc123");
        urlRepository.save(mapping);

        mockMvc.perform(get("/{shortUrl}", "abc123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "https://www.example.com"));
    }

    @Test
    void whenRedirectWithInvalidShortUrl_thenReturn404() throws Exception {
        mockMvc.perform(get("/{shortUrl}", "nonexistent"))
                .andExpect(status().isNotFound());
    }
} 