package com.nikowis.kalah.service;

import com.nikowis.kalah.dto.GameCreatedDTO;
import com.nikowis.kalah.dto.GameStateDto;

public interface GameService {
    GameCreatedDTO createGame();

    GameStateDto makeAMove(String gameId, Integer pitId);
}
