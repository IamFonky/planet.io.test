package ch.elmootan.client;

import ch.elmootan.core.physics.Body;
import ch.elmootan.core.sharedObjects.Game;
import ch.elmootan.protocol.Protocol;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;


/**
 * @brief This class a multicast client.
 */
public class ClientMulticast implements Runnable {

    //! Logger for debugging.
    private static final Logger LOG = Logger.getLogger(ClientMulticast.class.getSimpleName());

    private final ObjectMapper mapper = new ObjectMapper();

    //! Port to use.
    private int port;

    //! Multicast address.
    private InetAddress multicastGroup;

    public MulticastSocket getSocket() {
        return socket;
    }

    //! Socket to use.
    private MulticastSocket socket;

    //! Tells if the client is running.
    private boolean running;

    private static class ColorDeserializer extends JsonDeserializer<Color> {
        @Override
        public Color deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            TreeNode root = p.getCodec().readTree(p);
            TextNode rgba = (TextNode) root.get("argb");
            return new Color(Integer.parseUnsignedInt(rgba.textValue(), 16), true);
        }
    }

    /**
     * @param multicastAddress Multicast address to use.
     * @param port             Port to use for the communication.
     * @param interfaceToUse   Network interface to use.
     * @brief ClientMulticast constructor.
     */
    public ClientMulticast(String multicastAddress, int port, InetAddress interfaceToUse) {
        this.port = port;
        this.running = false;

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Color.class, new ColorDeserializer());

        mapper.registerModule(module);

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
            ArrayList<String> args = new ArrayList<>(commands);

            switch (command) {
                case Protocol.LOBBY_UPDATED:
                    /*try {
                        Game newGame = mapper.readValue(args.get(0), Game.class);
                        Client.addGameToLobby(newGame);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    System.out.println("UPDATE CLIENT");
                    String gameListJSON = args.get(0);
                    try {
                        ArrayList<Game> newGameList = mapper.readValue(gameListJSON, new TypeReference<ArrayList<Game>>() {
                        });
                        //System.out.println(newGameList);
                        Client.lobby.refreshGameList(newGameList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                case Protocol.GAME_UPDATE:
                    if (args.size() > 1) {
                        if (Client.currentGame != null && Integer.parseInt(args.get(0)) == Client.currentGame.getGameId()) {
                            try {
                                ArrayList<Body> bodies = mapper.readValue(args.get(1), new TypeReference<List<Body>>(){});

                                Client.updateGUniverse(bodies);
                            }
                            catch (IOException ioe)
                            {
                                ioe.printStackTrace();
                            }
                        }
                    }
            }
        }
    }
}

