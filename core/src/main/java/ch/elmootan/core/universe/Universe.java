package ch.elmootan.core.universe;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ch.elmootan.core.Game;
import ch.elmootan.core.physics.*;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import static java.lang.Math.*;

public class Universe extends Frame
{

   private final ArrayList<Body> allThings = new ArrayList<>();
   private double zoom = 500.0;

//   private boolean tadaam = false;

   public Universe()
   {
      super("Mon univers");

      addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            generatePlanetFromClick(e.getX(), e.getY());
         }
      });

      addKeyListener(new KeyAdapter()
      {
         @Override
         public void keyTyped(KeyEvent e)
         {
            switch (e.getKeyChar())
            {
               case 's':
                  hollySong("starwars", 0.025);
                  break;
               case 'a':
                  zoom += zoom * 0.1;
                  break;
               case 'd':
                  zoom -= zoom * 0.1;
                  break;
               case 't':
                  addNewPlanet("Terre", 0, 0, 5.9736E23         , 6371, Color.green);
                  break;
               case ' ':
                  if (e.isShiftDown())
                  {
                     generateExactSameShit();
                  }
                  else
                  {
                     generateRandomShit();
                  }
            }
            System.out.println(e.getKeyChar());
         }
      });

      setSize(1000, 1000);
      setVisible(true);
      addWindowListener(new WindowAdapter()
                        {
                           public void windowClosing(WindowEvent e)
                           {
                              dispose();
                              System.exit(0);
                           }
                        }
      );

      Timer myTime = new Timer("myTime");

      myTime.schedule(new TimerTask()
      {

         @Override
         public void run()
         {
            for (int i = 0; i < allThings.size(); ++i)
            {
               Body body = allThings.get(i);
               if(body != null)
               {
                  for (int j = i + 1; j < allThings.size(); ++j)
                  {
                     Body surrounding = allThings.get(j);
                     if (surrounding != null)
                     {
                        double gTgDistance = 0;
                        double sqDistance = 0;
                        double dX = 0;
                        double dY = 0;
                        //On calcule les distances x et y et la distance au carré
                        synchronized (body)
                        {
                           dX = surrounding.getPosition().getX() - body.getPosition().getX();
                           dY = surrounding.getPosition().getY() - body.getPosition().getY();
                           sqDistance = dX * dX + dY * dY;

                           //On calcule la distance réelle (Ground to ground)
                           gTgDistance = Math.sqrt(sqDistance) - body.getRadius() / 2 - surrounding.getRadius() / 2;
                        }

                        if (gTgDistance < 0) //Collision!
                        {
                           BodyState eatState;
                           synchronized (allThings)
                           {
                              if (body.getMass() > surrounding.getMass())
                              {
                                 eatState = body.eat(surrounding);
                                 allThings.remove(surrounding);
                              }
                              else
                              {
                                 eatState = surrounding.eat(body);
                                 allThings.remove(body);
                              }

                              switch (eatState)
                              {
                                 case EXPLODE:
                                    explode(body);
                                    break;
                                 //On peut imaginer ici un case SUN ou BLACKHOLE
                              }
                           }
                        }
                        else //Gravitation et physique
                        {
                           //On calcule le ratio des composantes de distance x et y (règle de 3, Thalès)
                           double rDX = dX / Math.sqrt(sqDistance);
                           double rDY = dY / Math.sqrt(sqDistance);

                           //Loi de gravité : F [N] = G [N*m2*kg-2] * mA [kg] * mB [kg] / d2 [m2]
                           //un Newton = 1 [kg*m*s-2]. Plus simplement [kg*a] ou a est l'accélération
                           //donc G [kg*m*s-2*m2*kg-2] ==> G [m3*s-2*kg-1]
                           //On peut donc calculer simplement l'accélération aN sur chaque corps avec :
                           //aA [m*s-2] = G [m3*s-2*kg-1] * mB [kg] / d2 [m2]
                           double bodyA = Constants.GRAVITATION.valeur * surrounding.getMass() / sqDistance;
                           //A cette étape nous avons un vecteur d'accélération mais pas de direction
                           //Il décomposer en deux composantes x et y
                           double bodyAX = bodyA * rDX;
                           double bodyAY = bodyA * rDY;

                           //Même chose pour les deuxième corps
                           double surrA = Constants.GRAVITATION.valeur * body.getMass() / sqDistance;
                           double surrAX = surrA * -rDX;
                           double surrAY = surrA * -rDY;

                           //Il faut maintenant appliquer accélérations x et y aux vitesses x et y
                           //Pour le moment la cadence du processeur règle la vitesse du programme
                           synchronized (body)
                           {
                              body.getSpeed().setX(body.getSpeed().getX() + bodyAX);
                              body.getSpeed().setY(body.getSpeed().getY() + bodyAY);

                              surrounding.getSpeed().setX(surrounding.getSpeed().getX() + surrAX);
                              surrounding.getSpeed().setY(surrounding.getSpeed().getY() + surrAY);
                           }

                        }
                     }
                  }
               }

               synchronized (body)
               {
                  body.getPosition().setX(body.getPosition().getX() + body.getSpeed().getX());
                  body.getPosition().setY(body.getPosition().getY() + body.getSpeed().getY());
               }

               // Freinage des corps
               // body.speed.x -= 0.005 * body.speed.x;
               // body.speed.y -= 0.005 * body.speed.y;
            }

            res();
         }

      }, 0, 33);
   }

   private void explode(Body body)
   {
      Random rand = new Random();
      double dThis = body.getMass() / (body.getRadius() * body.getRadius() * PI);
      double oldMass = body.getMass();


      while (body.getMass() > 0)
      {
         double fragMass = oldMass * rand.nextDouble() / 2;
         double fragRadius = sqrt(fragMass / (dThis * PI));
         Body frag = addNewFragment(
                 "FRAG" + body.getName(),
                 body.getPosition().getX() + rand.nextDouble() * body.getRadius() * 10 - 5,
                 body.getPosition().getY() + rand.nextDouble() * body.getRadius() * 10 - 5,
                 fragMass,
                 fragRadius,
                 Color.RED
         );

         double newDirection = rand.nextDouble() * 2 * PI;
         double newVX = cos(newDirection) * body.getSpeed().getSpeed();
         double newVY = sin(newDirection) * body.getSpeed().getSpeed();

         frag.setSpeed(new Speed(newVX, newVY));

         if (body.getMass() - fragMass < 0)
         {
            body.setMass(0);
         }
         else
         {
            body.setMass(body.getMass() - fragMass);
         }
      }
      allThings.remove(body);
//        hollySong("boom",0.001);
   }

   public void paint(Graphics g)
   {
      synchronized (allThings)
      {
         for (Body body : allThings)
         {
            int radius = (int) (body.getRadius() / zoom);
            int x = (getWidth() / 2) + ((int) ((body.getPosition().getX() - (body.getRadius() / 2)) / zoom));
            int y = (getHeight() / 2) + ((int) ((body.getPosition().getY() - (body.getRadius() / 2)) / zoom));
            g.setColor(body.getCouleur());

            if (Planet.class.isInstance(body))
            {
               g.drawOval(x, y, radius, radius);
            }
            else if (Fragment.class.isInstance(body))
            {
               g.drawRect(x, y, radius, radius);
            }
         }
      }
   }

   private void generatePlanetFromClick(double x, double y)
   {
      Random rand = new Random();
      double bodyRadius = rand.nextDouble() * 1000 + 4000;
      double bodyX = ((x - (getWidth() / 2)) * zoom);
      double bodyY = ((y - (getHeight() / 2)) * zoom);
      addNewPlanet("Click planet", bodyX, bodyY, rand.nextDouble() * 1E+22 + 1E+21, bodyRadius, new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
   }

   private Planet addNewPlanet(String name, double x, double y, double mass, double radius, Color couleur)
   {
      Planet newP = new Planet(name, new Position(x, y), mass, radius, couleur);
      allThings.add(newP);
      return newP;
   }

   private Fragment addNewFragment(String name, double x, double y, double mass, double radius, Color couleur)
   {
      Fragment newP = new Fragment(name, new Position(x, y), mass, radius, couleur);
      allThings.add(newP);
      return newP;
   }


   private void res()
   {
      invalidate();
      validate();
      repaint();
   }


   private void generateExactSameShit()
   {
      for (int i = 0; i < 25; ++i)
      {
         Random rand = new Random();
         Planet lune = this.addNewPlanet(
                 "Lune" + i,
                 rand.nextDouble() * 400000 + -200000,
                 rand.nextDouble() * 400000 + -200000,
                 2E+21,
                 3000,
                 new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
         lune.setSpeed(new Speed(rand.nextDouble() * 1500 - 750,
                 rand.nextDouble() * 1500 - 750));

      }

   }

   private void generateRandomShit()
   {
      for (int i = 0; i < 25; ++i)
      {
         Random rand = new Random();
         Planet lune = this.addNewPlanet(
                 "Lune" + i,
                 rand.nextDouble() * 400000 + -200000,
                 rand.nextDouble() * 400000 + -200000,
                 rand.nextDouble() * 1E+22 + 1E+21,
                 rand.nextDouble() * 1000 + 4000,
                 new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
         lune.setSpeed(new Speed(rand.nextDouble() * 1000 - 500,
                 rand.nextDouble() * 1000 - 500));
      }

   }

   private void hollySong(String soundFile, double intiVolume)
   {
      final String sound = soundFile;
      final double volume = intiVolume;

      (new Runnable()
      {
         @Override
         public void run()
         {
            new JFXPanel();
            System.out.println(System.getProperty("user.dir"));
            String bip = "sounds/" + sound + ".mp3";

//         File file = new File(bip);

            Media hit = new Media(Game.class.getResource(bip).toString());
            MediaPlayer mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.setVolume(volume);
            mediaPlayer.play();
         }
      }).run();

   }
}
