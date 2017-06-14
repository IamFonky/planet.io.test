package ch.elmootan.core.universe;

import ch.elmootan.core.physics.Body;
import ch.elmootan.core.physics.Position;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="class")
public class Planet extends Body
{
    public String shittyClass = "Planet";

    private int activeBonus = Bonus.NONE;
    private Timer bonusTimer;
    private int bonusDuration;

    // Durée en secondes
    private static final int MAX_BONUS_DURATION = 15;

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

    public void setActiveBonus(int type) {
        activeBonus = type;
        if (type != Bonus.NONE) {
            bonusTimer = new Timer(1000, setBonusTime);
            bonusTimer.start();
            bonusDuration = 0;
        } else if (bonusTimer != null){
            bonusTimer.stop();
        }
    }

    public int getActiveBonus() {
        return activeBonus;
    }

    ActionListener setBonusTime = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            if (++bonusDuration == MAX_BONUS_DURATION)
                setActiveBonus(Bonus.NONE);
        }
    };
}
