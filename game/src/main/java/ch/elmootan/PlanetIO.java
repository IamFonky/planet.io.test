package ch.elmootan;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
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
            server.addActionListener(this);

            client = new JButton("Client");
            client.setHorizontalAlignment(JButton.RIGHT);
            client.addActionListener(this);

            buttonsPanel.add(server);
            buttonsPanel.add(client);

            add(buttonsPanel);


            setSize(267, 150);

            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);

        }

        public void actionPerformed(ActionEvent e) {

            Object id = e.getSource();
            if (id == client) {
                new CredentialsPrompt();
                setVisible(false);
                //dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
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

    private static class CredentialsPrompt extends JFrame implements ActionListener {


        private JTextField pseudo;
        private JButton done;

        public CredentialsPrompt() {
            setLayout(new FlowLayout());

            done = new JButton("Done");

            pseudo = new JTextField("Enter your pseudo");

            add(pseudo);
            add(done);


            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(267, 150);

            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {

        }
    }

}