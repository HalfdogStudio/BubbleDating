package halfdog.bupt.edu.bubbledating.tool.HXTool;

/**
 * Created by andy on 2015/5/28.
 */
public abstract class HXSDKModel {

    public abstract void setSettingMsgNotification(boolean paramBoolean);

    // �𶯺������ܿ��أ�����Ϣʱ���Ƿ�����˿��ش�
    // the vibrate and sound notification are allowed or not?
    public abstract boolean getSettingMsgNotification();

    public abstract void setSettingMsgSound(boolean paramBoolean);

    // �Ƿ������
    // sound notification is switched on or not?
    public abstract boolean getSettingMsgSound();

    public abstract void setSettingMsgVibrate(boolean paramBoolean);

    // �Ƿ����
    // vibrate notification is switched on or not?
    public abstract boolean getSettingMsgVibrate();

    public abstract void setSettingMsgSpeaker(boolean paramBoolean);

    // �Ƿ��������
    // the speaker is switched on or not?
    public abstract boolean getSettingMsgSpeaker();

    public abstract boolean saveHXId(String hxId);
    public abstract String getHXId();

    public abstract boolean savePassword(String pwd);
    public abstract String getPwd();

    /**
     * ����application���ڵ�process name,Ĭ���ǰ���
     * @return
     */
    public abstract String getAppProcessName();
    /**
     * �Ƿ����ǽ��պ�������
     * @return
     */
    public boolean getAcceptInvitationAlways(){
        return false;
    }

    /**
     * �Ƿ���Ҫ���ź��ѹ�ϵ��Ĭ����false
     * @return
     */
    public boolean getUseHXRoster(){
        return false;
    }

    /**
     * �Ƿ���Ҫ�Ѷ���ִ
     * @return
     */
    public boolean getRequireReadAck(){
        return true;
    }

    /**
     * �Ƿ���Ҫ���ʹ��ִ
     * @return
     */
    public boolean getRequireDeliveryAck(){
        return false;
    }

    /**
     * �Ƿ�������sandbox���Ի���. Ĭ���ǹص���
     * ����sandbox ���Ի���
     * ���鿪���߿���ʱ���ô�ģʽ
     */
    public boolean isSandboxMode(){
        return false;
    }

    /**
     * �Ƿ�����debugģʽ
     * @return
     */
    public boolean isDebugMode(){
        return false;
    }

}
