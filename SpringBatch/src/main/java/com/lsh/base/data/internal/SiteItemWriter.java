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
import com.lsh.base.data.SiteWriterDao;
import com.lsh.base.data.bean.IssueBean;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.bean.Site;
import com.lsh.base.data.bean.Sitearticle;
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
public class SiteItemWriter implements ItemWriter<Site> {

	private static final Logger logger = LoggerFactory.getLogger(SiteItemWriter.class);

	private SiteWriterDao siteWriterDao;

	public SiteWriterDao getSiteWriterDao() {
		return siteWriterDao;
	}

	public void setSiteWriterDao(SiteWriterDao siteWriterDao) {
		this.siteWriterDao = siteWriterDao;
	}

	public void write(List<? extends Site> sites)  {
		for (Site site : sites) {
//			try {
				siteWriterDao.writeSite(site);
//			}catch (Exception e){
//				IssueInfo info=new IssueInfo();
//				String desc="error:"+e.toString()+",value:"+ JSON.toJSON(site).toString();
//				IssueBean bean=new IssueBean();
//				bean.setDescription(desc);
//				info.setIssue(bean);
//				try {
//					KafkaProducer kafkaProducer=KafkaProducer.getInstance();
//					kafkaProducer.produce(info);
//				}catch (Exception error){
//					logger.error(error.getMessage(),error);
//				}
//			}

		}
	}

}
