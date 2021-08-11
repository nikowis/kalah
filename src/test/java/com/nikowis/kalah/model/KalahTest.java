package com.nikowis.kalah.model;

import com.nikowis.kalah.exceptions.CantMoveFromEmptyPitException;
import com.nikowis.kalah.exceptions.CantMoveHouseException;
import com.nikowis.kalah.exceptions.NotYourPitException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class KalahTest {

    /**
     * Helper method to initialize kalah state before tests from a human readable state.
     *
     * @param state state in order: [p1Pits, p1House, p2Pits, p2House]
     * @return kalah internal state
     */
    private Map<Integer, Integer> arrayToKalahState(int[] state) {
        HashMap<Integer, Integer> kalahState = new HashMap<>(state.length);
        for (int i = 0; i < state.length; i++) {
            kalahState.put(i + 1, state[i]);
        }
        return kalahState;
    }

    /**
     * Helper method to assert kalah pits state in a human readable way.
     * Compares kalah internal state to the expected array representation.
     *
     * @param expected expected kalah state in order: [p1Pits, p1House, p2Pits, p2House]
     * @param kalah    actual kalah game
     */
    private void assertKalahState(int[] expected, Kalah kalah) {
        Map<Integer, Integer> pits = kalah.pits;
        pits.forEach((k, v) -> {
            Assertions.assertEquals(expected[k - 1], v, "Incorrect kalah state pit #" + k);
        });
    }

    @Test
    public void testCreateGamePitsInitialized() {
        Kalah kalah = new Kalah();
        Map<Integer, Integer> pits = kalah.pits;

        Assertions.assertNotNull(pits, "Pits should be initialized");
        Assertions.assertEquals(Kalah.PLAYER_PITS * 2 + 2, kalah.pits.size(), "Pits");
    }

    @Test
    public void testCreateGamePitsFilled() {
        Kalah kalah = new Kalah();

        assertKalahState(new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0}, kalah);
    }

    @Test
    public void testCreateGameWhoseTurnInitialized() {
        Kalah kalah = new Kalah();

        Assertions.assertEquals(Turn.P1, kalah.whoseTurn, "P1 should go first");
    }

    @Test
    public void testMoveRemovesStonesFromSelectedPit() {
        Kalah kalah = new Kalah();

        kalah.move(Kalah.FIRST_PIT_IDX);

        Assertions.assertEquals(0, kalah.pits.get(Kalah.FIRST_PIT_IDX));
    }

    @Test
    public void testMoveAddsStonesToFollowingPits() {
        Kalah kalah = new Kalah();

        kalah.move(Kalah.FIRST_PIT_IDX);

        assertKalahState(new int[]{0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0}, kalah);
    }

    @Test
    public void testMoveSkipsOpponentsHouseP1() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{1, 1, 1, 1, 1, 8, 0, 1, 1, 1, 1, 1, 1, 0});
        kalah.whoseTurn = Turn.P1;

        kalah.move(6);

        assertKalahState(new int[]{2, 1, 1, 1, 1, 0, 1, 2, 2, 2, 2, 2, 2, 0}, kalah);
    }

    @Test
    public void testMoveSkipsOpponentsHouseP2() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 8, 0});
        kalah.whoseTurn = Turn.P2;

        kalah.move(13);

        assertKalahState(new int[]{2, 2, 2, 2, 2, 2, 0, 2, 1, 1, 1, 1, 0, 1}, kalah);
    }

    @Test
    public void testCantMoveFromEmpty() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        kalah.whoseTurn = Turn.P1;

        Assertions.assertThrows(CantMoveFromEmptyPitException.class, () -> kalah.move(Kalah.FIRST_PIT_IDX), "Can't select an empty pit in a move");
    }

    @Test
    public void testCantMoveOtherPlayersPitP1() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        kalah.whoseTurn = Turn.P1;

        Assertions.assertThrows(NotYourPitException.class, () -> kalah.move(Kalah.P1_HOUSE_IDX + 1), "Shouldn't allow player to move other player pit");
    }

    @Test
    public void testCantMoveOtherPlayersPitP2() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        kalah.whoseTurn = Turn.P2;

        Assertions.assertThrows(NotYourPitException.class, () -> kalah.move(Kalah.FIRST_PIT_IDX), "Shouldn't allow player to move other player pit");
    }

    @Test
    public void testCantMoveHouseP1() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        kalah.whoseTurn = Turn.P1;

        Assertions.assertThrows(CantMoveHouseException.class, () -> kalah.move(Kalah.P1_HOUSE_IDX), "Can't select house pit in a move");
    }

    @Test
    public void testCantMoveHouseP2() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        kalah.whoseTurn = Turn.P2;

        Assertions.assertThrows(CantMoveHouseException.class, () -> kalah.move(Kalah.P2_HOUSE_IDX), "Can't select house pit in a move");
    }

    @Test
    public void testGetNextPitIdx() {
        Assertions.assertEquals(2, Kalah.getNextPitIdx(1));
        Assertions.assertEquals(14, Kalah.getNextPitIdx(13));
        Assertions.assertEquals(1, Kalah.getNextPitIdx(14));
    }

}