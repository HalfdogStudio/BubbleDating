package halfdog.bupt.edu.bubbledating.constants;

import java.io.File;

/**
 * Created by andy on 2015/4/29.
 */
public class Configuration {
    public static final String SERVER_IP = "http://10.108.245.37:8080";
    public static final String IMG_CACHE_PATH = "BubbleDatingServer/HandleDownloadImage?img=";
    public static final String SERVER_IMG_CACHE_DIR = Configuration.SERVER_IP + File.separator + Configuration.IMG_CACHE_PATH;
    public static final String FEED_BACK_REQUEST = SERVER_IP+File.separator+"BubbleDatingServer/HandleFeedback";
    public static final String SUBMIT_NEW_INVITATION = "BubbleDatingServer/HandleNewInvitation";
}
