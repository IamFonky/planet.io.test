package ch.elmootan.server;

import ch.elmootan.core.sharedObjects.Game;
import ch.elmootan.core.sharedObjects.Lobby;
import ch.elmootan.protocol.Protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Observer {

    //! Logger.
    final static Logger LOG = Logger.getLogger(Server.class.getName());

    //! TCP listening port.
    private static final int LISTENING_PORT = Protocol.PORT;

    //! Server socket.
    private ServerSocket serverSocket;


    /*
     * The server maintains a list of client workers, so that they can be notified
     * when the server shuts down
     */
    List<ClientWorker> clientWorkers = new CopyOnWriteArrayList<>();

    /*
     * A flag that indicates whether the server should continue to run (or whether
     * a shutdown is in progress)
     */
    private boolean shouldRun = false;

    /**
     * Constructor used to create a server that will accept connections on a known
     * TCP port
     */
    public Server() {
        System.out.println("New server created!");
    }

    public void startServer() throws IOException {
        System.out.println("Server starting!");

        Lobby.getSharedInstance().setNbGamesMax(7);

        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(LISTENING_PORT));

        // Delegate work to a ClientWorker.
        Thread serverThread = new Thread(() -> {
            shouldRun = true;
            while (shouldRun) {
                try {
                    LOG.info("Listening for connections on port " + LISTENING_PORT);

                    Socket clientSocket = serverSocket.accept();
                    LOG.info("New client has arrived. Delegating work...");

                    ClientWorker worker = new ClientWorker(clientSocket, Server.this);
                    clientWorkers.add(worker);

                    Thread clientThread = new Thread(worker);
                    clientThread.start();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "IOException in main server thread, exit: {0}", ex.getMessage());
                    shouldRun = false;
                }
            }
        });
        serverThread.start();
    }

    /**
     * Indicates whether the server is accepting connection requests, by checking
     * the state of the server socket
     *
     * @return true if the server accepts client connection requests
     */
    public boolean isRunning() {
        return (serverSocket != null && serverSocket.isBound());
    }

    /**
     * Getter for the TCP port number used by the server socket.
     *
     * @return the port on which client connection requests are accepted
     */
    public int getPort() {
        return serverSocket != null ? serverSocket.getLocalPort() : LISTENING_PORT;
    }

    /**
     * Requests a server shutdown. This will close the server socket and notify
     * all client workers.
     *
     * @throws IOException If there was an error attempting to close socket.
     */
    public void stopServer() throws IOException {
        shouldRun = false;
        serverSocket.close();
        for (ClientWorker clientWorker : clientWorkers) {
            clientWorker.notifyServerShutdown();
        }
    }

    /**
     * This method is invoked by the client worker when it has completed its
     * interaction with the server (e.g. the user has issued the BYE command, the
     * connection has been closed, etc.)
     *
     * @param worker the worker which has completed its work
     */
    public void notifyClientWorkerDone(ClientWorker worker) {
        clientWorkers.remove(worker);
    }


    public void update(Observable o, Object obj)  {
        Game newGame = (Game) obj;
        for (ClientWorker clientWorker : clientWorkers) {
            clientWorker.sendLobbyUpdate(newGame);
        }
    }
}
