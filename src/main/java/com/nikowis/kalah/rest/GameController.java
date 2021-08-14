package com.nikowis.kalah.rest;

import com.nikowis.kalah.dto.GameCreatedDTO;
import com.nikowis.kalah.dto.GameStateDto;
import com.nikowis.kalah.service.GameService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The game was created successfully"),
            @ApiResponse(code = 404, message = "The game does not exist"),
    })
    @ApiOperation(value = "Create a new game.", notes = "Returns the created game id to use in subsequent requests.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameCreatedDTO createGame() {
        GameCreatedDTO game = gameService.createGame();
        game.setUrl(getGameUrl(game.getId()));
        return game;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The move was valid and the new game state is returned"),
            @ApiResponse(code = 400, message = "The move was not valid", response = RestExceptionHandler.ErrorMessageDTO.class),
            @ApiResponse(code = 404, message = "The game does not exist"),
    })
    @ApiOperation(value = "Make a move in an existing game.")
    @PutMapping(path = MOVE_PATH)
    public GameStateDto makeAMove(@ApiParam(value = "The game id", required = true, example = "6117e3d3f86b8c0285711b65") @PathVariable(GAME_ID_VAR) String gameId
            , @ApiParam(value = "The selected pit: 1-6 for Player1 and 8-13 for Player2", required = true, example = "3") @PathVariable(PIT_ID_VAR) Integer pitId
    ) {
        GameStateDto gameState = gameService.makeAMove(gameId, pitId);
        gameState.setUrl(getGameUrl(gameState.getId()));
        return gameState;
    }

    private String getGameUrl(String id) {
        return String.format("http://%s:%s/%s/%s", serverAddress, serverPort, GAMES_ENDPOINT, id);
    }

}
