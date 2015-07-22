package halfdog.bupt.edu.bubbledating.constants;

import java.io.File;

/**
 * Created by andy on 2015/4/29.
 */
public class Configurations {
//    public static final String SERVER_IP = "http://10.108.245.220:8080";
//    public static final String SERVER_IP = "http://10.210.60.53:8080";
    public static final String SERVER_IP = "http://123.57.83.135:8080";
    public static final String IMG_CACHE_PATH = "BubbleDatingServer/HandleDownloadImage?img=";
    public static final String SERVER_IMG_CACHE_DIR = Configurations.SERVER_IP + File.separator + Configurations.IMG_CACHE_PATH;
    public static final String FEED_BACK_REQUEST = SERVER_IP+File.separator+"BubbleDatingServer/HandleFeedback";
    public static final String SUBMIT_NEW_INVITATION = "BubbleDatingServer/HandleNewInvitation";

    public static final int REQUEST_TIMEOUT_MS = 3000;
    public static final int MAX_RETRY_TIMES = 1;
    public static final int BACK_OFF_MULTI = 1;

    public static final int UPDATE_CHAT_ACTIVITY_CONTACT = 0x0001;
    public static final int UPDATE_MESSAGE_FRAGMENT = 0x0010;
    public static final int UPDATE_DATE_FRAGMENT = 0x0100;

    public static final int REQUEST_ADD_INVITATION = 10;
    public static final int RESULT_ADD_INVITATION_OK = 20;

    public static final String ACOUNT_SHARE_PREFERENCE = "count";
    public static final String ACOUNT_USERNAME = "username";
    public static final String ACOUNT_PASSWORD = "password";

}
