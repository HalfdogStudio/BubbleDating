package halfdog.bupt.edu.bubbledating.tool.HXTool;

/**
 * Created by andy on 2015/5/28.
 */
public abstract class HXSDKModel {

    public abstract void setSettingMsgNotification(boolean paramBoolean);

    // the vibrate and sound notification are allowed or not?
    public abstract boolean getSettingMsgNotification();

    public abstract void setSettingMsgSound(boolean paramBoolean);

    // sound notification is switched on or not?
    public abstract boolean getSettingMsgSound();

    public abstract void setSettingMsgVibrate(boolean paramBoolean);

    // vibrate notification is switched on or not?
    public abstract boolean getSettingMsgVibrate();

    public abstract void setSettingMsgSpeaker(boolean paramBoolean);

    // the speaker is switched on or not?
    public abstract boolean getSettingMsgSpeaker();

    public abstract boolean saveHXId(String hxId);
    public abstract String getHXId();

    public abstract boolean savePassword(String pwd);
    public abstract String getPwd();

    public abstract String getAppProcessName();
    public boolean getAcceptInvitationAlways(){
        return false;
    }

    public boolean getUseHXRoster(){
        return false;
    }
    public boolean getRequireReadAck(){
        return true;
    }

    public boolean getRequireDeliveryAck(){
        return false;
    }

    public boolean isSandboxMode(){
        return false;
    }

    public boolean isDebugMode(){
        return false;
    }

}
