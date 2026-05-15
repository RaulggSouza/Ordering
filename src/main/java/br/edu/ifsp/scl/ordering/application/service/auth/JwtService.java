package br.edu.ifsp.scl.ordering.application.service.auth;

import br.edu.ifsp.scl.ordering.domain.aggregate.User;
import br.edu.ifsp.scl.ordering.domain.constant.UserRole;
import br.edu.ifsp.scl.ordering.domain.exceptions.JwtValidationException;
import br.edu.ifsp.scl.ordering.domain.valueobject.UserId;
import br.edu.ifsp.scl.ordering.application.service.auth.config.JwtProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@Component
public class JwtService {
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final JwtProperties properties;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public JwtService(JwtProperties properties, ObjectMapper objectMapper) {
        this(properties, objectMapper, Clock.systemUTC());
    }

    JwtService(JwtProperties properties, ObjectMapper objectMapper, Clock clock) {
        this.properties = Objects.requireNonNull(properties, "Null properties");
        this.objectMapper = Objects.requireNonNull(objectMapper, "Null objectMapper");
        this.clock = Objects.requireNonNull(clock, "Null clock");
    }

    public String issueAccessToken(User user) {
        Objects.requireNonNull(user, "Null user");
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String header = BASE64_URL_ENCODER.encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));

        Instant now = clock.instant();
        long iat = now.getEpochSecond();
        long exp = now.plus(properties.accessTokenTtl()).getEpochSecond();

        Map<String, Object> payloadMap = Map.of(
                "iss", properties.issuer(),
                "sub", user.id().value(),
                "email", user.email(),
                "role", user.role().name(),
                "iat", iat,
                "exp", exp
        );

        byte[] payloadBytes;
        try {
            payloadBytes = objectMapper.writeValueAsBytes(payloadMap);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize JWT payload", e);
        }

        String payload = BASE64_URL_ENCODER.encodeToString(payloadBytes);

        String signingInput = header + "." + payload;
        String signature = BASE64_URL_ENCODER.encodeToString(hmacSha256(signingInput));

        return signingInput + "." + signature;
    }

    public JwtAuthenticatedUser parseAndValidateAccessToken(String token) {
        return parseAndValidateAccessToken(token, true);
    }

    public JwtAuthenticatedUser parseAndValidateAccessTokenAllowExpired(String token) {
        return parseAndValidateAccessToken(token, false);
    }

    private JwtAuthenticatedUser parseAndValidateAccessToken(String token, boolean validateExpiration) {
        if (token == null || token.isBlank()) throw new JwtValidationException("Blank token");
        String[] parts = token.split("\\.");
        if (parts.length != 3) throw new JwtValidationException("Invalid token format");

        String headerB64 = parts[0];
        String payloadB64 = parts[1];
        String signatureB64 = parts[2];

        String signingInput = headerB64 + "." + payloadB64;
        String expectedSignatureB64 = BASE64_URL_ENCODER.encodeToString(hmacSha256(signingInput));
        if (!constantTimeEquals(expectedSignatureB64, signatureB64)) throw new JwtValidationException("Invalid token signature");

        Map<String, Object> header = decodeJson(headerB64);
        Object alg = header.get("alg");
        if (!Objects.equals("HS256", alg)) throw new JwtValidationException("Unsupported JWT alg");

        Map<String, Object> payload = decodeJson(payloadB64);

        String issuer = asString(payload.get("iss"));
        if (!Objects.equals(properties.issuer(), issuer)) throw new JwtValidationException("Invalid issuer");

        long exp = asLong(payload.get("exp"));
        long now = clock.instant().getEpochSecond();
        if (validateExpiration && now >= exp) throw new JwtValidationException("Token expired");

        String sub = asString(payload.get("sub"));
        String email = asString(payload.get("email"));
        String roleStr = asString(payload.get("role"));
        UserRole role;
        try {
            role = UserRole.valueOf(roleStr);
        } catch (Exception e) {
            throw new JwtValidationException("Invalid role");
        }

        return new JwtAuthenticatedUser(new UserId(sub), email, role);
    }

    private Map<String, Object> decodeJson(String base64Url) {
        byte[] decoded;
        try {
            decoded = BASE64_URL_DECODER.decode(base64Url);
        } catch (IllegalArgumentException e) {
            throw new JwtValidationException("Invalid base64");
        }

        try {
            return objectMapper.readValue(decoded, MAP_TYPE);
        } catch (Exception e) {
            throw new JwtValidationException("Invalid JSON");
        }
    }

    private byte[] hmacSha256(String signingInput) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign JWT", e);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private static String asString(Object value) {
        if (value == null) throw new JwtValidationException("Missing claim");
        if (value instanceof String s) return s;
        throw new JwtValidationException("Invalid claim type");
    }

    private static long asLong(Object value) {
        if (value == null) throw new JwtValidationException("Missing claim");
        if (value instanceof Number n) return n.longValue();
        throw new JwtValidationException("Invalid claim type");
    }
}
