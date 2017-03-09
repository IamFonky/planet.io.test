/**
 * Created by Fonky on 20.02.2017.
 */
public enum Constantes
{
   GRAVITATION(6.66E-15,"N⋅m2⋅kg-2");

   Constantes(Double valeur, String unite)
   {
      this.valeur = valeur;
      this.unite = unite;
   }

   public Double valeur;
   public String unite;
}
