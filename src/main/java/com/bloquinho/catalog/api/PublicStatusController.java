package com.bloquinho.catalog.api;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
public class PublicStatusController {
    @GetMapping("/status")
    public Map<String, Map<String, String>> status() {
        return Map.of("data", Map.of("application", "bloquinho-api", "status", "UP"));
    }
}
