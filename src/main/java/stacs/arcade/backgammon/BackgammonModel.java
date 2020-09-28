package stacs.arcade.backgammon;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Random;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static stacs.arcade.backgammon.BackgammonModel.PlayerColour.BLACK;
import static stacs.arcade.backgammon.BackgammonModel.PlayerColour.WHITE;

/**
 * Created by 190026397,
 */

public class BackgammonModel {

    public enum PlayerColour {BLACK, WHITE}

    private PlayerColour currentPlayerToMove, firstPlayerToGo;

    private static final ObjectMapper mapper = new ObjectMapper();
    HashMap<Integer, LinkedList<Integer>> legalMoves;
    HashMap<Integer, LinkedList<Integer>> legalMovesFromBar;

    //24 points on the board. 25th is the bar, 26th is the bearing-off location of the BLACK player and 27th for WHITE.
    private static final int BOARD_POSITION_SIZE = 28;
    private static final int TOTAL_NUMBER_CHECKERS_PER_PLAYER = 15;
    private static final int IS_AVAILABLE = 0;
    private static final int IS_OCCUPIED = 1;
    private static final int CAN_BE_CAPTURED = 2;
    private static final int BLACK_DIRECTION = 1;
    private static final int WHITE_DIRECTION = -1;
    private static final int DICE_MAX_VALUE = 6;
    private static final int DICE_MIN_VALUE = 1;
    private static final int BLACK_CHECKER_BAR_ID = 25;
    private static final int WHITE_CHECKER_BAR_ID = 28;
    private boolean isCapturingMove = false;
    private boolean hasPlayedFirstDice = false;
    private boolean isDoublePlay = false;
    private int timesToPlay = 2;
    private boolean currentPlayerOnBar = false;


    private HashMap<Integer, Pair> location = new HashMap<>(); // HashMap used to represent the board
    private int dice1 = 0, dice2 = 0;
    private int dice1Value, dice2Value;

    public BackgammonModel() {
        this.initialiseGame();

    }

    public void initialiseGame() {
        setPieces();
    }

    private void setPieces() {

        location.put(1, new Pair(BLACK, 2));
        location.put(6, new Pair(PlayerColour.WHITE, 5));
        location.put(8, new Pair(PlayerColour.WHITE, 3));
        location.put(12, new Pair(BLACK, 5));
        location.put(13, new Pair(PlayerColour.WHITE, 5));
        location.put(17, new Pair(BLACK, 3));
        location.put(19, new Pair(BLACK, 5));
        location.put(24, new Pair(PlayerColour.WHITE, 2));



    }

    public int throwDice() {
        Random random = new Random();
        return random.nextInt(6) + 1; //+1 to avoid zero
    }


    public void setGameStatus() throws IllegalMoveException {
        if (firstPlayerToGo == null && dice1 == 0) {
            this.dice1 = throwDice();
            this.dice1Value = this.dice1;
        } else if (firstPlayerToGo == null && dice2 == 0) {
            this.dice2 = throwDice();
            this.dice2Value = this.dice2;
            assignPlayerTurn(dice1, dice2);
        } else {
            if(!diceWithinRange(dice1) && !diceWithinRange(dice2)){
                this.dice1Value = this.dice1 = throwDice();
                this.dice2Value = this.dice2 = throwDice();
                hasPlayedFirstDice = false;
                if(dice1 == dice2) {
                    isDoublePlay = true;
                    timesToPlay = 4;
                }
                int playerBarId = 25;
                PlayerColour switchTo = BLACK;
                if(currentPlayerToMove == WHITE) {
                    playerBarId = 28;
                    switchTo = WHITE;
                }
                if(getLegalMovesFromBar(dice1,dice2).size() == 0 && hasPiecesOnTheBar(playerBarId) || (!hasPiecesOnTheBar(playerBarId) && getLegalMovesOnBoard(dice1,dice2).size() == 0)){

                    dice1 = 0;
                    dice2 = 0;
                    currentPlayerToMove = switchTo;
                }
            }
        }
    }

