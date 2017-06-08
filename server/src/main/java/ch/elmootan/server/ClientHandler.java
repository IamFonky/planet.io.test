package ch.elmootan.server;

import ch.elmootan.core.physics.Body;
import ch.elmootan.core.sharedObjects.CustomObjectMapper;
import ch.elmootan.core.sharedObjects.Game;
import ch.elmootan.core.sharedObjects.Lobby;
import ch.elmootan.core.universe.InvisiblePlanet;
import ch.elmootan.core.universe.Planet;
import ch.elmootan.core.universe.Universe;
import ch.elmootan.protocol.Protocol;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler {

    private final static Logger LOG = Logger.getLogger(ClientHandler.class.getName());

    private Lobby lobby = Lobby.getSharedInstance();

    private final CustomObjectMapper mapper = new CustomObjectMapper();

    public ClientHandler() {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

        writer.println("Hello. Online HELP is available. Will you find it?");
        writer.flush();

        try {
            String commandLine;
            boolean done = false;
            while (!done && ((commandLine = reader.readLine()) != null)) {
                String[] cmdAndArgs = commandLine.split(Protocol.CMD_SEPARATOR);
                if (cmdAndArgs.length > 0) {
                    String command = cmdAndArgs[0];
                    LOG.log(Level.INFO, "COMMAND: {0}", command);
                    for (int i = 1; i < cmdAndArgs.length; ++i) {
                        LOG.log(Level.INFO, "ARGUMENT " + i + ":" + cmdAndArgs[i]);
                    }

                    switch (command) {

                        // Client wants to create a game.
                        case Protocol.CMD_CREATE_GAME: {
                            if (lobby.getGamesList().size() + 1 > lobby.getNbGamesMax()) {
                                writer.println(Protocol.PLANET_IO_FAILURE);
                                writer.flush();
                            } else {
                                writer.println(Protocol.PLANET_IO_SUCCESS);
                                writer.flush();
                                try {
                                    Game newGame = mapper.readValue(reader.readLine(), Game.class);
                                    int newGameID = lobby.addGame(newGame);
                                    writer.println(Protocol.PLANET_IO_SUCCESS
                                    + Protocol.CMD_SEPARATOR
                                    + newGameID);
                                    writer.flush();
                                } catch (JsonProcessingException jpe) {
                                    writer.println(Protocol.PLANET_IO_FAILURE);
                                    writer.flush();
                                }
                            }
                            break;
                        }
                        // Client wants to joi a game.
                        case Protocol.CMD_JOIN_GAME: {
                            if(cmdAndArgs.length > 1)
                            {
                                int idGame = Integer.parseInt(cmdAndArgs[1]);
                                if(idGame >= 0 && idGame < lobby.getGamesList().size())
                                {
                                    writer.println(Protocol.PLANET_IO_SUCCESS);
                                    writer.flush();
                                    Planet userPlanet = mapper.readValue(reader.readLine(), Planet.class);
                                    userPlanet = lobby.getEngineList().get(idGame).generateUserPlanet(userPlanet);
                                    writer.println(mapper.writeValueAsString(userPlanet));
                                    writer.flush();
                                }
                                else
                                {
                                    writer.println(Protocol.PLANET_IO_FAILURE);
                                    writer.flush();
                                }
                            }
                            else
                            {
                                writer.println(Protocol.PLANET_IO_FAILURE);
                                writer.flush();
                            }
                            break;
                        }

                        // Client wants to disconnect.
                        case Protocol.CMD_DISCONNECT: {
                            done = true;
//                            writer.println(Protocol.PLANET_IO_SUCCESS);
                            break;
                        }

                        // Client wants to connect.
                        case Protocol.PLANET_IO_HELLO: {
                            ArrayList<Game> gameList = Lobby.getSharedInstance().getGamesList();
                            String serializedData = mapper.writeValueAsString(gameList);

                            writer.println(Protocol.PLANET_IO_SUCCESS);
                            writer.flush();

                            writer.println(serializedData);
                            writer.flush();

                            break;
                        }
                        // Client wants to joi a game.
                        case Protocol.PLANET_IO_CREATE_PLANET: {
                            if(cmdAndArgs.length > 1)
                            {
                                int idGame = Integer.parseInt(cmdAndArgs[1]);
                                if(idGame >= 0 && idGame < lobby.getGamesList().size())
                                {
                                    writer.println(Protocol.PLANET_IO_SUCCESS);
                                    writer.flush();
                                    InvisiblePlanet invisible = mapper.readValue(reader.readLine(), InvisiblePlanet.class);
                                    invisible = lobby.getEngineList().get(idGame).addNewInvisiblePlanet(invisible);
                                    writer.println(mapper.writeValueAsString(invisible));
                                    writer.flush();
                                }
                                else
                                {
                                    writer.println(Protocol.PLANET_IO_FAILURE);
                                    writer.flush();
                                }
                            }
                            else
                            {
                                writer.println(Protocol.PLANET_IO_FAILURE);
                                writer.flush();
                            }
                            break;
                        }
                    }
                } else {
                    writer.println(Protocol.PLANET_IO_FAILURE);
                    writer.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
