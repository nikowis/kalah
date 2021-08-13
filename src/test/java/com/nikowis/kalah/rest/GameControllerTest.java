package com.nikowis.kalah.rest;

import com.nikowis.kalah.exception.GlobalExceptionHandler;
import com.nikowis.kalah.repository.KalahRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class GameControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private GameController gameController;

    @Autowired
    private KalahRepository kalahRepository;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gameController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    public void createGameTest() throws Exception {
        long gamesBeforeRequest = kalahRepository.count();

        mockMvc.perform(post(GameController.GAMES_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.url", is(notNullValue())));

        long gamesAfterRequest = kalahRepository.count();

        Assertions.assertEquals(gamesBeforeRequest + 1, gamesAfterRequest, "Games count should increment by one");
    }
}