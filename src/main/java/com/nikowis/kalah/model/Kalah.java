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

    public void move(int selectedPit) {
        if(isHousePit(selectedPit)) {
            throw new CantMoveHouseException();
        }
        if(isOpponentsPit(selectedPit)) {
            throw new NotYourPitException();
        }
        Integer stonesInHand = pits.get(selectedPit);
        if(stonesInHand == 0) {
            throw new CantMoveFromEmptyPitException();
        }
        pits.put(selectedPit, 0);

        int nextPit = getNextPitIdx(selectedPit);
        while(stonesInHand > 0) {
            if(isOpponentsHouse(nextPit)) {
                nextPit = getNextPitIdx(nextPit);
                continue;
            }
            pits.put(nextPit, pits.get(nextPit) + 1);
            stonesInHand--;
            nextPit = getNextPitIdx(nextPit);
        }
    }

    private boolean isOpponentsPit(int pit) {
        return (Turn.P1.equals(whoseTurn) && pit > P1_HOUSE_IDX) || (Turn.P2.equals(whoseTurn) && pit < P1_HOUSE_IDX);
    }

    private boolean isHousePit(int pit) {
        return P1_HOUSE_IDX.equals(pit) || P2_HOUSE_IDX.equals(pit);
    }

    private boolean isOpponentsHouse(int nextPit) {
        return (Turn.P1.equals(whoseTurn) && nextPit == P2_HOUSE_IDX) || (Turn.P2.equals(whoseTurn) && nextPit == P1_HOUSE_IDX);
    }

    @VisibleForTesting
    static int getNextPitIdx(int pit) {
        return pit == P2_HOUSE_IDX ? FIRST_PIT_IDX : pit + 1;
    }

}
