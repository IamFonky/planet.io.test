package ch.elmootan.universe;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

import ch.elmootan.physics.*;
import ch.elmootan.ui.Refresher;

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

        myTime.schedule(new Refresher(), 0, 33);

    }

    public void paint(Graphics g) {

        for (Body body : allThings) {
            int radius = (int) (body.radius / zoom);
            int x = (getWidth() / 2) + ((int) ((body.position.x - (body.radius / 2)) / zoom));
            int y = (getHeight() / 2) + ((int) ((body.position.y - (body.radius / 2)) / zoom));
            g.setColor(body.couleur);
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
