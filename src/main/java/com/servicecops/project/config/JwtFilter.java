package com.servicecops.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicecops.project.models.database.SystemUserModel;
import com.servicecops.project.utils.OperationReturnObject;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtility jwtUtility;
    private final ApplicationConf userDetailsService;
    private final ObjectMapper mapper;

    @Value("${app.version}")
    private String appVersion;
    OperationReturnObject errorDetails = new OperationReturnObject();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("App version is {}", appVersion);
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userTag;
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        try {

        userTag = jwtUtility.extractUsername(jwt);
        if (userTag != null && SecurityContextHolder.getContext().getAuthentication() == null){
            SystemUserModel userDetails = userDetailsService.loadUserByUsername(userTag);

            if (jwtUtility.isTokenValid(jwt, userDetails)){
                // check if is_authority_admin and add that permission here
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        } catch (ExpiredJwtException e) {
            errorDetails.setReturnCodeAndReturnMessage(HttpStatus.UNAUTHORIZED.value(), "TOKEN EXPIRED");
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
            mapper.writeValue(response.getWriter(), errorDetails);
            return;
        } catch (Exception e){
            errorDetails.setReturnCodeAndReturnMessage(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
            mapper.writeValue(response.getWriter(), errorDetails);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
