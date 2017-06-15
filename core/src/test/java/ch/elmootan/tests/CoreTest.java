package ch.elmootan.tests;

import ch.elmootan.core.serverCore.Engine;
import ch.elmootan.core.universe.Planet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CoreTest {

    static public void main(String... args)
    {
        new Engine();
    }
    @Test
    public void itShouldBePossibleToBigBang() throws InterruptedException {
        Engine universe = new Engine();
        assertNotNull(universe);
    }

    @Test
    public void itShouldBePossibleToAddAPlanetToTheUniverse() {
        Engine universe = new Engine();
        Planet jupiter = universe.addNewPlanet("Jupiter", 666, 666, 1.8986e27, 71492, 2, 1);
        assertNotNull(jupiter);
    }

    @Test
    public void aPlanetShouldBeAbleToEatAnotherPlanet() {
        Engine universe = new Engine();

        double jupiterMass = 1.8986e27;
        Planet jupiter = universe.addNewPlanet("Jupiter", 666, 666, jupiterMass, 71492, 2, 1);

        double marsMass = 641.85e21;
        Planet mars = universe.addNewPlanet("Mars", 666, 666, marsMass, 3396, 5, 2);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(-1, universe.getAllThings().indexOf(mars));
        assertNotNull(jupiter);
        assertEquals(-1, Double.compare(jupiterMass+marsMass, jupiter.getMass()));
    }

    @Test
    public void itShouldBePossibleToExplodeAPlanet() {
        Engine universe = new Engine();
        Planet jupiter = universe.addNewPlanet("Jupiter", 666, 666, 1.8986e27, 71492, 2, 1);

        universe.explode(jupiter);

        assertEquals(-1, universe.getAllThings().indexOf(jupiter));
    }
}
