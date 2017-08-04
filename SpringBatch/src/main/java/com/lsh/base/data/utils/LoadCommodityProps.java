package com.lsh.base.data.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.util.parsing.input.StreamReader;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wuhao on 15/12/31.
 */
public class LoadCommodityProps {
    private static final Logger logger = LoggerFactory.getLogger(LoadCommodityProps.class);
    private static final DecimalFormat df=new DecimalFormat("000000000000000000");
    public static Map getCommodityVals() {
        InputStream is= ClassLoader.getSystemResourceAsStream("insert_commodity.txt");
        Map<String,String> commodityVals=new HashMap<>();
        byte[] buffer = new byte[1024];
        try {
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            int len = -1;
            while ((len = is.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            String datas = outSteam.toString();
            String[] dataArry = datas.split("\n");
            for (String sku : dataArry) {
                String strKey = sku.trim();
                String strValue = strKey;
                commodityVals.put(strKey, strValue);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        logger.info("data:"+commodityVals.toString());
        return commodityVals;
    }
    public static Map testLoad(){
        //String filePath = LoadCommodityProps.class.getClassLoader().getResource("insert_commodity.txt").getPath();
        String filePath ="/home/dev/wuhao/SpringBatch/target/";
        Map<String,String> commodityVals=new HashMap<>();
        int i=1;
        try{
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String sku = br.readLine();//一次读入一行，直到读入null为文件结束
            while( sku!=null){
                String strKey=sku;
                String strValue =strKey;
                String key="";
                String valus="";
                String maches="^\\d{18}$";
                if(strKey.matches(maches)) {
                    if (Integer.parseInt(strKey) > 100000000) {
                        key = df.format(Integer.parseInt(strKey) / 1000);
                        valus = df.format(Integer.parseInt(strValue) / 1000);
                    } else {
                        key = df.format(strKey);
                        valus = df.format(strValue);
                    }
                }else{
                    key = strKey;
                    valus = strValue;
                }
                commodityVals.put(key, valus);

                sku = br.readLine(); //接着读下一行
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return commodityVals;
    }
    public static Map getSaleCommodityVals() {
        InputStream is= ClassLoader.getSystemResourceAsStream("insert_commodity.txt");
        Map<String,String> commodityVals=new HashMap<>();
        byte[] buffer = new byte[1024];
        try {
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            int len = -1;
            while ((len = is.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            String datas = outSteam.toString();
            String[] dataArry = datas.split("\n");
            for (String sku : dataArry) {
                String strKey = sku.trim();
                String strValue = strKey;
                String key = "";
                String valus = "";
                String maches = "^\\d{18}$";
                System.out.println(strKey);
                if (strKey.matches(maches)) {

                    if (Integer.parseInt(strKey) > 100000000) {
                        key = df.format(Integer.parseInt(strKey) / 1000);
                        valus = df.format(Integer.parseInt(strValue) / 1000);
                    } else {
                        key = strKey;
                        valus = strValue;
                    }
                } else {
                    key = strKey;
                    valus = strValue;
                }
                commodityVals.put(key, valus);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        logger.info("data:"+commodityVals.toString());
        return commodityVals;
    }
    public static void main(String args[])throws Exception{

    }
    public static Map getIsChangeVals() {
        File file=new  File("/home/work/wuhao/SpringBatch/target/isChangeCommodity.txt");
        logger.info("len:"+file.length());
        Map<String,String> isChangeVals=new HashMap<>();
        String tempString = null;
        try {
            BufferedReader reader =new BufferedReader(new FileReader(file));
            while ((tempString = reader.readLine()) != null) {
                String strKey = tempString.trim();
                String strValue = strKey;
                isChangeVals.put(strKey, strValue);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        logger.info("isChangeCommodity:"+isChangeVals.toString());
        return isChangeVals;
    }

}