    public boolean assignPlayerTurn(int dice1, int dice2) {

        /*If statements explanation - The BLACK player throws first
         *Case 1 - BLACK player throws the dice with the higher value and is assigned as the first player
         *Case 2 - WHITE player throws the dice with the higher value and is assigned as the first player
         *Case 3 - Dices are equal - no player is assigned*/

        if (dice1 == dice2) {
            this.dice1 = 0;
            this.dice2 = 0;
            firstPlayerToGo = null;
            return false;
        } else if (dice1 > dice2) {
            currentPlayerToMove = BLACK;
            this.dice1Value = this.dice1 = dice1;
            this.dice2Value = this.dice2 = dice2;
            firstPlayerToGo = currentPlayerToMove;
            return true;
        } else {
            currentPlayerToMove = WHITE;
            this.dice1Value = this.dice1 = dice1;
            this.dice2Value = this.dice2 = dice2;
            firstPlayerToGo = currentPlayerToMove;
            return true;
        }
    }

    public LinkedList<Integer> getAllPointsWithPlayerPieces(PlayerColour playerColour) {
        LinkedList<Integer> pointsWithPlayerCheckersOn = new LinkedList<>();
        //Iterate through the hasMap and store the keys that the Pair(playerColour) is equal to the playerColour.
        for (Map.Entry mapElement : location.entrySet()) {
            Pair pair = (Pair) mapElement.getValue();
            if (pair.getPlayerColour() == playerColour)
                pointsWithPlayerCheckersOn.add((Integer) mapElement.getKey());
        }
        return pointsWithPlayerCheckersOn;
    }

    public HashMap<Integer, LinkedList<Integer>> getLegalMovesOnBoard(int dice1, int dice2) throws IllegalMoveException {

        if ((!diceWithinRange(dice1) || !diceWithinRange(dice2)) && !hasPlayedFirstDice )
            throw new IllegalMoveException("Dice with invalid values - No move is available");

        legalMoves = new HashMap<>();
        LinkedList<Integer> pointsWithPlayerCheckers = getAllPointsWithPlayerPieces(currentPlayerToMove());
        // The key of the map represents the fromPoint and the list the possible toPoints
        int playerDirection = getPlayerDirection(currentPlayerToMove());

        /* For each point that the player has pieces on get the legalMoves by calling the checkMovesForDice with both dices
         * The method returns a point that represents the the legal ToPoint from the current From point.
         */

        for (Integer point : pointsWithPlayerCheckers) {
            LinkedList<Integer> toPoints = new LinkedList<>();
            int possiblePoint;
            if(diceWithinRange(dice1)){
                possiblePoint = checkMovesForDice(point, dice1, playerDirection, toPoints);
                if (possiblePoint != -1 && !toPoints.contains(possiblePoint))
                    toPoints.add(possiblePoint);
            }
            if(diceWithinRange(dice2)){
                possiblePoint = checkMovesForDice(point, dice2, playerDirection, toPoints);
                if (possiblePoint != -1 && !toPoints.contains(possiblePoint))
                    toPoints.add(possiblePoint);
            }
            if(toPoints.size() >0){
                legalMoves.put(point, toPoints);
            }
        }

        /* If there is only 1 point that has possible moves with both dices then check for the eitherDiceCondition.
         * If the eitherCondition is true then set the legal move to be the one that can be performed with the dice
         * with the higher value.
         */
        if ((legalMoves.size() == 1)) {

            Iterator<Integer> iterator = legalMoves.keySet().iterator();
            int key = iterator.next();
            LinkedList<Integer> toPoints = legalMoves.get(key);

            if (toPoints.size() == 2) {
                boolean isEitherCondition = checkEitherDiceCondition(toPoints, playerDirection, dice1, dice2);
                if (isEitherCondition) {
                    int greaterDice = Math.max(dice1, dice2);
                    legalMoves.put(key, new LinkedList<>(Arrays.asList(key + (greaterDice * playerDirection))));
                }
            }
        }
        return legalMoves;
    }

    public HashMap<Integer,LinkedList<Integer>> getLegalMovesFromBar(int dice1, int dice2){

        List<Integer> opponentBoardPoints = new ArrayList<>(Arrays.asList(1,2,3,4,5,6));
        legalMovesFromBar = new HashMap<Integer, LinkedList<Integer>>();
        int playerBarId = 25;
        if(currentPlayerToMove == WHITE){
            opponentBoardPoints = new ArrayList<>(Arrays.asList(19,20,21,22,23,24));
            playerBarId = 28;
        }
        LinkedList<Integer> toPoints = new LinkedList<>();
        for (Integer point: opponentBoardPoints) {
            if((isOccupied(point,currentPlayerToMove) != IS_OCCUPIED)){
                if(currentPlayerToMove == BLACK && (dice1 == point || dice2 == point)){
                    toPoints.add(point);
                }else if(currentPlayerToMove == WHITE && (dice1 == 25- point || dice2 == 25 - point)) {
                    toPoints.add(point);

                }
            }
            if(toPoints.size() >0 )
                legalMovesFromBar.put(playerBarId,toPoints);
        }
        return legalMovesFromBar;
    }

