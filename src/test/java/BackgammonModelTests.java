import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import stacs.arcade.backgammon.BackgammonModel;
import stacs.arcade.backgammon.IllegalMoveException;
import stacs.arcade.backgammon.Pair;

import static stacs.arcade.backgammon.BackgammonModel.PlayerColour.WHITE;
import static stacs.arcade.backgammon.BackgammonModel.PlayerColour.BLACK;


import java.util.HashMap;
import java.util.LinkedList;

public class BackgammonModelTests {

    BackgammonModel model = null;

    @BeforeEach
    void setup() {
        this.model = new BackgammonModel();
        this.model.initialiseGame();
    }

    @Test
    void mustInitializeBoardWithPiecesAtInitialPositions() {

        assertTrue(model.getPieceAt(1).equals(new Pair(BLACK, 2)));
        assertTrue(model.getPieceAt(6).equals(new Pair(WHITE, 5)));
        assertTrue(model.getPieceAt(8).equals(new Pair(WHITE, 3)));
        assertTrue(model.getPieceAt(12).equals(new Pair(BLACK, 5)));
        assertTrue(model.getPieceAt(13).equals(new Pair(WHITE, 5)));
        assertTrue(model.getPieceAt(17).equals(new Pair(BLACK, 3)));
        assertTrue(model.getPieceAt(19).equals(new Pair(BLACK, 5)));
        assertTrue(model.getPieceAt(24).equals(new Pair(WHITE, 2)));
        assertTrue(model.getPieceAt(2) == null);
    }

    @Test
    void diceMustBeBetweenOneAndSix() {
        assertTrue(this.model.throwDice() > 0 && this.model.throwDice() < 7);
    }

    @Test
    void playerTurnMustNotBeAllocatedBeforeDiceThrow() {
        assertNull(this.model.currentPlayerToMove());
    }

    @Test
    void checkFirstToMove() {

        assertFalse(model.assignPlayerTurn(1, 1));
        assertNull(model.currentPlayerToMove());

        assertTrue(model.assignPlayerTurn(2, 1));
        assertEquals(model.currentPlayerToMove(), BLACK);

        assertTrue(model.assignPlayerTurn(1, 2));
        assertEquals(model.currentPlayerToMove(), WHITE);
    }

    @Test
    void mustUpdateBoard() throws IllegalMoveException {

        model.assignPlayerTurn(2, 1);
        this.model.makeMove(BLACK, 1, 2);
        assertTrue(this.model.getPieceAt(2).equals(new Pair(BLACK, 1)));
        assertTrue(this.model.getPieceAt(1).equals(new Pair(BLACK, 1)));
    }

    @Test
    void mustExecuteValidMoves() throws IllegalMoveException {
        model.assignPlayerTurn(2, 1);
        assertTrue(model.getPieceAt(1).equals(new Pair(BLACK, 2)));
        assertNull(model.getPieceAt(2));
        this.model.makeMove(BLACK, 1, 2);
        assertTrue(model.getPieceAt(2).equals(new Pair(BLACK, 1)));
        assertTrue(model.getPieceAt(1).equals(new Pair(BLACK, 1)));
    }

    @Test
    void mustExecuteMoveOnPieceWithCurrentPlayersCheckersOn() throws IllegalMoveException {
        model.assignPlayerTurn(5, 1);
        this.model.makeMove(BLACK, 12, 17);
        assertTrue(model.getPieceAt(17).equals(new Pair(BLACK, 4)));
        assertTrue(model.getPieceAt(12).equals(new Pair(BLACK, 4)));
    }

