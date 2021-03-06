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
import com.lsh.base.data.bean.BarcodeItem;
import com.lsh.base.data.bean.IssueBean;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.jira.KafkaProducer;
import com.lsh.base.data.utils.LoadProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class DelBarcodeItemRowMapper implements RowMapper<BarcodeItem> {

    private static final Logger logger = LoggerFactory.getLogger(DelBarcodeItemRowMapper.class);
    private static final String sku_id="sku_id";
    private static final String market_id="market_id";
    private static final String barcode="barcode";
    private static final String is_valid="is_valid";


	public BarcodeItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        BarcodeItem barcodeItem = new BarcodeItem();

        try {
            int marketId=rs.getInt(market_id);

            barcodeItem.setSkuId(rs.getString(sku_id));
            barcodeItem.setMarketId(marketId);
            barcodeItem.setBarcode(rs.getString(barcode));
            barcodeItem.setIsValid(rs.getLong(is_valid));
        }catch (Exception e){
            logger.error(String.format("sale error:%s", e.toString()));
            //发消息
            IssueInfo info=new IssueInfo();
            String desc="error:"+e.toString()+",value:"+ JSON.toJSON(barcodeItem).toString();
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
        return barcodeItem;
	}

}
