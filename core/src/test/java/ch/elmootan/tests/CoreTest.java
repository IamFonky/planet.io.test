package ch.elmootan.tests;

import ch.elmootan.core.universe.GUniverse;
import ch.elmootan.core.universe.Planet;
import ch.elmootan.core.universe.Universe;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class CoreTest extends Universe {

    static public void main(String... args)
    {
        new Universe();
    }
    @Test
    public void itShouldBePossibleToBigBang() throws InterruptedException {
        Universe universe = new Universe();
        assertNotNull(universe);
    }

    @Test
    public void itShouldBePossibleToAddAPlanetToTheUniverse() {
        Universe universe = new Universe();
        Planet jupiter = universe.addNewPlanet("Jupiter", 666, 666, 1.8986e27, 71492, 2, 1);
        assertNotNull(jupiter);
    }

    @Test
    public void aPlanetShouldBeAbleToEatAnotherPlanet() {
        Universe universe = new Universe();

        double jupiterMass = 1.8986e27;
        Planet jupiter = universe.addNewPlanet("Jupiter", 666, 666, jupiterMass, 71492, 2, 1);

        double marsMass = 641.85e21;
        Planet mars = universe.addNewPlanet("Mars", 666, 666, marsMass, 3396, 5, 2);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, universe.getAllThings().indexOf(mars));
        assertNotNull(jupiter);
        assertEquals(0, Double.compare(jupiterMass+marsMass, jupiter.getMass()));
    }

    @Test
    public void itShouldBePossibleToExplodeAPlanet() {
        Universe universe = new Universe();
        Planet jupiter = universe.addNewPlanet("Jupiter", 666, 666, 1.8986e27, 71492, 2, 1);

        universe.explode(jupiter);

        assertEquals(-1, universe.getAllThings().indexOf(jupiter));
    }
}
