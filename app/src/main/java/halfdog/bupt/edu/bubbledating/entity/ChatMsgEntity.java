package halfdog.bupt.edu.bubbledating.entity;

/**
 * Created by andy on 2015/5/5.
 */
public class ChatMsgEntity {

    private String mName;
    private String mContent;
    private String mDate;
    private final boolean mReceive;

    public ChatMsgEntity(String mTo, String mContent, String mDate, boolean mReceive) {
        this.mName = mTo;
        this.mContent = mContent;
        this.mDate = mDate;
        this.mReceive = mReceive;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public boolean isReceive() {
        return mReceive;
    }

//    public void setReceive(boolean mReceive) {
//        this.mReceive = mReceive;
////    }

    @Override
    public String toString() {
        return "ChatMsgEntity{" +
                "mName='" + mName + '\'' +
                ", mContent='" + mContent + '\'' +
                ", mDate='" + mDate + '\'' +
                ", mReceive=" + mReceive +
                '}';
    }
}
