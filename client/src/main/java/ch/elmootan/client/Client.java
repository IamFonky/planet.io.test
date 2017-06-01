package ch.elmootan.client;

import ch.elmootan.core.sharedObjects.Game;
import ch.elmootan.core.sharedObjects.GameCreator;
import ch.elmootan.core.sharedObjects.Lobby;
import ch.elmootan.core.sharedObjects.Player;
import ch.elmootan.protocol.Protocol;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Client implements Runnable{

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    private final ObjectMapper mapper = new ObjectMapper();

    protected Socket socket;

    PrintWriter out;
    BufferedReader in;
    Player player;

    static LobbyClient lobbyClient = null;

    private ClientMulticast clientMulticast;



    public String serverRead() throws IOException {
        return in.readLine();
    }

    public void serverWrite(String toWrite) {
        out.println(toWrite);
        out.flush();
    }

    public Client(Player player) {
        try {
            clientMulticast = new ClientMulticast(Protocol.IP_MULTICAST, Protocol.PORT_UDP, InetAddress.getByName("localhost"));

            new Thread(clientMulticast).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.player = player;
        socket = new Socket();
        lobbyClient = new LobbyClient();
        lobbyClient.showUI();
        try {
            connect("localhost", Protocol.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect(String server, int port) throws IOException {

        socket = new Socket(server, port);

        try {
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            LOG.warning(e.toString());
        }
        LOG.info(serverRead());

        serverWrite(Protocol.PLANET_IO_HELLO);

        if (serverRead().equals(Protocol.PLANET_IO_SUCCESS)) {
            LOG.info("Connexion successful");
            String gameListJSON = serverRead();
            ArrayList<Game> initialGameList = mapper.readValue(gameListJSON, new TypeReference<ArrayList<Game>>(){});

            System.out.println(gameListJSON);
            lobbyClient.addGameList(initialGameList);
        }

        new Thread(this).start();
    }

    public void run() {

        while(true) {

            String input = "";
            try {
                input = serverRead();
                switch (input) {
                    case Protocol.LOBBY_UPDATED:
                        input = serverRead();
                        Game newGame = mapper.readValue(input, Game.class);
                        lobbyClient.addGame(newGame);
                        break;

                    default:
                        break;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    public void disconnect() throws IOException {
        // Todo: send the QUIT sequence to the server
        out.close();
        in.close();
        socket.close();
    }

    public boolean isConnected() {
        LOG.info(String.valueOf(socket.isConnected()));
        return socket.isConnected() && !socket.isClosed();
    }

    public static void addGameToLobby(Game game) {
        lobbyClient.addGame(game);
    }

    private class LobbyClient extends Lobby {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == addGameButton) {
                new GameCreator() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (e.getSource() == createGame) {
                            Game newGame = new Game(gameName.getText(), null, Integer.parseInt(playerMax.getText()));

                            String gameSerialized = "";
                            try {
                                gameSerialized = mapper.writeValueAsString(newGame);
                            } catch (JsonProcessingException e1) {
                                e1.printStackTrace();
                            }

                            serverWrite(Protocol.CMD_CREATE_GAME);
                            serverWrite(gameSerialized);

                           /* try {
                                if (serverRead().equals(Protocol.PLANET_IO_SUCCESS)) {
                                    LOG.info("Success!");
                                    //lobbyClient.addGame(newGame);
                                } else {
                                    LOG.info("Failure!");
                                }
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }*/

                            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                        }
                    }
                };
            } else {
                int indexGame = table.getSelectedRow();
                if (indexGame != -1) {
                    gamesList.get(indexGame).join();
                }
            }
        }
    }
}
