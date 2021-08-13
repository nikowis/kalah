package com.nikowis.kalah.service;

import com.nikowis.kalah.dto.GameCreatedDTO;
import com.nikowis.kalah.dto.GameStateDto;
import com.nikowis.kalah.exception.GameDoesntExistException;
import com.nikowis.kalah.model.Kalah;
import com.nikowis.kalah.repository.KalahRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameServiceImplTest {

    private GameService gameService;

    private KalahRepository repositoryMock;

    @BeforeEach
    void initService() {
        repositoryMock = mock(KalahRepository.class);
        gameService = new GameServiceImpl(repositoryMock);
        when(repositoryMock.save(any(Kalah.class))).then(inv -> inv.getArgument(0));
    }

    @Test
    void testCreateGameReturnsObject() {
        GameCreatedDTO createdGame = gameService.createGame();

        Assertions.assertNotNull(createdGame, "Game should not be null");
    }

    @Test
    void testCreateGameSavesKalahToDb() {
        gameService.createGame();

        verify(repositoryMock).save(any(Kalah.class));
    }

    @Test
    void testMakeAMoveReturnsObject() {
        String gameId = "12312";
        Integer pitId = 2;
        when(repositoryMock.findById(eq(gameId))).then(inv -> Optional.of(new Kalah()));

        GameStateDto result = gameService.makeAMove(gameId, pitId);

        Assertions.assertNotNull(result, "Make a move result should not be null");
    }

    @Test
    void testMakeAMoveFetchesTheGame() {
        String gameId = "12312";
        Integer pitId = 2;
        when(repositoryMock.findById(eq(gameId))).then(inv -> Optional.of(new Kalah()));

        gameService.makeAMove(gameId, pitId);

        verify(repositoryMock).findById(eq(gameId));
    }

    @Test
    void testMakeAMoveSavesTheGame() {
        when(repositoryMock.findById(any(String.class))).then(inv -> Optional.of(new Kalah()));

        String gameId = "12312";
        Integer pitId = 2;
        gameService.makeAMove(gameId, pitId);

        verify(repositoryMock).save(any(Kalah.class));
    }

    @Test
    void testMakeAMoveCallsKalah() {
        Kalah kalahMock = mock(Kalah.class);
        when(repositoryMock.findById(any(String.class))).then(inv -> Optional.of(kalahMock));

        String gameId = "12312";
        Integer pitId = 2;
        gameService.makeAMove(gameId, pitId);

        verify(kalahMock).move(eq(pitId));
    }

    @Test
    void testMakeAMoveOnNonExistingGame() {
        String gameId = "12312";
        Integer pitId = 2;
        when(repositoryMock.findById(eq(gameId))).then(inv -> Optional.empty());

        Assertions.assertThrows(GameDoesntExistException.class, () -> gameService.makeAMove(gameId, pitId), "Random game id should throw an exception");
    }
}