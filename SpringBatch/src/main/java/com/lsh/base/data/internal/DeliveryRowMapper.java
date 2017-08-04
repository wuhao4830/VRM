/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lsh.base.data.internal;

import com.alibaba.fastjson.JSON;
import com.lsh.base.data.bean.Delivery;
import com.lsh.base.data.bean.IssueBean;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.jira.KafkaProducer;
import com.lsh.base.data.utils.LoadProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Map;

public class DeliveryRowMapper implements RowMapper<Delivery> {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryRowMapper.class);
    private static final String MANDT="MANDT";
    private static final String MATNR="MATNR";
    private static final String KUNNR="KUNNR";
    private static final String WERKS="WERKS";
    private static final String MAKTX="MAKTX";
    private static final String MMSTA_TXT="MMSTA_TXT";
    private static final String ZKHSPBM="ZKHSPBM";
    private static final String LBKUM="LBKUM";
    private static final String MEINS="MEINS";
    private static final String ZDATE="ZDATE";
    private static final String ZTIME="ZTIME";
    private static final String ZUNAME="ZUNAME";
    private static final DecimalFormat df=new DecimalFormat("000000000000000000");
    private static Map marketConf= LoadProps.getMarketProps();

	public Delivery mapRow(ResultSet rs, int rowNum) throws SQLException {
        Delivery delivery = new Delivery();
        try {
            String marketKey=rs.getString(MANDT);
            int marketId=0;
            if(marketConf.get(marketKey)!=null){
                marketId=Integer.parseInt((String)marketConf.get(marketKey));
            }
            delivery.setMarketId(marketId);
            String skuId=rs.getString(MATNR);
            if(skuId.matches("^[0-9]*$")){
                delivery.setSkuId(df.format(Integer.parseInt(skuId)));
            }else {
                delivery.setSkuId(skuId);
            }
            delivery.setKunnr(rs.getString(KUNNR));
            delivery.setWerks(rs.getString(WERKS));
            delivery.setMaktx(rs.getString(MAKTX));
            delivery.setMmstaTxt(rs.getString(MMSTA_TXT));
            delivery.setZkhspbm(rs.getString(ZKHSPBM));
            delivery.setLbkum(rs.getBigDecimal(LBKUM));
            delivery.setMeins(rs.getString(MEINS));
            delivery.setZdate(rs.getString(ZDATE));
            delivery.setZtime(rs.getString(ZTIME));
            delivery.setZuname(rs.getString(ZUNAME));
        }catch (Exception e){
            logger.error(String.format("sale error:%s", e.toString()));
            //发消息
            IssueInfo info=new IssueInfo();
            String desc="error:"+e.toString()+",value:"+ JSON.toJSON(delivery).toString();
            IssueBean bean=new IssueBean();
            bean.setDescription(desc);
            info.setIssue(bean);
            try {
                KafkaProducer kafkaProducer= KafkaProducer.getInstance();
                kafkaProducer.produce(info);
            }catch (Exception error){
                logger.error(error.getMessage(),error);
            }
        }
        return delivery;
	}

}