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
import com.lsh.base.data.bean.BarcodeItem;
import com.lsh.base.data.bean.IssueBean;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.bean.Sale;
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

public class BarcodeItemRowMapper implements RowMapper<BarcodeItem> {

    private static final Logger logger = LoggerFactory.getLogger(BarcodeItemRowMapper.class);
    private static final String MATNR="MATNR";
    private static final String MANDT="MANDT";
    private static final String EAN11="EAN11";

    private static Map marketConf= LoadProps.getMarketProps();

	public BarcodeItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        BarcodeItem barcodeItem = new BarcodeItem();

       // try {
            String marketKey=rs.getString(MANDT);
            int marketId=0;
            if(marketConf.get(marketKey)!=null){
                marketId=Integer.parseInt((String)marketConf.get(marketKey));
            }
            barcodeItem.setSkuId(rs.getString(MATNR));
            barcodeItem.setMarketId(marketId);
            barcodeItem.setBarcode(rs.getString(EAN11));
//        }catch (Exception e){
//            logger.error(String.format("sale error:%s", e.toString()));
//            //发消息
//            IssueInfo info=new IssueInfo();
//            String desc="error:"+e.toString()+",value:"+ JSON.toJSON(barcodeItem).toString();
//            IssueBean bean=new IssueBean();
//            bean.setDescription(desc);
//            info.setIssue(bean);
//            try {
//                KafkaProducer kafkaProducer=KafkaProducer.getInstance();
//                kafkaProducer.produce(info);
//            }catch (Exception error){
//                logger.error(error.getMessage(),error);
//            }
//        }
        return barcodeItem;
	}

}
