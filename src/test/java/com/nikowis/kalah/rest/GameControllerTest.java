package com.nikowis.kalah.rest;

import com.nikowis.kalah.model.Kalah;
import com.nikowis.kalah.model.Player;
import com.nikowis.kalah.repository.KalahRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private RestExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gameController)
                .setControllerAdvice(exceptionHandler)
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

    @Test
    public void makeAMoveTest() throws Exception {
        Kalah newGame = kalahRepository.save(new Kalah());

        int pitId = 2;
        mockMvc.perform(put(GameController.MOVE_ENDPOINT, newGame.getId(), pitId)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newGame.getId())))
                .andExpect(jsonPath("$.url", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(notNullValue())))
                .andExpect(jsonPath("$.status.2", is(0)))
                .andExpect(jsonPath("$.gameFinished", is(false)))
                .andExpect(jsonPath("$.whoseTurn", is(Player.P2.name())))
                .andExpect(jsonPath("$.winner", is(nullValue())));

        Kalah updatedGame = kalahRepository.findById(newGame.getId()).get();

        Assertions.assertEquals(0, updatedGame.getPits().get(pitId), "Pit after a move should be empty");
    }

    @Test
    public void makeAMoveNonExistingGameTest() throws Exception {
        mockMvc.perform(put(GameController.MOVE_ENDPOINT, "randomid", 2)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notNullValue())));
    }

    @Test
    public void makeIncorrectMoveExistingGame() throws Exception {
        Kalah newGame = kalahRepository.save(new Kalah());

        mockMvc.perform(put(GameController.MOVE_ENDPOINT, newGame.getId(), 200)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(notNullValue())));
    }
}