package ch.elmootan.core.database.dbWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AWrapper implements IWrapper
{
   public Object get(ResultSet set) throws SQLException
   {
      return get(set,0);
   }

   public abstract Object get(ResultSet set, int offset) throws SQLException;
}
