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
import com.lsh.base.data.bean.OldSalekey;
import com.lsh.base.data.bean.OldSkukey;
import com.lsh.base.data.jira.KafkaProducer;
import com.lsh.base.data.utils.GenVals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Delegates actual writing to a custom DAO. 
 * 
 * @author wuhao
 */
public class OldSaleItemWriter implements ItemWriter<OldSalekey> {

	private static final Logger logger = LoggerFactory.getLogger(OldSaleItemWriter.class);

	private GenVals genVals;

	public void setGenVals(GenVals genVals) {
		this.genVals = genVals;
	}

	public void write(List<? extends OldSalekey> oldSalekeys)  {
		for (OldSalekey oldSalekey : oldSalekeys) {
			try {
				genVals.saveOldSaleCommodityVals(oldSalekey.getSkuId()+oldSalekey.getMarketId()+oldSalekey.getSupNo());
			}catch (Exception e){
				IssueInfo info=new IssueInfo();
				String desc="error:"+e.toString()+",value:"+ JSON.toJSON(oldSalekey).toString();
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

		}
	}

}
