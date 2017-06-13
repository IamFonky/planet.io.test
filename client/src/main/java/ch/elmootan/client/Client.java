package ch.elmootan.client;

import ch.elmootan.core.database.DBObjects.User;
import ch.elmootan.core.physics.Body;
import ch.elmootan.core.sharedObjects.*;
import ch.elmootan.core.universe.GUniverse;
import ch.elmootan.core.universe.Planet;
import ch.elmootan.protocol.Protocol;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Client implements Runnable {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    protected final CustomObjectMapper mapper = new CustomObjectMapper();

    protected Socket tcpSocket;

    protected static GUniverse gui;


    static PrintWriter out;
    static BufferedReader in;
    static Player player = new Player("");

    boolean connectionRunning = false;

    boolean isAdmin;

    //débug
    boolean noGUI = false;

    static protected Lobby lobby = null;

    // Current game
    public static Game currentGame;
    static CredentialsPrompt cPrompt = null;


    protected ClientMulticast clientMulticast;


    static public String serverRead() throws IOException {
        return in.readLine();
    }

    static public void serverWrite(String toWrite) {
        out.println(toWrite);
        out.flush();
    }


    public Client() {
        createClient(player, false);
    }


    private void createClient(Player player, boolean debug) {

        tcpSocket = new Socket();

        cPrompt = new CredentialsPrompt();

        try {
            connect("localhost", Protocol.PORT);


        } catch (Exception e) {
            e.printStackTrace();
        }

        noGUI = debug;
        try {
            clientMulticast = new ClientMulticast(Protocol.IP_MULTICAST, Protocol.PORT_UDP, InetAddress.getByName("localhost"));

            new Thread(clientMulticast).start();
        } catch (UnknownHostException e) {
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
            new Thread(this).start();
        }
    }

    public void run() {
        connectionRunning = true;
        while (connectionRunning) {
           /* try {
                synchronized (lobbyClient) {
                    lobbyClient.wait();
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }*/
        }
    }

    public void disconnect() throws IOException {
        synchronized (lobby) {
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
        synchronized (lobby) {
            try {
                serverWrite(Protocol.CMD_CREATE_GAME);
                if (serverRead().equals(Protocol.PLANET_IO_SUCCESS)) {
                    serverWrite(mapper.writeValueAsString(game));
                    String succesAndGameId = serverRead();
                    if (succesAndGameId.indexOf(Protocol.PLANET_IO_SUCCESS) != -1) {
                        // currentGame.setGameId(Integer.parseInt(succesAndGameId.split(Protocol.CMD_SEPARATOR)[1]));
                        System.out.println("Game n° " + Integer.parseInt(succesAndGameId.split(Protocol.CMD_SEPARATOR)[1]) + " created");
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

    public void joinGame(int skin) {
        synchronized (lobby) {
            try {
                serverWrite(Protocol.CMD_JOIN_GAME
                        + Protocol.CMD_SEPARATOR
                        + currentGame.getGameId());
                if (serverRead().equals(Protocol.PLANET_IO_SUCCESS)) {
                    serverWrite(mapper.writeValueAsString(new Planet(player.getName(), skin)));
                    Planet initialPlanet = mapper.readValue(serverRead(), Planet.class);
                    gui = new GUniverse(
                            out,
                            in,
                            clientMulticast.getSocket(),
                            currentGame.getGameId(),
                            initialPlanet, false);
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
                lobby.dispose();
                disconnect();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void addGameToLobby(Game game) {
        lobby.addGame(game);
    }

    protected static void updateGUniverse(ArrayList<Body> bodies) {
        synchronized (lobby) {
            if (gui != null) {
                gui.setAllThings(bodies);
            } else {
                System.out.println("Error");
            }
        }
    }

    private class LobbyClient extends Lobby {

        public LobbyClient() {
            super();
            setTitle("Lobby Client");
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
                                currentGame = gamesList.get(indexGame);
                                joinGame(idSkin);
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
                }
            }
        }
    }

    private class CredentialsPrompt extends JFrame implements ActionListener {

        private JTextField pseudo;

        private JButton done;

        private JLabel error;

        JCheckBox admin;

        public CredentialsPrompt() {
            super("Enter your pseudo");

            done = new JButton("Done");
            done.addActionListener(this);

            pseudo = new JTextField(17);

            admin = new JCheckBox("Admin");

            error = new JLabel("");
            error.setForeground(Color.RED);

            JPanel pseudoPanel = new JPanel(new FlowLayout());

            pseudoPanel.add(new JLabel("Pseudo"), BorderLayout.CENTER);
            pseudoPanel.add(pseudo, BorderLayout.CENTER);
            pseudoPanel.add(admin, BorderLayout.EAST);

            JPanel bottomPanel = new JPanel(new GridLayout(2, 1));

            JPanel donePannel = new JPanel(new FlowLayout());

            donePannel.add(done, BorderLayout.CENTER);

            bottomPanel.add(donePannel);
            bottomPanel.add(error);

            getRootPane().setDefaultButton(done);

            getContentPane().add(pseudoPanel, BorderLayout.NORTH);
            getContentPane().add(bottomPanel, BorderLayout.SOUTH);

            setLocationRelativeTo(null);

            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(350, 150);

            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == done) {
                if (pseudo.getText().equals("")) {
                    error.setText("You need to choose a pseudo!");
                } else {
                    // TODO check if in DB
                    try {
                        User checkUser = new User(pseudo.getText());
                        serverWrite(Protocol.PLANET_IO_LOGIN);
                        serverWrite(mapper.writeValueAsString(checkUser));
                        String test = serverRead();
                        if (test.equals(Protocol.PLANET_IO_SUCCESS)) {

                            player = new Player(pseudo.getText());
                            if (admin.isSelected()) {
                                lobby = new LobbyAdmin();
                                isAdmin = true;
                            } else {
                                lobby = new LobbyClient();
                                isAdmin = false;
                            }

                            serverWrite(Protocol.PLANET_IO_LOBBY_JOINED);
                            if (serverRead().equals(Protocol.PLANET_IO_SUCCESS)) {
                                LOG.info("Connexion successful");
                                String gameListJSON = serverRead();
                                ArrayList<Game> initialGameList = mapper.readValue(gameListJSON, new TypeReference<ArrayList<Game>>() {
                                });

                                System.out.println(gameListJSON);
                                if (!noGUI) {
                                    lobby.addGameList(initialGameList);
                                }
                            }

                            lobby.showUI();

                            this.dispose();
                        } else {
                            error.setText("Pseudo already taken!");
                        }
                    } catch (Exception jpe) {
                        jpe.printStackTrace();
                    }
                }
            }
        }
    }

    private class LobbyAdmin extends Lobby {

        JButton changeProperties = new JButton("Properties");

        public LobbyAdmin() {
            super();
            setTitle("Lobby Admin");

            changeProperties.addActionListener(this);
            bottomPanel.add(changeProperties);
        }

        private void joinGame() {
            synchronized (lobby) {
                try {
                    serverWrite(Protocol.CMD_JOIN_GAME
                            + Protocol.CMD_SEPARATOR
                            + currentGame.getGameId());
                    if (serverRead().equals(Protocol.PLANET_IO_SUCCESS)) {
                        serverWrite(mapper.writeValueAsString(null));
                        serverRead();

                        gui = new GUniverse(
                                out,
                                in,
                                clientMulticast.getSocket(),
                                currentGame.getGameId(),
                                null, true);
                        gui.showUI();
                    } else {
                        System.out.println("Error, this game is not reachable");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == addGameButton) {
                new GameCreator() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (e.getSource() == createGame) {
                            if (!playerMax.getText().matches("[a-zA-Z]+")) {
                                Game newGame = new Game(gameName.getText(), null, (Integer) playerMax.getValue());
                                sendGameToServer(newGame);
                                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                            }
                        }
                    }
                };
            } else if (e.getSource() == joinGameButton) {
                int indexGame = table.getSelectedRow();
                if (indexGame != -1) {
                    currentGame = gamesList.get(indexGame);
                    joinGame();
                }
            } else if (e.getSource() == changeProperties) {
                new PropertiesChanger();
            }
        }
    }

    private class PropertiesChanger extends JFrame implements ActionListener {

        JFormattedTextField maxGame;

        JButton done;

        public PropertiesChanger() {
            JPanel topPanel = new JPanel(new FlowLayout());
            topPanel.setPreferredSize(new Dimension(30, 20));

            done = new JButton("Done");
            done.addActionListener(this);

            NumberFormat format = NumberFormat.getInstance();
            NumberFormatter formatter = new NumberFormatter(format);

            formatter.setValueClass(Integer.class);
            formatter.setMinimum(0);
            formatter.setMaximum(64);
            formatter.setAllowsInvalid(true);
            // If you want the value to be committed on each keystroke instead of focus lost
            formatter.setCommitsOnValidEdit(true);

            maxGame = new JFormattedTextField(formatter);
            maxGame.setColumns(10);
            maxGame.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    done.doClick();
                }
            });

            topPanel.add(new JLabel("Number of games max"));
            topPanel.add(maxGame);

            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

            bottomPanel.add(done);

            this.getContentPane().add(topPanel, BorderLayout.CENTER);
            this.getContentPane().add(bottomPanel, BorderLayout.PAGE_END);

            getRootPane().setDefaultButton(done);

            pack();

            this.setResizable(false);
            this.setSize(150, 200);
            this.setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == done) {

                if (!maxGame.getText().matches("[a-zA-Z]+")) {
                    serverWrite(Protocol.NB_GAME_MAX_UPDATE + Protocol.CMD_SEPARATOR + (Integer) maxGame.getValue());

                    dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                }
            }
        }

    }
}
