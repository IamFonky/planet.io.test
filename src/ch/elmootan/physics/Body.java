package ch.elmootan.physics;

import java.awt.*;

public abstract class Body {
    private Position position;

    private double mass;
    private double radius;

    private Body orbiting;

    private String name;

    private Color couleur;

    private Speed speed = new Speed(0, 0);

    public Body(String name, Position position, double mass, double radius, Color couleur) {
        this.name = name;
        this.position = position;
        this.mass = mass;
        this.radius = radius;
        this.couleur = couleur;
    }

    public double getMass() {
        return mass;
    }

    public Position getPosition() {
        return position;
    }

    public double getRadius() {
        return radius;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Body getOrbiting() {
        return orbiting;
    }

    public void setOrbiting(Body orbiting) {
        this.orbiting = orbiting;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getCouleur() {
        return couleur;
    }

    public void setCouleur(Color couleur) {
        this.couleur = couleur;
    }

    public Speed getSpeed() {
        return speed;
    }

    public void setSpeed(Speed speed) {
        this.speed = speed;
    }

    public void eat(Body meal) {
        this.mass += meal.mass;
        this.radius = Math.sqrt((meal.radius * meal.radius * Math.PI + this.radius * this.radius * Math.PI) / Math.PI);
        //allThings.remove(meal);
    }
}