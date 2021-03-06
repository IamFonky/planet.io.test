package ch.elmootan.core.sharedObjects;

import ch.elmootan.core.serverCore.Engine;
import ch.elmootan.core.serverCore.ServerMulticast;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by David on 04.05.2017.
 */
public class Lobby extends JFrame implements ActionListener {

    protected ArrayList<Game> gamesList = new ArrayList<>();
    protected ArrayList<Engine> engineList = new ArrayList<>();
    protected JTable table;

    protected JButton addGameButton;
    protected JButton joinGameButton;

    protected JPanel bottomPanel = new JPanel();

    protected int nbGamesMax;

    public void setMulticastServer(ServerMulticast multicastServer) {
        this.multicastServer = multicastServer;
    }

    protected ServerMulticast multicastServer;

    protected Observable lobbyChanged = new Observable() {
        public void notifyObservers(Object obj) {
            super.setChanged();
            super.notifyObservers(obj);
        }
    };

    private static Lobby sharedLobby = null;

    public static Lobby getSharedInstance() {
        if (sharedLobby == null) {
            sharedLobby = new Lobby();
        }
        return sharedLobby;
    }

    protected Lobby() {
        super("Lobby");

        Object[] tableTitles = {"Name", "Players"};


        table = new JTable(new DefaultTableModel(null, tableTitles) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        });

        addGameButton = new JButton("Add Game");
        addGameButton.addActionListener(this);

        joinGameButton = new JButton("Join Game");
        joinGameButton.addActionListener(this);

        DefaultTableModel model = (DefaultTableModel) table.getModel();


        JScrollPane js = new JScrollPane(table);


        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        bottomPanel.add(addGameButton);
        bottomPanel.add(joinGameButton);

        getContentPane().add(js, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.PAGE_END);

        pack();

        setSize(500, 500);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void showUI() {
        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addGameButton) {
            new GameCreator() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == createGame) {
                        Game newGame = new Game(gameName.getText(), null, Integer.parseInt(playerMax.getText()));
                        addGame(newGame);
                        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                    }
                }
            };
        }
    }


    public int addGame(Game game) {
        gamesList.add(game);
        engineList.add(new Engine(multicastServer, gamesList.size() - 1));
        game.setGameId(gamesList.size() - 1);
        //lobbyChanged.notifyObservers(game);
        refreshGameList(gamesList);
        return gamesList.size();
    }

    public void addGameList(ArrayList<Game> gameList) {
        for (Game game : gameList) {
            addGame(game);
        }
    }

    public void addAPlayerToGame(int idGame) {
        gamesList.get(idGame).addPlayer();
        //lobbyChanged.notifyObservers();
        refreshGameList(gamesList);
    }

    public void removeAPlayerToGame(int idGame, String playerName) {
        gamesList.get(idGame).removePlayer();
        engineList.get(idGame).removeAUserByName(playerName);
        //lobbyChanged.notifyObservers();
        refreshGameList(gamesList);
    }


    public void refreshGameList(ArrayList<Game> newGameList) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        model.setRowCount(0);

        gamesList = newGameList;

        for (int i = 0; i < gamesList.size(); ++i) {
            model.addRow(new Object[]{gamesList.get(i).getName(), gamesList.get(i).getNbPlaylersCurrent() + "/" + gamesList.get(i).getNbPlayersMax()});
        }

        lobbyChanged.notifyObservers();

    }

    public ArrayList<Game> getGamesList() {
        return gamesList;
    }

    public ArrayList<Engine> getEngineList() {
        return engineList;
    }

    public void setNbGamesMax(int nbGamesMax) {
        this.nbGamesMax = nbGamesMax;
    }

    public int getNbGamesMax() {
        return nbGamesMax;
    }

    protected class SkinChooser extends JFrame implements ActionListener {
        protected JButton btnNext = new JButton(">");
        protected JButton btnPrev = new JButton("<");

        protected JButton btnChoose = new JButton("GO!");

        protected ArrayList<BufferedImage> skins = new ArrayList<>();
        protected JLabel imgSkin;
        protected int idSkin = 0;

        protected boolean chooseStatus = false;

        public SkinChooser() {
            JPanel imgPanel = new JPanel(new FlowLayout());
            JPanel goPanel = new JPanel();

            try {
                for (int i = 1; i <= 8; i++) {
                    skins.add(ImageIO.read(getClass().getResourceAsStream("/skins/planet" + i + "_64x64.png")));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            imgSkin = new JLabel(new ImageIcon(skins.get(idSkin)));
            imgPanel.add(btnPrev);
            btnPrev.addActionListener(this);
            imgPanel.add(imgSkin);
            btnNext.addActionListener(this);
            imgPanel.add(btnNext);

            goPanel.add(btnChoose);
            btnChoose.addActionListener(this);

            setLocationRelativeTo(null);

            getContentPane().add(imgPanel, BorderLayout.CENTER);
            getContentPane().add(goPanel, BorderLayout.PAGE_END);
            setTitle("Choix du skin");
            setResizable(false);
            setSize(200, 150);
            setVisible(true);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnChoose) {
                System.out.println(idSkin);
                chooseStatus = true;
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


        public boolean skinChoosed() {
            return chooseStatus;
        }

    }

    public static void main(String... args) {
        new Lobby();
    }


    public void addServerObserver(Observer server) {
        if (server != null) {
            lobbyChanged.addObserver(server);
        }
    }

}