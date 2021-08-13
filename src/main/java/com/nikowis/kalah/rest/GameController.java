package com.nikowis.kalah.rest;

import com.nikowis.kalah.dto.GameCreatedDTO;
import com.nikowis.kalah.dto.GameStateDto;
import com.nikowis.kalah.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(GameController.GAMES_ENDPOINT)
public class GameController {

    public static final String GAMES_ENDPOINT = "/games";
    public static final String GAME_ID_VAR = "gameId";
    public static final String PIT_ID_VAR = "pitId";
    public static final String MOVE_PATH = "/{" + GAME_ID_VAR + "}/pits/{" + PIT_ID_VAR + "}";
    public static final String MOVE_ENDPOINT = GAMES_ENDPOINT + MOVE_PATH;

    private final String serverAddress;
    private final String serverPort;
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService, @Value("${server.address}") String serverAddress, @Value("${server.port}") String serverPort) {
        this.gameService = gameService;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @PostMapping
    public ResponseEntity<GameCreatedDTO> createGame() {
        GameCreatedDTO game = gameService.createGame();
        game.setUrl(getGameUrl(game.getId()));
        return new ResponseEntity<>(game, HttpStatus.CREATED);
    }

    @PutMapping(path = MOVE_PATH)
    public GameStateDto makeAMove(@PathVariable(GAME_ID_VAR) String gameId, @PathVariable(PIT_ID_VAR) Integer pitId) {
        GameStateDto gameState = gameService.makeAMove(gameId, pitId);
        gameState.setUrl(getGameUrl(gameState.getId()));
        return gameState;
    }

    private String getGameUrl(String id) {
        return String.format("http://%s:%s/%s/%s", serverAddress, serverPort, GAMES_ENDPOINT, id);
    }

}
