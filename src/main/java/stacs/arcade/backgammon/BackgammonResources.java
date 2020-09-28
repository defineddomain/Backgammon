package stacs.arcade.backgammon;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.NotFoundException;

import org.glassfish.jersey.message.internal.AcceptableMediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Group 4.
 * references used- MinesweeperResources and TutorialPoints(https://www.tutorialspoint.com/restful/index.htm)
 */
@Path("/Backgammon")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public class BackgammonResources {

    private final stacs.arcade.backgammon.BackgammonGames games;
    private final ObjectMapper mapper = new ObjectMapper();

    @Context
    private UriInfo uri;
    @Context
    private HttpHeaders headers;

    /**
     * @param games   the BackgammonGame object.
     * @param uri     the UriInfo object
     * @param headers the HttpHeaders object
     */
    @Inject
    public BackgammonResources(stacs.arcade.backgammon.BackgammonGames games, UriInfo uri, HttpHeaders headers) {
        this.games = games;
        this.uri = uri;
        this.headers = headers;
    }

    /**
     * @return response in the client acceptable format
     * @throws JsonProcessingException the exception caused during processing JSON data
     */
    @GET
    public String getGames() throws JsonProcessingException {
        if (this.clientAcceptsJson()) {
            return this.games.getGamesAsJson();
        } else {
            return "\n" + this.games.getGamesAsString() + "\n";
        }
    }

    /**
     * @return response in the client acceptable format
     */
    @GET
    @Path("/newGame")
    public String newGame() {
        int gameNo = this.games.newGame();
        BackgammonModel model = this.games.getModel(gameNo);
        if (this.clientAcceptsJson()) {
            ObjectNode json = this.mapper.createObjectNode();
            json.put("gameNo", gameNo);
            json.put("url", uri.getBaseUri() + "backgammon/" + gameNo);
            json.set("game", model.getJson());
            return json.toString();
        } else {
            return String.format("New game available at:\n%backgammon/%d\n", uri.getBaseUri(), gameNo);
        }
    }

    /**
     * @param id represents the gameNo
     * @return response in the client acceptable format
     */
    @GET
    @Path("/{id}")
    public String getGame(@PathParam("id") int id) {
        BackgammonModel model = this.games.getModel(id);
        if (model == null) {
            throw new NotFoundException();
        }
        if (this.clientAcceptsJson()) {
            return model.getJson().toString();
        } else {
            return model.getText();
        }
    }

    /**
     * @param id represents the gameNo
     * @param x  represents the fromPosition
     * @param y  represents the toPosition
     * @return response in the client acceptable format
     * @throws IllegalMoveException throws exception if it is an illegal move
     */
    @POST
    @Path("/{id}/playTurn/{x}/{y}")
    public String playTurn(@PathParam("id") int id, @PathParam("x") int x, @PathParam("y") int y) throws IllegalMoveException {
        BackgammonModel model = this.games.getModel(id);
        if (model != null) {
            model.playTurn(x, y);
            if (this.clientAcceptsJson()) {
                return model.getJson().toString();
            } else {
                return model.getText();
            }
        } else {
            throw new NotFoundException();
        }
    }

    /**
     * @param id represents the gameNo
     * @return response in the client acceptable format
     * @throws IllegalMoveException throws exception if it is an illegal move
     */
    @GET
    @Path("/{id}/rollDice")
    public String rollDice(@PathParam("id") int id) throws IllegalMoveException {
        BackgammonModel model = this.games.getModel(id);
        if (model != null) {
            model.setGameStatus();
            if (this.clientAcceptsJson()) {
                return model.getJson().toString();
            } else {
                return model.getText();
            }
        } else {
            throw new NotFoundException();
        }

    }

    /**
     * @return returns true if the client accepts json as response
     */
    private boolean clientAcceptsJson() {
        List<MediaType> mimeTypes = this.headers.getAcceptableMediaTypes();
        return mimeTypes.contains(AcceptableMediaType.valueOf(MediaType.APPLICATION_JSON));
    }
}