    public boolean isLegalMoveFromBar(int fromPoint, int toPoint) throws IllegalMoveException {
        HashMap<Integer,LinkedList<Integer>> legalMoves = getLegalMovesFromBar(dice1,dice2);
        if(legalMoves.containsKey(fromPoint)){
            return legalMoves.get(fromPoint).contains(toPoint);
        }
        return false;
    }
    /** Returns list of BearOfMoves
     */
    public void getBearOfMoves(int fromPoint) {
        LinkedList<Integer> movesFromBearOff = new LinkedList<>();
        List<Integer> myPlaces = new ArrayList<Integer>();

        if (currentPlayerToMove.equals(BLACK)) {
            movesFromBearOff.add(25);
        } else {
            movesFromBearOff.add(28);
        }

        // Loop checks if dice value is excact number that corresponds to point check is on, if not place has to move highest numbered point that contains pieces.

        if (isBearOff(currentPlayerToMove) && currentPlayerToMove.equals(WHITE)) {
            for (int i = 1; i <= 6; i++) {
                if (location.get(i).getNumberOfPieces() > 0) {
                    myPlaces.add(i);
                    if (i == this.dice1 || i == this.dice2) {
                        legalMoves.put(i, movesFromBearOff);
                    }

                    if (i != this.dice1 || i != this.dice2) {
                        legalMoves.put(myPlaces.get(-1), movesFromBearOff);
                    }
                }
            }

        } else if (isBearOff(currentPlayerToMove) && currentPlayerToMove.equals(BLACK)) {
            for (int i = 19; i <= 24; i++) {
                if (location.get(i).getNumberOfPieces() > 0) {
                    myPlaces.add(i);
                    if ((24 - i) == this.dice1 || (24 - i) == this.dice2) {
                        legalMoves.put(i, movesFromBearOff);
                    }
                    if (i != this.dice1 || i != this.dice2) {
                        legalMoves.put(myPlaces.get(0), movesFromBearOff);
                    }
                }
            }
        }
    }

    private boolean diceWithinRange(int dice) {
        return dice >= DICE_MIN_VALUE && dice <= DICE_MAX_VALUE;
    }

    private int checkMovesForDice(int point, int dice, int playerDirection, LinkedList<Integer> toPoints) {
        int possibleToPoint = point + (dice * playerDirection);
        int toPointStatus = isOccupied(possibleToPoint, currentPlayerToMove());
        if( possibleToPoint >0  &&  possibleToPoint <25){
            if (toPointStatus == IS_AVAILABLE || toPointStatus == CAN_BE_CAPTURED) {
                return possibleToPoint;
            }
        }
        return -1;
    }

    private boolean checkEitherDiceCondition(LinkedList<Integer> toPoints, int playerDirection, int dice1, int dice2) {

        /* The first element in the list will be the ToPoint1 resulting from moving a piece from the location (key) with dice1
         * The second element will be the the ToPoint2 resulting from moving a piece form the location (key) with dice2.
         * Check that from ToPoint1 we can play the second dice and
         * that from ToPoint2 we can play the first dice.
         * If neither of these moves are available then the eitherCondition is true.*/

        int toPoint1 = toPoints.getFirst();
        int possibleToPointAfterPlayingDice1 = toPoint1 + (dice2 * playerDirection);
        int possiblePoint1Status = isOccupied(possibleToPointAfterPlayingDice1, currentPlayerToMove());

        int toPoint2 = toPoints.getLast();
        int possibleToPointAfterPlayingDice2 = toPoint2 + (dice1 * playerDirection);
        int possiblePoint2Status = isOccupied(possibleToPointAfterPlayingDice2, currentPlayerToMove());

        return possiblePoint1Status == IS_OCCUPIED && possiblePoint2Status == IS_OCCUPIED;
    }

