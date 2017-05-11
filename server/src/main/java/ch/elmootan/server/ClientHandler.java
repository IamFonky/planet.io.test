package ch.elmootan.server;

import ch.elmootan.protocol.Credentials;
import ch.elmootan.protocol.Protocol;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler {

   final static Logger LOG = Logger.getLogger(ClientHandler.class.getName());
   final static ObjectMapper jsonMapper = new ObjectMapper();

   public ClientHandler() {
   }

   public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

      writer.println("Hello. Online HELP is available. Will you find it?");
      writer.flush();

      String command;
      boolean done = false;
      while (!done && ((command = reader.readLine()) != null)) {
         LOG.log(Level.INFO, "COMMAND: {0}", command);
         switch (command.toUpperCase()) {
            case Protocol.PLANET_IO_LOGIN:
               Credentials creds = jsonMapper.readValue(reader.readLine(),Credentials.class);

               //TODO : TEST CREDENTIALS HERE

               writer.println(Protocol.PLANET_IO_SUCCESS);

            default:
               writer.println("Huh? please use HELP if you don't know what commands are available.");
               writer.flush();
               break;
         }
         writer.flush();
      }

   }

}
