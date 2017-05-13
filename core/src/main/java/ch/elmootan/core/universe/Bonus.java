package ch.elmootan.core.universe;

import ch.elmootan.core.physics.Body;
import ch.elmootan.core.physics.Position;

import java.awt.*;

/**
 * Created by David on 04.05.2017.
 */
public class Bonus extends Body {
    public Bonus(String name, Position position, double mass, double radius, Color couleur, double fragmentationRatio)
    {
        super(name, position, mass, radius, couleur, 0);
    }
}
