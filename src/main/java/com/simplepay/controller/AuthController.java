package com.simplepay.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "API authentication using client credentials")
public class AuthController {
    @Value("${jwt.secret}")
    private String secret;
    private final long expirationMs = 86400000; // 1 day
    private SecretKey key;

    @PostConstruct
    public void initKey() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Operation(summary = "Authenticate client and get JWT token", 
               description = "Authenticate using client credentials and receive a JWT access token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful", 
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid client credentials")
    })
    @PostMapping
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest request) {
        // Validate client credentials
        if (request.getClientId() == null || request.getClientSecret() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Client ID and client secret required"));
        }
        
        // For demo: accept specific client credentials. In production, validate against secure storage!
        if (!"simplepay-client".equals(request.getClientId()) || 
            !"simplepay-secret-key-2025".equals(request.getClientSecret())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid client credentials"));
        }
        
        // Generate JWT with client_id as subject
        String token = Jwts.builder()
                .subject(request.getClientId())
                .claim("client_id", request.getClientId())
                .claim("scope", "api:read api:write")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
                
        Map<String, Object> response = new HashMap<>();
        response.put("access_token", token);
        response.put("token_type", "Bearer");
        response.put("expires_in", expirationMs / 1000);
        response.put("scope", "api:read api:write");
        return ResponseEntity.ok(response);
    }

    public static class AuthRequest {
        private String clientId;
        private String clientSecret;
        
        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
        public String getClientSecret() { return clientSecret; }
        public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
    }
    
    public static class AuthResponse {
        private String accessToken;
        private String tokenType;
        private Long expiresIn;
        private String scope;
        
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        public Long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }
    }
}
