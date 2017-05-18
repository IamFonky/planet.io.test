package ch.elmootan.core.universe;


import ch.elmootan.core.physics.Body;
import ch.elmootan.core.physics.BodyState;
import ch.elmootan.core.physics.Position;

import java.awt.*;

public class InvisiblePlanet extends Planet {
    public InvisiblePlanet(String name, Position position, double mass, double radius, int id)  {
        super(name, position, mass, radius, 1, id);
    }

    public BodyState eat(Body meal)
    {
        return BodyState.DEFAULT;
    }
}