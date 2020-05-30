package com.hackhu.seckill.sms;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author hackhu
 * @date 2020/5/21
 */
@Component
public class SMSUtils {
    private static String secretId="AKIDoQrdGlNC93ls35ejLl282jKaf2E0LL4e";
    private static String secretKey="rUqa4HDsNlDNN7Zjb2vKSIuOby90atug";
    private static String region="ap-guangzhou";
    private static String appId="1400373529";
    private static String sign="hackhu学习编程";
    private static String senderId="";
    private static String templateId="611968";
    private static String prefix = "+86";
    public static void sendSMS(String telephone, String otpCode) {
        Credential credential = new Credential(secretId, secretKey);

        SmsClient client = new SmsClient(credential, region);
        SendSmsRequest request = new SendSmsRequest();
        request.setSmsSdkAppid(appId);
        request.setSign(sign);
        request.setSenderId(senderId);
        request.setTemplateID(templateId);
        String[] phoneNumbers = {prefix + telephone};
        request.setPhoneNumberSet(phoneNumbers);
        String[] templateParams = {otpCode};
        request.setTemplateParamSet(templateParams);
        try {
            SendSmsResponse response = client.SendSms(request);
            System.out.printf(SendSmsResponse.toJsonString(response));
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
    }
}