    @Test
    void checkLegalMovesWithDifferentDices() throws IllegalMoveException {
        //Assign Black as the first player and then calculate the legal moves
        model.assignPlayerTurn(2, 1);
        HashMap<Integer, LinkedList<Integer>> actualValue = model.getLegalMovesOnBoard(1, 2);

        LinkedList<Integer> toTrianglesListForFromTriangles = actualValue.get(1);
        assertEquals(toTrianglesListForFromTriangles.getFirst(), 2);
        assertEquals(toTrianglesListForFromTriangles.get(1), 3);

        toTrianglesListForFromTriangles = actualValue.get(12);
        assertEquals(toTrianglesListForFromTriangles.getFirst(), 14);

        toTrianglesListForFromTriangles = actualValue.get(17);
        assertEquals(toTrianglesListForFromTriangles.getFirst(), 18);
        assertEquals(toTrianglesListForFromTriangles.get(1), 19);

        toTrianglesListForFromTriangles = actualValue.get(19);
        assertEquals(toTrianglesListForFromTriangles.getFirst(), 20);
        assertEquals(toTrianglesListForFromTriangles.get(1), 21);
    }

    @Test
    void checkIsOccupied() {
        assertEquals(this.model.isOccupied(6, WHITE), 0);
        assertEquals(this.model.isOccupied(2, WHITE), 0);
        assertEquals(this.model.isOccupied(12, WHITE), 1);
        //TODO that isOccupied returns 2 if only one on that position -> meaning it can be captured.
    }

    @Test
    void mustReturnAllPointsWithPlayerPiecesOn() {

        LinkedList<Integer> pointsWithPlayerPieces = model.getAllPointsWithPlayerPieces(BLACK);
        HashMap<Integer, Pair> location = model.getLocation();
        assertEquals(location.get(pointsWithPlayerPieces.get(0)).getPlayerColour(), BLACK);
        assertEquals(location.get(pointsWithPlayerPieces.get(1)).getPlayerColour(), BLACK);
        assertEquals(location.get(pointsWithPlayerPieces.get(2)).getPlayerColour(), BLACK);
        assertEquals(location.get(pointsWithPlayerPieces.get(3)).getPlayerColour(), BLACK);

        pointsWithPlayerPieces = model.getAllPointsWithPlayerPieces(WHITE);
        assertEquals(location.get(pointsWithPlayerPieces.get(0)).getPlayerColour(), WHITE);
        assertEquals(location.get(pointsWithPlayerPieces.get(1)).getPlayerColour(), WHITE);
        assertEquals(location.get(pointsWithPlayerPieces.get(2)).getPlayerColour(), WHITE);
        assertEquals(location.get(pointsWithPlayerPieces.get(3)).getPlayerColour(), WHITE);

    }

    @Test
    void checkLegalMovesWithDoubleDices() throws IllegalMoveException {
        //Assign the Black player turn and then get the legal moves for a double dice.
        this.model.assignPlayerTurn(2, 1);
        HashMap<Integer, LinkedList<Integer>> actualValue = model.getLegalMovesOnBoard(4, 4);

        LinkedList<Integer> toTrianglesListForFromTriangles = actualValue.get(1);
        assertEquals(toTrianglesListForFromTriangles.size(), 1);
        assertEquals(toTrianglesListForFromTriangles.getFirst(), 5);

        toTrianglesListForFromTriangles = actualValue.get(12);
        assertEquals(toTrianglesListForFromTriangles.size(), 1);
        assertEquals(toTrianglesListForFromTriangles.getFirst(), 16);

        toTrianglesListForFromTriangles = actualValue.get(17);
        assertEquals(toTrianglesListForFromTriangles.size(), 1);
        assertEquals(toTrianglesListForFromTriangles.getFirst(), 21);

        toTrianglesListForFromTriangles = actualValue.get(19);
        assertEquals(toTrianglesListForFromTriangles.size(), 1);
        assertEquals(toTrianglesListForFromTriangles.getFirst(), 23);
    }


    @Test
    void mustRejectIllegalMoves() throws IllegalMoveException {
        model.assignPlayerTurn(2, 1);
        assertThrows(IllegalMoveException.class, () -> {
            this.model.makeMove(BLACK, 1, 6);
        });
    }


