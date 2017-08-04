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
import com.lsh.base.data.SkuFileWriteDao;
import com.lsh.base.data.bean.IssueBean;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.bean.SkuFileBean;
import com.lsh.base.data.jira.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Delegates actual writing to a custom DAO. 
 * 
 * @author wuhao
 */
public class SkuFileItemWriter implements ItemWriter<SkuFileBean> {
	private static final Logger logger = LoggerFactory.getLogger(SkuFileItemWriter.class);
	private SkuFileWriteDao skuFileWriteDao;


	public SkuFileWriteDao getSkuFileWriteDao() {
		return skuFileWriteDao;
	}

	/**
	 * Public setter for the {@link SkuFileWriteDao}.
	 * @param skuFileWriteDao the {@link SkuFileWriteDao} to set
	 */

	public void setSkuFileWriteDao(SkuFileWriteDao skuFileWriteDao) {
		this.skuFileWriteDao = skuFileWriteDao;
	}


	public void write(List<? extends SkuFileBean> skuFileBeans)  {
		for (SkuFileBean skuFileBean : skuFileBeans) {
			try {
				if(!skuFileBean.getBarCode().isEmpty()) {
					skuFileWriteDao.writeSkuFileBean(skuFileBean);
				}
			}catch (Exception e){
				logger.error(String.format(" save article error %s",e.toString()));
				IssueInfo info=new IssueInfo();
				String desc="error:"+e.toString()+",value:"+ JSON.toJSON(skuFileBean).toString();
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
	}

}
