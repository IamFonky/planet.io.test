import java.util.ArrayList;

/**
 * Created by Fonky on 20.02.2017.
 */
public class Espace
{
   ArrayList<Body> allThings = new ArrayList<>();

   public abstract class Body
   {
      double weight;
      double radius;
      Body orbiting;
   }

   public class Planet extends Body
   {
	  String name;
   }
}
