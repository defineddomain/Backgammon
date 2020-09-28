import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stacs.arcade.backgammon.BackgammonGames;
import stacs.arcade.backgammon.BackgammonResources;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class BackgammonAPITests {
    private BackgammonResources resources = null;

    private BackgammonGames mockGames;
    private HttpHeaders mockHeaders;
    private UriInfo mockUriInfo;

    private List<MediaType> mediaPlainText = new ArrayList<>();

    {
        mediaPlainText.add(MediaType.TEXT_PLAIN_TYPE);
    }

    private List<MediaType> mediaJson = new ArrayList<>();

    {
        mediaJson.add(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Set up API class with its dependencies.
     */
    @BeforeEach
    public void setup() {
        this.mockGames = mock(BackgammonGames.class);
        this.mockHeaders = mock(HttpHeaders.class);
        this.mockUriInfo = mock(UriInfo.class);
        resources = new BackgammonResources(mockGames, mockUriInfo, mockHeaders);
    }

    @Test
    public void getGamesShouldReturnJsonWhenRequired() throws JsonProcessingException {
        when(mockHeaders.getAcceptableMediaTypes()).thenReturn(mediaJson);
        resources.getGames();
        verify(mockHeaders).getAcceptableMediaTypes();
        verify(mockGames).getGamesAsJson();
    }

    @Test
    public void getGamesShouldReturnTextWhenRequired() throws JsonProcessingException {
        when(mockHeaders.getAcceptableMediaTypes()).thenReturn(mediaPlainText);
        resources.getGames();
        verify(mockHeaders).getAcceptableMediaTypes();
        verify(mockGames).getGamesAsString();
    }
}
