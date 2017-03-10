import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Fonky on 20.02.2017.
 */
public class Espace extends Frame
{
   public static class Speed
   {
      public double getX()
      {
         return x;
      }

      public void setX(double x)
      {
         this.x = x;
      }

      public double getY()
      {
         return y;
      }

      public void setY(double y)
      {
         this.y = y;
      }

      double x;
      double y;

      public Speed(double x, double y)
      {
         this.x = x;
         this.y = y;
      }
   }

   public static class Position
   {
      Position(double x, double y)
      {
         this.x = x;
         this.y = y;
      }

      public double getX()
      {
         return x;
      }

      public void setX(double x)
      {
         this.x = x;
      }

      public double getY()
      {
         return y;
      }

      public void setY(double y)
      {
         this.y = y;
      }

      double x, y;
   }

   public abstract class Body
   {
      public Position getPosition()
      {
         return position;
      }

      public void setPosition(Position position)
      {
         this.position = position;
      }

      public double getmass()
      {
         return mass;
      }

      public void setmass(double mass)
      {
         this.mass = mass;
      }

      public double getRadius()
      {
         return radius;
      }

      public void setRadius(double radius)
      {
         this.radius = radius;
      }

      public Body getOrbiting()
      {
         return orbiting;
      }

      public void setOrbiting(Body orbiting)
      {
         this.orbiting = orbiting;
      }

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

      public Color getCouleur()
      {
         return couleur;
      }

      public void setCouleur(Color couleur)
      {
         this.couleur = couleur;
      }

      public Speed getSpeed()
      {
         return speed;
      }

      public void setSpeed(Speed speed)
      {
         this.speed = speed;
      }

      Position position;
      double mass;
      double radius;
      Body orbiting;
      String name;
      Color couleur;
      Speed speed = new Speed(0,0);

      public void eat(Body meal)
      {
         this.mass += meal.mass;
         this.radius = Math.sqrt((meal.radius * meal.radius * Math.PI + this.radius *  this.radius * Math.PI)/Math.PI);
         allThings.remove(meal);
      }


      public Body(String name, Position position, double mass, double radius, Color couleur)
      {
         this.name = name;
         this.position = position;
         this.mass = mass;
         this.radius = radius;
         this.couleur = couleur;
      }
   }

   public class Planet extends Body
   {
      public Planet(String name, double mass, double radius)
      {
         super(name, new Position(0,0),mass,radius,Color.BLACK);
      }

      public Planet(String name, double mass, double radius, Color couleur)
      {
         super(name, new Position(0,0),mass,radius,couleur);
      }

      public Planet(String name, Position position, double mass, double radius, Color couleur)
      {
         super(name, position,mass,radius,couleur);
      }
   }

   public ArrayList<Body> allThings = new ArrayList<>();
   public double zoom = 500.0;


   final TimerTask refresher = new TimerTask()
   {
      @Override
      public void run()
      {
         for(int i = 0; i < allThings.size(); ++i)
         {
            Body body = allThings.get(i);
            for(int j = i + 1; j < allThings.size(); ++j)
            {
               Body surrounding = allThings.get(j);

               //On calcule les distances x et y et la distance au carré
               double dX = surrounding.position.x - body.position.x;
               double dY = surrounding.position.y - body.position.y;
               double sqDistance = dX * dX + dY * dY;

               //On calcule la distance réelle (Ground to ground)
               double gTgDistance = Math.sqrt(sqDistance) - body.radius/2 - surrounding.radius/2;

               if(gTgDistance < 0) //Collision!
               {
                  if(body.mass > surrounding.mass)
                  {
                     body.eat(surrounding);
                  }
                  else
                  {
                     surrounding.eat(body);
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
                  double bodyA = Constantes.GRAVITATION.valeur * surrounding.mass / sqDistance;
                  //A cette étape nous avons un vecteur d'accélération mais pas de direction
                  //Il décomposer en deux composantes x et y
                  double bodyAX = bodyA * rDX;
                  double bodyAY = bodyA * rDY;

                  //Même chose pour les deuxième corps
                  double surrA = Constantes.GRAVITATION.valeur * body.mass / sqDistance;
                  double surrAX = surrA * -rDX;
                  double surrAY = surrA * -rDY;

                  //Il faut maintenant appliquer accélérations x et y aux vitesses x et y
                  //Pour le moment la cadence du processeur règle la vitesse du programme
                  body.speed.x += bodyAX;
                  body.speed.y += bodyAY;
                  surrounding.speed.x += surrAX;
                  surrounding.speed.y += surrAY;

               }
            }
            body.position.x += body.speed.x;
            body.position.y += body.speed.y;

            //Freinage des corps
//            body.speed.x -= 0.005 * body.speed.x;
//            body.speed.y -= 0.005 * body.speed.y;
         }
         res();
      }
   };

   public Espace()
   {
      super("Mon univers");

      addKeyListener(new KeyAdapter() {
         @Override
         public void keyTyped(KeyEvent e)
         {
            switch (e.getKeyChar())
            {
               case 'a':
                  zoom += zoom * 0.1;
                  break;
               case 'd':
                  zoom -= zoom * 0.1;
                  break;
               case ' ':
                  generateShit();
                  break;
            }
            System.out.println(e.getKeyChar());
         }
      });

      setSize(1000,1000);
      setVisible(true);
      addWindowListener(new WindowAdapter()
                        {public void windowClosing(WindowEvent e)
                        {dispose(); System.exit(0);}
                        }
      );

      Timer myTime = new Timer("myTime");

      myTime.schedule(refresher,0,33);

   }

   public void paint(Graphics g) {
      
      for(Body body : allThings)
      {
         int radius = (int)(body.radius/zoom);
         int x = (getWidth()/2) + ((int)((body.position.x - (body.radius/2))/zoom));
         int y = (getHeight()/2) + ((int)((body.position.y - (body.radius/2))/zoom));
         g.setColor(body.couleur);
         g.drawOval(x,y,radius,radius);
      }
   }

   public Planet addNewPlanet(String name, double mass, double radius)
   {
      return addNewPlanet(name,0,0,mass,radius,Color.BLACK);
   }

   public Planet addNewPlanet(String name, double x, double y, double mass, double radius, Color couleur)
   {
      Planet newP = new Planet(name,new Position(x,y),mass,radius,couleur);
      allThings.add(newP);
      return newP;
   }

   public void res()
   {
      invalidate();
      validate();
      repaint();
   }

   public void generateShit()
   {
      for(int i = 0; i < 25; ++i)
      {
         Random rand = new Random();
         Espace.Planet lune = this.addNewPlanet("Lune" + i, rand.nextDouble()*100000 + -50000, rand.nextDouble()*100000 + -50000,  rand.nextDouble()*1E+22 + 1E+21,  rand.nextDouble()*1000 + 4000, Color.darkGray);
//         lune.speed.setX(i*10);
      }

   }
}
