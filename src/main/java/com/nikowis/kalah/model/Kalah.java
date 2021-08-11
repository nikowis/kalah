package com.nikowis.kalah.model;

import com.nikowis.kalah.exceptions.CantMoveFromEmptyPitException;
import com.nikowis.kalah.exceptions.CantMoveHouseException;
import com.nikowis.kalah.exceptions.NotYourPitException;
import org.assertj.core.util.VisibleForTesting;

import java.util.HashMap;
import java.util.Map;

public class Kalah {

    static final Integer INITIAL_STONE_COUNT = 6;
    static final Integer FIRST_PIT_IDX = 1;
    static final Integer PLAYER_PITS = 6;
    static final Integer P1_IDX = 1;
    static final Integer P2_IDX = 2;
    static final Integer P1_HOUSE_IDX = FIRST_PIT_IDX + PLAYER_PITS;
    static final Integer P2_HOUSE_IDX = P1_HOUSE_IDX + PLAYER_PITS + 1;

    public Kalah() {
        pits = new HashMap<>();
        for (int i = FIRST_PIT_IDX; i <= FIRST_PIT_IDX + 2 * PLAYER_PITS + 1; i++) {
            pits.put(i, i == P1_HOUSE_IDX || i == P2_HOUSE_IDX ? 0 : INITIAL_STONE_COUNT);
        }

        whoseTurn = Turn.P1;
    }

    @VisibleForTesting
    Map<Integer, Integer> pits;
    @VisibleForTesting
    Turn whoseTurn;

    public void move(int movePit) {
        if(P1_HOUSE_IDX.equals(movePit) || P2_HOUSE_IDX.equals(movePit)) {
            throw new CantMoveHouseException();
        }
        if((Turn.P1.equals(whoseTurn) && movePit > P1_HOUSE_IDX) || (Turn.P2.equals(whoseTurn) && movePit < P1_HOUSE_IDX)) {
            throw new NotYourPitException();
        }
        Integer stones = pits.get(movePit);
        if(stones == 0) {
            throw new CantMoveFromEmptyPitException();
        }

        pits.put(movePit, 0);
        int nextPit = getNextPitIdx(movePit);
        while(stones > 0) {
            if((Turn.P1.equals(whoseTurn) && nextPit == P2_HOUSE_IDX) || (Turn.P2.equals(whoseTurn) && nextPit == P1_HOUSE_IDX)) {
                nextPit = getNextPitIdx(nextPit);
                continue;
            }
            pits.put(nextPit, pits.get(nextPit) + 1);
            stones--;
            nextPit = getNextPitIdx(nextPit);
        }
    }

    @VisibleForTesting
    static int getNextPitIdx(int pit) {
        return pit == P2_HOUSE_IDX ? FIRST_PIT_IDX : pit + 1;
    }

}
