package ch.elmootan.client;

import ch.elmootan.core.database.DBObjects.User;
import ch.elmootan.core.database.Database;
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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private static final CustomObjectMapper mapper = new CustomObjectMapper();

    protected Socket tcpSocket;

    private static GUniverse gui;

    static PrintWriter out;
    static BufferedReader in;
    static Player player = new Player("");
    boolean connectionRunning = false;

    //débug
    boolean noGUI = false;

    static LobbyClient lobbyClient = null;
    static CredentialsPrompt cPrompt = null;

    //Current game is set for
    public static int idCurrentGame = -1;

    private ClientMulticast clientMulticast;


    static public String serverRead() throws IOException {
        return in.readLine();
    }

    static public void serverWrite(String toWrite) {
        out.println(toWrite);
        out.flush();
    }

    public Client()
    {
       lobbyClient = new LobbyClient();
        try
        {
            clientMulticast = new ClientMulticast(Protocol.IP_MULTICAST, Protocol.PORT_UDP, InetAddress.getByName("localhost"));

            new Thread(clientMulticast).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        tcpSocket = new Socket();

        try {
            connect("localhost", Protocol.PORT);
            cPrompt = new CredentialsPrompt();
            synchronized (cPrompt)
            {
                cPrompt.wait();
            }
            lobbyClient.showUI();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public Client(Player player)
//    {
//        createClient(player,false);
//    }
//
//    public Client(Player player, boolean debug)
//    {
//        createClient(player,debug);
//    }
//
//    private void createClient(Player player, boolean debug)
//    {
//        noGUI = debug;
//        try
//        {
//            clientMulticast = new ClientMulticast(Protocol.IP_MULTICAST, Protocol.PORT_UDP, InetAddress.getByName("localhost"));
//
//            new Thread(clientMulticast).start();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        player = player;
//        tcpSocket = new Socket();
//
//
//        if (!debug) {
//            lobbyClient = new LobbyClient();
//            lobbyClient.showUI();
//        }
//
//        try {
//            connect("localhost", Protocol.PORT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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
            try
            {
                synchronized (lobbyClient)
                {
                    lobbyClient.wait();
                };
            }
            catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }
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

    protected static void updateGUniverse(ArrayList<Body> bodies) {
        synchronized (lobbyClient) {
            if (gui != null) {
                gui.setAllThings(bodies);
            }
            else {
                System.out.println("qu'Allah te brise le dos fdp");
            }
        }
    }

    private class LobbyClient extends Lobby {

        public LobbyClient()
        {
            super();
            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    notifyAll();
                }
            });
        }

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

    private static class CredentialsPrompt extends JFrame implements ActionListener {

        private JLabel erreur;
        private JTextField pseudo;
        private JPasswordField motdepasse;
        private JButton done;

        public CredentialsPrompt() {
            //setLayout(new FlowLayout());

            done = new JButton("Done");
            done.addActionListener(this);

            JPanel pseudoPanel = new JPanel(new FlowLayout());

            erreur = new JLabel();
            erreur.setVisible(false);
            pseudoPanel.add(erreur);

            pseudoPanel.add(new JLabel("Pseudo"));
            pseudo = new JTextField();
            pseudoPanel.add(pseudo);

            pseudoPanel.add(new JPasswordField("Mot de passe"));
            motdepasse = new JPasswordField();
            pseudoPanel.add(motdepasse);

            getRootPane().setDefaultButton(done);

            getContentPane().add(pseudoPanel);


            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(300, 150);

            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == done && pseudo.getText() != "" && motdepasse.getText() != "") {
                try
                {
                    User checkUser = new User(pseudo.getText(),motdepasse.getText());
                    serverWrite(Protocol.PLANET_IO_LOGIN);
                    serverWrite(mapper.writeValueAsString(checkUser));
                    if(serverRead().equals(Protocol.PLANET_IO_SUCCESS))
                    {
                        notifyAll();
                        this.dispose();
                    }
                    else
                    {
                        erreur.setText("Wrong password bro");
                        erreur.setVisible(true);
                    }
                }
                catch (Exception jpe)
                {
                    jpe.printStackTrace();
                }
            }
        }

        private void checkUser(User user)
        {
            serverWrite("");
        }
    }
}
