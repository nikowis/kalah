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
        validateMove(selectedPit);
        Integer stonesInHand = removeStones(selectedPit);
        int lastPit = sowStones(selectedPit, stonesInHand);

        if(isEligibleToCaptureOppositePit(lastPit)) {
            captureOppositePit(lastPit);
        }

        if (isNotAHousePit(lastPit)) {
            changeTurns();
        }
    }

    private void captureOppositePit(int pit) {
        Integer selfStones = removeStones(pit);
        Integer capturedStones = removeStones(getOppositePitIdx(pit));
        moveStonesToPlayersHouse(selfStones + capturedStones);
    }

    private void moveStonesToPlayersHouse(int stones) {
        if(Turn.P1.equals(whoseTurn)) {
            pits.put(P1_HOUSE_IDX, pits.get(P1_HOUSE_IDX) + stones);
        } else {
            pits.put(P2_HOUSE_IDX, pits.get(P2_HOUSE_IDX) + stones);
        }
    }

    private boolean isEligibleToCaptureOppositePit(int pit) {
        return isPlayersRegularPit(pit) && wasEmptyBeforeTheMove(pit);
    }

    private boolean wasEmptyBeforeTheMove(int pit) {
        return pits.get(pit) == 1;
    }

    private boolean isPlayersRegularPit(int pit) {
        return !isOpponentsPit(pit) && !isHousePit(pit);
    }

    private void changeTurns() {
        if (Turn.P1.equals(whoseTurn)) {
            whoseTurn = Turn.P2;
        } else {
            whoseTurn = Turn.P1;
        }
    }

    private boolean isNotAHousePit(int lastPit) {
        return !P1_HOUSE_IDX.equals(lastPit) && !P2_HOUSE_IDX.equals(lastPit);
    }

    private int sowStones(int selectedPit, Integer stonesInHand) {
        int nextPit = selectedPit;
        while (stonesInHand > 0) {
            nextPit = getNextPitIdx(nextPit);
            if (isOpponentsHouse(nextPit)) {
                continue;
            }
            pits.put(nextPit, pits.get(nextPit) + 1);
            stonesInHand--;
        }
        return nextPit;
    }

    private Integer removeStones(int selectedPit) {
        Integer stonesInHand = pits.get(selectedPit);
        pits.put(selectedPit, 0);
        return stonesInHand;
    }

    private void validateMove(int selectedPit) {
        if (isHousePit(selectedPit)) {
            throw new CantMoveHouseException();
        }
        if (isOpponentsPit(selectedPit)) {
            throw new NotYourPitException();
        }

        if (pits.get(selectedPit) == 0) {
            throw new CantMoveFromEmptyPitException();
        }
    }

    private boolean isOpponentsPit(int pit) {
        return (Turn.P1.equals(whoseTurn) && pit > P1_HOUSE_IDX) || (Turn.P2.equals(whoseTurn) && pit < P1_HOUSE_IDX);
    }

    private boolean isHousePit(int pit) {
        return P1_HOUSE_IDX.equals(pit) || P2_HOUSE_IDX.equals(pit);
    }

    private boolean isOpponentsHouse(int nextPit) {
        return (Turn.P1.equals(whoseTurn) && P2_HOUSE_IDX.equals(nextPit)) || (Turn.P2.equals(whoseTurn) && P1_HOUSE_IDX.equals(nextPit));
    }

    @VisibleForTesting
    static int getNextPitIdx(int pit) {
        return P2_HOUSE_IDX.equals(pit) ? FIRST_PIT_IDX : pit + 1;
    }

    @VisibleForTesting
    static int getOppositePitIdx(int pit) {
        if(P1_HOUSE_IDX.equals(pit) || P2_HOUSE_IDX.equals(pit)) {
            throw new IllegalArgumentException("Houses do not have opposite pits");
        }
        return P2_HOUSE_IDX - pit;
    }

}
