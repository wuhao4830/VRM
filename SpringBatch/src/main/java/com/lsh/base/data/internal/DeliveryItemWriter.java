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
import com.lsh.base.data.DeliveryWriterDao;
import com.lsh.base.data.bean.Delivery;
import com.lsh.base.data.bean.IssueBean;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.jira.KafkaProducer;
import com.lsh.base.data.utils.MailUtils;
import com.lsh.base.data.utils.SmsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Delegates actual writing to a custom DAO. 
 * 
 * @author wuhao
 */
public class DeliveryItemWriter implements ItemWriter<Delivery> {

	private static final Logger logger = LoggerFactory.getLogger(DeliveryItemWriter.class);

	private DeliveryWriterDao deliveryWriterDao;

	public DeliveryWriterDao getDeliveryWriterDao() {
		return deliveryWriterDao;
	}

	public void setDeliveryWriterDao(DeliveryWriterDao deliveryWriterDao) {
		this.deliveryWriterDao = deliveryWriterDao;
	}

	public void write(List<? extends Delivery> deliveries)  {
		Map errorMap=new HashMap();
		for (Delivery delivery : deliveries) {
			try {
				errorMap=deliveryWriterDao.writeDelivery(delivery,errorMap);
			}catch (Exception e){
				IssueInfo info=new IssueInfo();
				errorMap.put(delivery.getSkuId(),"数据异常:"+e.toString());
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
		}
//		try {
//			if(!errorMap.isEmpty()){
//				System.out.println("");
//				MailUtils.send("美批库存异常","异常商品码:"+errorMap.toString());
//				SmsUtils.sender("美批库存异常，异常商品码详情请看邮件");
//			}
//		}catch (Exception e){
//			logger.error("报警发送异常:"+e.toString());
//		}

	}

}
