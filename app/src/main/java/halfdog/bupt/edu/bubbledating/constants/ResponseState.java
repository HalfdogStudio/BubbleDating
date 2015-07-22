package halfdog.bupt.edu.bubbledating.constants;

/**
 * Created by andy on 2015/4/22.
 */
public class ResponseState {
    public  static final String RESPONSE_STATUS_KEY = "status";

    public static final int OK = 0;
    public static final int USER_NAME_DUPLICATE= 1;
    public static final int EMAIL_DUPLICATE = 2;
    public static final int UNKNOWN_ERROR = 3;
    public static final int UNKOWN_USERNAME = 4;
    public static final int USERNAME_PASSWORD_UNCOMPATIBLE = 5;
    public static final int HX_REGISTER_FAILED = 6;
    public static final int INSERT_FAILED = 7;
    public static final int SQL_EXCEPTION = 8;

}
