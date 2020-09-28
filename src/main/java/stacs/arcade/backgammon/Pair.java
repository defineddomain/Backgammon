package stacs.arcade.backgammon;

import java.util.Objects;

import static stacs.arcade.backgammon.BackgammonModel.PlayerColour;

public class Pair {

    private int numberOfPieces;
    private BackgammonModel.PlayerColour playerColour;


    public Pair(BackgammonModel.PlayerColour playerColour, int numberOfPieces) {
        this.playerColour = playerColour;
        this.numberOfPieces = numberOfPieces;
    }

    public int getNumberOfPieces() {
        return numberOfPieces;
    }

    public PlayerColour getPlayerColour() {
        return playerColour;
    }

    public void setPlayerColour(PlayerColour aPlayerColour) {
        this.playerColour = aPlayerColour;
    }

    public void setNumberOfPieces(int noOfPieces) {
        this.numberOfPieces = noOfPieces;
    }

    public boolean isPlayerColour(BackgammonModel.PlayerColour colour) {
        if (colour == playerColour)
            return true;
        else return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair pair = (Pair) o;
        return getNumberOfPieces() == pair.getNumberOfPieces() &&
                getPlayerColour() == pair.getPlayerColour();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumberOfPieces(), getPlayerColour());
    }

    @Override
    public String toString() {
        return "Pair{" +
                "numberOfPieces=" + numberOfPieces +
                ", playerColour=" + playerColour +
                '}';
    }
}
