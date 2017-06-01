package ch.elmootan.client;

import ch.elmootan.core.sharedObjects.Game;
import ch.elmootan.core.sharedObjects.GameCreator;
import ch.elmootan.core.sharedObjects.Lobby;
import ch.elmootan.core.sharedObjects.Player;
import ch.elmootan.core.universe.Bonus;
import ch.elmootan.protocol.Protocol;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

public class Client implements Runnable
{

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    private final ObjectMapper mapper = new ObjectMapper();

    protected Socket socket;

    PrintWriter out;
    BufferedReader in;
    Player player;
    boolean connectionRunning = false;

    //débug
    boolean noGUI = true;

    static LobbyClient lobbyClient = null;

    private ClientMulticast clientMulticast;


    public String serverRead() throws IOException
    {
        return in.readLine();
    }

    public void serverWrite(String toWrite)
    {
        out.println(toWrite);
        out.flush();
    }

    public Client(Player player)
    {
        createClient(player,false);
    }

    public Client(Player player, boolean debug)
    {
        createClient(player,debug);
    }

    private void createClient(Player player, boolean debug)
    {
        try
        {
            clientMulticast = new ClientMulticast(Protocol.IP_MULTICAST, Protocol.PORT_UDP, InetAddress.getByName("localhost"));

            new Thread(clientMulticast).start();
        } catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        this.player = player;
        socket = new Socket();

        if(!debug)
        {
            lobbyClient = new LobbyClient();
            lobbyClient.showUI();
        }

        try
        {
            connect("localhost", Protocol.PORT);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void connect(String server, int port) throws IOException
    {

        if (socket != null)
            socket.close();

        socket = new Socket(server, port);

        try
        {
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e)
        {
            LOG.warning(e.toString());
        }
        LOG.info(serverRead());

        serverWrite(Protocol.PLANET_IO_HELLO);

        if (serverRead().equals(Protocol.PLANET_IO_SUCCESS))
        {
            LOG.info("Connexion successful");
            String gameListJSON = serverRead();
            ArrayList<Game> initialGameList = mapper.readValue(gameListJSON, new TypeReference<ArrayList<Game>>()
            {
            });

            System.out.println(gameListJSON);
            if(!noGUI)
            {
                lobbyClient.addGameList(initialGameList);
            }
        }

        new Thread(this).start();
    }

    public void run()
    {
        connectionRunning = true;
        while (connectionRunning)
        {
            //Tu ne peux pas faire communiquer simultanément cette thread et les commandes
            //si cette thread fait un read alors qu'une autre commande est en train d'être faite
            //alors cette thread nique le protocole x).
            //J'ai essayé de synchroniser mais ça bloque tout :(
            //Je recommande fortement d'utiliser de l'UDP pour refresh ton lobby ;) (ptit datagram vite fait :P)

//            synchronized(lobbyClient)
//            {
//                String input = "";
//                try
//                {
//                    input = serverRead();
//                    switch (input)
//                    {
//                        case Protocol.LOBBY_UPDATED:
//                            input = serverRead();
//                            Game newGame = mapper.readValue(input, Game.class);
//                            lobbyClient.addGame(newGame);
//                            break;
//
//                        default:
//                            break;
//
//                    }
//                } catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//            }
        }


    }

     public void disconnect() throws IOException
    {
        synchronized(lobbyClient)
        {
            serverWrite(Protocol.CMD_DISCONNECT);
            connectionRunning = false;
            out.close();
            in.close();
            socket.close();
        }
    }

    public boolean isConnected()
    {
        LOG.info(String.valueOf(socket.isConnected()));
        return socket.isConnected() && !socket.isClosed();
    }

    public void sendGameToServer(Game game)
    {
        synchronized(lobbyClient)
        {
            try
            {
                serverWrite(Protocol.CMD_CREATE_GAME);
                if (serverRead().equals(Protocol.PLANET_IO_SUCCESS))
                {
                    serverWrite(mapper.writeValueAsString(game));
                    if (serverRead().equals(Protocol.PLANET_IO_SUCCESS))
                    {
                        System.out.println("Game created");
                    } else
                    {
                        System.out.println("Error, game was not created");
                    }
                } else
                {
                    System.out.println("Error, game was not created");
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void exit()
    {
        try
        {
            if(!noGUI)
            {
                lobbyClient.dispose();
                disconnect();
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public static void addGameToLobby(Game game)
    {
        lobbyClient.addGame(game);
    }

    private class LobbyClient extends Lobby
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == addGameButton)
            {
                new GameCreator()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        if (e.getSource() == createGame)
                        {
                            Game newGame = new Game(gameName.getText(), null, Integer.parseInt(playerMax.getText()));

                            sendGameToServer(newGame);

                            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                        }
                    }
                };
            } else
            {
                int indexGame = table.getSelectedRow();
                if (indexGame != -1)
                {
                    gamesList.get(indexGame).join();
                }
            }
        }
    }
}
