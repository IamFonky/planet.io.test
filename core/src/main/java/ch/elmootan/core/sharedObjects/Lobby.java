package ch.elmootan.core.sharedObjects;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * Created by David on 04.05.2017.
 */
public class Lobby extends JFrame implements ActionListener {

    protected ArrayList<Game> gamesList = new ArrayList<>();
    protected JTable table;

    protected JButton addGameButton;
    protected JButton joinGameButton;

    protected int nbGamesMax;

    private Observable lobbyChanged = new Observable() {
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
        super("Best lobby. Ever.");

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

        Game gameTest1 = new Game("Test1", null, 12);
        Game gameTest2 = new Game("Test2", null, 39);

        //addGame(gameTest1);
        //addGame(gameTest2);

        JScrollPane js = new JScrollPane(table);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        bottomPanel.add(addGameButton);
        bottomPanel.add(joinGameButton);

        getContentPane().add(js, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.PAGE_END);

        pack();


        setSize(500, 500);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    public void showUI() {
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == addGameButton) {
            //new GameCreator();
        } else {
            int indexGame = table.getSelectedRow();
            if(indexGame!= -1) {
                gamesList.get(indexGame).join();
            }
        }
    }

    public void addGame(Game game) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[] {game.getName(), game.getNbPlaylersCurrent() + "/" + game.getNbPlayersMax()});
        gamesList.add(game);
        lobbyChanged.notifyObservers(game);
    }

    public void addGameList(ArrayList<Game> gameList) {
        for (Game game : gameList) {
            addGame(game);
        }
    }

    public ArrayList<Game> getGamesList() {
        return gamesList;
    }


    public void setNbGamesMax(int nbGamesMax) {
        this.nbGamesMax = nbGamesMax;
    }

    public int getNbGamesMax() {
        return nbGamesMax;
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