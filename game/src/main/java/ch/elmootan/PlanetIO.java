package ch.elmootan;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import ch.elmootan.core.sharedObjects.Lobby;
import ch.elmootan.core.universe.Universe;
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

        JTextField serverIPField;
        JTextField interfaceIPField;

        public IdentityChooser() {
            setLayout(new GridLayout(3, 1));

            JLabel whoAreYou = new JLabel("Who are you");

            whoAreYou.setHorizontalAlignment(JLabel.CENTER);
            add(whoAreYou);

            JPanel addressPanel = new JPanel(new GridLayout(2,2));
            JLabel serverIPLabel = new JLabel("Server IP");
            serverIPField = new JTextField();
            serverIPField.setToolTipText("Adresse IP du serveur");
            addressPanel.add(serverIPLabel);
            addressPanel.add(serverIPField);

            JLabel interfaceIPLabel = new JLabel("Interface IP");
            interfaceIPField = new JTextField();
            interfaceIPField.setToolTipText("Adresse IP de l'interface");
            addressPanel.add(interfaceIPLabel);
            addressPanel.add(interfaceIPField);
            add(addressPanel);


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

            setLocationRelativeTo(null);

            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);

        }

        public void actionPerformed(ActionEvent e) {

            Object id = e.getSource();

            //Settings addresses
            String interfaceIP = interfaceIPField.getText();
            String serverIP = serverIPField.getText();

            if(interfaceIP.equals(""))
            {
                interfaceIP = "localhost";
            }
            if(serverIP.equals(""))
            {
                serverIP = "localhost";
            }


            if (id == clientChoiceButton) {
                this.dispose();
                new Client(serverIP,interfaceIP);

                //dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            } else if (id == serverChoiceButton) {
                clientChoiceButton.setEnabled(false);
                Server server = new Server(interfaceIP);
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