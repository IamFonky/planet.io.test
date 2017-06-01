package ch.elmootan.server;

import ch.elmootan.core.sharedObjects.Game;
import ch.elmootan.core.sharedObjects.Lobby;
import ch.elmootan.protocol.Protocol;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler {

    final static ObjectMapper jsonMapper = new ObjectMapper();
    private final static Logger LOG = Logger.getLogger(ClientHandler.class.getName());

    private final GamesManager gamesManager = GamesManager.getSharedManager();
    private Lobby lobby = Lobby.getSharedInstance();

    private final ObjectMapper mapper = new ObjectMapper();

    public ClientHandler() {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

        writer.println("Hello. Online HELP is available. Will you find it?");
        writer.flush();

        try {
            String command;
            boolean done = false;
            while (!done && ((command = reader.readLine()) != null)) {
                LOG.log(Level.INFO, "COMMAND: {0}", command);
                //int index = command.toUpperCase().indexOf(':');
               // index = index != -1 ? index : command.length();
                switch (command.toUpperCase()) {

                    // Client wants to create a game.
                    case Protocol.CMD_CREATE_GAME: {

                        if (lobby.getGamesList().size() + 1 > lobby.getNbGamesMax()) {
                            reader.readLine();
                            //writer.println(Protocol.PLANET_IO_FAILURE);
                        } else {
                            Game newGame = mapper.readValue(reader.readLine(), Game.class);
                            lobby.addGame(newGame);
                            //writer.println(Protocol.PLANET_IO_SUCCESS);
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
                        ArrayList<Game> gameList = Lobby.getSharedInstance().getGamesList();
                        String serializedData = mapper.writeValueAsString(gameList);

                        writer.println(Protocol.PLANET_IO_SUCCESS);

                        writer.println(serializedData);

                        break;
                    }
                }
                writer.flush();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
