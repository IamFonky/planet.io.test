package ch.elmootan.core.universe;

import ch.elmootan.core.physics.Body;
import ch.elmootan.core.physics.Position;

import javax.swing.*;
import java.awt.*;

public class Planet extends Body
{
    //private final Image image = new ImageIcon(getClass().getClassLoader().getResource("ch/elmootan/core/textures/planet1.jpg").getPath()).getImage();

    public Planet(String name, double mass, double radius) {
        super(name, new Position(0, 0), mass, radius, Color.BLACK,0.1);
    }

    public Planet(String name, double mass, double radius, Color couleur) {
        super(name, new Position(0, 0), mass, radius, couleur,0.1);
    }

    public Planet(String name, Position position, double mass, double radius, Color couleur) {
        super(name, position, mass, radius, couleur,0.1);
    }

//    public Image getImage() {
//        image.getScaledInstance((int)getRadius(), (int)getRadius(),Image.SCALE_FAST);
//        return image;
//    }
}
