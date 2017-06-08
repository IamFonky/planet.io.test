package ch.elmootan.core.physics;

import ch.elmootan.core.universe.Bonus;
import ch.elmootan.core.universe.Fragment;
import ch.elmootan.core.universe.InvisiblePlanet;
import ch.elmootan.core.universe.Planet;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.awt.*;

import static java.lang.Math.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include=JsonTypeInfo.As.PROPERTY,
        property = "shittyClass")
@JsonSubTypes({
        @JsonSubTypes.Type(value=Planet.class, name = "Planet"),
        @JsonSubTypes.Type(value=InvisiblePlanet.class, name = "InvisiblePlanet"),
        @JsonSubTypes.Type(value=Bonus.class, name = "Bonus"),
        @JsonSubTypes.Type(value=Fragment.class, name = "Fragment")
})
public abstract class Body {
    private Position position;

    private double mass;
    private double radius;

    private Body orbiting;

    private String name;

//    private Color couleur;

    private Speed speed = new Speed(0, 0);

    private double fragmentationRatio;

    private int id;

    public Body(){}

    public Body(String name, Position position, double mass, double radius, Color couleur, double fragmentationRatio) {
        this.name = name;
        this.position = position;
        this.mass = mass;
        this.radius = radius;
//        this.couleur = couleur;
        this.fragmentationRatio = fragmentationRatio;
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

//    public Color getCouleur() {
//        return couleur;
//    }

//    public void setCouleur(Color couleur) {
//        this.couleur = couleur;
//    }

    public Speed getSpeed() {
        return speed;
    }

    public void setSpeed(Speed speed) {
        this.speed = speed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BodyState eat(Body meal) {
        if (this instanceof Fragment)
            return BodyState.DEFAULT;
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

        if (!explode) {
            return BodyState.EAT_MEAL;
        } else //Too much to eat, the planet with explode!
        {
            return BodyState.EXPLODE;
        }
    }
    @Override
    public boolean equals(Object o)
    {
        if(o.getClass().isInstance(this))
        {
            name.equals(((Body)o).getName());
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "Body : " + name + "\r\n" + position + "\r\n" + speed;
    }
}