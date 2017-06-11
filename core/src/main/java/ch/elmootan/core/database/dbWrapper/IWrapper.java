package ch.elmootan.core.database.dbWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * Cette interface offre la méthode get qui permet de transformer
 * un ResultSet (ligne de résultat d'une requête SQLite) en un objet
 *
 * Chaque Wrapper implémentant cette interface doit retourner un
 * objet correspondant, ex : Pour un UserWrapper la méthode get doit retourner
 * un objet de la classe User.
 *
 */

public interface IWrapper
{
   Object get(ResultSet set) throws SQLException;
   Object get(ResultSet set, int offset) throws SQLException;
}
