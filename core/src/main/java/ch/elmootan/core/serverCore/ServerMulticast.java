package ch.elmootan.core.serverCore;

import ch.elmootan.protocol.Protocol;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;


/**
 * @brief This class a multicast client.
 */
public class ServerMulticast implements Runnable {

    //! Logger for debugging.
    private static final Logger LOG = Logger.getLogger(ServerMulticast.class.getSimpleName());

    private final ObjectMapper mapper = new ObjectMapper();

    //! Port to use.
    private int port;

    //! Multicast address.
    private InetAddress multicastGroup;

    //! Socket to use.
    private MulticastSocket socket;

    //! Tells if the client is running.
    private boolean running;

    /**
     * @param multicastAddress Multicast address to use.
     * @param port             Port to use for the communication.
     * @param interfaceToUse   Network interface to use.
     * @brief ClientMulticast constructor.
     */
    public ServerMulticast(String multicastAddress, int port, InetAddress interfaceToUse) {
        this.port = port;
        this.running = false;

        try {
            socket = new MulticastSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.setInterface(interfaceToUse);
            socket.setLoopbackMode(false);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        try {
            multicastGroup = InetAddress.getByName(multicastAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            socket.joinGroup(multicastGroup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Stop the multicast client.
     */
    public void stop() {
        running = false;
        socket.close();
    }

    /**
     * @param message The message to send.
     * @brief Send a message by multicast.
     */
    public void send(String message) {

        // Transforms the message in bytes
        byte[] messageBytes = message.getBytes();

        // Prepare the packet
        DatagramPacket out = new DatagramPacket(messageBytes, messageBytes.length, multicastGroup, port);

        // Send the packet
        try {
            socket.send(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        running = true;

        // Get the response
        byte[] reponse = new byte[8192];
        DatagramPacket in = new DatagramPacket(reponse, reponse.length);

        String command;
        String input = null;

        while (running) {

            // Store the commands sent by the client
            ArrayList<String> commands = new ArrayList<>();

            try {

                socket.receive(in);

                byte[] rawData = in.getData();

                String data = new String(rawData).trim();

                BufferedReader reader = new BufferedReader(new StringReader(data));

                input = reader.readLine();

                while ((input != null) && !Objects.equals(input, Protocol.END_OF_COMMAND)) {
                    commands.add(input);
                    input = reader.readLine();
                }

                reader.close();

            } catch (SocketException e) {
                // Do nothing and continue
                continue;
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Get the requested command
            command = commands.remove(0);

            // Prepare the args to send to the controller
            ArrayList<Object> args = new ArrayList<>(commands);

            /*switch (command) {
                case Protocol.LOBBY_UPDATED:
                    try {
                        Game newGame = mapper.readValue((String) args.get(0), Game.class);
                        Client.
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }*/
        }
    }
}

