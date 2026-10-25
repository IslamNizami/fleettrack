package com.islamnizami.fleettrack.config;

import com.islamnizami.fleettrack.service.CustomUserDetailsService;
import com.islamnizami.fleettrack.service.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Authenticates WebSocket clients with the same JWT used by the REST API.
 *  - CONNECT must carry "Authorization: Bearer <jwt>" or the connection is refused.
 *  - Every later frame must belong to an authenticated session.
 *  - Clients may only send to /app/**, never straight to /topic/** (only the server broadcasts).
 */
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            accessor.setUser(authenticate(accessor.getFirstNativeHeader("Authorization")));
            return message;
        }

        if (accessor.getUser() == null
                && (StompCommand.SUBSCRIBE.equals(command) || StompCommand.SEND.equals(command))) {
            throw new MessageDeliveryException("Not authenticated");
        }

        if (StompCommand.SEND.equals(command)) {
            String destination = accessor.getDestination();
            if (destination == null || !destination.startsWith("/app")) {
                throw new MessageDeliveryException("Clients may not publish to " + destination);
            }
        }

        return message;
    }

    private UsernamePasswordAuthenticationToken authenticate(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new MessageDeliveryException("Missing or invalid Authorization header");
        }

        String token = header.substring(7);

        try {
            String username = jwtService.extractUsername(token);
            UserDetails user = userDetailsService.loadUserByUsername(username);

            if (!jwtService.isValid(token, user)) {
                throw new MessageDeliveryException("Invalid or expired token");
            }

            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException ex) {
            throw new MessageDeliveryException("Invalid or expired token");
        }
    }
}
