package ch.elmootan.tests;

import ch.elmootan.protocol.Protocol;
import ch.elmootan.server.Server;
import ch.elmootan.client.Client;
import org.junit.Test;

import java.io.IOException;

public class ServerTest
{

   @Test
   public void aClientShouldConnectToAServer() throws IOException
   {
      Server testServer = new Server();
      testServer.startServer();

      Client client = new Client();

      client.connect("localhost", Protocol.PORT);

      client.serverWrite(Protocol.CMD_CREATE_GAME + ":MYNEWGAME");

      client.serverRead();
   }
}