    /**
     * Adds a piece in the move position, and removes the piece from the current location
     *
     * @param playerColour
     * @param fromPoint
     * @param toPoint
     */
    public void makeMove(PlayerColour playerColour, int fromPoint, int toPoint) throws IllegalMoveException {
        int newToNumberOfPieces;

        if (!isValidPlayer(playerColour)) {
            throw new IllegalMoveException("This is an illegal move - This is not your turn, you cannot perform that move now.");
        }
        if (!isLegalMove(fromPoint, toPoint) && !isLegalMoveFromBar(fromPoint,toPoint) ) {
            throw new IllegalMoveException("This is an illegal move");
        }

        // Used to first check if the toPoint exists in the HashMap and if the point belongs to the opponent -> if Yes get the current number otherwise set it to 1.
        if (!location.containsKey(toPoint) || (location.containsKey(toPoint) && location.get(toPoint).getPlayerColour() != currentPlayerToMove)) {
            // creates a new map location and Pair
            newToNumberOfPieces = 1;
        } else {
            //hashmap pair location
            newToNumberOfPieces = location.get(toPoint).getNumberOfPieces() + 1;
        }

        int newFromNumberOfPieces = location.get(fromPoint).getNumberOfPieces() - 1;
        if(newFromNumberOfPieces == 0){
            location.remove(fromPoint);
        }else {
            Pair newFromPair = new Pair(playerColour, newFromNumberOfPieces);
            location.put(fromPoint, newFromPair);
        }
        if(isOccupied(toPoint, currentPlayerToMove) == CAN_BE_CAPTURED){
            isCapturingMove = true;
        }
        Pair newToPair = new Pair(playerColour, newToNumberOfPieces);
        location.put(toPoint, newToPair);
    }

    public void playTurn(int fromPoint, int toPoint) throws IllegalMoveException {
        /* Get the dice that the player wants to move based on the from and to points.
         * Check that both dices have valid values.
         * Then check if the player has pieces on the bar. If the player has pieces on the bar call moveFromBar.
         * If not then call the makeMove method to move the piece.
         * If a legal move is performed check if that move resulted to a capture of the opponenet's piece*/
        currentPlayerOnBar = false;
        try {
            int playerBarId = BLACK_CHECKER_BAR_ID; //key in the locationMapForBlackBarCheckers
            if(currentPlayerToMove == WHITE)
                playerBarId = WHITE_CHECKER_BAR_ID;
            if(hasPiecesOnTheBar(playerBarId)) {
                currentPlayerOnBar = true;
                makeMove(currentPlayerToMove,fromPoint,toPoint);
                if(!hasPiecesOnTheBar(playerBarId))
                    currentPlayerOnBar = false;

            }
            else if (isBearOff(currentPlayerToMove)) {
                getBearOfMoves(fromPoint);
                //TODO call getBearOfMoves and change make move method to look in the bearoff moves as well
                makeMove(currentPlayerToMove(),fromPoint,toPoint);
            }else {
                getLegalMovesOnBoard(dice1,dice2);
                getLegalMovesFromBar(dice1,dice2);
                makeMove(currentPlayerToMove(),fromPoint,toPoint);
            }
        } catch (IllegalMoveException e) {
            //if the move that the player wants to make is not legal then just return without updating the board.
            return;
        }
        resetPlayedDice(fromPoint,toPoint);
        if(isCapturingMove){
            capturePiece();
            isCapturingMove = false;
        }

        timesToPlay --;

        if(((!diceWithinRange(dice1) && !diceWithinRange(dice2)) || (getLegalMovesOnBoard(dice1,dice2).size() == 0 )|| (currentPlayerOnBar && getLegalMovesFromBar(dice1,dice2).size() == 0)) && currentPlayerToMove == BLACK){
            timesToPlay = 2;
            currentPlayerToMove = WHITE;
            dice1 = 0;
            dice2 = 0;
            hasPlayedFirstDice = false;
        }else if((!diceWithinRange(dice1) && !diceWithinRange(dice2) && currentPlayerToMove == WHITE)|| getLegalMovesOnBoard(dice1,dice2).size() == 0 || (currentPlayerOnBar && getLegalMovesFromBar(dice1,dice2).size() == 0)) {
            currentPlayerToMove = BLACK;
            timesToPlay = 2;
            dice1 = 0;
            dice2 = 0;
            hasPlayedFirstDice = false;
        }

    }

