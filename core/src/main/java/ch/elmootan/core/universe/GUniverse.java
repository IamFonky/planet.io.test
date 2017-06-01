package ch.elmootan.core.universe;

import ch.elmootan.core.physics.*;
import ch.elmootan.core.sharedObjects.GameCreator;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import static java.lang.Math.*;

//import com.zenjava.javafx.maven.plugin.*;

public class GUniverse extends JFrame {

   private final ArrayList<Body> allThings = new ArrayList<>();
   private double zoom = 500.0;

   private JPanel rootPane;

   private Planet clickedPlanet;
   private Planet myPlanet;
   private double myPlanetInitMass;
   private double nbClicks;
   private boolean mousePressed = false;

   private ArrayList<BufferedImage> planets = new ArrayList<>();
   private BufferedImage invisible;

//   private boolean tadaam = false;

   public GUniverse(PrintWriter wr, BufferedReader rd) {
      super("Mon univers");

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
            //A transformer de manière à send une commande
            //ex : CDM_SET_POSITION:<userID>\r\n{InvisiblePlanet:{JSONblabla}}
            if (clickedPlanet != null && mousePressed) {
               clickedPlanet.setMass(myPlanetInitMass * getControlForce(e));
               clickedPlanet.setPosition(convertXYToPosition(e.getX(), e.getY()));
            }
         }
      });

      addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            //A transformer de manière à send une commande
            //ex : CDM_SET_POSITION:<userID>\r\n{InvisiblePlanet:{JSONblabla}}
            if (clickedPlanet == null || !mousePressed) {
               generatePlanetFromClick(e.getX(), e.getY());
               myPlanetInitMass = clickedPlanet.getMass();
               clickedPlanet.setMass(myPlanetInitMass);
               //clickedPlanet.setRadius(clickedPlanet.getRadius()*nbClicks);
               mousePressed = true;
            }
         }

         @Override
         public void mouseReleased(MouseEvent e) {
            //A transformer de manière à send une commande
            //ex : CDM_REMOVE_PLANET:<userID>
            removePlanet(clickedPlanet);
            clickedPlanet = null;
            mousePressed = false;
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
               case ' ':
                  if (e.isShiftDown()) {
                     generateExactSameShit();
                  } else {
                     generateRandomShit();
                  }
                  break;
               case 'q':
                  generateMyPlanet();
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

               for (Body body : allThings) {
                  int radius = (int) (body.getRadius() / zoom);
                  int x = (getWidth() / 2) + ((int) ((body.getPosition().getX() - (body.getRadius() / 2)) / zoom));
                  int y = (getHeight() / 2) + ((int) ((body.getPosition().getY() - (body.getRadius() / 2)) / zoom));

                  if (InvisiblePlanet.class.isInstance(body)) {
                     g2d.drawImage(invisible.getScaledInstance(radius, radius, 0), x, y, this);
                  } else if (Planet.class.isInstance(body)) {
                     g2d.drawString(body.getName(), x - (body.getName().length() / 2) * 5 + radius / 2, y - 10);
                     g2d.drawImage(planets.get(((Planet) body).getIdSkin() - 1).getScaledInstance(radius, radius, 0), x, y, this);
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
            refreshBodies();
            rootPane.repaint();
         }
      };

      javax.swing.Timer displayTimer = new javax.swing.Timer(10, repaintLol);
      displayTimer.start();

      rootPane.setBackground(Color.BLACK);

      add(rootPane);

      setSize(1000, 1000);
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

   private Position convertXYToPosition(double x, double y) {
      double bodyX = ((x - (getWidth() / 2)) * zoom);
      double bodyY = ((y - (getHeight() / 2)) * zoom);
      return new Position(bodyX, bodyY);
   }

   private void generatePlanetFromClick(double x, double y) {
      double bodyRadius = 30000;
      InvisiblePlanet p = new InvisiblePlanet("Invisible", convertXYToPosition(x, y), 1E+24, bodyRadius, 1);

      clickedPlanet = addNewPlanet(p);
   }

   public Planet addNewPlanet(String name, double x, double y, double mass, double radius, int skin, int id) {
      Planet newP = new Planet(name, new Position(x, y), mass, radius, skin, id);
      allThings.add(newP);
      return newP;
   }

   public InvisiblePlanet addNewPlanet(InvisiblePlanet newP) {
      allThings.add(newP);
      return newP;
   }

   public void removePlanet(Planet planet) {
      allThings.remove(planet);
      planet = null;
   }

   private Fragment addNewFragment(String name, double x, double y, double mass, double radius, Color couleur) {
      Fragment newP = new Fragment(name, new Position(x, y), mass, radius, couleur);
      allThings.add(newP);
      return newP;
   }

   private void generateExactSameShit() {
      for (int i = 0; i < 25; ++i) {
         Random rand = new Random();
         double x = rand.nextDouble() * 400000 + -200000;
         Planet lune = this.addNewPlanet(
                 "Lune" + i,
                 x,
                 rand.nextDouble() * 400000 + -200000,
                 2E+21,
                 3000,
                 rand.nextInt(8) + 1,
                 (int) x);
         lune.setSpeed(new Speed(rand.nextDouble() * 1500 - 750,
                 rand.nextDouble() * 1500 - 750));

      }

   }

   private void generateRandomShit() {
      for (int i = 0; i < 1; ++i) {
         Random rand = new Random();
         Planet lune = this.addNewPlanet(
                 "Lune" + rand.nextInt(100) + 1,
                 rand.nextDouble() * 400000 + -200000,
                 rand.nextDouble() * 400000 + -200000,
                 rand.nextDouble() * 1E+22 + 1E+21,
                 rand.nextDouble() * 1000 + 4000,
                 rand.nextInt(8) + 1,
                 0);
         lune.setSpeed(new Speed(rand.nextDouble() * 100 - 50,
                 rand.nextDouble() * 100 - 50));
      }

   }

   private void generateMyPlanet() {
      Random rand = new Random();
      myPlanet = this.addNewPlanet(
              "GatiGato",
              rand.nextDouble() * 400000 + -200000,
              rand.nextDouble() * 400000 + -200000,
              rand.nextDouble() * 1E+22 + 1E+21,
              rand.nextDouble() * 1000 + 4000,
              rand.nextInt(8) + 1,
              1);
      myPlanet.setSpeed(new Speed(rand.nextDouble() * 100 - 50,
              rand.nextDouble() * 100 - 50));
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
}
