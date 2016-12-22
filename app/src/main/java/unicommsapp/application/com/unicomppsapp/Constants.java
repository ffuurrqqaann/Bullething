package unicommsapp.application.com.unicomppsapp;

/**
 * Created by GleasonK on 6/8/15.
 *
 * Constants used by this chatting application.
 * TODO: Register app for GCM and replace GCM_SENDER_ID
 */

public class Constants {
    public static final String PUBLISH_KEY   = "pub-c-4b07e701-719f-4f43-af24-9aabc12f603b";
    public static final String SUBSCRIBE_KEY = "sub-c-e9cc24fa-d266-11e5-8408-0619f8945a4f";

    public static final String CHAT_PREFS    = "com.unicomms.app";
    public static final String CHAT_USERNAME = "name";
    public static final String CHAT_ROOM     = "Chat";

    public static final String JSON_GROUP = "groupMessage";
    public static final String JSON_DM    = "directMessage";
    public static final String JSON_USER  = "chatUser";
    public static final String JSON_MSG   = "chatMsg";
    public static final String JSON_TIME  = "chatTime";

    public static final String STATE_LOGIN = "loginTime";

    public static final String GCM_REG_ID    = "gcmRegId";
    public static final String GCM_SENDER_ID = ""; // Get this from
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
}
