package com.lsh.base.data.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by wuhao on 16/3/30.
 */
public class GenVals {
    protected RedisTemplate<Serializable, Serializable> redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(GenVals.class);

    public void setRedisTemplate(RedisTemplate<Serializable, Serializable> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveBarCodeInfo(final String strKey) {
        logger.info("gen in old barcode:"+strKey);
        redisTemplate.execute(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key=redisTemplate.getStringSerializer().serialize("old_barcode."+strKey);
                byte[] val=redisTemplate.getStringSerializer().serialize("old_barcode." + strKey);

                connection.set(key,val);
                connection.expire(key, 2 * 60 * 60);
                return null;
            }
        });
    }
    public void saveIsChangeVal(final String strKey) {
        logger.info("gen in change:"+strKey);
        redisTemplate.execute(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key=redisTemplate.getStringSerializer().serialize("change."+strKey);
                byte[] val=redisTemplate.getStringSerializer().serialize("change."+strKey);

                connection.set(key,val);
                connection.expire(key, 2 * 60 * 60);
                return null;
            }
        });
    }
    public void saveOldCommodityVals(final String oldSkukey) {
        logger.info("gen in old:"+oldSkukey);
        redisTemplate.execute(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key=redisTemplate.getStringSerializer().serialize("old.sku."+oldSkukey);
                byte[] val=redisTemplate.getStringSerializer().serialize("old.sku."+oldSkukey);

                connection.set(key, val);
                connection.expire(key, 2 * 60 * 60);
                return null;
            }
        });
    }
    public void saveOldSaleCommodityVals(final String oldSkukey) {
        logger.info("gen in old_sale:"+oldSkukey);
        redisTemplate.execute(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key=redisTemplate.getStringSerializer().serialize("old.sale."+oldSkukey);
                byte[] val=redisTemplate.getStringSerializer().serialize("old.sale."+oldSkukey);

                connection.set(key, val);
                connection.expire(key,2*60*60);
                return null;
            }
        });
    }
    public String getIsChangeVal(final String Strkey) {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {

                byte[] key = redisTemplate.getStringSerializer().serialize("change."+Strkey);
                if (connection.exists(key)) {
                    byte[] value = connection.get(key);
                    String str = redisTemplate.getStringSerializer().deserialize(value);
                    return str;
                }
                return null;
            }
        });
    }
    public String getOldCommodityVals(final String strKey) {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key = redisTemplate.getStringSerializer().serialize("old.sku."+strKey);
                if (connection.exists(key)) {
                    byte[] value = connection.get(key);
                    String str = redisTemplate.getStringSerializer().deserialize(value);
                    return str;
                }
                return null;
            }
        });
    }
    public String getOldSaleCommodityVals(final String strKey) {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key = redisTemplate.getStringSerializer().serialize("old.sale."+strKey);
                if (connection.exists(key)) {
                    byte[] value = connection.get(key);
                    String str = redisTemplate.getStringSerializer().deserialize(value);
                    return str;
                }
                return null;
            }
        });
    }
    public String getOldBarcodeVals(final String strKey) {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key = redisTemplate.getStringSerializer().serialize("old_barcode."+strKey);
                if (connection.exists(key)) {
                    byte[] value = connection.get(key);
                    String str = redisTemplate.getStringSerializer().deserialize(value);
                    return str;
                }
                return null;
            }
        });
    }
    public static String changgePro(String oldProperties,String newProperties) {
        List oldList = JSONObject.parseArray(oldProperties);
        List newList = JSONObject.parseArray(newProperties);
        Map toMap = getMapByPro(newList);
        Boolean is_have_type = false;
        for (int i = 0; i < newList.size(); i++) {
            Map mapVal = (Map) newList.get(i);
            if (mapVal.get("name").equals("zzseaart")) {
                mapVal.put("value", toMap.get("zzseaart"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("lvorm")) {
                mapVal.put("value", toMap.get("lvorm"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("cd")) {
                mapVal.put("value", toMap.get("cd"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("specQt")) {
                mapVal.put("value", toMap.get("specQt"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("specUt")) {
                mapVal.put("value", toMap.get("specUt"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("grade")) {
                mapVal.put("value", toMap.get("grade"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("meins")) {
                mapVal.put("value", toMap.get("meins"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("mtart")) {
                mapVal.put("value", toMap.get("mtart"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("attyp")) {
                mapVal.put("value", toMap.get("attyp"));
                oldList.set(i, mapVal);
            }

            if (mapVal.get("name").equals("comp")) {
                mapVal.put("value", toMap.get("comp"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("methodeat")) {
                mapVal.put("value", toMap.get("methodeat"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("methodstore")) {
                mapVal.put("value", toMap.get("methodstore"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("mhdrz")) {
                mapVal.put("value", toMap.get("mhdrz"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("mhdhb")) {
                mapVal.put("value", toMap.get("mhdhb"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("laeng")) {
                mapVal.put("value", toMap.get("laeng"));
                oldList.set(i, mapVal);
            }


            if (mapVal.get("name").equals("breit")) {
                mapVal.put("value", toMap.get("breit"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("hoehe")) {
                mapVal.put("value", toMap.get("hoehe"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("meabm")) {
                mapVal.put("value", toMap.get("meabm"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("brgew")) {
                mapVal.put("value", toMap.get("brgew"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("gewei")) {
                mapVal.put("value", toMap.get("gewei"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("specNam")) {
                mapVal.put("value", toMap.get("specNam"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("zzseaem")) {
                mapVal.put("value", toMap.get("zzseaem"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("zzseasm")) {
                mapVal.put("value", toMap.get("zzseasm"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("brand_type")) {
                is_have_type = true;
                mapVal.put("value", toMap.get("brand_type"));
                oldList.set(i, mapVal);
            }
        }
        if(is_have_type==false){
            Map map = new HashMap();
            map.put("name","brand_type");
            map.put("value",toMap.get("brand_type"));
        }
        String result = JSONObject.toJSONString(oldList);
        logger.info("map:" + result.toString());
        return result;
    }


    public static String changgeSalePro(String oldProperties,String newProperties) {
        List oldList = JSONObject.parseArray(oldProperties);
        List newList = JSONObject.parseArray(newProperties);
        Map toMap = getMapByPro(newList);
        for (int i = 0; i < newList.size(); i++) {
            Map mapVal = (Map) newList.get(i);
            if (mapVal.get("name").equals("infnr")) {
                mapVal.put("value", toMap.get("infnr"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("ekorg")) {
                mapVal.put("value", toMap.get("ekorg"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("esokz")) {
                mapVal.put("value", toMap.get("esokz"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("meins")) {
                mapVal.put("value", toMap.get("meins"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("umrez")) {
                mapVal.put("value", toMap.get("umrez"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("umren")) {
                mapVal.put("value", toMap.get("umren"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("idnlf")) {
                mapVal.put("value", toMap.get("idnlf"));
                oldList.set(i, mapVal);
            }
            if (mapVal.get("name").equals("rdprf")) {
                mapVal.put("value", toMap.get("rdprf"));
                oldList.set(i, mapVal);
            }

        }
        String result = JSONObject.toJSONString(oldList);
        logger.info("map:" + result.toString());
        return result;
    }
    public static Map getMapByPro(List<Map> mapList) {
        Map map=new HashMap();
        for (Map mapVal:mapList){
            map.put(mapVal.get("name"),mapVal.get("value"));
        }
        return map;
    }
}
