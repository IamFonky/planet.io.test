package ch.elmootan.server;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientWorker implements Runnable {

    private static final Logger LOG = Logger.getLogger(ClientWorker.class.getSimpleName());

    private ClientHandler handler = null;
    private Socket clientSocket = null;
    private InputStream is = null;
    private OutputStream os = null;
    private boolean done = false;
    private Server server = null;

    public ClientWorker(Socket clientSocket, Server server) throws IOException {
        this.clientSocket = clientSocket;
        this.handler = new ClientHandler();
        this.server = server;
        is = clientSocket.getInputStream();
        os = clientSocket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            handler.handleClientConnection(is, os);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Exception in client handler: {0}", ex.getMessage());
        } finally {
            done = true;
            server.notifyClientWorkerDone(this);
            try {
                clientSocket.close();
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }
            try {
                is.close();
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }
            try {
                os.close();
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }
        }
    }

    public boolean isDone() {
        return done;
    }

    public void notifyServerShutdown() {
        try {
            is.close();
        } catch (IOException ex) {
            LOG.log(Level.INFO, "Exception while closing input stream on the server: {0}", ex.getMessage());
        }

        try {
            os.close();
        } catch (IOException ex) {
            LOG.log(Level.INFO, "Exception while closing output stream on the server: {0}", ex.getMessage());
        }

        try {
            clientSocket.close();
        } catch (IOException ex) {
            LOG.log(Level.INFO, "Exception while closing socket on the server: {0}", ex.getMessage());
        }
    }
}
