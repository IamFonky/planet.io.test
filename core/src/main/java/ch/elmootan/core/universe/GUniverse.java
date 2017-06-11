package ch.elmootan.core.universe;

import ch.elmootan.core.physics.*;
import ch.elmootan.core.sharedObjects.GameCreator;
import ch.elmootan.protocol.Protocol;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import oracle.jrockit.jfr.JFR;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import static java.lang.Math.*;

//import com.zenjava.javafx.maven.plugin.*;

public class GUniverse extends JFrame {

   private final ArrayList<Body> allThings = new ArrayList<>();
   private double zoom = 500.0;

   private TheEndFrame theEndFrame = new TheEndFrame();
   private JPanel rootPane;
   private final double controlPlanetMass = 1E+24;
   private InvisiblePlanet clickedPlanet;
   private Planet myPlanet;
   private double myPlanetInitMass;
   private boolean mousePressed = false;

   private ArrayList<BufferedImage> planets = new ArrayList<>();
   private BufferedImage invisible;

   private BufferedReader rd;
   private PrintWriter wr;

   private int gameId;
   private boolean playerAlive = true;
   private boolean playerIsWatching = false;
   private boolean playerWantQuit = false;

   private final ObjectMapper mapper = new ObjectMapper();

//   private boolean tadaam = false;

   public GUniverse(PrintWriter wr, BufferedReader rd, MulticastSocket udpSocket, int gameId, Planet myPlanet) {
      super("Mon univers");
      this.rd = rd;
      this.wr = wr;
      this.gameId = gameId;
      this.myPlanet = myPlanet;

      try {
         for (int i = 1; i <= 8; i++)
            planets.add(ImageIO.read(GUniverse.class.getResource("../skins/planet" + i + "_32x32.png")));
         invisible = ImageIO.read(GUniverse.class.getResource("../skins/invisible_64x64.png"));
      } catch (IOException e) {
         e.printStackTrace();
      }

      addMouseMotionListener(new MouseMotionAdapter() {

         @Override
         public void mouseDragged(MouseEvent e) {
            if (!playerIsWatching && clickedPlanet != null && mousePressed) {
               setInvisiblePlanet(e);
            }
         }
      });

      addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            if (!playerIsWatching && (clickedPlanet == null || !mousePressed)) {
               createInvisiblePlanet(e);
            }
         }

