package com.qianmo.eshop.jpush;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.APIConnectionException;
import cn.jpush.api.common.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;

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

    private static final String appKey = "4eec9a5096d32823ac5355a3";
    private static final String masterSecret = "f3cc1382cb147982d5b2bb82";

    public static void main(String registrationId,String message){
        send(registrationId, message);
    }

    private static void send(String registrationId,String message){
        JPushClient jpushClient = new JPushClient(masterSecret,appKey);
        PushPayload payload = buildPushObject_all_all_alert(registrationId,message);

        try {
            PushResult result = jpushClient.sendPush(payload);
            System.out.println("推送送出");
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (APIRequestException e) {
            e.printStackTrace();
        }

    }





    public static PushPayload buildPushObject_all_all_alert(String registrationId,String message) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.registrationId(registrationId))
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(message)
                        .build())
                .build();
    }

}
