package cn.rongcloud.ktvwithcalllib.utils;

import cn.rongcloud.ktvmusickit.callback.KtvDataCallBack;
import cn.rongcloud.ktvwithcalllib.message.RCKTVRefreshMessage;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;

/**
 * @author gyn
 * @date 2022/7/12
 */
public class MessageUtil {

    public static void sendMessage(String targetId, MessageContent messageContent, KtvDataCallBack<Boolean> callBack) {
        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, targetId, messageContent, "", "", new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onSuccess(Message message) {
                if (callBack != null) {
                    callBack.onResult(true);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                if (callBack != null) {
                    callBack.onResult(false);
                }
            }
        });
    }

    public static void sendRefreshMessage(String targetId, String name, String content, KtvDataCallBack<Boolean> callBack) {
        RCKTVRefreshMessage message = new RCKTVRefreshMessage();
        message.setName(name);
        message.setContent(content);
        sendMessage(targetId, message, callBack);
    }
}
