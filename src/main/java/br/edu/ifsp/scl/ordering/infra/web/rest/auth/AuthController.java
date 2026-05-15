package br.edu.ifsp.scl.ordering.infra.web.rest.auth;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.IAuthService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos.AuthTokensResponse;
import br.edu.ifsp.scl.ordering.domain.exceptions.JwtValidationException;
import br.edu.ifsp.scl.ordering.domain.exceptions.InvalidCredentialsException;
import br.edu.ifsp.scl.ordering.domain.exceptions.UserAlreadyExistsException;
import br.edu.ifsp.scl.ordering.infra.web.rest.auth.dtos.AuthCredentialsRequestDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.auth.dtos.AuthRefreshRequestDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.auth.dtos.AuthTokensResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthTokensResponseDTO> register(@RequestBody AuthCredentialsRequestDTO body) {
        try {
            AuthTokensResponse response = authService.register(body.toRegisterRequest());
            return ResponseEntity.status(HttpStatus.CREATED).body(AuthTokensResponseDTO.fromApplication(response));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokensResponseDTO> login(@RequestBody AuthCredentialsRequestDTO body) {
        try {
            AuthTokensResponse response = authService.login(body.toLoginRequest());
            return ResponseEntity.ok(AuthTokensResponseDTO.fromApplication(response));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokensResponseDTO> refresh(@RequestBody AuthRefreshRequestDTO body) {
        try {
            AuthTokensResponse response = authService.refresh(body.toRequest());
            return ResponseEntity.ok(AuthTokensResponseDTO.fromApplication(response));
        } catch (JwtValidationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
