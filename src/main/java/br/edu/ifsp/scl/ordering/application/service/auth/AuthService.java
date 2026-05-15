package br.edu.ifsp.scl.ordering.application.service.auth;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.IAuthService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos.AuthTokensResponse;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos.LoginRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos.RefreshRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos.RegisterRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.auth.IUserRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.User;
import br.edu.ifsp.scl.ordering.domain.constant.UserRole;
import br.edu.ifsp.scl.ordering.domain.exceptions.InvalidCredentialsException;
import br.edu.ifsp.scl.ordering.domain.exceptions.JwtValidationException;
import br.edu.ifsp.scl.ordering.domain.exceptions.UserAlreadyExistsException;
import br.edu.ifsp.scl.ordering.domain.valueobject.UserId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuthService implements IAuthService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            IUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthTokensResponse register(RegisterRequest request) {
        Objects.requireNonNull(request, "Null request");
        String email = normalizeEmail(request.email());
        String password = requireNonBlank(request.password(), "Null/blank password");

        boolean exists = userRepository.existsByEmail(email);
        if (exists) throw new UserAlreadyExistsException("User already exists");

        String passwordHash = passwordEncoder.encode(password);
        UserId userId = new UserId(UUID.randomUUID().toString());

        User user = User.create(userId, email, passwordHash, UserRole.USER);
        userRepository.save(user);

        return issueTokensFor(user);
    }

    @Override
    public AuthTokensResponse login(LoginRequest request) {
        Objects.requireNonNull(request, "Null request");
        String email = normalizeEmail(request.email());
        String password = requireNonBlank(request.password(), "Null/blank password");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        boolean matches = passwordEncoder.matches(password, user.passwordHash());
        if (!matches) throw new InvalidCredentialsException("Invalid credentials");

        return issueTokensFor(user);
    }

    @Override
    public AuthTokensResponse refresh(RefreshRequest request) {
        Objects.requireNonNull(request, "Null request");
        String accessToken = requireNonBlank(request.accessToken(), "Null/blank accessToken");

        JwtAuthenticatedUser authenticatedUser = jwtService.parseAndValidateAccessTokenAllowExpired(accessToken);

        User user = userRepository.findById(authenticatedUser.userId())
                .orElseThrow(() -> new JwtValidationException("Invalid token"));

        return issueTokensFor(user);
    }

    private AuthTokensResponse issueTokensFor(User user) {
        String accessToken = jwtService.issueAccessToken(user);
        return new AuthTokensResponse(accessToken);
    }

    private static String normalizeEmail(String email) {
        String value = requireNonBlank(email, "Null/blank email").trim().toLowerCase(Locale.ROOT);
        if (!value.contains("@")) throw new IllegalArgumentException("Invalid email");
        return value;
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value;
    }
}
