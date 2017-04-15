package ch.elmootan.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server
{

   final static Logger LOG = Logger.getLogger(Server.class.getName());

   /*
    * The TCP port where client connection requests are accepted. -1 indicates that
    * we want to use an ephemeral port number, assigned by the OS
    */
   private int listenPort = -1;

   /*
    * The server socket, used to accept client connection requests
    */
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

   private String protocolVersion;

   /**
    * Constructor used to create a server that will accept connections on a known
    * TCP port
    *
    * @param listenPort the TCP port on which connection requests are accepted
    */
   public Server(int listenPort, String protocolVersion)
   {
      this.listenPort = listenPort;
      this.protocolVersion = protocolVersion;
   }

   /**
    * Constructor used to create a server that will accept connections on an
    * ephemeral port
    */
   public Server(String protocolVersion)
   {
      this.listenPort = 1313;
      this.protocolVersion = protocolVersion;
   }

   public void startServer() throws IOException
   {
      //Le code ci-dessous n'est pas propre cependant c'est la seule solution trouvée
      //comme workaround pour les problèmes de BindException (port already in use)
      //sur Linux.
      //Ici, on boucle tant que le port est utilisé (on assume donc que celui qui
      //lance ce serveur sait qu'il va attendre tant que le port n'est pas disponible)
      boolean succes = false;
      while (!succes)
      {
         try
         {
            if (serverSocket == null || serverSocket.isBound() == false)
            {
               if (listenPort == -1)
               {
                  bindOnEphemeralPort();
               }
               else
               {
                  bindOnKnownPort(listenPort);
               }
            }
            succes = true;
         }
         catch (BindException be)
         {
            succes = false;
         }
      }

      Thread serverThread = new Thread(new Runnable()
      {
         @Override
         public void run()
         {
            shouldRun = true;
            while (shouldRun)
            {
               try
               {
                  LOG.log(Level.INFO, "Listening for client connection on {0}", serverSocket.getLocalSocketAddress());
                  Socket clientSocket = serverSocket.accept();
                  LOG.info("New client has arrived...");
                  ClientWorker worker = new ClientWorker(clientSocket, Server.this);
                  clientWorkers.add(worker);
                  LOG.info("Delegating work to client worker...");
                  Thread clientThread = new Thread(worker);
                  clientThread.start();
               }
               catch (IOException ex)
               {
                  LOG.log(Level.SEVERE, "IOException in main server thread, exit: {0}", ex.getMessage());
                  shouldRun = false;
               }
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
   public boolean isRunning()
   {
      return (serverSocket.isBound());
   }

   /**
    * Getter for the TCP port number used by the server socket.
    *
    * @return the port on which client connection requests are accepted
    */
   public int getPort()
   {
//    return serverSocket.getLocalPort();
      return serverSocket != null ? serverSocket.getLocalPort() : listenPort;
   }

   /**
    * Requests a server shutdown. This will close the server socket and notify
    * all client workers.
    *
    * @throws IOException
    */
   public void stopServer() throws IOException
   {
      shouldRun = false;
      serverSocket.close();
      for (ClientWorker clientWorker : clientWorkers)
      {
         clientWorker.notifyServerShutdown();
      }
   }

   private void bindOnKnownPort(int port) throws IOException
   {
      serverSocket = new ServerSocket();
      serverSocket.setReuseAddress(true);
      serverSocket.bind(new InetSocketAddress(port));
   }

   private void bindOnEphemeralPort() throws IOException
   {
      serverSocket = new ServerSocket();
      serverSocket.setReuseAddress(true);
      serverSocket.bind(null);
      this.listenPort = serverSocket.getLocalPort();
   }

   /**
    * This method is invoked by the client worker when it has completed its
    * interaction with the server (e.g. the user has issued the BYE command, the
    * connection has been closed, etc.)
    *
    * @param worker the worker which has completed its work
    */
   public void notifyClientWorkerDone(ClientWorker worker)
   {
      clientWorkers.remove(worker);
   }

}
