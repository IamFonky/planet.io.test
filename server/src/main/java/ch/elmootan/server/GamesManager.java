package ch.elmootan.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles currently running games.
 */
public class GamesManager {

    private static GamesManager sharedManager = null;

    private final List<String> gamesList;

    private GamesManager() {
        gamesList = new ArrayList<>();
    }

    public static synchronized GamesManager getSharedManager() {
        if (sharedManager == null) {
            sharedManager = new GamesManager();
        }

        return sharedManager;
    }

    public void addGame(String name) {
        if (!gamesList.contains(name)) {
            gamesList.add(name);
        }
    }

    public List<String> getGamesList() {
        return gamesList;
    }

}
