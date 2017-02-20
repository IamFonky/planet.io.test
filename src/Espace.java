import java.util.ArrayList;

/**
 * Created by Fonky on 20.02.2017.
 */
public class Espace
{
   public class Position
   {
      Position(double x, double y)
      {
         this.x = x;
         this.y = y;
      }

      public double getX()
      {
         return x;
      }

      public void setX(double x)
      {
         this.x = x;
      }

      public double getY()
      {
         return y;
      }

      public void setY(double y)
      {
         this.y = y;
      }

      double x, y;
   }

   public abstract class Body
   {
      public double getWeight()
      {
         return weight;
      }

      public void setWeight(double weight)
      {
         this.weight = weight;
      }

      public double getRadius()
      {
         return radius;
      }

      public void setRadius(double radius)
      {
         this.radius = radius;
      }

      public Body getOrbiting()
      {
         return orbiting;
      }

      public void setOrbiting(Body orbiting)
      {
         this.orbiting = orbiting;
      }

      public Position getPosition()
      {

         return position;
      }

      public void setPosition(Position position)
      {
         this.position = position;
      }

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

      Position position;
      double weight;
      double radius;
      Body orbiting;
      String name;


      public Body(Position position, double weight, double radius)
      {
         this.position = position;
         this.weight = weight;
         this.radius = radius;
      }
   }

   public class Planet extends Body
   {
      public Planet(double weight, double radius)
      {
         super(new Position(0.0,0.0),weight,radius);
      }

      public Planet(Position position, double weight, double radius)
      {
         super(position,weight,radius);
      }
   }

   public ArrayList<Body> allThings = new ArrayList<>();

   public Planet addNewPlanet(double weight, double radius)
   {
      Planet newP = new Planet(weight,radius);
      allThings.add(newP);
      return newP;
   }

}
