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
import com.lsh.base.data.UgroupFileWriteDao;
import com.lsh.base.data.bean.IssueBean;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.bean.UgroupBean;
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
public class UgroupFileItemWriter implements ItemWriter<UgroupBean> {
	private static final Logger logger = LoggerFactory.getLogger(UgroupFileItemWriter.class);
	private UgroupFileWriteDao ugroupFileWriteDao;


	public UgroupFileWriteDao getUgroupFileWriteDao() {
		return ugroupFileWriteDao;
	}


	/**
	 * Public setter for the {@link UgroupFileWriteDao}.
	 * @param ugroupFileWriteDao the {@link UgroupFileWriteDao} to set
	 */

	public void setUgroupFileWriteDao(UgroupFileWriteDao ugroupFileWriteDao) {
		this.ugroupFileWriteDao = ugroupFileWriteDao;
	}



	public void write(List<? extends UgroupBean> ugroupBeans)  {
		for (UgroupBean ugroupBean : ugroupBeans) {
			try {
					ugroupFileWriteDao.writeUgroupFileBean(ugroupBean);

			}catch (Exception e){
				logger.error(String.format(" update ugroup error %s",e.toString()));
				IssueInfo info=new IssueInfo();
				String desc="error:"+e.toString()+",value:"+ JSON.toJSON(ugroupBean).toString();
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
