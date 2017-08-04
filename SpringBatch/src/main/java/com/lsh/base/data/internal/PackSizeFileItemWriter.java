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
import com.lsh.base.data.PackSizeFileWriteDao;
import com.lsh.base.data.bean.IssueBean;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.bean.PackBean;
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
public class PackSizeFileItemWriter implements ItemWriter<PackBean> {
	private static final Logger logger = LoggerFactory.getLogger(PackSizeFileItemWriter.class);
	private PackSizeFileWriteDao packSizeFileWriteDao;


	public PackSizeFileWriteDao getPackSizeFileWriteDao() {
		return packSizeFileWriteDao;
	}


	/**
	 * Public setter for the {@link PackSizeFileWriteDao}.
	 * @param packSizeFileWriteDao the {@link PackSizeFileWriteDao} to set
	 */

	public void setPackSizeFileWriteDao(PackSizeFileWriteDao packSizeFileWriteDao) {
		this.packSizeFileWriteDao = packSizeFileWriteDao;
	}



	public void write(List<? extends PackBean> packBeans)  {
		for (PackBean packBean : packBeans) {
			try {
					packSizeFileWriteDao.updateSKu(packBean);

			}catch (Exception e){
				logger.error(String.format(" update ugroup error %s",e.toString()));
				IssueInfo info=new IssueInfo();
				String desc="error:"+e.toString()+",value:"+ JSON.toJSON(packBean).toString();
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
