package ch.elmootan.client;

import ch.elmootan.core.physics.Body;
import ch.elmootan.core.sharedObjects.*;
import ch.elmootan.core.universe.Bonus;
import ch.elmootan.core.universe.GUniverse;
import ch.elmootan.core.universe.Planet;
import ch.elmootan.core.universe.Universe;
import ch.elmootan.protocol.Protocol;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Logger;

public class Client implements Runnable {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    private final CustomObjectMapper mapper = new CustomObjectMapper();

    protected Socket tcpSocket;

    private static GUniverse gui;

    PrintWriter out;
    BufferedReader in;
    Player player;
    boolean connectionRunning = false;

    //débug
    boolean noGUI = true;

    static LobbyClient lobbyClient = null;

    public static int idCurrentGame;

    private ClientMulticast clientMulticast;


    public String serverRead() throws IOException {
        return in.readLine();
    }

    public void serverWrite(String toWrite) {
        out.println(toWrite);
        out.flush();
    }

    public Client(Player player)
    {
        createClient(player,false);
    }

    public Client(Player player, boolean debug)
    {
        createClient(player,debug);
    }

    private void createClient(Player player, boolean debug)
    {
        noGUI = debug;
        try
        {
            clientMulticast = new ClientMulticast(Protocol.IP_MULTICAST, Protocol.PORT_UDP, InetAddress.getByName("localhost"));

            new Thread(clientMulticast).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.player = player;
        tcpSocket = new Socket();


        if (!debug) {
            lobbyClient = new LobbyClient();
            lobbyClient.showUI();
        }

        try {
            connect("localhost", Protocol.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect(String server, int port) throws IOException {

        if (tcpSocket != null)
            tcpSocket.close();

        tcpSocket = new Socket(server, port);

        try {
            out = new PrintWriter(tcpSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
        } catch (IOException e) {
            LOG.warning(e.toString());
        }
        LOG.info(serverRead());

        serverWrite(Protocol.PLANET_IO_HELLO);

        if (serverRead().equals(Protocol.PLANET_IO_SUCCESS)) {
            LOG.info("Connexion successful");
            String gameListJSON = serverRead();
            ArrayList<Game> initialGameList = mapper.readValue(gameListJSON, new TypeReference<ArrayList<Game>>() {
            });

            System.out.println(gameListJSON);
            if (!noGUI) {
                lobbyClient.addGameList(initialGameList);
            }
        }

        new Thread(this).start();
    }

    public void run() {
        connectionRunning = true;
        while (connectionRunning) {
            //Tu ne peux pas faire communiquer simultanément cette thread et les commandes
            //si cette thread fait un read alors qu'une autre commande est en train d'être faite
            //alors cette thread nique le protocole x).
            //J'ai essayé de synchroniser mais ça bloque tout :(
            //Je recommande fortement d'utiliser de l'UDP pour refresh ton lobby ;) (ptit datagram vite fait :P)

//            synchronized(lobbyClient)
//            {
//                String input = "";
//                try
//                {
//                    input = serverRead();
//                    switch (input)
//                    {
//                        case Protocol.LOBBY_UPDATED:
//                            input = serverRead();
//                            Game newGame = mapper.readValue(input, Game.class);
//                            lobbyClient.addGame(newGame);
//                            break;
//
//                        default:
//                            break;
//
//                    }
//                } catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//            }
        }


    }

    public void disconnect() throws IOException {
        synchronized (lobbyClient) {
            serverWrite(Protocol.CMD_DISCONNECT);
            connectionRunning = false;
            out.close();
            in.close();
            tcpSocket.close();
        }
    }

    public boolean isConnected() {
        LOG.info(String.valueOf(tcpSocket.isConnected()));
        return tcpSocket.isConnected() && !tcpSocket.isClosed();
    }

    public void sendGameToServer(Game game) {
        synchronized (lobbyClient) {
            try {
                serverWrite(Protocol.CMD_CREATE_GAME);
                if (serverRead().equals(Protocol.PLANET_IO_SUCCESS)) {
                    serverWrite(mapper.writeValueAsString(game));
                    String succesAndGameId = serverRead();
                    if (succesAndGameId.indexOf(Protocol.PLANET_IO_SUCCESS) != -1) {
                        idCurrentGame = Integer.parseInt(succesAndGameId.split(Protocol.CMD_SEPARATOR)[1]);
                        System.out.println("Game n° " + idCurrentGame + " created");
                    } else {
                        System.out.println("Error, game was not created");
                    }
                } else {
                    System.out.println("Error, game was not created");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void joinServer(int skin) {
        synchronized (lobbyClient) {
            try {
                serverWrite(Protocol.CMD_JOIN_GAME
                + Protocol.CMD_SEPARATOR
                + idCurrentGame);
                if (serverRead().equals(Protocol.PLANET_IO_SUCCESS)) {
                    serverWrite(mapper.writeValueAsString(new Planet(player.getName(),skin)));
                    Planet initialPlanet = mapper.readValue(serverRead(),Planet.class);
                    gui = new GUniverse(
                          out,
                          in,
                          clientMulticast.getSocket(),
                          idCurrentGame,
                          initialPlanet);
                    gui.showUI();
                } else {
                    System.out.println("Error, this game is not reachable");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void exit() {
        try {
            if (!noGUI) {
                lobbyClient.dispose();
                disconnect();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void addGameToLobby(Game game) {
        lobbyClient.addGame(game);
    }

    protected static void updateGUniverse(ArrayList<Body> bodies)
    {
        gui.setAllThings(bodies);
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
                            sendGameToServer(newGame);
                            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                        }
                    }
                };
            } else if (e.getSource() == joinGameButton) {
                int indexGame = table.getSelectedRow();
                if (indexGame != -1) {
                    // Choix du skin quand on rejoint la partie.
                    SkinChooser skinChooser = new SkinChooser() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (e.getSource() == btnChoose) {
                                System.out.println(idSkin);
                                chooseStatus = true;
                                idCurrentGame = gamesList.get(indexGame).getGameId();
                                joinServer(idSkin);
                                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                            }

                            if (e.getSource() == btnNext) {
                                idSkin = idSkin + 1 > 7 ? 0 : ++idSkin;
                            }
                            if (e.getSource() == btnPrev) {
                                idSkin = idSkin - 1 < 0 ? 7 : --idSkin;
                            }

                            imgSkin.setIcon(new ImageIcon(skins.get(idSkin)));
                            revalidate();
                            repaint();
                        }
                    };
                    //while (!skinChooser.skinChoosed());
                }
            }
        }
    }
}
