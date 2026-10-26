package com.example.Project_Tracker.security;

import com.example.Project_Tracker.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .success(false)
                .message("Access denied — you do not have permission for this action.")
                .data(null)
                .timestamp(null)
                .build();

        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        new ObjectMapper().writeValue(response.getOutputStream(), apiResponse);
    }
}
