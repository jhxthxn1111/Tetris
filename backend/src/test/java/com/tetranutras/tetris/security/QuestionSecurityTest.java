package com.tetranutras.tetris.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QuestionSecurityTest {

    private static final String ORIGEN_FRONTEND = "http://localhost:5173";

    @Autowired
    MockMvc mvc;

    private ResultActions responder(int id, String cuerpo) throws Exception {
        return mvc.perform(post("/api/questions/" + id + "/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo));
    }

    // ---------- CORS ----------

    @Test
    void cors_origenDelFrontend_recibeLaCabeceraPermitida() throws Exception {
        mvc.perform(get("/api/questions/random").header("Origin", ORIGEN_FRONTEND))
                .andExpect(header().string("Access-Control-Allow-Origin", ORIGEN_FRONTEND));
    }

    @Test
    void cors_otroOrigen_responde403() throws Exception {
        mvc.perform(get("/api/questions/random").header("Origin", "http://sitio-malo.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cors_get_noPermiteCredenciales() throws Exception {
        mvc.perform(get("/api/questions/random").header("Origin", ORIGEN_FRONTEND))
                .andExpect(header().doesNotExist("Access-Control-Allow-Credentials"));
    }

    @Test
    void cors_preflightDePost_responde200() throws Exception {
        mvc.perform(options("/api/questions/1/answer")
                        .header("Origin", ORIGEN_FRONTEND)
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk());
    }

    @Test
    void cors_preflightDePost_permiteLaCabeceraContentType() throws Exception {
        mvc.perform(options("/api/questions/1/answer")
                        .header("Origin", ORIGEN_FRONTEND)
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(header().string("Access-Control-Allow-Headers", containsString("Content-Type")));
    }

    @Test
    void cors_preflightDeDelete_responde403() throws Exception {
        mvc.perform(options("/api/questions/1/answer")
                        .header("Origin", ORIGEN_FRONTEND)
                        .header("Access-Control-Request-Method", "DELETE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cors_preflightConCabeceraNoPermitida_responde403() throws Exception {
        mvc.perform(options("/api/questions/1/answer")
                        .header("Origin", ORIGEN_FRONTEND)
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "X-Token"))
                .andExpect(status().isForbidden());
    }

    // ---------- Errores ----------

    @Test
    void error_preguntaInexistente_devuelve404() throws Exception {
        responder(999, "{\"selectedIndex\":0}")
                .andExpect(status().isNotFound());
    }

    @Test
    void error_preguntaInexistente_noIncluyeTrazaDePila() throws Exception {
        responder(999, "{\"selectedIndex\":0}")
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void error_preguntaInexistente_devuelveCuerpoEstructurado() throws Exception {
        responder(999, "{\"selectedIndex\":0}")
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void error_indiceMayorA3_devuelve400() throws Exception {
        responder(1, "{\"selectedIndex\":99}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void error_indiceNegativo_devuelve400() throws Exception {
        responder(1, "{\"selectedIndex\":-1}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void error_jsonMalFormado_devuelve400() throws Exception {
        responder(1, "{esto no es json")
                .andExpect(status().isBadRequest());
    }

    @Test
    void error_jsonMalFormado_usaUnMensajeFijo() throws Exception {
        responder(1, "{esto no es json")
                .andExpect(jsonPath("$.message").value("Cuerpo de la petición inválido o mal formado"));
    }

    @Test
    void error_valorNoNumerico_devuelve400() throws Exception {
        responder(1, "{\"selectedIndex\":\"abc\"}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void error_cuerpoVacio_devuelve400() throws Exception {
        responder(1, "")
                .andExpect(status().isBadRequest());
    }

    // ---------- Datos que no deben salir ----------

    @Test
    void random_noExponeLaRespuestaCorrecta() throws Exception {
        mvc.perform(get("/api/questions/random").param("difficulty", "EASY"))
                .andExpect(jsonPath("$.correctIndex").doesNotExist());
    }

    @Test
    void random_noExponeLaExplicacion() throws Exception {
        mvc.perform(get("/api/questions/random").param("difficulty", "EASY"))
                .andExpect(jsonPath("$.explanation").doesNotExist());
    }

    @Test
    void listado_noExponeLaRespuestaCorrecta() throws Exception {
        mvc.perform(get("/api/questions"))
                .andExpect(jsonPath("$[*].correctIndex").doesNotExist());
    }

    @Test
    void listado_noExponeLaExplicacion() throws Exception {
        mvc.perform(get("/api/questions"))
                .andExpect(jsonPath("$[*].explanation").doesNotExist());
    }

    // ---------- Entradas raras y respuestas reales ----------

    @Test
    void random_conDificultadRara_noFalla() throws Exception {
        mvc.perform(get("/api/questions/random").param("difficulty", "<script>alert(1)</script>"))
                .andExpect(status().isOk());
    }

    @Test
    void random_conDificultadRara_noDevuelveElTextoEnviado() throws Exception {
        mvc.perform(get("/api/questions/random").param("difficulty", "<script>alert(1)</script>"))
                .andExpect(content().string(not(containsString("<script>"))));
    }

    @Test
    void respuesta_preguntaUnoOpcionUno_esCorrecta() throws Exception {
        responder(1, "{\"selectedIndex\":1}")
                .andExpect(jsonPath("$.correct").value(true));
    }

    @Test
    void respuesta_preguntaUnoOpcionCero_esIncorrecta() throws Exception {
        responder(1, "{\"selectedIndex\":0}")
                .andExpect(jsonPath("$.correct").value(false));
    }
}