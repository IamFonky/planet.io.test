package ch.elmootan.universe;

import ch.elmootan.physics.*;

import java.awt.*;

public class Planet extends Body {
    public Planet(String name, double mass, double radius) {
        super(name, new Position(0, 0), mass, radius, Color.BLACK);
    }

    public Planet(String name, double mass, double radius, Color couleur) {
        super(name, new Position(0, 0), mass, radius, couleur);
    }

    public Planet(String name, Position position, double mass, double radius, Color couleur) {
        super(name, position, mass, radius, couleur);
    }
}