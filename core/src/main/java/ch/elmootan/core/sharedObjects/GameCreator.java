package ch.elmootan.core.sharedObjects;

import ch.elmootan.core.sharedObjects.Game;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;

public class GameCreator extends JFrame implements ActionListener {

    protected JTextField gameName;
    protected JFormattedTextField playerMax;

    protected JButton createGame;

    public GameCreator() {
        JPanel topPanel = new JPanel(new GridLayout(1, 2));

        JPanel namePanel = new JPanel(new FlowLayout());
        namePanel.setPreferredSize(new Dimension(100, 20));
        JPanel numberOfPlayerPanel = new JPanel(new FlowLayout());
        numberOfPlayerPanel.setPreferredSize(new Dimension(30, 20));


        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(64);
        //formatter.setAllowsInvalid(false);

        // If you want the value to be committed on each keystroke instead of focus lost
        formatter.setCommitsOnValidEdit(true);

        playerMax = new JFormattedTextField(formatter);

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

        setLocationRelativeTo(null);

        pack();

        this.setResizable(false);
        this.setSize(450, 150);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}