package com.lms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new LinkedHashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            status.put("status", conn.isValid(2) ? "UP" : "DOWN");
            status.put("database", "OK");
        } catch (Exception e) {
            status.put("status", "DOWN");
            status.put("database", "ERROR: " + e.getMessage());
        }
        return ResponseEntity.ok(status);
    }
}
