package ch.elmootan.core.database.DBObjects;

public class User extends DBObject {
    private int errorCode = 0;
    private int id = 0;
    private String name = "";
    private int bestScore = 0;

    public User() {
    }

    public User(String name) {
        construct(0, 0, name, 0);
    }

    public User(int id, String name, int bestScore) {
        construct(0, id, name, bestScore);
    }

    private void construct(int e, int i, String n, int b) {
        errorCode = e;
        id = i;
        name = n;
        bestScore = b;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public void set(DBObject object) {
        if (User.class.isInstance(object)) {
            User user = (User) object;
            errorCode = user.errorCode;
            id = user.id;
            name = user.name;
            bestScore = user.bestScore;
        }
    }
}