    @Test
    void mustRejectMoveToTheOppositeDirection() throws IllegalMoveException {
        assertThrows(IllegalMoveException.class, () -> {
            this.model.makeMove(BLACK, 12, 11);
        });
    }

    @Test
    void mustRejectMovesWithTheSameDice() throws IllegalMoveException {
        //Assign the players with dice 2,1. The black player must play 2,1
        //The player tried to play dice = 1 two times.
        HashMap<Integer,Pair> previousState = model.getLocation();
        model.assignPlayerTurn(2, 1);
        model.playTurn(1, 2);
        this.model.playTurn(2, 3);
        assertTrue(previousState.equals(model.getLocation()));
    }

    @Test
    void checkLegalMovesWithInvalidDice() {

        //Assign the Black player turn and then get the legal moves for a double dice.
        this.model.assignPlayerTurn(2, 1);
        assertThrows(IllegalMoveException.class, () -> {
            this.model.getLegalMovesOnBoard(-1, 4);
        });

        assertThrows(IllegalMoveException.class, () -> {
            this.model.getLegalMovesOnBoard(1, 7);
        });
    }

    @Test
    void mustRejectMoveFromTheNonCurrentPlayer() throws IllegalMoveException {
        //Black is playing
        model.assignPlayerTurn(2, 1);

        assertThrows(IllegalMoveException.class, () -> {
            model.makeMove(WHITE, 1, 2);
        });
    }

    @Test
    void mustRejectMoveToOccupiedPoint() {
        model.assignPlayerTurn(5, 1);
        assertThrows(IllegalMoveException.class, () -> {
            model.makeMove(BLACK, 1, 6);
        });
    }

    @Test
    void checkInitialPlayersAtHomePosition() {
        assertTrue(model.getPieceAt(6).equals(new Pair(WHITE, 5)));
        assertTrue(model.getPieceAt(19).equals(new Pair(BLACK, 5)));
        assertEquals(model.countPiecesInHomePosition(BLACK), 5);
        assertEquals(model.countPiecesInHomePosition(WHITE), 5);
    }
    @Test
    void checkInitialPlayersAtHomeIsFalse() {
        assertTrue(model.getPieceAt(6).equals(new Pair(WHITE, 5)));
        assertTrue(model.getPieceAt(19).equals(new Pair(BLACK, 5)));
        assertEquals(model.countPiecesInHomePosition(BLACK), 5);
        assertEquals(model.countPiecesInHomePosition(WHITE), 5);
        assertEquals(model.allPiecesInHome(BLACK), false);
        assertEquals(model.allPiecesInHome(WHITE), false);
    }

    @Test
    void bearOffIsEmtpyAtStart() {
        assertEquals(model.countPiecesInBearOff(BLACK), 0);
        assertEquals(model.countPiecesInBearOff(WHITE), 0);
    }



    @Test
    void bearOffIsFalseAtStart() {
        assertEquals(model.isBearOff(WHITE), false);
        assertEquals(model.isBearOff(BLACK), false);
    }

