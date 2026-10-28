package com.example.postapi;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(CorrelationIdFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId = UUID.randomUUID().toString();

        response.setHeader("X-Correlation-ID", correlationId);

        logger.info(
                "Request received | ID: {} | {} {}",
                correlationId,
                request.getMethod(),
                request.getRequestURI()
        );

        filterChain.doFilter(request, response);

        logger.info(
                "Request completed | ID: {} | Status: {}",
                correlationId,
                response.getStatus()
        );
    }
}