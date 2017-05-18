package ch.elmootan.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    protected Socket socket;

    PrintWriter out;
    BufferedReader in;


    public String serverRead() throws IOException {
        return in.readLine();
    }

    public void serverWrite(String toWrite) {
        out.println(toWrite);
        out.flush();
    }

    public Client() {
        socket = new Socket();
    }

    public void connect(String server, int port) throws IOException {

        socket = new Socket(server, port);

        try {
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            LOG.warning(e.toString());
        }
        LOG.info(serverRead());
    }

    // Todo: the whole shit that a client should do

    public void disconnect() throws IOException {
        // Todo: send the QUIT sequence to the server
        out.close();
        in.close();
        socket.close();
    }

    public boolean isConnected() {
        LOG.info(String.valueOf(socket.isConnected()));
        return socket.isConnected() && !socket.isClosed();
    }

}