         @Override
         public void mouseReleased(MouseEvent e) {
            if (clickedPlanet != null && mousePressed) {
               killInvisiblePlanet(e);
            }
         }
      });

      addKeyListener(new KeyAdapter() {
         @Override
         public void keyTyped(KeyEvent e) {
            switch (e.getKeyChar()) {
               case 's':
                  hollySong("starwars", 0.025);
                  break;
               case 'a':
                  zoom += zoom * 0.1;
                  break;
               case 'd':
                  zoom -= zoom * 0.1;
                  break;
               default:
                  break;
            }
            System.out.println(e.getKeyChar());
         }
      });

      setSize(1000, 1000);
      setVisible(true);
      addWindowListener(new WindowAdapter() {
                           public void windowClosing(WindowEvent e) {
                              dispose();
                              System.exit(0);
                           }
                        }
      );

      rootPane = new JPanel() {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g.setColor(Color.WHITE);
            synchronized (allThings) {
               allThings.sort(Comparator.comparingDouble(Body::getRadius));
               Collections.reverse(allThings);

               g2d.drawString("Top bitches:", 0, 10);

               int nbScores = allThings.size() > 5 ? 5 : allThings.size();
               for (int i = 0; i < nbScores; i++) {
                  g2d.drawString(allThings.get(i).getName() + " : " + (int) allThings.get(i).getRadius(), 0, 15 * (i + 2));
               }
               playerAlive = false;
               for (Body body : allThings) {
                  if(!playerAlive && body.getId() == myPlanet.getId())
                  {
                     playerAlive = true;
                  }
                  int radius = (int) (body.getRadius() / zoom);
                  radius = radius > 0 ? radius : 1;
                  int x = (getWidth() / 2) + ((int) ((body.getPosition().getX() - (body.getRadius() / 2)) / zoom));
                  int y = (getHeight() / 2) + ((int) ((body.getPosition().getY() - (body.getRadius() / 2)) / zoom));

                  if (InvisiblePlanet.class.isInstance(body)) {
                     g2d.drawImage(invisible.getScaledInstance(radius, radius, 0), x, y, this);
                  } else if (Planet.class.isInstance(body)) {
                     g2d.drawString(body.getName(), x - (body.getName().length() / 2) * 5 + radius / 2, y - 10);
                     g2d.drawImage(planets.get(((Planet) body).getIdSkin()).getScaledInstance(radius, radius, 0), x, y, this);
                  } else if (Fragment.class.isInstance(body)) {
                     g.drawRect(x, y, radius, radius);
                  }
               }
            }
         }
      };

      //ScorePane scorePane = new ScorePane();
      // rootPane.add(scorePane);

      ActionListener repaintLol = new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            //scorePane.setScores();
            rootPane.repaint();
            if(!playerAlive && !playerIsWatching && !playerWantQuit)
            {
               theEndFrame.setVisible(true);
            }
            else if(playerWantQuit)
            {
               setVisible(false);
               dispose();
            }
         }
      };

      javax.swing.Timer displayTimer = new javax.swing.Timer(10, repaintLol);
      displayTimer.start();

      rootPane.setBackground(Color.BLACK);

      add(rootPane);

      setSize(1000, 1000);
      setVisible(true);
   }

   public void showUI() {
      setVisible(true);
   }

   private void refreshBodies()
   {
      //WAIT FOR UNIVERSE INFOS
   }

   private int getControlForce(MouseEvent e) {
      if (e.isShiftDown() && e.isControlDown()) {
         return 10;
      } else if (e.isShiftDown()) {
         return 2;
      } else if (e.isControlDown()) {
         return 3;
      } else {
         return 1;
      }
   }

   private boolean createInvisiblePlanet(MouseEvent e) {
      try
      {
         wr.println(Protocol.PLANET_IO_CREATE_PLANET + Protocol.CMD_SEPARATOR + gameId);
         wr.flush();
         if(rd.readLine().equals(Protocol.PLANET_IO_SUCCESS))
         {
            generatePlanetFromClick(e.getX(), e.getY());
            clickedPlanet.setMass(controlPlanetMass*getControlForce(e));
            //clickedPlanet.setRadius(clickedPlanet.getRadius()*nbClicks);
            mousePressed = true;
            wr.println(mapper.writeValueAsString(clickedPlanet));
            wr.flush();
            String pressedPlanet = rd.readLine();
            if(!pressedPlanet.equals(Protocol.PLANET_IO_FAILURE))
            {
               clickedPlanet = mapper.readValue(pressedPlanet,InvisiblePlanet.class);
               mousePressed = true;
            }
            else
            {
               clickedPlanet = null;
            }
         }
      }
      catch (IOException ioe)
      {
         ioe.printStackTrace();
         return false;
      }
      return false;
   }

   private boolean setInvisiblePlanet(MouseEvent e) {
         try {
            mousePressed = false;
            //Demande des modification de la planete au serveur
            wr.println(Protocol.PLANET_IO_SET_PLANET + Protocol.CMD_SEPARATOR + gameId);
            wr.flush();
            //Vérification si la demande est valable
            if (rd.readLine().equals(Protocol.PLANET_IO_SUCCESS)) {
               //Récupération de la masse de la planete cliquée
               clickedPlanet.setMass(controlPlanetMass*getControlForce(e));
               clickedPlanet.setPosition(convertXYToPosition(e.getX(), e.getY()));

               wr.println(mapper.writeValueAsString(clickedPlanet));
               wr.flush();
               if (rd.readLine().equals(Protocol.PLANET_IO_SUCCESS)) {
                  mousePressed = true;
               } else {
                  clickedPlanet = null;
               }
            }
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
      return mousePressed;
   }

   private boolean killInvisiblePlanet(MouseEvent e) {
         try {
            wr.println(Protocol.PLANET_IO_KILL_PLANET + Protocol.CMD_SEPARATOR + gameId);
            wr.flush();
            if (rd.readLine().equals(Protocol.PLANET_IO_SUCCESS)) {
               wr.println(mapper.writeValueAsString(clickedPlanet));
               wr.flush();
               mousePressed = false;
               clickedPlanet = null;
            }
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
      return mousePressed;
   }

   private Position convertXYToPosition(double x, double y) {
      double bodyX = ((x - (getWidth() / 2)) * zoom);
      double bodyY = ((y - (getHeight() / 2)) * zoom);
      return new Position(bodyX, bodyY);
   }

   private void generatePlanetFromClick(double x, double y) {
      double bodyRadius = 30000;
      clickedPlanet = new InvisiblePlanet(
              "Invisible-" + myPlanet.getName(),
              convertXYToPosition(x, y),
            controlPlanetMass,
              bodyRadius,
              myPlanet.getId());
   }

   private void hollySong(String soundFile, double intiVolume) {
      final String sound = soundFile;
      final double volume = intiVolume;

      (new Runnable() {
         @Override
         public void run() {
            new JFXPanel();
            System.out.println(System.getProperty("user.dir"));
            String bip = "sounds/" + sound + ".mp3";

//         File file = new File(bip);

            Media hit = new Media(GameCreator.class.getResource(bip).toString());
            MediaPlayer mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.setVolume(volume);
            mediaPlayer.play();
         }
      }).run();

   }

   public synchronized ArrayList<Body> getAllThings() {
      return allThings;
   }
   public void setAllThings(ArrayList<Body> bodies) {
      synchronized (allThings) {
         allThings.clear();
         allThings.addAll(bodies);
      }
   }

   public class TheEndFrame extends JFrame {
      private TheEndFrame()
      {
         super();
         setSize(500,100);
         JPanel theEndPannel = new JPanel();
         JLabel theEndLabel = new JLabel();
         JButton theEndWatch = new JButton();
         JButton theEndQuit = new JButton();
         setTitle("This is the end, my friend.");


         //End label
         theEndLabel.setText("Sorry bro, you died...");

         //Watch button
         theEndWatch.setText("Keep watching");
         theEndWatch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               playerIsWatching = true;
               setVisible(false);
            }
         });

         //Quit button
         theEndQuit.setText("Go to lobby");
         theEndQuit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               playerWantQuit = true;
               setVisible(false);
            }
         });


         //Adding content to pannel
         theEndPannel.add(theEndLabel);
         theEndPannel.add(theEndWatch);
         theEndPannel.add(theEndQuit);
         add(theEndPannel);
      }
   }
}
