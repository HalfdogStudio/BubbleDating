package halfdog.bupt.edu.bubbledating.entity;

import android.text.method.HideReturnsTransformationMethod;

/**
 * Created by andy on 2015/5/5.
 */
public class ChatMsgEntity {

    private String mTo;
    private String mFrom;
    private String mContent;
    private String mDate;

    private final boolean mReceive;

    public ChatMsgEntity(String mTo, String mFrom, String mContent, String mDate, boolean mReceive) {
        this.mTo = mTo;
        this.mFrom = mFrom;
        this.mContent = mContent;
        this.mDate = mDate;
        this.mReceive = mReceive;
    }

    public String getTo() {
        return mTo;
    }

    public void setTo(String mTo) {
        this.mTo = mTo;
    }

    public String getmFrom(){
        return mFrom;
    }

    public void setmFrom(String mFrom){
        this.mFrom = mFrom;
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
                "mName='" + mTo + '\'' +
                ", mContent='" + mContent + '\'' +
                ", mDate='" + mDate + '\'' +
                ", mReceive=" + mReceive +
                '}';
    }
}
