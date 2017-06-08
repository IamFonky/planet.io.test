package ch.elmootan.server;

import ch.elmootan.core.serverCore.ServerMulticast;
import ch.elmootan.core.serverCore.Engine;
import ch.elmootan.protocol.Protocol;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles currently running games.
 */
public class GamesManager {

    private static GamesManager sharedManager = null;

    private final List<String> gamesList;
    private final List<Engine> engineList;

    private ServerMulticast udpServer;

    private GamesManager(ServerMulticast udpServer) {
        this.udpServer = udpServer;
        gamesList = new ArrayList<>();
        engineList = new ArrayList<>();
    }

    public static synchronized GamesManager getSharedManager(ServerMulticast udpServer) {
        if (sharedManager == null) {
            sharedManager = new GamesManager(udpServer);
        }
        else
        {
            sharedManager.udpServer = udpServer;
        }
        return sharedManager;
    }

    public static synchronized GamesManager getSharedManager() {
        if (sharedManager == null) {
            try
            {
                sharedManager = new GamesManager(new ServerMulticast(
                      Protocol.IP_MULTICAST,
                      Protocol.PORT,
                      InetAddress.getByName("localhost")
                ));
            }
            catch (UnknownHostException uhe)
            {
                uhe.printStackTrace();
            }
        }

        return sharedManager;
    }


    public void addGame(String name) {
//        if (!gamesList.contains(name)) {
//            gamesList.add(name);
//            engineList.add(new Engine());
//        }
    }

    public List<String> getGamesList() {
        return gamesList;
    }

}
