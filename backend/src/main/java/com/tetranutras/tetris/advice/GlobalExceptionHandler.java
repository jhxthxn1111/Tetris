package com.tetranutras.tetris.advice;

import com.tetranutras.tetris.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

/**
 * Manejador global de excepciones para la API REST.
 * Devuelve errores estructurados y nunca expone stack traces ni detalles
 * internos al cliente (defensa en profundidad junto a
 * server.error.include-stacktrace=never).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> construir(HttpStatus status, String mensaje, WebRequest request) {
        ApiError error = new ApiError(
                Instant.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex, WebRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ApiError error = new ApiError(
                Instant.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : status.getReasonPhrase(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMessageNotReadable(HttpMessageNotReadableException ex,
                                                             WebRequest request) {
        return construir(HttpStatus.BAD_REQUEST, "Cuerpo de la petición inválido o mal formado", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                 WebRequest request) {
        return construir(HttpStatus.BAD_REQUEST, "Errores de validación en la petición", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMetodoNoPermitido(HttpRequestMethodNotSupportedException ex,
                                                            WebRequest request) {
        return construir(HttpStatus.METHOD_NOT_ALLOWED, "Método no permitido", request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleTipoNoSoportado(HttpMediaTypeNotSupportedException ex,
                                                          WebRequest request) {
        return construir(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de contenido no soportado", request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoEncontrado(NoResourceFoundException ex,
                                                       WebRequest request) {
        return construir(HttpStatus.NOT_FOUND, "Recurso no encontrado", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleParametroInvalido(MethodArgumentTypeMismatchException ex,
                                                            WebRequest request) {
        return construir(HttpStatus.BAD_REQUEST, "Parámetro con formato inválido", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, WebRequest request) {
        // ERROR 500: mensaje genérico sin filtrar stack trace al cliente
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request);
    }
}