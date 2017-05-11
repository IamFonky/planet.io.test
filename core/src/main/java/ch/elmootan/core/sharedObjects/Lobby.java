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
import java.util.Vector;

/**
 * Created by David on 04.05.2017.
 */
public class Lobby extends JFrame implements ActionListener {

    private ArrayList<Game> gamesList = new ArrayList<>();
    private JList<Game> games;

    private JTable table;

    private JButton addGameButton;
    private JButton joinGameButton;

    public Lobby() {
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

        addGame(gameTest1);
        addGame(gameTest2);

        JScrollPane js = new JScrollPane(table);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        bottomPanel.add(addGameButton);
        bottomPanel.add(joinGameButton);

        getContentPane().add(js, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.PAGE_END);

        pack();


        setSize(500, 500);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == addGameButton) {
            new GameCreator();


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
    }


    private class GameCreator extends JFrame implements ActionListener {

        JTextField gameName;
        JFormattedTextField playerMax;

        JButton createGame;

        public GameCreator() {
            JPanel topPanel = new JPanel(new GridLayout(1, 2));

            JPanel namePanel = new JPanel(new FlowLayout());
            namePanel.setPreferredSize(new Dimension(100, 20));
            JPanel numberOfPlayerPanel = new JPanel(new FlowLayout());
            numberOfPlayerPanel.setPreferredSize(new Dimension(30, 20));

            playerMax = new JFormattedTextField(NumberFormat.getIntegerInstance());
            playerMax.setColumns(10);
            playerMax.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    createGame.doClick();
                }
            });

            gameName = new JTextField(17);

            namePanel.add(new JLabel("Game name"));
            namePanel.add(gameName);

            numberOfPlayerPanel.add(new JLabel("Number of player max"));
            numberOfPlayerPanel.add(playerMax);

            topPanel.add(namePanel);
            topPanel.add(numberOfPlayerPanel);

            createGame = new JButton("Create Game!");
            createGame.addActionListener(this);


            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

            bottomPanel.add(createGame);

            this.getContentPane().add(topPanel, BorderLayout.CENTER);
            this.getContentPane().add(bottomPanel, BorderLayout.PAGE_END);

            getRootPane().setDefaultButton(createGame);

            pack();

            this.setResizable(false);
            this.setSize(450, 150);
            this.setVisible(true);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == createGame) {
                Game newGame = new Game(gameName.getText(), null, Integer.parseInt(playerMax.getText()));
                addGame(newGame);
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }
        }
    }

    public static void main(String... args) {
        new Lobby();

    }

}