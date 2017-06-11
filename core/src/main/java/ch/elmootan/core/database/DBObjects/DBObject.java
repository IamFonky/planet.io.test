package ch.elmootan.core.database.DBObjects;

import ch.elmootan.core.database.dbCore.SqliteDbCore;

public abstract class DBObject
{
   public int errorCode = SqliteDbCore.NO_ERROR;

   public abstract void set(DBObject o);
   public abstract int getId();
   public abstract void setId(int id);

   @Override
   public int hashCode()
   {
      return getId();
   }

   @Override
   public boolean equals(Object obj)
   {

      if(this.getClass().isInstance(obj))
      {
         return getId() == ((DBObject)obj).getId();
      }
      return false;
   }
}
