package ch.elmootan.core.universe;

import ch.elmootan.core.physics.Body;
import ch.elmootan.core.physics.Position;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.awt.*;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="class")
public class Bonus extends Body {

    public String shittyClass = "Bonus";

    public Bonus(String name, Position position, double mass, double radius, Color couleur, double fragmentationRatio)
    {
        super(name, position, mass, radius, couleur, 0);
    }
}
