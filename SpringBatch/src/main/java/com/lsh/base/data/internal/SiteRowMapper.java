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
import com.lsh.base.data.bean.Site;
import com.lsh.base.data.bean.Sitearticle;
import com.lsh.base.data.jira.KafkaProducer;
import com.lsh.base.data.utils.LoadProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class SiteRowMapper implements RowMapper<Site> {

    private static final Logger logger = LoggerFactory.getLogger(SiteRowMapper.class);
    private static final String SCAGR="scagr";
    private static final String RBZUL="rbzul";
    private static final String MANDT="mandt";
    private static final String MATNR="matnr";
    private static final String LIFNR_INNER="lifnr_inner";
    private static final String PRERF="prerf";
    private static final String BWSCL="bwscl";
    private static final String PPRICE="pprice";
    private static final String SPRICE="sprice";
    private static Map marketConf= LoadProps.getMarketProps();

	public Site mapRow(ResultSet rs, int rowNum) throws SQLException {

        Site site = new Site();
        try {
            site.setBwscl(rs.getString(BWSCL));
            site.setScagr(rs.getString(SCAGR));
            site.setRbzul(rs.getString(RBZUL));
            site.setPrerf(rs.getString(PRERF));
            site.setPprice(rs.getBigDecimal(PPRICE));
            site.setSprice(rs.getBigDecimal(SPRICE));
            site.setSkuId(rs.getString(MATNR));
            site.setDcId(rs.getString(LIFNR_INNER));
            String marketKey=rs.getString(MANDT);
            int marketId=0;
            if(marketConf.get(marketKey)!=null){
                marketId=Integer.parseInt((String)marketConf.get(marketKey));
            }else {
                logger.info("is null"+marketKey);
            }
            site.setMarketId(marketId);
        }catch (Exception e){
            logger.error(String.format("site error:%s", e.toString()));
            //发消息
            IssueInfo info=new IssueInfo();
            String desc="error:"+e.toString()+",value:"+ JSON.toJSON(site).toString();
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

        return site;
	}

}
