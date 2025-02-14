package com.assignment.urlShortener.repository;

import com.assignment.urlShortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, String> {
} 