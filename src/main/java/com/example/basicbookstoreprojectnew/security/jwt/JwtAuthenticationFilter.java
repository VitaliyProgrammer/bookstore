package com.example.basicbookstoreprojectnew.security.jwt;

import com.example.basicbookstoreprojectnew.exception.JwtException;
import com.example.basicbookstoreprojectnew.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_SCHEMA_BEARER = "Bearer";
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = httpServletRequest.getRequestURI();

        if (path.startsWith("/auth/")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        try {
            String token = extractToken(httpServletRequest);

            if (token != null && jwtUtil.isValidToken(token)) {

                String username = jwtUtil.getUsernameFromToken(token);

                List<String> roles = jwtUtil.getRolesFomToken(token);

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (JwtException exception) {

            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write("{\"error\": \"Token expired or invalid!\"}");
        }
    }

    private String extractToken(HttpServletRequest httpServletRequest) {

        String header = httpServletRequest.getHeader("Authorization");

        if (header == null) {
            return null;
        }

        header = header.trim();

        if (!StringUtils.startsWithIgnoreCase(header, AUTHORIZATION_SCHEMA_BEARER)) {
            return null;
        }

        return header.substring(7).trim();
    }
}
