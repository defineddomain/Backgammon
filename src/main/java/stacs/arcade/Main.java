package stacs.arcade;

import java.io.IOException;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Main class for StArcade, sets up the Grizzly server and attaches a static
 * content handler to hand out HTML, JavaScript and other resources.
 * 
 * @author alex.voss@st-andrews.ac.uk
 */
public class Main {

  /**
   * Main method.
   */
  public static void main(String[] args) throws IOException {

    URI baseUri = UriBuilder.fromUri("http://localhost/api/").port(8080).build();
    final ResourceConfig config = new AppConfig().packages("stacs.arcade");

    HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
    server.getServerConfiguration()
        .addHttpHandler(new CLStaticHttpHandler(HttpServer.class.getClassLoader(), "/ui/"));

    System.out.println(String.format("Jersey app started with WADL available at "
        + "%sapplication.wadl\nHit enter to stop it...", baseUri));
    System.in.read();
    server.shutdownNow();
  }
}
