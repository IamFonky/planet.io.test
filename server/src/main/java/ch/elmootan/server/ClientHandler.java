package ch.elmootan.server;

import ch.elmootan.protocol.Protocol;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler {

    final static Logger LOG = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler() {
    }

    public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

        String command;
        boolean done = false;
        while (!done && ((command = reader.readLine()) != null)) {
            LOG.log(Level.INFO, "COMMAND: {0}", command);
            switch (command.toUpperCase()) {

                // Client wants to disconnect.
                case Protocol.CMD_DISCONNECT: {
                    done = true;
                    writer.println(Protocol.CMD_SUCCESS);
                }

                // Client wants to connect.
                case Protocol.CMD_HELLO: {
                    writer.println(Protocol.CMD_SUCCESS);
                }
            }
            writer.flush();
        }

    }

}
