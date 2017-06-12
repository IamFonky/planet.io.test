package ch.elmootan.core.database;

import ch.elmootan.core.database.dbCore.DBProcedures;

public class Database extends DBProcedures
{
   public Database()
   {
      super();
   }

   public void resetDatabase()
   {
      initialise(true);
   }
}
