package com.nikowis.kalah.model;

import com.nikowis.kalah.exceptions.CantMoveFromEmptyPitException;
import com.nikowis.kalah.exceptions.CantMoveHouseException;
import com.nikowis.kalah.exceptions.GameFinishedException;
import com.nikowis.kalah.exceptions.NotYourPitException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
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
            Assertions.assertEquals(expected[k - 1], v, String.format("Incorrect kalah state pit #%d: %s", k, Arrays.toString(pits.values().toArray())));
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

        Assertions.assertEquals(Player.P1, kalah.whoseTurn, "P1 should go first");
    }

    @Test
    public void testCreateGameNotFinished() {
        Kalah kalah = new Kalah();

        Assertions.assertFalse(kalah.gameFinished, "Game should initialize unfinished");
    }

    @Test
    public void testCreateGameNoWinner() {
        Kalah kalah = new Kalah();

        Assertions.assertNull(kalah.winner, "Game should initialize without a winner");
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
        kalah.whoseTurn = Player.P1;

        kalah.move(6);

        assertKalahState(new int[]{2, 1, 1, 1, 1, 0, 1, 2, 2, 2, 2, 2, 2, 0}, kalah);
    }

    @Test
    public void testMoveSkipsOpponentsHouseP2() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 8, 0});
        kalah.whoseTurn = Player.P2;

        kalah.move(13);

        assertKalahState(new int[]{2, 2, 2, 2, 2, 2, 0, 2, 1, 1, 1, 1, 0, 1}, kalah);
    }

    @Test
    public void testCantMoveFromEmpty() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        kalah.whoseTurn = Player.P1;

        Assertions.assertThrows(CantMoveFromEmptyPitException.class, () -> kalah.move(Kalah.FIRST_PIT_IDX), "Can't select an empty pit in a move");
    }

    @Test
    public void testCantMoveOtherPlayersPitP1() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        kalah.whoseTurn = Player.P1;

        Assertions.assertThrows(NotYourPitException.class, () -> kalah.move(Kalah.P1_HOUSE_IDX + 1), "Shouldn't allow player to move other player pit");
    }

    @Test
    public void testCantMoveOtherPlayersPitP2() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        kalah.whoseTurn = Player.P2;

        Assertions.assertThrows(NotYourPitException.class, () -> kalah.move(Kalah.FIRST_PIT_IDX), "Shouldn't allow player to move other player pit");
    }

    @Test
    public void testCantMoveHouseP1() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        kalah.whoseTurn = Player.P1;

        Assertions.assertThrows(CantMoveHouseException.class, () -> kalah.move(Kalah.P1_HOUSE_IDX), "Can't select house pit in a move");
    }

    @Test
    public void testCantMoveHouseP2() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        kalah.whoseTurn = Player.P2;

        Assertions.assertThrows(CantMoveHouseException.class, () -> kalah.move(Kalah.P2_HOUSE_IDX), "Can't select house pit in a move");
    }

    @Test
    public void testMoveChangesWhoseTurnWhenFinishedInARegularPit() {
        Kalah kalah = new Kalah();
        kalah.whoseTurn = Player.P1;

        kalah.move(Kalah.FIRST_PIT_IDX + 2);

        Assertions.assertEquals(Player.P2, kalah.whoseTurn, "Whose turn should change after a move");
    }

    @Test
    public void testMoveDoesntChangeWhoseTurnWhenFinishedInPlayersHouse() {
        Kalah kalah = new Kalah();
        kalah.whoseTurn = Player.P1;

        kalah.move(Kalah.FIRST_PIT_IDX);

        Assertions.assertEquals(Player.P1, kalah.whoseTurn, "Whose turn shouldn't change when move finished in a house");
    }

    @Test
    public void testMoveCapturesCorrectly() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{0, 1, 1, 1, 1, 8, 0, 1, 1, 1, 1, 1, 3, 0});
        kalah.whoseTurn = Player.P1;

        kalah.move(6);

        assertKalahState(new int[]{0, 1, 1, 1, 1, 0, 6, 2, 2, 2, 2, 2, 0, 0}, kalah);
    }

    @Test
    public void testMoveSecondPlayerCanCapture() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{1, 1, 17, 1, 1, 0, 77, 5, 1, 1, 0, 12, 2, 0});
        kalah.whoseTurn = Player.P2;

        kalah.move(12);

        assertKalahState(new int[]{2, 2, 0, 2, 2, 1, 77, 6, 2, 2, 0, 0, 3, 20}, kalah);
    }

    @Test
    public void testMoveFinishesInOpponentsEmptyPitDoesntCapture() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{7, 7, 7, 7, 7, 3, 7, 1, 0, 1, 1, 1, 1, 1});
        kalah.whoseTurn = Player.P1;

        kalah.move(6);

        assertKalahState(new int[]{7, 7, 7, 7, 7, 0, 8, 2, 1, 1, 1, 1, 1, 1}, kalah);
    }

    @Test
    public void testLastMoveShouldFinishGameSetWinnerAndMoveAllStones() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{0, 0, 0, 0, 0, 1, 14, 1, 1, 1, 1, 1, 1, 5});
        kalah.whoseTurn = Player.P1;

        kalah.move(6);

        Assertions.assertEquals(Player.P1, kalah.winner, "Winner should selected properly");
        Assertions.assertTrue(kalah.gameFinished, "Game should be finished");
        assertKalahState(new int[]{0, 0, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0, 0, 11}, kalah);
    }

    @Test
    public void testLastMoveOppositePlayerWins() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{0, 0, 0, 0, 0, 1, 14, 2, 2, 2, 2, 2, 2, 5});
        kalah.whoseTurn = Player.P1;

        kalah.move(6);

        Assertions.assertEquals(Player.P2, kalah.winner, "Winner should selected properly");
        Assertions.assertTrue(kalah.gameFinished, "Game should be finished");
        assertKalahState(new int[]{0, 0, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0, 0, 17}, kalah);
    }

    @Test
    public void testLastMoveWithCapture() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{0, 0, 0, 0, 0, 8, 14, 1, 1, 1, 1, 1, 3, 5});
        kalah.whoseTurn = Player.P1;

        kalah.move(6);

        Assertions.assertEquals(Player.P1, kalah.winner, "Winner should selected properly");
        Assertions.assertTrue(kalah.gameFinished, "Game should be finished");
        assertKalahState(new int[]{0, 0, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 15}, kalah);
    }

    @Test
    public void testMoveCapturesOpponentsLastStonesAndEndsGame() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{1, 0, 1, 1, 1, 1, 10, 0, 0, 0, 0, 15, 0, 5});
        kalah.whoseTurn = Player.P1;

        kalah.move(1);

        Assertions.assertEquals(Player.P1, kalah.winner, "Winner should selected properly");
        Assertions.assertTrue(kalah.gameFinished, "Game should be finished");
        assertKalahState(new int[]{0, 0, 0, 0, 0, 0, 30, 0, 0, 0, 0, 0, 0, 5}, kalah);
    }

    @Test
    public void testGameFinishedWithDraw() {
        Kalah kalah = new Kalah();
        kalah.pits = arrayToKalahState(new int[]{0, 0, 0, 0, 1, 0, 8, 1, 0, 0, 0, 0, 0, 10});
        kalah.whoseTurn = Player.P1;

        kalah.move(5);

        Assertions.assertNull(kalah.winner, "When there is a draw then no winner should be selected");
        Assertions.assertTrue(kalah.gameFinished, "Game should be finished");
        assertKalahState(new int[]{0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 10}, kalah);
    }

    @Test
    public void testMoveAfterGameFinishedThrowsException() {
        Kalah kalah = new Kalah();
        kalah.gameFinished = true;

        Assertions.assertThrows(GameFinishedException.class, () -> kalah.move(6), "Shouldn't allow to move in a finished game");
    }

    @Test
    public void testGetNextPitIdx() {
        Assertions.assertEquals(Kalah.FIRST_PIT_IDX + 1, Kalah.getNextPitIdx(Kalah.FIRST_PIT_IDX));
        Assertions.assertEquals(Kalah.P2_HOUSE_IDX, Kalah.getNextPitIdx(Kalah.P2_HOUSE_IDX - 1));
        Assertions.assertEquals(Kalah.FIRST_PIT_IDX, Kalah.getNextPitIdx(Kalah.P2_HOUSE_IDX));
    }

    @Test
    public void testGetOppositePitIdx() {
        Assertions.assertEquals(Kalah.P2_HOUSE_IDX - 1, Kalah.getOppositePitIdx(Kalah.FIRST_PIT_IDX));
        Assertions.assertEquals(Kalah.FIRST_PIT_IDX, Kalah.getOppositePitIdx(Kalah.P2_HOUSE_IDX - 1));
        Assertions.assertEquals(9, Kalah.getOppositePitIdx(5));
        Assertions.assertEquals(5, Kalah.getOppositePitIdx(9));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Kalah.getOppositePitIdx(Kalah.P1_HOUSE_IDX);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Kalah.getOppositePitIdx(Kalah.P2_HOUSE_IDX);
        });
    }


}