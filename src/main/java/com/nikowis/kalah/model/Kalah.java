package com.nikowis.kalah.model;

import com.nikowis.kalah.exception.*;
import lombok.Getter;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.data.annotation.Id;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Kalah {

    static final Integer INITIAL_STONE_COUNT = 6;
    static final Integer FIRST_PIT_IDX = 1;
    static final Integer PLAYER_PITS = 6;
    static final Integer P1_HOUSE_IDX = FIRST_PIT_IDX + PLAYER_PITS;
    static final Integer P2_HOUSE_IDX = P1_HOUSE_IDX + PLAYER_PITS + 1;

    @Id
    private String id;
    @VisibleForTesting
    Map<Integer, Integer> pits;
    @VisibleForTesting
    Player whoseTurn;
    @VisibleForTesting
    boolean gameFinished;
    @VisibleForTesting
    Player winner;

    public Kalah() {
        pits = new HashMap<>();
        for (int i = FIRST_PIT_IDX; i <= P2_HOUSE_IDX; i++) {
            pits.put(i, i == P1_HOUSE_IDX || i == P2_HOUSE_IDX ? 0 : INITIAL_STONE_COUNT);
        }

        whoseTurn = Player.P1;
    }


    public void move(int selectedPit) {
        validateMove(selectedPit);
        Integer stonesInHand = removeStones(selectedPit);
        int lastPit = sowStones(selectedPit, stonesInHand);

        if (isEligibleToCaptureOppositePit(lastPit)) {
            captureAndMoveStones(lastPit);
        }

        if (isNotAHousePit(lastPit)) {
            changeTurns();
        }

        if (gameCanBeFinished()) {
            gameFinished = true;
            moveAllStonesToHouses();
            winner = selectWinner();
        }

    }

    private void moveAllStonesToHouses() {
        int p1RemainingStones = 0, p2RemainingStones = 0;
        for (int i = FIRST_PIT_IDX; i < P2_HOUSE_IDX; i++) {
            if (i < P1_HOUSE_IDX) {
                p1RemainingStones += removeStones(i);
            } else if (i > P1_HOUSE_IDX) {
                p2RemainingStones += removeStones(i);
            }
        }

        pits.put(P1_HOUSE_IDX, pits.get(P1_HOUSE_IDX) + p1RemainingStones);
        pits.put(P2_HOUSE_IDX, pits.get(P2_HOUSE_IDX) + p2RemainingStones);
    }

    private Player selectWinner() {
        Integer p1Stones = pits.get(P1_HOUSE_IDX);
        Integer p2Stones = pits.get(P2_HOUSE_IDX);

        if (p1Stones > p2Stones) {
            return Player.P1;
        } else if (p2Stones > p1Stones) {
            return Player.P2;
        }
        return null;
    }

    private boolean gameCanBeFinished() {
        return p1DoesntHaveStonesInPits() || p2DoesntHaveStonesInPits();
    }

    private boolean p1DoesntHaveStonesInPits() {
        return !pits.entrySet().stream().filter(e -> e.getKey() < P1_HOUSE_IDX).anyMatch(e -> e.getValue() > 0);
    }

    private boolean p2DoesntHaveStonesInPits() {
        return !pits.entrySet().stream().filter(e -> e.getKey() > P1_HOUSE_IDX && e.getKey() < P2_HOUSE_IDX).anyMatch(e -> e.getValue() > 0);
    }

    private void captureAndMoveStones(int pit) {
        Integer selfStones = removeStones(pit);
        Integer capturedStones = removeStones(getOppositePitIdx(pit));
        moveStonesToPlayersHouse(selfStones + capturedStones);
    }

    private void moveStonesToPlayersHouse(int stones) {
        if (Player.P1.equals(whoseTurn)) {
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
        if (Player.P1.equals(whoseTurn)) {
            whoseTurn = Player.P2;
        } else {
            whoseTurn = Player.P1;
        }
    }

    private boolean isNotAHousePit(int pit) {
        return !P1_HOUSE_IDX.equals(pit) && !P2_HOUSE_IDX.equals(pit);
    }

    private int sowStones(int fromPit, Integer stonesInHand) {
        int nextPit = fromPit;
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

    private Integer removeStones(int pit) {
        Integer stonesInHand = pits.get(pit);
        pits.put(pit, 0);
        return stonesInHand;
    }

    private void validateMove(int selectedPit) {
        if (gameFinished) {
            throw new GameFinishedException();
        }
        if (isPitOutOfBounds(selectedPit)) {
            throw new PitOutOfBoundsException();
        }

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

    private boolean isPitOutOfBounds(int pit) {
        return pit < FIRST_PIT_IDX || pit > P2_HOUSE_IDX;
    }

    private boolean isOpponentsPit(int pit) {
        return (Player.P1.equals(whoseTurn) && pit > P1_HOUSE_IDX) || (Player.P2.equals(whoseTurn) && pit < P1_HOUSE_IDX);
    }

    private boolean isHousePit(int pit) {
        return P1_HOUSE_IDX.equals(pit) || P2_HOUSE_IDX.equals(pit);
    }

    private boolean isOpponentsHouse(int pit) {
        return (Player.P1.equals(whoseTurn) && P2_HOUSE_IDX.equals(pit)) || (Player.P2.equals(whoseTurn) && P1_HOUSE_IDX.equals(pit));
    }

    @VisibleForTesting
    static int getNextPitIdx(int pit) {
        return P2_HOUSE_IDX.equals(pit) ? FIRST_PIT_IDX : pit + 1;
    }

    @VisibleForTesting
    static int getOppositePitIdx(int pit) {
        if (P1_HOUSE_IDX.equals(pit) || P2_HOUSE_IDX.equals(pit)) {
            throw new IllegalArgumentException("Houses do not have opposite pits");
        }
        return P2_HOUSE_IDX - pit;
    }

}
