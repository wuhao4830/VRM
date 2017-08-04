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
import com.lsh.base.data.bean.Status;
import com.lsh.base.data.jira.KafkaProducer;
import com.lsh.base.data.utils.LoadProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ChangeRowMapper implements RowMapper<Status> {

    private static final Logger logger = LoggerFactory.getLogger(ChangeRowMapper.class);
    private static final String SKUID="sku_id";
    private static final String MARKETID="market_id";

	public Status mapRow(ResultSet rs, int rowNum) throws SQLException {

       Status status=new Status();
        try {
            status.setSkuId(rs.getString(SKUID));
            status.setMarketId(rs.getInt(MARKETID));

        }catch (Exception e){
            logger.error(String.format("site error:%s", e.toString()));
            //发消息
            IssueInfo info=new IssueInfo();
            String desc="error:"+e.toString()+",value:"+ JSON.toJSON(status).toString();
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
        return status;
	}

}
