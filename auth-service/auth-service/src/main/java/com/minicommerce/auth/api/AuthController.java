package com.minicommerce.auth.api;

import com.minicommerce.auth.jwt.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req,
                                               @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId) {

        // Hardcoded user (acceptable for story 1.1)
        // username: user
        // password: pass
        if (!"user".equals(req.username()) || !"pass".equals(req.password())) {
            return ResponseEntity.status(401).build();
        }

        String cid = (correlationId == null || correlationId.isBlank())
                ? UUID.randomUUID().toString()
                : correlationId;

        String token = jwtService.generateToken(req.username(), List.of("USER"), cid);

       // return ResponseEntity.ok(new LoginResponse(token, "Bearer", jwtService.getTtlSeconds()));
       return new ResponseEntity<>(
               new LoginResponse(token, "Bearer", jwtService.getTtlSeconds()),
               org.springframework.http.HttpStatus.OK
       );
    }
}
