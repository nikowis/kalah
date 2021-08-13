package com.nikowis.kalah.dto;

import com.nikowis.kalah.model.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class GameStateDto {

    private String id;
    private String url;
    private Map<Integer, Integer> status;
    private boolean gameFinished;
    private Player whoseTurn;
    private Player winner;

}
