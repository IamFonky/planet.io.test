package ch.elmootan.core.universe;


import ch.elmootan.core.physics.Body;
import ch.elmootan.core.physics.BodyState;
import ch.elmootan.core.physics.Position;

import java.awt.*;

public class InvisiblePlanet extends Planet {
    public InvisiblePlanet(String name, Position position, double mass, double radius, Color couleur)  {
        super(name, position, mass, radius, couleur);
    }

    public BodyState eat(Body meal)
    {
        return BodyState.DEFAULT;
    }
}
