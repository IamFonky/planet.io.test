package ch.elmootan.physics;

import java.awt.*;
import java.util.Random;

import static java.lang.Math.*;

public abstract class Body
{
    private Position position;

    private double mass;
    private double radius;

    private Body orbiting;

    private String name;

    private Color couleur;

    private Speed speed = new Speed(0, 0);

    double fragmentationRatio;

    public Body(String name, Position position, double mass, double radius, Color couleur, double fragmentationRatio)
    {
        this.name = name;
        this.position = position;
        this.mass = mass;
        this.radius = radius;
        this.couleur = couleur;
        this.fragmentationRatio = fragmentationRatio;
    }

    public double getMass()
    {
        return mass;
    }

    public Position getPosition()
    {
        return position;
    }

    public double getRadius()
    {
        return radius;
    }

    public void setMass(double mass)
    {
        this.mass = mass;
    }

    public void setPosition(Position position)
    {
        this.position = position;
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

    public BodyState eat(Body meal)
    {
        boolean explode = (abs(mass - meal.mass) < mass * fragmentationRatio);

        double newMass = mass + meal.mass;
        //Pour le nouveau rayon on additionne les aires puis on retrouve le rayon
        double newRadius = Math.sqrt((meal.radius * meal.radius * Math.PI + this.radius * this.radius * Math.PI) / Math.PI);

        double mTot = mass + meal.mass;
        double vXrThis = speed.getX() * mass / mTot;
        double vYrThis = speed.getY() * mass / mTot;
        double vXrMeal = meal.speed.getX() * meal.mass / mTot;
        double vYrMeal = meal.speed.getY() * meal.mass / mTot;
        double newVX = vXrThis + vXrMeal;
        double newVY = vYrThis + vYrMeal;

        setMass(newMass);
        setRadius(newRadius);
        setSpeed(new Speed(newVX, newVY));

        if (!explode)
        {
            return BodyState.EAT_MEAL;
        }
        else //Too much to eat, the planet with explode!
        {
            return BodyState.EXPLODE;
        }
    }
}