package br.edu.ifsp.scl.ordering.infra.security;

import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.auth.IUserRepository;
import br.edu.ifsp.scl.ordering.application.service.auth.JwtAuthenticatedUser;
import br.edu.ifsp.scl.ordering.application.service.auth.JwtService;
import br.edu.ifsp.scl.ordering.domain.exceptions.JwtValidationException;
import br.edu.ifsp.scl.ordering.domain.aggregate.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final IUserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, IUserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring("Bearer ".length()).trim();
        try {
            JwtAuthenticatedUser authenticatedUser = jwtService.parseAndValidateAccessToken(token);

            Optional<User> user = userRepository.findById(authenticatedUser.userId());
            if (user.isEmpty()) {
                unauthorized(response, "Invalid token");
                return;
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    authenticatedUser,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + authenticatedUser.role().name()))
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (JwtValidationException e) {
            SecurityContextHolder.clearContext();
            unauthorized(response, e.getMessage());
        }
    }

    private static void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"" + escapeJson(message) + "\"}");
    }

    private static String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

