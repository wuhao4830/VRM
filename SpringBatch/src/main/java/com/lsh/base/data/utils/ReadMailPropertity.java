package com.lsh.base.data.utils;

/**
 * Created by wuhao on 16/5/19.
 */
import java.io.IOException;
import java.util.Properties;
public class ReadMailPropertity {
    static Properties props = new Properties();
    static {
        try {
            props.load(ClassLoader.getSystemResourceAsStream("mail.properties"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public static String getProperty(String key) {
        return props.getProperty(key);
    }
}