    public void resetPlayedDice(int fromPoint,int toPoint){
        //After making a move deactivate the played dice
        int playedDice = Math.abs(fromPoint - toPoint);
        if(fromPoint == BLACK_CHECKER_BAR_ID && timesToPlay <= 2 && toPoint == dice1){
            dice1 = 0;
            hasPlayedFirstDice = true;
        }else if(fromPoint == BLACK_CHECKER_BAR_ID && timesToPlay <=2 && toPoint == dice2) {
            dice2 = 0;
            hasPlayedFirstDice = true;
        }
        else if(fromPoint == WHITE_CHECKER_BAR_ID && timesToPlay <=2 && dice1 == 25- toPoint) {
            dice1 = 0;
            hasPlayedFirstDice = true;
        } else if(fromPoint == WHITE_CHECKER_BAR_ID && timesToPlay <=2 && dice2 == 25 - toPoint) {
            dice2 = 0;
            hasPlayedFirstDice = true;
        }else if(playedDice == dice1 && timesToPlay <= 2){
            dice1 = 0;
            hasPlayedFirstDice = true;
        }else if(playedDice  == dice2 && timesToPlay <= 2){
            hasPlayedFirstDice = true;
            dice2 = 0;
        }else if(timesToPlay == 1){
            dice1 = 0;
            dice2 = 0;
        }
    }
    public boolean isValidPlayer(PlayerColour playerColour) {
        return playerColour == currentPlayerToMove();
    }

    public int isOccupied(int pointPosition, PlayerColour playerColour) {
        //Check if the piece square has a piece on it and if that piece belongs to the opponent
        Pair checkingPair = location.get(pointPosition);
        if (checkingPair == null || checkingPair.getPlayerColour() == playerColour)
            return IS_AVAILABLE;
        else if (checkingPair.getPlayerColour() != playerColour && checkingPair.getNumberOfPieces() > 1)
            return IS_OCCUPIED;
        else
            return CAN_BE_CAPTURED;
    }

    private void capturePiece() {
        int piecesCaptured =1;
        if(currentPlayerToMove == BLACK){
            if(location.containsKey(WHITE_CHECKER_BAR_ID)){
                piecesCaptured = location.get(WHITE_CHECKER_BAR_ID).getNumberOfPieces() + 1;
            }
            location.put(WHITE_CHECKER_BAR_ID,new Pair(WHITE,piecesCaptured));
        }else {
            if(location.containsKey(BLACK_CHECKER_BAR_ID)){
                piecesCaptured = location.get(BLACK_CHECKER_BAR_ID).getNumberOfPieces() + 1;
            }
            location.put(BLACK_CHECKER_BAR_ID,new Pair(BLACK,piecesCaptured));
        }
    }

    /**
     * Check all pieces either in home or at respective bearing off position
     * 24 points on the board. 25th is the bar, 26th is the bearing-off location of the BLACK player and 27th for WHITE.
     */
    public boolean isBearOff(PlayerColour playerColour) {
        return ((countPiecesInHomePosition(playerColour) + countPiecesInBearOff(playerColour)) == 15);
    }


    public int countPiecesInBearOff(PlayerColour playerColour) {

        if (playerColour.equals(BLACK) && location.containsKey(26)) {
            return location.get(26).getNumberOfPieces();
        } else if(playerColour.equals(WHITE) && location.containsKey(27)) {
            return location.get(27).getNumberOfPieces();
        } else {
            return 0;
        }
    }


    public int countPiecesInHomePosition(PlayerColour playerColour) {
        int numberOfWhitepieces = 0;
        int numberOfBlackpeices = 0;

        for (Integer boardLocation : location.keySet()) {

            if ((boardLocation >= 1 && boardLocation <= 6) && location.get(boardLocation).getPlayerColour().equals(WHITE)) {
                numberOfWhitepieces += location.get(boardLocation).getNumberOfPieces();
            }

            if ((boardLocation >= 19 && boardLocation <= 24) && location.get(boardLocation).getPlayerColour().equals(BLACK)) {
                numberOfBlackpeices += location.get(boardLocation).getNumberOfPieces();
            }
        }
        if (playerColour.equals(BLACK)) {
            return numberOfBlackpeices;
        } else {
            return numberOfWhitepieces;
        }
    }

    public boolean allPiecesInHome(PlayerColour playerColour) {
        boolean result = false;
        if (countPiecesInHomePosition(playerColour) == 15) {
            result = true;
        }
        return result;
    }

