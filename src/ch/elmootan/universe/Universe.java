package ch.elmootan.universe;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ch.elmootan.physics.*;

public class Universe extends Frame {

    public ArrayList<Body> allThings = new ArrayList<>();
    public double zoom = 500.0;

    public Universe() {
        super("Mon univers");

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                switch (e.getKeyChar()) {
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

        setSize(1000, 1000);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent e) {
                                  dispose();
                                  System.exit(0);
                              }
                          }
        );

        Timer myTime = new Timer("myTime");

        myTime.schedule(new TimerTask() {

            @Override
            public void run() {
                for (int i = 0; i < allThings.size(); ++i) {
                    Body body = allThings.get(i);
                    for (int j = i + 1; j < allThings.size(); ++j) {
                        Body surrounding = allThings.get(j);

                        //On calcule les distances x et y et la distance au carré
                        double dX = surrounding.getPosition().getX() - body.getPosition().getX();
                        double dY = surrounding.getPosition().getY() - body.getPosition().getY();
                        double sqDistance = dX * dX + dY * dY;

                        //On calcule la distance réelle (Ground to ground)
                        double gTgDistance = Math.sqrt(sqDistance) - body.getRadius() / 2 - surrounding.getRadius() / 2;

                        if (gTgDistance < 0) //Collision!
                        {
                            if (body.getMass() > surrounding.getMass()) {
                                body.eat(surrounding);
                                allThings.remove(surrounding);
                            } else {
                                surrounding.eat(body);
                                allThings.remove(body);
                            }
                        } else //Gravitation et physique
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
                            body.getSpeed().setX(body.getSpeed().getX() + bodyAX);
                            body.getSpeed().setY(body.getSpeed().getY() + bodyAY);

                            surrounding.getSpeed().setX(surrounding.getSpeed().getX() + surrAX);
                            surrounding.getSpeed().setY(surrounding.getSpeed().getY() + surrAY);

                        }
                    }

                    body.getPosition().setX(body.getPosition().getX() + body.getSpeed().getX());
                    body.getPosition().setY(body.getPosition().getY() + body.getSpeed().getY());

                    // Freinage des corps
                    // body.speed.x -= 0.005 * body.speed.x;
                    // body.speed.y -= 0.005 * body.speed.y;
                }

                res();
            }

        }, 0, 33);
    }

    public void paint(Graphics g) {

        for (Body body : allThings) {
            int radius = (int) (body.getRadius() / zoom);
            int x = (getWidth() / 2) + ((int) ((body.getPosition().getX() - (body.getRadius() / 2)) / zoom));
            int y = (getHeight() / 2) + ((int) ((body.getPosition().getY() - (body.getRadius() / 2)) / zoom));
            g.setColor(body.getCouleur());
            g.drawOval(x, y, radius, radius);
        }
    }

    public Planet addNewPlanet(String name, double mass, double radius) {
        return addNewPlanet(name, 0, 0, mass, radius, Color.BLACK);
    }

    public Planet addNewPlanet(String name, double x, double y, double mass, double radius, Color couleur) {
        Planet newP = new Planet(name, new Position(x, y), mass, radius, couleur);
        allThings.add(newP);
        return newP;
    }

    public void res() {
        invalidate();
        validate();
        repaint();
    }

    public void generateShit() {
        for (int i = 0; i < 25; ++i) {
            Random rand = new Random();
            Planet lune = this.addNewPlanet("Lune" + i, rand.nextDouble() * 100000 + -50000, rand.nextDouble() * 100000 + -50000, rand.nextDouble() * 1E+22 + 1E+21, rand.nextDouble() * 1000 + 4000, Color.darkGray);
//          lune.speed.setX(i*10);
        }

    }
}
