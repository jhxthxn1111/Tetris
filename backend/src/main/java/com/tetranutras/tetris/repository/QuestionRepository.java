package com.tetranutras.tetris.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetranutras.tetris.model.Question;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.List;

@Repository
public class QuestionRepository {

    private final ObjectMapper mapper = new ObjectMapper();
    private List<Question> questions = List.of();

    @PostConstruct
    void load() throws Exception {
        try (InputStream in = new ClassPathResource("questions.json").getInputStream()) {
            questions = mapper.readValue(in, new TypeReference<List<Question>>() {});
        }
    }

    public List<Question> findAll() {
        return questions;
    }
}
