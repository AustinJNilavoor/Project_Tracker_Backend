package com.example.Project_Tracker.exceptions;

import com.example.Project_Tracker.dto.ApiResponse;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
// import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice // works for @RestController automatically
public class GlobalExceptionHandler {

        // ✅ Generic exception handler
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
                ApiResponse<String> response = ApiResponse.<String>builder()
                                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .success(false)
                                .message(ex.getMessage())
                                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build();
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // ✅ Handle validation errors (e.g., @Valid)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
                        MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
                String errorDetails = errors.entrySet().stream()
                                // Format each entry as "field: message"
                                .map(entry -> entry.getKey() + ": " + entry.getValue())
                                // Join them using a semicolon and space as a separator
                                .collect(Collectors.joining("; "));

                ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .success(false)
                                .message("Validation failed, " + errorDetails)
                                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // ✅ Custom exception example
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<String>> handleResourceNotFound(ResourceNotFoundException ex) {

                ApiResponse<String> response = ApiResponse.<String>builder()
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .success(false)
                                .message(ex.getMessage())
                                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiResponse<String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
                String message;

                if (ex.getMessage().contains("not-null")) {
                        message = "A required field is missing or null.";
                } else if (ex.getMessage().contains("foreign key")) {
                        message = "Cannot delete or modify this record because it is linked to other data.";
                } else if (ex.getMessage().contains("unique")) {
                        message = "Duplicate value detected. This record already exists.";
                } else {
                        message = "Data integrity violation occurred.";
                }

                ApiResponse<String> response = ApiResponse.<String>builder()
                                .statusCode(HttpStatus.CONFLICT.value())
                                .success(false)
                                .message(message)
                                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build();

                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
        public ResponseEntity<ApiResponse<String>> handleResponseStatusException(ResponseStatusException ex) {
                HttpStatusCode status = ex.getStatusCode();

                ApiResponse<String> response = ApiResponse.<String>builder()
                                .statusCode(status.value())
                                .success(false)
                                .message(ex.getReason()) // shows your custom message
                                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(response, status);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

                String detailedMessage;
                if (ex.getCause() != null && ex.getCause().getMessage().contains("Cannot deserialize value of type")) {
                        detailedMessage = "Invalid value provided for an enum field.";
                        if (ex.getCause().getMessage().contains("not from the set")) {
                                detailedMessage = ex.getCause().getMessage();
                        }
                } else {
                        detailedMessage = "Malformed JSON request or invalid data type.";
                }

                // 2. Build the consistent ApiResponse body
                ApiResponse<String> response = ApiResponse.<String>builder()
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .success(false)
                                .message(detailedMessage)
                                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build();

                // 3. Return the response with the 400 BAD_REQUEST status
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<String>> handleAccessDenied(AccessDeniedException ex) {
                // Explicitly return 403 status
                HttpStatusCode status = HttpStatus.FORBIDDEN;
                ApiResponse<String> response = ApiResponse.<String>builder()
                                .statusCode(status.value())
                                .success(false)
                                .message(ex.getMessage()) // shows your custom message
                                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(response, status);
        }

        @ExceptionHandler(MemberAlreadyExistsException.class)
        public ResponseEntity<ApiResponse<String>> handleMemberAlreadyExists(MemberAlreadyExistsException ex) {
                ApiResponse<String> response = ApiResponse.<String>builder()
                                .statusCode(HttpStatus.CONFLICT.value())
                                .success(false)
                                .message(ex.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ApiResponse<String>> handleInvalidCredentials(InvalidCredentialsException ex) {

                ApiResponse<String> response = ApiResponse.<String>builder()
                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                .success(false)
                                .message(ex.getMessage())
                                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // ✅ Handle Bad Request (400) - For general business logic errors
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiResponse<String>> handleBadRequest(BadRequestException ex) {

                ApiResponse<String> response = ApiResponse.<String>builder()
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .success(false)
                                .message(ex.getMessage())
                                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // ✅ Handle Conflict/State Error (409) - For dependency/state violations
        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ApiResponse<String>> handleConflict(ConflictException ex) {

                ApiResponse<String> response = ApiResponse.<String>builder()
                                .statusCode(HttpStatus.CONFLICT.value())
                                .success(false)
                                .message(ex.getMessage())
                                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ApiResponse<String>> handleMissingParams(MissingServletRequestParameterException ex) {

                String paramName = ex.getParameterName();
                String message = "Required request parameter '" + paramName + "' is missing";

                ApiResponse<String> response = ApiResponse.<String>builder()
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .success(false)
                                .message(message)
                                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ApiResponse<String>> handleNoHandler(NoHandlerFoundException ex) {

                ApiResponse<String> response = ApiResponse.<String>builder()
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .success(false)
                                .message("Endpoint not found: " + ex.getRequestURL())
                                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

@ExceptionHandler(MilestoneCompletionException.class)
    public ResponseEntity<ApiResponse<String>> handleMilestoneCompletion(MilestoneCompletionException ex) {
        
        // Use a 409 Conflict status code because the request violates a business rule 
        // due to the current state of the resource (incomplete tasks).
        HttpStatus status = HttpStatus.CONFLICT; 

        ApiResponse<String> response = ApiResponse.<String>builder()
                .statusCode(status.value())
                .success(false)
                .message(ex.getMessage()) // The message set when the exception was thrown
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, status);
    }

}
