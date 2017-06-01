package ch.elmootan.tests;

import ch.elmootan.core.sharedObjects.Game;
import ch.elmootan.core.sharedObjects.Player;
import ch.elmootan.core.universe.Bonus;
import ch.elmootan.protocol.Protocol;
import ch.elmootan.server.Server;
import ch.elmootan.client.Client;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;

public class ServerTest
{

   @Test
   public void aClientShouldConnectToAServer() throws IOException
   {
      Server testServer = new Server();
      testServer.startServer();

      Client client = new Client(new Player("Bro"),false);

//      client.connect("localhost", Protocol.PORT);

      client.sendGameToServer(new Game("TEST GAME",new HashSet<>(),32));

      client.disconnect();

      client.exit();
   }
}
