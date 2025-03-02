package com.assignment.urlShortener.unit;

import com.assignment.urlShortener.dto.UrlRequestDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UrlValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenUrlIsValid_thenNoValidationErrors() {
        UrlRequestDto request = new UrlRequestDto();
        request.setUrl("https://www.example.com");

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenUrlIsNull_thenValidationError() {
        UrlRequestDto request = new UrlRequestDto();
        request.setUrl(null);

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }
} 