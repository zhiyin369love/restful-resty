package com.qianmo.eshop.jpush;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.APIConnectionException;
import cn.jpush.api.common.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import cn.jpush.api.push.model.notification.PlatformNotification;

/**
 * Title:
 * Description
 * Copyringt :
 * Company : 安徽阡陌网络科技有限公司
 *
 * @author fxg on 16-4-11
 * @version 1.0
 */
public class JPushClientServer {

    private static final String appKey = "90dfa31987a482d15bb32e41";
    private static final String masterSecret = "03d03b48bd2588846122229b";

    public static void pushMassage(String username,String message,String orderId){
        send(username, message, orderId);
    }

    private static void send(String username,String message,String orderNum){
        JPushClient jpushClient = new JPushClient(masterSecret,appKey);
        PushPayload payload = buildPushObject(username,message,orderNum);
        try {
            PushResult result = jpushClient.sendPush(payload);
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (APIRequestException e) {
            e.printStackTrace();
        }

    }

    public static PushPayload buildPushObject(String username,String message,String orderId) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(username))
                .setMessage(Message.newBuilder()
                    .setMsgContent(message)
                    .addExtra("orderNum", orderId).build())
                //.setNotification(Notification.alert(message))
                .build();
    }

    public static  PushPayload build(String username,String message){
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(username))
                .setNotification(Notification.alert(message))
                .build();
    }

}
