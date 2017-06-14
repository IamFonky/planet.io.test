package ch.elmootan;


import ch.elmootan.client.Client;
import ch.elmootan.core.sharedObjects.Lobby;
import ch.elmootan.server.Server;
import ch.elmootan.utils.Network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.TreeMap;

public class PlanetIO {

    public static void main(String... args) {


        new IdentityChooser();

        //new Universe();
        //Universe monUnivers = new Universe();
    }

    private static class IdentityChooser extends JFrame implements ActionListener {

        JButton serverChoiceButton;
        JButton clientChoiceButton;

        JComboBox<Network.InterfaceIP> interfacesComboBox;

        JTextField serverIPField;
        JTextField interfaceIPField;

        String serverIP = "";
        String interfaceIP = "";

        public IdentityChooser() {

            setLayout(new GridLayout(3, 1));

            JLabel whoAreYou = new JLabel("Who are you");

            whoAreYou.setHorizontalAlignment(JLabel.CENTER);
            add(whoAreYou);

            JPanel addressPanel = new JPanel(new GridLayout(2, 2));
            JLabel serverIPLabel = new JLabel("Server IP");
            serverIPField = new JTextField();
            serverIPField.setToolTipText("Adresse IP du serveur");
            addressPanel.add(serverIPLabel);
            addressPanel.add(serverIPField);

            interfacesComboBox = new JComboBox<>(Network.getInterfaceIPForComboBox());

            interfacesComboBox.addActionListener(this);

            JLabel interfaceIPLabel = new JLabel("Interface IP");
            interfaceIPField = new JTextField();
            interfaceIPField.setToolTipText("Adresse IP de l'interface");
            addressPanel.add(interfaceIPLabel);
            addressPanel.add(interfacesComboBox);
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

            pack();

            setLocationRelativeTo(null);

            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);

        }

        @Override
        public void actionPerformed(ActionEvent e) {

            Object id = e.getSource();

            //Settings addresses
            //String interfaceIP = interfaceIPField.getText();
            serverIP = serverIPField.getText();


            if (id == interfacesComboBox) {
                interfaceIP = interfacesComboBox.getItemAt(interfacesComboBox.getSelectedIndex()).getIpAddress();
                return;
            }

            if (interfaceIP.equals("")) {
                interfaceIP = interfacesComboBox.getItemAt(0).getIpAddress();;
            }
            if (serverIP.equals("")) {
                serverIP = "localhost";
            }

            if (id == clientChoiceButton) {
                this.dispose();
                new Client(serverIP, interfaceIP);

                //dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            } else if (id == serverChoiceButton) {
                Server server = new Server(interfaceIP);
                Lobby.getSharedInstance().addServerObserver(server);
                try {
                    server.startServer();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        /*
        private class InterfaceIPComboBoxModel extends DefaultComboBoxModel<Network.InterfaceIP>{
            public InterfaceIPComboBoxModel(Network.InterfaceIP[] items) {
                super(items);
            }

            @Override
            public Job getSelectedItem() {
                Job selectedJob = (Job) super.getSelectedItem();

                // do something with this job before returning...

                return selectedJob;
            }
        }*/


    }
}