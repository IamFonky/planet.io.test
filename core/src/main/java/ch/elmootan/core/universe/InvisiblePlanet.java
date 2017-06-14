package ch.elmootan.core.universe;


import ch.elmootan.core.physics.Body;
import ch.elmootan.core.physics.BodyState;
import ch.elmootan.core.physics.Position;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.awt.*;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="class")
public class InvisiblePlanet extends Planet {

    public String shittyClass = "InvisiblePlanet";

    public InvisiblePlanet(){}

    public InvisiblePlanet(String name, Position position, double mass, double radius, int id)  {
        super(name, position, mass, radius, 1, id);
    }

    public BodyState eat(Body meal)
    {
        return BodyState.DEFAULT;
    }

    public void setIdSkin(int idSkin) {
        this.idSkin = idSkin;
    }

}