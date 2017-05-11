package ch.elmootan.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {
    private static final Logger LOG = Logger.getLogger(Test.class.getSimpleName());

    public static void main(String... args) {
        Server server = new Server();

        try {
            server.startServer();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
}
