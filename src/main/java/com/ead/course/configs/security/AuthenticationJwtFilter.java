package com.ead.course.configs.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class AuthenticationJwtFilter extends OncePerRequestFilter {

    Logger logger = LogManager.getLogger(AuthenticationJwtFilter.class);

    final JwtProvider jwtProvider;

    public AuthenticationJwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtString = getTokenHeader(request);

            if (jwtString != null && jwtProvider.validateJwt(jwtString)) {
                String userId = jwtProvider.getSubjectJwt(jwtString);
                String rolesStr = jwtProvider.getClaimNameJwt(jwtString, "roles");
                UserDetails userDetails = UserDetailsImpl.build(UUID.fromString(userId), rolesStr);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception ex) {
            logger.error("Cannot set User Authentication: {}", ex);
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenHeader(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer "))
            return headerAuth.substring(7, headerAuth.length());

        return null;
    }
}