    /** _________________________________________________________________________________________________________
     *     Tests below are commented because they require a method that cannot be public because it would allow the user to
     *     change the values of the dices manually
     * _________________________________________________________________________________________________________
     */
    /*@Test
    void checkPlayersAtHomePosition() throws IllegalMoveException {
        assertTrue(model.getPieceAt(6).equals(new Pair(WHITE, 5)));
        assertTrue(model.getPieceAt(19).equals(new Pair(BLACK, 5)));

        assertEquals(model.countPiecesInHomePosition(BLACK), 5);
        assertEquals(model.countPiecesInHomePosition(WHITE), 5);

        model.assignPlayerTurn(1,3);
        this.model.playTurn( 8, 5);
        assertEquals(model.countPiecesInHomePosition(WHITE), 6);

        this.model.playTurn(5,4);
        assertEquals(model.countPiecesInHomePosition(WHITE), 6);

        model.setDices(4,3);
        this.model.playTurn(17, 21);
        assertEquals(model.countPiecesInHomePosition(BLACK), 6);


        this.model.playTurn(17, 20);
        assertEquals(model.countPiecesInHomePosition(BLACK), 7);
    }*/

/*
    @Test
    void mustOnlyPlayAvailableMovesWhenDoublePlay() throws IllegalMoveException {
        model.assignPlayerTurn(2,1);
        model.setDices(6,6);

        model.playTurn(1,7);
        assertTrue(model.getPieceAt(1).equals(new Pair(BLACK,1)));
        assertTrue(model.getPieceAt(7).equals(new Pair(BLACK,1)));
        assertEquals(model.currentPlayerToMove(),BLACK);

        model.playTurn(1,7);
        assertNull(model.getPieceAt(1));
        assertTrue(model.getPieceAt(7).equals(new Pair(BLACK,2)));


        model.playTurn(12,18);
        assertTrue(model.getPieceAt(12).equals(new Pair(BLACK,4)));
        assertTrue(model.getPieceAt(18).equals(new Pair(BLACK,1)));


        model.playTurn(12,18);
        assertTrue(model.getPieceAt(12).equals(new Pair(BLACK,3)));
        assertTrue(model.getPieceAt(18).equals(new Pair(BLACK,2)));

        assertEquals(model.currentPlayerToMove(),WHITE);
        model.setDices(6,6);
        model.playTurn(8,2);
        model.playTurn(8,2);
        model.playTurn(8,2);
        assertEquals(model.currentPlayerToMove(),BLACK);
    }

    @Test
    void mustAllowDoubleDicePlay() throws IllegalMoveException {
        model.assignPlayerTurn(2,1);
        model.setDices(6,6);

        assertEquals(model.currentPlayerToMove(),BLACK);

        model.playTurn(1,7);
        assertEquals(model.currentPlayerToMove(),BLACK);

        model.playTurn(1,7);
        assertEquals(model.currentPlayerToMove(),BLACK);

        model.playTurn(12,18);
        assertEquals(model.currentPlayerToMove(),BLACK);

        model.playTurn(12,18);
        assertEquals(model.currentPlayerToMove(),WHITE);
    }
*/

