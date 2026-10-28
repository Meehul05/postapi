package com.example.postapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/credentials")
public class CredentialController {

    @GetMapping("/test")
    public Map<String, String> testCredential() {

        String apiKey = System.getenv("THIRD_PARTY_API_KEY");

        return Map.of(
                "message", "Third-party credential loaded successfully",
                "credentialPresent", String.valueOf(apiKey != null)
        );
    }
}   