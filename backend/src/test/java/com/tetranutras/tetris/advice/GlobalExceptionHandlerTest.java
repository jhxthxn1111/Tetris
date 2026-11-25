package com.tetranutras.tetris.advice;

import com.tetranutras.tetris.dto.ApiError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new ServletWebRequest(new MockHttpServletRequest("POST", "/api/questions/999/answer"));
    }

    private ApiError cuerpo(ResponseEntity<ApiError> respuesta) {
        return Objects.requireNonNull(respuesta.getBody());
    }

    @Test
    void responseStatus_conNotFound_devuelveEstado404() {
        var ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada: 999");

        var respuesta = handler.handleResponseStatus(ex, request);

        assertEquals(404, respuesta.getStatusCode().value());
    }

    @Test
    void responseStatus_conBadRequest_devuelveEstado400() {
        var ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Índice de opción inválido");

        var respuesta = handler.handleResponseStatus(ex, request);

        assertEquals(400, respuesta.getStatusCode().value());
    }

    @Test
    void responseStatus_incluyeElMensajeDelError() {
        var ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada: 999");

        var respuesta = handler.handleResponseStatus(ex, request);

        assertTrue(cuerpo(respuesta).message().contains("Pregunta no encontrada: 999"));
    }

    @Test
    void responseStatus_incluyeLaRutaSinElPrefijoUri() {
        var ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada: 999");

        var respuesta = handler.handleResponseStatus(ex, request);

        assertEquals("/api/questions/999/answer", cuerpo(respuesta).path());
    }

    @Test
    void responseStatus_usaElNombreDelEstadoHttp() {
        var ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada: 999");

        var respuesta = handler.handleResponseStatus(ex, request);

        assertEquals("Not Found", cuerpo(respuesta).error());
    }

    @Test
    void mensajeNoLegible_devuelve400() {
        var ex = mock(HttpMessageNotReadableException.class);

        var respuesta = handler.handleMessageNotReadable(ex, request);

        assertEquals(400, respuesta.getStatusCode().value());
    }

    @Test
    void mensajeNoLegible_usaUnMensajeFijoSinDetallesInternos() {
        var ex = mock(HttpMessageNotReadableException.class);

        var respuesta = handler.handleMessageNotReadable(ex, request);

        assertEquals("Cuerpo de la petición inválido o mal formado", cuerpo(respuesta).message());
    }

    @Test
    void validacionFallida_devuelve400() {
        var ex = mock(MethodArgumentNotValidException.class);

        var respuesta = handler.handleMethodArgumentNotValid(ex, request);

        assertEquals(400, respuesta.getStatusCode().value());
    }

    @Test
    void errorGenerico_devuelve500() {
        var respuesta = handler.handleGeneric(new RuntimeException("falla"), request);

        assertEquals(500, respuesta.getStatusCode().value());
    }

    @Test
    void errorGenerico_usaUnMensajeGenerico() {
        var respuesta = handler.handleGeneric(new RuntimeException("falla"), request);

        assertEquals("Error interno del servidor", cuerpo(respuesta).message());
    }

    @Test
    void errorGenerico_noFiltraElMensajeInterno() {
        var respuesta = handler.handleGeneric(new RuntimeException("clave-secreta-123"), request);

        assertFalse(cuerpo(respuesta).message().contains("clave-secreta-123"));
    }

    @Test
    void errorGenerico_incluyeLaFechaEnFormatoIso() {
        var respuesta = handler.handleGeneric(new RuntimeException("falla"), request);

        assertDoesNotThrow(() -> Instant.parse(cuerpo(respuesta).timestamp()));
    }

    @Test
    void apiError_noTieneCampoDeTrazaDePila() {
        boolean tieneTraza = Arrays.stream(ApiError.class.getRecordComponents())
                .anyMatch(c -> c.getName().toLowerCase().contains("trace"));

        assertFalse(tieneTraza);
    }
}