   /* @Test
    void testPlayTurn() throws IllegalMoveException {

        model.assignPlayerTurn(2, 1);
        assertNull(model.getPieceAt(3));
        assertEquals(model.getFirstPlayerToGo(), BLACK);
        assertEquals(model.currentPlayerToMove(), BLACK);

        model.playTurn(1, 3);
        assertTrue(model.getPieceAt(3).equals(new Pair(BLACK, 1)));
        assertTrue(model.getPieceAt(1).equals(new Pair(BLACK, 1)));
        assertEquals(model.currentPlayerToMove(), BLACK);

        model.playTurn(3, 4);
        assertNull(model.getPieceAt(3));
        assertTrue(model.getPieceAt(4).equals(new Pair(BLACK, 1)));
        assertEquals(model.currentPlayerToMove(), WHITE);

        model.setDices(5,5);
        model.playTurn(13,8);
        assertTrue(model.getPieceAt(13).equals(new Pair(WHITE, 4)));
        assertTrue(model.getPieceAt(8).equals(new Pair(WHITE, 4)));
        assertEquals(model.currentPlayerToMove(), WHITE);

        model.playTurn(8,3);
        assertTrue(model.getPieceAt(8).equals(new Pair(WHITE, 3)));
        assertTrue(model.getPieceAt(3).equals(new Pair(WHITE, 1)));
        assertEquals(model.currentPlayerToMove(), WHITE);

        model.playTurn(6,1);
        assertTrue(model.getPieceAt(1).equals(new Pair(WHITE, 1)));
        assertTrue(model.getPieceAt(25).equals(new Pair(BLACK, 1)));
        assertEquals(model.currentPlayerToMove(), WHITE);

        model.playTurn(6,1);
        assertTrue(model.getPieceAt(1).equals(new Pair(WHITE, 2)));

        model.setDices(6,3);
        model.playTurn(25,3);
        assertNull(model.getPieceAt(25));
        assertTrue(model.getPieceAt(28).equals(new Pair(WHITE,1)));
        assertEquals(model.currentPlayerToMove(), BLACK);


        model.playTurn(4,10);
        assertTrue(model.getPieceAt(10).equals(new Pair(BLACK,1)));
        assertEquals(model.currentPlayerToMove(), WHITE);
    }

    @Test
    void mustPlaceCapturedPiece() throws IllegalMoveException {
        model.assignPlayerTurn(2, 1);
        model.playTurn(1,3);
        model.playTurn(1,2);

        model.setDices(3,1);
        model.playTurn(6,3); // WHITE CAPTURES BLACK PIECE
        model.playTurn(6,5);

        assertEquals(model.currentPlayerToMove(), BLACK);

        model.setDices(6,1);
        model.playTurn(25,1);

        assertNull(model.getPieceAt(25));
        assertTrue(model.getPieceAt(1).equals(new Pair(BLACK,1)));
        assertEquals(model.currentPlayerToMove(),BLACK);
    }

    @Test
    void mustAllowMoveAfterPlacingCapturingPiece() throws  IllegalMoveException{
        model.assignPlayerTurn(2, 1);
        model.playTurn(1,3);
        model.playTurn(1,2);

        model.setDices(3,1);
        model.playTurn(6,3); // WHITE CAPTURES BLACK PIECE
        model.playTurn(6,5);

        assertEquals(model.currentPlayerToMove(), BLACK);

        model.setDices(6,1);
        model.playTurn(25,1);

        assertNull(model.getPieceAt(25));
        assertTrue(model.getPieceAt(1).equals(new Pair(BLACK,1)));
        assertEquals(model.currentPlayerToMove(),BLACK);
        model.playTurn(1,7);
        assertEquals(model.currentPlayerToMove(),WHITE);
    }

    @Test
    void cannotPlayIfCaptured() throws IllegalMoveException {
        model.assignPlayerTurn(2, 1);
        model.playTurn(1, 3);
        model.playTurn(1,2);

        model.setDices(3,1);
        model.playTurn(6,3); // WHITE CAPTURES BLACK PIECE
        model.playTurn(3,2);

        assertEquals(model.currentPlayerToMove(), BLACK);
        model.setDices(6,6);
        assertEquals(model.getLegalMovesFromBar(6,6).size(),0);
    }

    @Test
    void canCaptureFromBar() throws IllegalMoveException{
        model.assignPlayerTurn(2, 1);
        model.playTurn(1,3);
        model.playTurn(1,2);

        model.setDices(3,1);
        model.playTurn(6,3); // WHITE CAPTURES BLACK PIECE
        model.playTurn(6,5);

        model.setDices(6,3);
        model.playTurn(25,3);
        assertTrue(model.getPieceAt(3).equals(new Pair(BLACK,1)));
        assertTrue(model.getPieceAt(28).equals(new Pair(WHITE,1)));
    }

    @Test
    void canCapturePiece() throws IllegalMoveException{
        model.assignPlayerTurn(2, 1);
        model.playTurn(1,3);
        model.playTurn(1,2);

        model.setDices(3,1);
        model.playTurn(6,3); // WHITE CAPTURES BLACK PIECE
        assertTrue(model.getPieceAt(25).equals(new Pair(BLACK,1)));
        assertTrue(model.getPieceAt(3).equals(new Pair(WHITE,1)));
    }

    @Test
    void isWinner() throws IllegalMoveException {
        assertTrue(model.getPieceAt(25).equals(new Pair(BLACK,15)));
        assertEquals(model.isWinner(),BLACK);
        assertTrue(model.getPieceAt(28).equals(new Pair(WHITE,15)));
        assertEquals(model.isWinner(),WHITE);
    }
*/

}
