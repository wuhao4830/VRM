package com.lsh.base.data.utils;

import com.alibaba.fastjson.JSONObject;
import net.sf.json.JSON;

import java.io.File;
import java.util.*;

/**
 * Created by wuhao on 15/12/31.
 */
public class Test {
    public static void main(String args[]){
//        Map<String,String> map=LoadCommodityProps.getCommodityVals();
//        int i=0;
//        for ( String key : map.keySet()) {
//            i++;
//            System.out.println("key= "+ key + " and value= " + map.get(key));
//        }
//        System.out.println("i="+i);
//        String tmp="HUA8-T99";
//
//        String valus[]=tmp.split("-");
//        System.out.println(valus[1]);
//        System.out.println(valus[1].charAt(0)=='T');
//        System.out.println(tmp.charAt(0));
//        String skuId="000000000000374582";
//        int marketId=1;
//        String key=skuId+marketId;
//        System.out.println(key);

        //发送 GET 请求
//        String s=HttpRequest.sendGet("http://localhost:6144/Home/RequestString", "key=123&v=456");
//        System.out.println(s);
//
//        //发送 POST 请求
//        String sr=HttpRequest.sendPost("https://www.baidu.com/s", "ie=UTF-8&wd=dwadawd");
//        System.out.println(sr);
            testObj();
        String sr=HttpRequest.sendPost("https://www.baidu.com/s", "ie=UTF-8&wd=dwadawd");
        System.out.println(sr);

//        int i=1;
//        for(int j=1;j<10;j++) {
//           i= add(i);
//            System.out.println("main:" + i);
//        }
//        Map errorMap=new HashMap();
//        errorMap.put("456342","美批库存异常");
//        errorMap.put("342412","美批库存异常");
//        System.out.print(errorMap.toString());
    }
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

        }
        return marketProps;
    }
    public static void TestWork(){
        File file=new  File("/Users/wuhao/test.txt");
        String path=file.getPath();
        System.out.println(file.getAbsolutePath());
        System.out.println(path);
    }
    public static int add(int i){
        i=i+1;
        System.out.println("add:" + i);
        return i;
    }
    public static void testObj() {
        List list=new ArrayList();
        list.add("test1");
        list.add("test2");
        String jsonTest= JSONObject.toJSONString(list);
        System.out.println("json:"+jsonTest);
        List list1=JSONObject.parseArray(jsonTest);
        System.out.println("object:" + list1.toString());
    }
}
