package ch.elmootan.core.database.dbCore;

public class DBStructure
{

   public DBStructure()
   {
      initialise(false);
   }

   public DBStructure(boolean reset)
   {
      initialise(reset);
   }

   protected void initialise(boolean reset)
   {
      //Si connect() retourne true alors c'est la première connexion et la db
      //doit être créée
      if(SqliteDbCore.connect() || reset)
      {
         resetPlanetIODatabase();
      }
   }

   public static void resetPlanetIODatabase()
   {
      clearPlanetIODatabase();
      setPlanetIODatabase();
   }

   private static void clearPlanetIODatabase()
   {
      SqliteDbCore.set("DROP TABLE IF EXISTS tblUser");
   }

   private static void setPlanetIODatabase()
   {
      //INIT VARIABLES
      SqliteDbCore.set(
              "PRAGMA foreign_keys=ON"
      );

      //TABLE USER
      SqliteDbCore.set(
              "CREATE TABLE tblUser\n" +
                      "(\n" +
                      "  idUser INTEGER PRIMARY KEY   AUTOINCREMENT,\n" +
                      "  usrName TEXT NOT NULL,\n" +
                      "  usrPassword INTEGER NOT NULL,\n" +
                      "  usrBestScore INTEGER DEFAULT 0" +
                      ")"
      );

      SqliteDbCore.set(
              "CREATE UNIQUE INDEX tblUser_usrName_uindex " +
                      "ON tblUser (usrName)"
      );
   }
}


