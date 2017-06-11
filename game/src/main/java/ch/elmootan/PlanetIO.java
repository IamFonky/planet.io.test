package ch.elmootan;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import ch.elmootan.core.sharedObjects.Lobby;
import ch.elmootan.server.Server;
import ch.elmootan.client.Client;

public class PlanetIO {

    public static void main(String... args) {

        new IdentityChooser();

        //new Universe();
        //Universe monUnivers = new Universe();
    }

    private static class IdentityChooser extends JFrame implements ActionListener {

        JButton serverChoiceButton;
        JButton clientChoiceButton;

        public IdentityChooser() {
            setLayout(new GridLayout(2, 1));

            JLabel whoAreYou = new JLabel("Who are you");

            whoAreYou.setHorizontalAlignment(JLabel.CENTER);
            add(whoAreYou);

            JPanel buttonsPanel = new JPanel(new FlowLayout());

            serverChoiceButton = new JButton("Server");
            serverChoiceButton.setHorizontalAlignment(JButton.LEFT);
            serverChoiceButton.addActionListener(this);

            clientChoiceButton = new JButton("Client");
            clientChoiceButton.setHorizontalAlignment(JButton.RIGHT);
            clientChoiceButton.addActionListener(this);

            buttonsPanel.add(serverChoiceButton);
            buttonsPanel.add(clientChoiceButton);

            add(buttonsPanel);


            setSize(267, 150);

            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);

        }

        public void actionPerformed(ActionEvent e) {

            Object id = e.getSource();
            if (id == clientChoiceButton) {
                this.dispose();
                new Client();

                //dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            } else if (id == serverChoiceButton) {
                clientChoiceButton.setEnabled(false);
                Server server = new Server();
                Lobby.getSharedInstance().addServerObserver(server);


                try {
                    serverChoiceButton.setEnabled(false);
                    server.startServer();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}