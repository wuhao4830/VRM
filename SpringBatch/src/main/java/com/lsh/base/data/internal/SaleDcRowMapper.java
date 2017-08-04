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
import com.lsh.base.data.SaleProperies;
import com.lsh.base.data.bean.IssueBean;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.bean.SaleDc;
import com.lsh.base.data.jira.KafkaProducer;
import com.lsh.base.data.utils.LoadProps;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class SaleDcRowMapper implements RowMapper<SaleDc> {

    private static final Logger logger = LoggerFactory.getLogger(SaleDcRowMapper.class);
    private static final String MANDT="MANDT";
    private static final String INFNR="INFNR";
    private static final String EKORG="EKORG";
    private static final String ESOKZ="ESOKZ";
    private static final String MATNR="MATNR";
    private static final String MEINS="MEINS";
    private static final String UMREZ="UMREZ";
    private static final String UMREN="UMREN";
    private static final String IDNLF="IDNLF";
    private static final String LMEIN="LMEIN";
    private static final String RUECK="RUECK";
    private static final String LIFAB="LIFAB";
    private static final String LIFBI="LIFBI";
    private static final String NETPR="NETPR";
    private static final String MINBM="MINBM";
    private static final String APLFZ="APLFZ";
    private static final String BSTMA="BSTMA";
    private static final String RDPRF="RDPRF";
    private static final String LIFNR="LIFNR";
    private static final long MAX=4294967295l;
    private static Map marketConf= LoadProps.getMarketProps();

	public SaleDc mapRow(ResultSet rs, int rowNum) throws SQLException {
        SaleDc sale = new SaleDc();
        SaleProperies properies=new SaleProperies();
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
        try {
            String marketKey=rs.getString(MANDT);
            int marketId=0;
            if(marketConf.get(marketKey)!=null){
                marketId=Integer.parseInt((String)marketConf.get(marketKey));
            }
            sale.setMarketId(marketId);
            sale.setSkuId(rs.getString(MATNR));
            sale.setSupNo(rs.getString(LIFNR));
            sale.setUnit(rs.getString(LMEIN));
            int refundabel = 1;
            if (rs.getString(RUECK).equals("01")) {
                refundabel = 0;
            }
            sale.setRefundable(refundabel);
            Date effectedAt=format.parse(rs.getString(LIFAB));
            long time=effectedAt.getTime();
            if(time/1000>MAX){
                sale.setEffectedAt(MAX);
            }else if(time>0&&time<MAX){
                sale.setEffectedAt(time/1000);
            }else{
                sale.setEffectedAt(0);
            }
            Date endAt=format.parse(rs.getString(LIFBI));
            long endtime=endAt.getTime();
            if(endtime/1000>MAX){
                sale.setEndAt(MAX);
            }else if(endtime>0&&endtime<Integer.MAX_VALUE) {
                sale.setEndAt(endtime/1000);
            }else {
                sale.setEndAt(0);
            }
            sale.setNetPrice(rs.getBigDecimal(NETPR));
            sale.setMinQty(rs.getInt(MINBM));
            sale.setDueDay(rs.getInt(APLFZ));
            sale.setMaxQty(rs.getInt(BSTMA));

            properies.setList(rs.getString(INFNR),rs.getString(EKORG),rs.getString(ESOKZ),rs.getString(MEINS),rs.getInt(UMREZ),rs.getInt(UMREN),rs.getString(IDNLF),rs.getString(RDPRF));
            JSONArray object = JSONArray.fromObject(properies.getList());
            sale.setProperties(object.toString());
        }catch (Exception e){
            logger.error(String.format("saleDc error:%s", e.toString()));
            //发消息
            IssueInfo info=new IssueInfo();
            String desc="error:"+e.toString()+",value:"+ JSON.toJSON(sale).toString();
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
        return sale;
	}

}
