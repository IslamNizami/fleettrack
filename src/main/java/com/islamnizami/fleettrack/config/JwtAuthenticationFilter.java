
package com.islamnizami.fleettrack.config;

import com.islamnizami.fleettrack.service.CustomUserDetailsService;
import com.islamnizami.fleettrack.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        logger.info(
                "JWT filter called for: {} {}",
                request.getMethod(),
                request.getRequestURI()
        );

        if (header == null || !header.startsWith("Bearer ")) {
            logger.info("No Bearer token found in Authorization header");
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            String username = jwtService.extractUsername(token);

            logger.info("JWT username: {}", username);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user =
                        userDetailsService.loadUserByUsername(username);

                boolean valid = jwtService.isValid(token, user);

                logger.info("JWT valid: {}", valid);
                logger.info("User authorities: {}", user.getAuthorities());

                if (valid) {
                    var authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    user.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                    logger.info("Authentication set successfully");
                } else {
                    logger.warn("JWT is invalid for user: {}", username);
                }
            } else {
                logger.info("SecurityContext already contains authentication");
            }

        } catch (JwtException | IllegalArgumentException ex) {
            logger.warn("JWT validation failed: {}", ex.getMessage());
        } catch (UsernameNotFoundException ex) {
            logger.warn("JWT user no longer exists: {}", ex.getMessage());
        }

        chain.doFilter(request, response);
    }
}
