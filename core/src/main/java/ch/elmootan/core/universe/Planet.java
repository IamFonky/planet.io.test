package ch.elmootan.core.universe;

import ch.elmootan.core.physics.Body;
import ch.elmootan.core.physics.Position;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.swing.*;
import java.awt.*;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="class")
public class Planet extends Body
{
    public String shittyClass = "Planet";

    public BonusType activeBonus = BonusType.NONE;

    private int idSkin = 1;

    public Planet(){}

    public Planet(String name, int skin) {
        super(name, new Position(0, 0), 10, 10, Color.BLACK,0.1);
        idSkin = skin;
    }

    public Planet(String name, double mass, double radius) {
        super(name, new Position(0, 0), mass, radius, Color.BLACK,0.1);
    }

    public Planet(String name, double mass, double radius, Color couleur) {
        super(name, new Position(0, 0), mass, radius, couleur,0.1);
    }

    public Planet(String name, Position position, double mass, double radius, Color couleur) {
        super(name, position, mass, radius, couleur,0.1);
    }

    public Planet(String name, Position position, double mass, double radius, int skin, int id) {
        super(name, position, mass, radius, Color.BLACK,0.1);
        idSkin = skin;
        setId(id);
    }

    public int getIdSkin() {
        return idSkin;
    }

    public void setActiveBonus(BonusType type) {
        activeBonus = type;
    }

    public BonusType getActiveBonus() {
        return activeBonus;
    }
}
