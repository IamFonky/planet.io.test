package ch.elmootan.server;

import ch.elmootan.protocol.Protocol;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler {

   final static ObjectMapper jsonMapper = new ObjectMapper();
    private final static Logger LOG = Logger.getLogger(ClientHandler.class.getName());

    private final GamesManager gamesManager = GamesManager.getSharedManager();

    private final ObjectMapper mapper = new ObjectMapper();

    public ClientHandler() {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
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
            int index = command.toUpperCase().indexOf(':');
            index = index != -1 ? index : command.length();
            switch (command.toUpperCase().substring(0,  index)) {

                // Client wants to create a game.
                case Protocol.CMD_CREATE_GAME: {
                    if (index != command.length()) {
                        String data = command.substring(index + 1);
                        gamesManager.addGame(data);
                        writer.println(Protocol.PLANET_IO_SUCCESS);
                    } else {
                        writer.println(Protocol.PLANET_IO_FAILURE);
                    }

                    break;
                }

                // Client wants to disconnect.
                case Protocol.CMD_DISCONNECT: {
                    done = true;
                    writer.println(Protocol.PLANET_IO_SUCCESS);

                    break;
                }

                // Client wants to connect.
                case Protocol.PLANET_IO_HELLO: {
                    String serializedData = mapper.writeValueAsString(gamesManager.getGamesList());

                    writer.println(Protocol.PLANET_IO_SUCCESS + ":" + serializedData);

                    break;
                }
            }
            writer.flush();
        }

    }

}
