package ch.elmootan.core.database.dbCore;

import ch.elmootan.core.database.dbWrapper.IWrapper;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe s'occupe de la gestion de la bse de donnée au niveau le plus bas.
 * Elle permet de se connecter à une base de données, d'y envoyer des requêtes et
 * de récupérer les résultats directement sous forme de liste d'objets.
 */
public class SqliteDbCore
{
   private static Connection _connection;
   private static String url = "discover.db";
   private static String _dbUrl = "jdbc:sqlite:" + url;
   private static boolean _autoCommit = false;

   public static final int UNIQUE_CONSTRAINT_FAILS = 2067;
   public static final int UNIQUE_PRIMARY_FAILS = 1555;

   public static final int NO_ERROR = 0;
   public static final int INSERT_ERROR = -1;


   //CONNEXIONS
   protected static void connect(boolean autoCommit, String url)
   {
      _dbUrl = url;
      _autoCommit = autoCommit;
      connect();
   }

   protected static void connect(String url)
   {
      _dbUrl = url;
      connect();
   }

   protected static boolean connect()
   {
      boolean firstTime = !((new File(url)).exists());

      try
      {
         //Ici la classe JDBC est une classe statique appelée dynamiquement à l'aide
         //de la méthode forName. Elle s'enregistre elle même dans le DriverManager.$
         // cf. http://www.xyzws.com/Javafaq/what-does-classforname-method-do/17
         Class.forName("org.sqlite.JDBC");
         _connection = DriverManager.getConnection(_dbUrl);
         _connection.setAutoCommit(_autoCommit);
      }
      catch (Exception e)
      {
         System.err.println(e.getClass().getName() + ": " + e.getMessage());
         System.exit(0);
      }

      return firstTime;
   }


   //Appliquer une modification de la base de donnée
   protected static int set(String sqliteQuery)
   {
      try
      {
         Statement statement = _connection.createStatement();
         statement.executeUpdate(sqliteQuery);
         _connection.commit();
         ResultSet results = statement.executeQuery("SELECT last_insert_rowid()");
         _connection.commit();
         results.next();
         int result = results.getInt(1);
         statement.close();

         return result;
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      return 0;
   }

   //Récupérer une liste d'objets définis par le AWrapper
   protected static List<Object> get(String sqliteQuery, IWrapper wrapper)
   {
      List<Object> queryAnswer = new ArrayList<Object>();
      try
      {
         Statement statement = _connection.createStatement();
         //statement.executeUpdate(sqliteQuery);
         ResultSet results = statement.executeQuery(sqliteQuery);
         while (results.next())
         {
            queryAnswer.add(wrapper.get(results));
         }
         results.close();
         statement.close();
         _connection.commit();
      }
      catch (Exception e)
      {
         System.out.println(e.getClass().getName() + " : " + e.getMessage());
      }

      return queryAnswer;


      //DEBUG
      //System.out.println("Records created successfully");
   }

   protected static void disconnect()
   {
      try
      {
         _connection.close();
      }
      catch (Exception e)
      {
         System.err.println(e.getClass().getName() + ": " + e.getMessage());
         System.exit(0);
      }
   }
}

