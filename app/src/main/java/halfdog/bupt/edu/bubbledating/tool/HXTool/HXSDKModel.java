package halfdog.bupt.edu.bubbledating.tool.HXTool;

/**
 * Created by andy on 2015/5/28.
 */
public abstract class HXSDKModel {

    public abstract void setSettingMsgNotification(boolean paramBoolean);

    // 震动和声音总开关，来消息时，是否允许此开关打开
    // the vibrate and sound notification are allowed or not?
    public abstract boolean getSettingMsgNotification();

    public abstract void setSettingMsgSound(boolean paramBoolean);

    // 是否打开声音
    // sound notification is switched on or not?
    public abstract boolean getSettingMsgSound();

    public abstract void setSettingMsgVibrate(boolean paramBoolean);

    // 是否打开震动
    // vibrate notification is switched on or not?
    public abstract boolean getSettingMsgVibrate();

    public abstract void setSettingMsgSpeaker(boolean paramBoolean);

    // 是否打开扬声器
    // the speaker is switched on or not?
    public abstract boolean getSettingMsgSpeaker();

    public abstract boolean saveHXId(String hxId);
    public abstract String getHXId();

    public abstract boolean savePassword(String pwd);
    public abstract String getPwd();

    /**
     * 返回application所在的process name,默认是包名
     * @return
     */
    public abstract String getAppProcessName();
    /**
     * 是否总是接收好友邀请
     * @return
     */
    public boolean getAcceptInvitationAlways(){
        return false;
    }

    /**
     * 是否需要环信好友关系，默认是false
     * @return
     */
    public boolean getUseHXRoster(){
        return false;
    }

    /**
     * 是否需要已读回执
     * @return
     */
    public boolean getRequireReadAck(){
        return true;
    }

    /**
     * 是否需要已送达回执
     * @return
     */
    public boolean getRequireDeliveryAck(){
        return false;
    }

    /**
     * 是否运行在sandbox测试环境. 默认是关掉的
     * 设置sandbox 测试环境
     * 建议开发者开发时设置此模式
     */
    public boolean isSandboxMode(){
        return false;
    }

    /**
     * 是否设置debug模式
     * @return
     */
    public boolean isDebugMode(){
        return false;
    }

}
