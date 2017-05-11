package ch.elmootan.protocol;

public enum Error {
    UNKNOWN(666, "Shit happens"),
    CONNEXION_FAILURE(444, "An error happened during the connexion !"),
    INTERNAL_ERROR(500, "An internal error occured !"),
    OBJECT_NOT_FOUND(404, "The required object was not found !"),
    CMD_NOT_FOUND(400, "The command sent doesn't exist !"),
    JSON_MAPPING_ERROR(422, "There was an error in the JSON object !");

    private int id;
    private String message;

    Error(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
