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
import com.lsh.base.data.bean.IssueBean;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.jira.KafkaProducer;
import com.lsh.base.data.utils.LoadProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lsh.base.data.bean.Sitearticle;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class SitearticleRowMapper implements RowMapper<Sitearticle> {

    private static final Logger logger = LoggerFactory.getLogger(SitearticleRowMapper.class);
    private static final String WERKS="WERKS";
    private static final String MATNR="MATNR";
    private static final String LIFNR_INNER="LIFNR_INNER";
    private static final String PLIFZ="PLIFZ";
    private static final String CDFLAG="CDFLAG";
    private static final String MANDT="MANDT";
    private static Map marketConf= LoadProps.getMarketProps();

	public Sitearticle mapRow(ResultSet rs, int rowNum) throws SQLException {

        Sitearticle sitearticle = new Sitearticle();
        try {
            sitearticle.setShopId(rs.getString(WERKS));
            sitearticle.setSkuId(rs.getString(MATNR));
            sitearticle.setDcId(rs.getString(LIFNR_INNER));
            sitearticle.setDueDate(rs.getInt(PLIFZ));
            String cdflag=rs.getString(CDFLAG);
            if(cdflag.equals("在库")){
                sitearticle.setDeliveryType(0);
            }else {
                sitearticle.setDeliveryType(1);
            }
            String marketKey=rs.getString(MANDT);
            int marketId=0;
            if(marketConf.get(marketKey)!=null){
                marketId=Integer.parseInt((String)marketConf.get(marketKey));
            }else {
                logger.info("is null"+marketKey);
            }
            sitearticle.setMarketId(marketId);
        }catch (Exception e){
            logger.error(String.format("sitearticle error:%s", e.toString()));
            //发消息
            IssueInfo info=new IssueInfo();
            String desc="error:"+e.toString()+",value:"+ JSON.toJSON(sitearticle).toString();
            IssueBean bean=new IssueBean();
            bean.setDescription(desc);
            info.setIssue(bean);
            try {
                KafkaProducer kafkaProducer=KafkaProducer.getInstance();
                kafkaProducer.produce(info);
            }catch (Exception error){
                logger.error(error.getMessage(),error);
            }
        }

        return sitearticle;
	}

}
