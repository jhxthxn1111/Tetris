package com.tetranutras.tetris.controller;

import com.tetranutras.tetris.dto.AnswerRequest;
import com.tetranutras.tetris.dto.AnswerResult;
import com.tetranutras.tetris.dto.QuestionDto;
import com.tetranutras.tetris.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService service;

    public QuestionController(QuestionService service) {
        this.service = service;
    }

    @GetMapping
    public List<QuestionDto> all() {
        return service.allPublic();
    }

    @GetMapping("/random")
    public QuestionDto random(@RequestParam(defaultValue = "EASY") String difficulty) {
        return service.randomPublic(difficulty);
    }

    @PostMapping("/{id}/answer")
    public AnswerResult check(@PathVariable int id, @RequestBody AnswerRequest request) {
        if (request == null || request.selectedIndex() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe enviar selectedIndex");
        }
        return service.check(id, request.selectedIndex());
    }
}