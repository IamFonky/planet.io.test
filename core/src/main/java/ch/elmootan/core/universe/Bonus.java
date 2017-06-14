package ch.elmootan.core.universe;

import ch.elmootan.core.physics.Body;
import ch.elmootan.core.physics.Position;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.awt.*;
import java.util.Random;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="class")
public class Bonus extends Body {

    public String shittyClass = "Bonus";

    public static final int NONE = 0;
    public static final int MOON = 1;
    public static final int ATMOSPHER = 2;

    private int type;

    private long creationTime;

    public Bonus() {}

    public Bonus(String name, Position position, double mass, double radius, Color couleur, double fragmentationRatio, long time)
    {
        super(name, position, mass, radius, couleur, 0);
        type = new Random().nextInt(2)+1;
        creationTime = time;

    }

    public int getType() {
        return type;
    }

    public void setType(int newType) {
        type = newType;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
}
