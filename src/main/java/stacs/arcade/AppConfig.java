package stacs.arcade;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import stacs.arcade.backgammon.BackgammonGames;

@ApplicationPath("/api")
public class AppConfig extends ResourceConfig {
  public AppConfig() {
    register(BackgammonGames.class);
    register(new AbstractBinder() {
      @Override
      protected void configure() {
        bindAsContract(BackgammonGames.class).in(Singleton.class);
      }
    });
  }
}
