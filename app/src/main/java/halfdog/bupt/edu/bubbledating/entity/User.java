package halfdog.bupt.edu.bubbledating.entity;

/**
 * Created by andy on 2015/4/22.
 */
public class User {
    private int mId;
    private String mName;
    private String mPw;
    private String mEmail;
    private String mGender;
    private String mImage;
    private boolean mOnline;



    public User(int mId, String mName, String mPw, String mEmail,
                String mGender, String mImage, boolean mOnline) {
        super();
        this.mId = mId;
        this.mName = mName;
        this.mPw = mPw;
        this.mEmail = mEmail;
        this.mGender = mGender;
        this.mImage = mImage;
        this.mOnline = mOnline;
    }
    public long getmId() {
        return mId;
    }
    public void setmId(int mId) {
        this.mId = mId;
    }
    public String getmName() {
        return mName;
    }
    public void setmName(String mName) {
        this.mName = mName;
    }
    public String getmPw() {
        return mPw;
    }
    public void setmPw(String mPw) {
        this.mPw = mPw;
    }
    public String getmEmail() {
        return mEmail;
    }
    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }
    public String getmGender() {
        return mGender;
    }
    public void setmGender(String mGender) {
        this.mGender = mGender;
    }
    public String getmImage() {
        return mImage;
    }
    public void setmImage(String mImage) {
        this.mImage = mImage;
    }
    public boolean getmOnline() {
        return mOnline;
    }
    public void setmOnline(boolean mOnline) {
        this.mOnline = mOnline;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "mid:"+mId+",username:"+mName+",password:"+mPw+",email:"+mEmail+",gender:"+mGender+",image:"+mImage+",online:"+mOnline;
    }

}
