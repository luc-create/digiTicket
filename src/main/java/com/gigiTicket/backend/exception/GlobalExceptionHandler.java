package com.gigiTicket.backend.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, WebRequest request) {
		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(ex.getStatus().value())
				.error(ex.getStatus().getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();
		
		return new ResponseEntity<>(errorResponse, ex.getStatus());
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.NOT_FOUND.value())
				.error(HttpStatus.NOT_FOUND.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();
		
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
		List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
		
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			validationErrors.add(ErrorResponse.ValidationError.builder()
					.field(fieldName)
					.message(errorMessage)
					.build());
		});
		
		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message("Erreurs de validation")
				.path(request.getDescription(false).replace("uri=", ""))
				.validationErrors(validationErrors)
				.build();
		
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
		List<ErrorResponse.ValidationError> validationErrors = ex.getConstraintViolations()
				.stream()
				.map(violation -> ErrorResponse.ValidationError.builder()
						.field(violation.getPropertyPath().toString())
						.message(violation.getMessage())
						.build())
				.collect(Collectors.toList());
		
		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message("Erreurs de validation")
				.path(request.getDescription(false).replace("uri=", ""))
				.validationErrors(validationErrors)
				.build();
		
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
		String message = ex.getMessage();
		HttpStatus status = HttpStatus.BAD_REQUEST;
		
		if (message != null && (message.contains("non trouvé") || message.contains("not found"))) {
			status = HttpStatus.NOT_FOUND;
		} else if (message != null && (message.contains("incorrect") || message.contains("Accès refusé"))) {
			status = HttpStatus.UNAUTHORIZED;
		}
		
		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(status.value())
				.error(status.getReasonPhrase())
				.message(message != null ? message : "Une erreur est survenue")
				.path(request.getDescription(false).replace("uri=", ""))
				.build();
		
		return new ResponseEntity<>(errorResponse, status);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
				.message("Une erreur interne est survenue")
				.path(request.getDescription(false).replace("uri=", ""))
				.build();
		
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

