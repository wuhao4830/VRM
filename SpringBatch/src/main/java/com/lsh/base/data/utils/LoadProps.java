package com.lsh.base.data.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wuhao on 15/12/31.
 */
public class LoadProps {
    private static final Logger logger = LoggerFactory.getLogger(LoadProps.class);
    public static Map getMarketProps(){
        Map<String,String> marketProps=new HashMap<>();
        Properties props = new Properties();
        try {
            props.load(ClassLoader.getSystemResourceAsStream("marketProps.properties"));
            Enumeration en = props.propertyNames();
            while (en.hasMoreElements()) {
                String strKey = (String) en.nextElement();
                String strValue = props.getProperty(strKey);
                marketProps.put(strKey, strValue);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return marketProps;
    }
    public static Map getMethodStoreProps(){
       Map<String,String> methodStoreProps = new HashMap<>();
        methodStoreProps.put("常温","01");
        methodStoreProps.put("低温","02");
        methodStoreProps.put("冷藏","03");
        return methodStoreProps;
    }

}
