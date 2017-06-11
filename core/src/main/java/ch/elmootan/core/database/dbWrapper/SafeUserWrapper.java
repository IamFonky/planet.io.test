package ch.elmootan.core.database.dbWrapper;

import ch.elmootan.core.database.DBObjects.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SafeUserWrapper extends AWrapper
{
   public User get(ResultSet set, int offset) throws SQLException
   {
      return new User(
              set.getInt(offset + 1),
              set.getString(offset + 2),
              "",
              set.getInt(offset + 4)
      );
   }
}
