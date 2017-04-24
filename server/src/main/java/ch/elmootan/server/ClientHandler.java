package ch.elmootan.server;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler {

   final static Logger LOG = Logger.getLogger(ClientHandler.class.getName());


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

            //TODO : All the cases related to the current protocol!

            default:
               writer.println("Huh? please use HELP if you don't know what commands are available.");
               writer.flush();
               break;
         }
         writer.flush();
      }

   }

}
