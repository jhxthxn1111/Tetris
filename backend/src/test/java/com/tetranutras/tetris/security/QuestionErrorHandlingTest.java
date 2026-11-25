package com.tetranutras.tetris.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QuestionErrorHandlingTest {

    @Autowired
    MockMvc mvc;

    @Test
    void metodoNoPermitido_devuelve405() throws Exception {
        mvc.perform(delete("/api/questions/random"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void idNoNumerico_devuelve400() throws Exception {
        mvc.perform(post("/api/questions/abc/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"selectedIndex\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rutaInexistente_devuelve404() throws Exception {
        mvc.perform(get("/api/ruta-que-no-existe"))
                .andExpect(status().isNotFound());
    }

    @Test
    void contentTypeIncorrecto_devuelve415() throws Exception {
        mvc.perform(post("/api/questions/1/answer")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("selectedIndex=1"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void cuerpoSinIndice_devuelve400() throws Exception {
        mvc.perform(post("/api/questions/1/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}