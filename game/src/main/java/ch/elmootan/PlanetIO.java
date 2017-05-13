package ch.elmootan;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import ch.elmootan.server.Server;

public class PlanetIO {

    public static void main(String... args) {

        new IdentityChooser();

        //new Universe();
        //Universe monUnivers = new Universe();
    }

    private static class IdentityChooser extends JFrame implements ActionListener {

        JButton server;
        JButton client;

        public IdentityChooser() {
            setLayout(new GridLayout(2, 1));

            JLabel whoAreYou = new JLabel("Who are you");

            whoAreYou.setHorizontalAlignment(JLabel.CENTER);
            add(whoAreYou);

            JPanel buttonsPanel = new JPanel(new FlowLayout());

            server = new JButton("Server");
            server.setHorizontalAlignment(JButton.LEFT);

            client = new JButton("Client");
            client.setHorizontalAlignment(JButton.RIGHT);

            buttonsPanel.add(server);
            buttonsPanel.add(client);

            add(buttonsPanel);


            setSize(267, 150);
            setVisible(true);

        }

        public void actionPerformed(ActionEvent e) {

            Object id = e.getSource();
            if (id == client) {

            } else if (id == server) {
                Server server = new ch.elmootan.server.Server();
                try {
                    server.startServer();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}