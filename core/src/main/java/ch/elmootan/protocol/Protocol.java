package ch.elmootan.protocol;


public final class Protocol {
    // Commandes envoyées par le serveur.
    public static final String PLANET_IO_SUCCESS = "GREATSUCCESS";
    public static final String PLANET_IO_FAILURE = "AH!AH!";

    // Commandes envoyées par le client.
    public static final String PLANET_IO_HELLO = "HELLOPELO";
    public static final String PLANET_IO_LOGIN = "LOGIN";
    public static final String PLANET_IO_REGISTER = "REGISTER";
    public static final String PLANET_IO_GET_SCORES = "GIVEMEMYSCORESPLZ";
    public static final String PLANET_IO_GET_UNIVERSE = "SHREKISLIFE";
    public static final String PLANET_IO_SEND_POSITION = "IAMHERE";
    public static final String CMD_CREATE_GAME = "CREATEGAME";
    public static final String CMD_DISCONNECT = "DISCONECT";


    public static final int PORT = 666;
}
