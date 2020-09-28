package stacs.arcade.backgammon;

import java.awt.*;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class BackgammonGames {

    private static final int PERCENT_MINES = 10;

    private int gameNo = 0;
    private HashMap<Integer, BackgammonModel> games = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public synchronized String getGamesAsString() {
        return this.games.keySet().toString();
    }

    public synchronized String getGamesAsJson() throws JsonProcessingException {
        return mapper.writeValueAsString(this.games.keySet());
    }


    public synchronized int newGame() {
        this.gameNo++;
        BackgammonModel model = new BackgammonModel();
        this.games.put(gameNo, model);
        return gameNo;
    }


    public synchronized void removeGame(int id) {
        this.games.remove(id);
    }

    public synchronized BackgammonModel getModel(int id) {
        return this.games.get(id);
    }
}
