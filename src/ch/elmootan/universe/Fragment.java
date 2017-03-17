package ch.elmootan.universe;

import ch.elmootan.physics.*;

import java.awt.*;

/**
 * Created by Fonky on 16.03.2017.
 */
public class Fragment extends Body
{
   public Fragment(String name, Position position, double mass, double radius, Color couleur)
   {
      super(name, position, mass, radius, couleur,0.5);
   }
}