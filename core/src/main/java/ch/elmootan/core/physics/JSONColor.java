package ch.elmootan.core.physics;

import java.awt.*;

public class JSONColor extends Color {
   public JSONColor()
   {
      super(0,0,0);
   }

   public JSONColor(Color color)
   {
      super(color.getRGB());
   }
}
