package com.assignment.ride_hailing.filter;

import io.netty.util.internal.StringUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final Map<String, String> API_KEYS = Map.of(
            "rider-key-1", "ROLE_RIDER",
            "rider-key-2", "ROLE_RIDER",
            "rider-key-3", "ROLE_RIDER",
            "driver-key-11", "ROLE_DRIVER",
            "driver-key-12", "ROLE_DRIVER",
            "driver-key-13", "ROLE_DRIVER"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String apiKey = request.getHeader("X-API-KEY");

        if (StringUtil.isNullOrEmpty(apiKey)) {
           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
            return;
        }

        String role = API_KEYS.get(apiKey);

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        extractUserId(apiKey),
                        null,
                        List.of(new SimpleGrantedAuthority(role))
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    private Long extractUserId(String apiKey) {
        String[] splitString = apiKey.split("-");
        return Long.parseLong(splitString[2]);
    }
}