    public boolean isLegalMove(int fromPoint, int toPoint) throws IllegalMoveException {
        /* The key of the hashMap represents the fromPoint so me first check if the map contains that key.
         * If not then this is an illegal move.
         * If the map contains the key, we need to check that the list for that point contains the toPoint */
        getLegalMovesOnBoard(dice1,dice2);

        if(currentPlayerOnBar)
            isLegalMoveFromBar(fromPoint,toPoint);
        if (!legalMoves.containsKey(fromPoint))
            return false;
        else {
            LinkedList<Integer> toPoints = legalMoves.get(fromPoint);
            return toPoints.contains(toPoint);
        }
    }

    private int getPlayerDirection(PlayerColour playerColour) {
        //the black direction is positive while the white is negative.
        if (playerColour == BLACK)
            return BLACK_DIRECTION;
        else if (playerColour == WHITE)
            return WHITE_DIRECTION;
        else return 0;
    }

    public boolean hasPiecesOnTheBar(int playerBarValue){
        return location.containsKey(playerBarValue);
    }
    /**
     * Used for the getText method
     *
     * @param playerColour
     * @return
     */
    public String returnColourAsLetter(PlayerColour playerColour) {
        if (playerColour.equals(BLACK)) {
            return "B";
        } else if (playerColour.equals(WHITE)) {
            return "W";
        } else {
            return "NULL";
        }
    }

    public int highestDice(int dice1, int dice2) {
        if (dice1 > dice2) {
            return dice1;
        } else {
            return dice2;
        }
    }

    public PlayerColour getFirstPlayerToGo() {
        return firstPlayerToGo;
    }

    public Pair getPieceAt(int position) {
        return location.get(position);
    }

    public PlayerColour currentPlayerToMove() {
        return currentPlayerToMove;
    }

    public PlayerColour isWinner() {
        if (location.get(25).getNumberOfPieces() ==  15) {
            return BLACK;
        } else if (location.get(28).getNumberOfPieces() == 15) {
            return WHITE;
        } else {
            return null;
        }
    }

    public HashMap<Integer, Pair> getLocation() {
        return location;
    }

    public ObjectNode getJson() {

        ArrayNode board = mapper.createArrayNode();
        for (int y = 1; y <= BackgammonModel.BOARD_POSITION_SIZE; y++) {
            ArrayNode row = mapper.createArrayNode();
            board.add(row);


            if (location.containsKey(y)) {
                Pair aPair = location.get(y);
                for (int x = 0; x < aPair.getNumberOfPieces(); x++) {

                    row.add(returnColourAsLetter(aPair.getPlayerColour()));
                }
                for (int i = aPair.getNumberOfPieces(); i < 15; i++) {
                    row.add("NULL");
                }
            } else {
                for (int i = 0; i < 15; i++) {
                    row.add("NULL");
                }

            }
        }
        ObjectNode json = mapper.createObjectNode();
        json.put("height", BackgammonModel.BOARD_POSITION_SIZE);
        json.put("dice1Value", this.dice1Value);
        json.put("dice2Value", this.dice2Value);
        json.put("currentPlayer", String.valueOf(this.currentPlayerToMove));
        json.set("board", board);
        System.out.println(json);
        return json;
    }

    public String getText() {

        StringBuilder sb = new StringBuilder();

        for (int y = 1; y <= BackgammonModel.BOARD_POSITION_SIZE; y++) {

            sb.append(y + ". ");

            if (location.containsKey(y)) {
                Pair aPair = location.get(y);
                for (int x = 0; x < aPair.getNumberOfPieces(); x++) {
                    if (x < 14) {
                        sb.append(returnColourAsLetter(aPair.getPlayerColour()) + ", ");
                    } else if (x == 14)
                    {
                        sb.append(returnColourAsLetter(aPair.getPlayerColour()));
                    }
                }
                for (int i = aPair.getNumberOfPieces(); i < 15; i++) {
                    if (i < 14) {
                        sb.append("NULL" + ", ");
                    } else if (i == 14)
                    {
                        sb.append("NULL");
                    }
                }
            } else {
                for (int i = 0; i < 14; i++) {
                    sb.append("NULL, ");
                }
                sb.append("NULL");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

   /* //This method will only be activated for testing purposes.
    public void setDices(int dice1, int dice2){
        if(dice1 == dice2){
            timesToPlay = 4;
        }
        hasPlayedFirstDice = false;
        this.dice1 = dice1;
        this.dice2 = dice2;
    }*/
}
