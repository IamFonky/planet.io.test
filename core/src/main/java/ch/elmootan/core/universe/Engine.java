package ch.elmootan.core.universe;

import ch.elmootan.core.physics.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.*;

public class Engine {

   private final ArrayList<Body> allThings = new ArrayList<>();
   private double zoom = 500.0;

   private Planet clickedPlanet;
   private Planet myPlanet;
   private double myPlanetInitMass;
   private double nbClicks;
   private boolean mousePressed = false;

//   private boolean tadaam = false;

   public Engine() {
      ActionListener repaintLol = evt -> calculateBodies();
      javax.swing.Timer displayTimer = new javax.swing.Timer(10, repaintLol);
      displayTimer.start();
   }

   private void calculateBodies() {
      for (int i = 0; i < allThings.size(); ++i) {
         Body body = allThings.get(i);
         if (body != null) {
            for (int j = i + 1; j < allThings.size(); ++j) {
               Body surrounding = allThings.get(j);
               if (surrounding != null) {
                  double gTgDistance = 0;
                  double sqDistance = 0;
                  double dX = 0;
                  double dY = 0;
                  //On calcule les distances x et y et la distance au carré
                  synchronized (body) {
                     dX = surrounding.getPosition().getX() - body.getPosition().getX();
                     dY = surrounding.getPosition().getY() - body.getPosition().getY();
                     sqDistance = dX * dX + dY * dY;

                     //On calcule la distance réelle (Ground to ground)
                     gTgDistance = Math.sqrt(sqDistance) - body.getRadius() / 2 - surrounding.getRadius() / 2;
                  }

                  if (gTgDistance < 0) //Collision!
                  {
                     // Si la planète du joueur et la planète cliquée rentrent en collision, la planète cliquée disparait
                     // (on évite ainsi les valeurs limites).
                     if (body.getId() == surrounding.getId()) {
                        removePlanet(clickedPlanet);
                     }
                     // Sinon, si une des deux planète est une planète cliquée, saute cette comparaison car la planète
                     // est invisible et n'interragit pas avec celle des autres joueurs.
                     else if ((body instanceof InvisiblePlanet) || (surrounding instanceof InvisiblePlanet)) {
                        continue;
                     } else {
                        BodyState eatState;
                        synchronized (allThings) {
                           if (body.getMass() > surrounding.getMass()) {
                              eatState = body.eat(surrounding);
                              allThings.remove(surrounding);
                           } else {
                              eatState = surrounding.eat(body);
                              allThings.remove(body);
                           }

                           switch (eatState) {
                              case EXPLODE:
                                 explode(body);
                                 break;
                              //On peut imaginer ici un case SUN ou BLACKHOLE
                           }
                        }
                     }
                  }
                  // On applique la gravité et la physique uniquement dans 2 cas :
                  //   - Si c'est la planète cliquée et la planète du joueur.
                  //   - Si aucune des deux planète n'est une planète cliquée.
                  else if ((!(body instanceof InvisiblePlanet) && !(surrounding instanceof InvisiblePlanet)) ||
                          (body.getId() == surrounding.getId())) {
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
                     synchronized (body) {
                        body.getSpeed().setX(body.getSpeed().getX() + bodyAX);
                        body.getSpeed().setY(body.getSpeed().getY() + bodyAY);

                        surrounding.getSpeed().setX(surrounding.getSpeed().getX() + surrAX);
                        surrounding.getSpeed().setY(surrounding.getSpeed().getY() + surrAY);
                     }

                  }
               }
            }
         }

         synchronized (body) {
            body.getPosition().setX(body.getPosition().getX() + body.getSpeed().getX());
            body.getPosition().setY(body.getPosition().getY() + body.getSpeed().getY());
         }

         // Freinage des corps
         // body.speed.x -= 0.005 * body.speed.x;
         // body.speed.y -= 0.005 * body.speed.y;
      }
   }

   public void explode(Body body) {
      Random rand = new Random();
      double dThis = body.getMass() / (body.getRadius() * body.getRadius() * PI);
      double oldMass = body.getMass();


      while (body.getMass() > 0) {
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

         if (body.getMass() - fragMass < 0) {
            body.setMass(0);
         } else {
            body.setMass(body.getMass() - fragMass);
         }
      }
      allThings.remove(body);
//        hollySong("boom",0.001);
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

   public synchronized ArrayList<Body> getAllThings() {
      return allThings;
   }
}
