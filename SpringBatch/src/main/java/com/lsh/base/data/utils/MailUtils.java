package com.lsh.base.data.utils;

/**
 * Created by wuhao on 16/5/18.
 */
public class MailUtils {
    public static void send(String subject,String content)throws Exception{

        String hostName = ReadMailPropertity.getProperty("emailsmtp");
        String fromAddress = ReadMailPropertity.getProperty("emailaddress");
        String fromAPass = ReadMailPropertity.getProperty("emailpass");
        String sendTo=ReadMailPropertity.getProperty("sendTo");

        EmailHandle emailHandle = new EmailHandle(hostName);
        emailHandle.setFrom(fromAddress);
        emailHandle.setNeedAuth(true);
        emailHandle.setSubject(subject);
        emailHandle.setBody(content);
        emailHandle.setTo(sendTo);
        emailHandle.setFrom(fromAddress);
        emailHandle.setNamePass(fromAddress, fromAPass);
        emailHandle.sendEmail();
    }
}
