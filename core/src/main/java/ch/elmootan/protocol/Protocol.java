package ch.elmootan.protocol;


public final class Protocol {
    // Commandes envoyées par le serveur.
    public static final String PLANET_IO_SUCCESS = "GREATSUCCESS";
    public static final String PLANET_IO_FAILURE = "AH!AH!";

    public static final String LOBBY_UPDATED = "OHBOITHELOBBYISUPDATED!";
    public static final String GAME_UPDATE = "OHBOITHEGAMEISUPDATED!";
    public static final String PLANET_IO_LOBBY_JOINED = "LOBBY_JOINED";

    public static final String PLAY_MUSIC = "ANDHISNAMEISJOHNCENA";

    // Commandes envoyées par le client.
    public static final String PLANET_IO_HELLO = "HELLOPELO";
    public static final String PLANET_IO_LOGIN = "LOGIN";
    public static final String PLANET_IO_REGISTER = "REGISTER";
    public static final String PLANET_IO_GET_SCORES = "GIVEMEMYSCORESPLZ";
    public static final String PLANET_IO_GET_UNIVERSE = "SHREKISLIFE";
    public static final String PLANET_IO_SEND_POSITION = "IAMHERE";
    public static final String NB_GAME_MAX_UPDATE = "NB_GAME_MAX_UPDATE";

    // Commande de controle
    public static final String PLANET_IO_CREATE_PLANET = "CREATEPLANET";
    public static final String PLANET_IO_SET_PLANET = "SETPLANET";
    public static final String PLANET_IO_KILL_PLANET = "KILLPLANET";

    //Commandes du client au serveur
    public static final String CMD_CREATE_GAME = "CREATEGAME";
    public static final String CMD_JOIN_GAME = "JOINGAME";
    public static final String CMD_DISCONNECT = "DISCONECT";
    public static final String CMD_SEPARATOR = ":";
    public static final String PLANET_IO_LEAVING_GAME = "PLANET_IO_LEAVING_GAME";
    public static final String CMD_BLABLA = "blablabla";


    public static final String END_OF_COMMAND = "COMMAND_DONE_MA_BOI";

    public static final int PORT = 8585;

    public static final int PORT_UDP = 9999;

    public static final String IP_MULTICAST = "239.192.0.2";

    public static final String IP_SERVER = "localhost";
}