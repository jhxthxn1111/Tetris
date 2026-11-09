package com.tetranutras.tetris.controller;

import com.tetranutras.tetris.dto.AnswerResult;
import com.tetranutras.tetris.dto.QuestionDto;
import com.tetranutras.tetris.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
class QuestionControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    QuestionService service;

    @BeforeEach
    void setUp() {
        when(service.randomPublic("EASY")).thenReturn(
                new QuestionDto(1, "Historia", "EASY", "Pregunta", List.of("a", "b", "c", "d")));
    }

    @Test
    void random_respondeOk() throws Exception {
        mvc.perform(get("/api/questions/random").param("difficulty", "EASY"))
                .andExpect(status().isOk());
    }

    @Test
    void random_devuelveCuatroOpciones() throws Exception {
        mvc.perform(get("/api/questions/random").param("difficulty", "EASY"))
                .andExpect(jsonPath("$.options.length()").value(4));
    }

    @Test
    void random_noExponeLaRespuestaCorrecta() throws Exception {
        mvc.perform(get("/api/questions/random").param("difficulty", "EASY"))
                .andExpect(jsonPath("$.correctIndex").doesNotExist());
    }

    @Test
    void random_sinParametro_usaDificultadEasy() throws Exception {
        mvc.perform(get("/api/questions/random"));

        verify(service).randomPublic("EASY");
    }

    @Test
    void answer_conRespuestaCorrecta_devuelveCorrectTrue() throws Exception {
        when(service.check(1, 1)).thenReturn(new AnswerResult(true, 1, 1, "Explicacion"));

        mvc.perform(post("/api/questions/1/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"selectedIndex\":1}"))
                .andExpect(jsonPath("$.correct").value(true));
    }

    @Test
    void answer_conRespuestaIncorrecta_devuelveCorrectFalse() throws Exception {
        when(service.check(1, 0)).thenReturn(new AnswerResult(false, 1, 0, "Explicacion"));

        mvc.perform(post("/api/questions/1/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"selectedIndex\":0}"))
                .andExpect(jsonPath("$.correct").value(false));
    }

    @Test
    void answer_conIdInexistente_responde404() throws Exception {
        when(service.check(999, 0)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mvc.perform(post("/api/questions/999/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"selectedIndex\":0}"))
                .andExpect(status().isNotFound());
    }
}