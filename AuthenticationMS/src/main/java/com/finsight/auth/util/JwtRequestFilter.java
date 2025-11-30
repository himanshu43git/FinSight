package com.finsight.auth.util;

import com.finsight.auth.services.AppUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtil jwtUtil;

    private static final Set<String> BASE_PUBLIC = Set.of(
            "/login",
            "/register",
            "/send-otp",
            "/verify-otp",
            "/send-reset-otp",
            "/reset-password",
            "/logout",
            "/actuator/health"
    );

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    public JwtRequestFilter(AppUserDetailsService appUserDetailsService, JwtUtil jwtUtil) {
        this.appUserDetailsService = appUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestUri = (request.getRequestURI() == null) ? "" : request.getRequestURI();
        String servletPath = (request.getServletPath() == null) ? "" : request.getServletPath();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isPublicPath(requestUri, servletPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = null;

        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        }

        if (jwt == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("jwt".equals(c.getName())) {
                        jwt = c.getValue();
                        break;
                    }
                }
            }
        }

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String email = jwtUtil.extractEmail(jwt);
                if (email != null) {
                    UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);
                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        log.debug("JWT validation failed for request to '{}'", requestUri);
                    }
                }
            } catch (Exception ex) {
                log.warn("Failed to authenticate request via JWT for '{}': {}", requestUri, ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String requestUri, String servletPath) {
        String cp = (contextPath == null) ? "" : contextPath.trim();
        if (!cp.isEmpty() && !cp.startsWith("/")) cp = "/" + cp;

        for (String base : BASE_PUBLIC) {
            if (requestUri.equals(base) || requestUri.equals(cp + base)
                    || requestUri.startsWith(base + "/") || requestUri.startsWith(cp + base + "/")) {
                return true;
            }
            if (servletPath.equals(base) || servletPath.startsWith(base + "/")) {
                return true;
            }
        }
        return false;
    }
}
