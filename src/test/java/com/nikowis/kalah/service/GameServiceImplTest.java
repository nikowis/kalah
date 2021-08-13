package com.nikowis.kalah.service;

import com.nikowis.kalah.dto.GameCreatedDTO;
import com.nikowis.kalah.model.Kalah;
import com.nikowis.kalah.repository.KalahRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameServiceImplTest {

    private GameService gameService;

    private KalahRepository repositoryMock;

    @BeforeEach
    void initService() {
        repositoryMock = mock(KalahRepository.class);
        gameService = new GameServiceImpl(repositoryMock);
        when(repositoryMock.save(any(Kalah.class))).then(inv -> {
                    Kalah game = inv.getArgument(0);
                    game.setId("someId");
                    return game;
                }
        );
    }

    @Test
    void testCreateGameReturnsFilledObject() {
        GameCreatedDTO createdGame = gameService.createGame();

        Assertions.assertNotNull(createdGame.getId(), "Game id should not be null");
        Assertions.assertFalse(createdGame.getId().trim().isEmpty(), "Game id should not be empty");
    }

    @Test
    void testCreateGameSavesKalahToDb() {
        gameService.createGame();

        verify(repositoryMock).save(any(Kalah.class));
    }
}