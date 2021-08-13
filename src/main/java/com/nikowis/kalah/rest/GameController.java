package com.nikowis.kalah.rest;

import com.nikowis.kalah.dto.GameCreatedDTO;
import com.nikowis.kalah.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(GameController.GAMES_ENDPOINT)
public class GameController {

    public static final String GAMES_ENDPOINT = "/games";
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
        game.setUrl(String.format("http://%s:%s/%s/%s", serverAddress, serverPort, GAMES_ENDPOINT, game.getId()));
        return new ResponseEntity<>(game, HttpStatus.CREATED);
    }

}
