package com.lsh.base.data.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wuhao on 16/5/18.
 */
public class SmsUtils {
    private static final Logger logger = LoggerFactory.getLogger(SmsUtils.class);
    public static void sender(String content){
        String [] tosList={"18301170720","18701496016","18612462549","18001215817"};
        for(String tos:tosList) {
            String params = "tos=" + tos + "&" + "content=" + content;
            //发送 POST 请求
            String s = HttpRequest.sendPost("http://192.168.21.56:8899/alarm/sender/sms", params);
            logger.info("短信已发送:" + s);
        }
    }
}
