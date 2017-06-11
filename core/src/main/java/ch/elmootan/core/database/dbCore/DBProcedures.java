package ch.elmootan.core.database.dbCore;

import ch.elmootan.core.database.dbWrapper.*;
import ch.elmootan.core.database.DBObjects.*;

import java.util.*;

public class DBProcedures extends DBStructure {
    public DBProcedures() {
        super();
    }


    ///////////////////////////////////////////////////////////////
    //General setter

    private void set(User object, String SQLiteQuery,
                     String methodName, Class<?> argType) {
        int newId = SqliteDbCore.set(SQLiteQuery);

        if (newId == SqliteDbCore.INSERT_ERROR) {
            object.setErrorCode(newId);
            try {
                DBProcedures.class.getMethod(methodName, argType).
                        invoke(this, object);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("It seem to have a problem with the method. Error :"
                        + e.toString());
            }
        } else {
            object.setId(newId);
        }
    }

    ///////////////////////////////////////////////////////////////
    //General getter
    private List<Object> get(String selectQuery, IWrapper wrapper) {
        List<Object> queryReturn = SqliteDbCore.get
                (
                        selectQuery,
                        wrapper
                );

        return queryReturn;
    }

    private DBObject getSingle(DBObject o, String selectQuery, IWrapper wrapper) {
        List<Object> queryReturn = get(selectQuery, wrapper);
        if (queryReturn.size() > 0) {
            o.set(((DBObject) queryReturn.get(0)));
        } else {
            o.setId(0);
        }
        return o;
    }

    private void getCastedObjectList(Collection dbObjectList, String SQLiteQuery, IWrapper wrapper) {
        List<Object> objList = get(SQLiteQuery, wrapper);
        for (Object obj : objList) {
            dbObjectList.add(obj);
        }

    }

    ///////////////////////////////////////////////////////////////
    //User

    public void insertUser(User user) {
        set(
                user,
                "INSERT INTO tblUser " +
                        "(usrName) " +
                        "VALUES ('" +
                        user.getName() + "')",
                "getUser",
                User.class);
    }

    public void getUser(User user) {
        getSingle(
                user,
                "SELECT * FROM tblUser " +
                        "WHERE usrName = '" + user.getName() + "'",
                new UserWrapper());
    }

    public void checkUser(User user) {
        getSingle(
                user,
                "SELECT * FROM tblUser " +
                        "WHERE usrName = '" + user.getName() + "' ",
                new SafeUserWrapper());
    }

    public void checkRegister(User user) {
        insertUser(user);
        checkUser(user);
    }

    public ArrayList<User> getTopUsers() {
        ArrayList<User> topUsers = new ArrayList<>();
        getCastedObjectList(
                topUsers,
                "SELECT * FROM tblUser ORDER BY usrBestScore DESC",
                new SafeUserWrapper());
        return topUsers;
    }

    public void updateBestScore(User user) {
        SqliteDbCore.set(
                "UPDATE tblUser SET " +
                        "usrBestScore = " + user.getBestScore() + " " +
                        "WHERE usrName = '" + user.getName() + "'"
        );
    }

    public void removeUser(User user) {
        SqliteDbCore.set("DELETE FROM tblUser WHERE idUser = " + user.getId());
        user.setId(0);
    }
}
