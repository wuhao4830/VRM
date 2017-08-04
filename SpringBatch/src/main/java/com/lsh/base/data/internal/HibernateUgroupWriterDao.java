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
import com.lsh.base.data.bean.SaleUpdate;
import com.lsh.base.data.bean.UgroupBean;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatListener;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuhao
 *
 */
public class HibernateUgroupWriterDao implements
		UgroupFileWriteDao, RepeatListener {
	private List<Throwable> errors = new ArrayList<Throwable>();
	private SessionFactory sessionFactory;
	private static final Logger logger = LoggerFactory.getLogger(HibernateUgroupWriterDao.class);

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Public accessor for the errors property.
	 *
	 * @return the errors - a list of Throwable instances
	 */
	public List<Throwable> getErrors() {
		return errors;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.sample.domain.trade.internal.CustomerCreditWriter#write(org.springframework.batch.sample.domain.Eina)
	 */
	public void writeUgroupFileBean(UgroupBean ugroupBean) throws Exception{
		logger.info(String.format("update ugroup:%s",ugroupBean.toString()));
		Session session;//hibernate会话
		Transaction transaction; //hiberante事务
		session=sessionFactory.openSession();
		transaction = session.beginTransaction();
		String selectHql="select saleUpdates from SaleUpdate as saleUpdates where saleUpdates.skuId=?";
		Query query=session.createQuery(selectHql);
		query.setString(0, ugroupBean.getSkuId());
		List<SaleUpdate> list=query.list();
		Map<String,Integer> sqlmap;
		Map<String,String> map;
		if(!list.isEmpty()){
			for(SaleUpdate sqlugroup:list) {
				sqlmap=getShelfMap();
				map=JSON.parseObject(ugroupBean.getShelfNum(), Map.class);
				for (String key : map.keySet()) {
					String valuestr=map.get(key);
					int value=Integer.parseInt(valuestr.trim());
					sqlmap.put(key.replace(" ",""),value);
				}
				sqlugroup.setShlefNum(JSON.toJSON(sqlmap).toString());
				logger.info(String.format("update ugroup:%s", sqlugroup.toString()));
				session.update(sqlugroup);

			}
		}
		transaction.commit();
		session.close();

	}
	public static Map<String,Integer> getShelfMap(){
		Map<String,Integer> shelfNum=new HashMap<>();
		for(int i=0;i<5;i++){
			String key="U"+i;
			shelfNum.put(key,0);
		}
		return shelfNum;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.io.OutputSource#write(java.lang.Object)
	 */
	public void write(Object output) throws Exception{
		writeUgroupFileBean((UgroupBean) output);
	}
	public void onError(RepeatContext context, Throwable e) {
		errors.add(e);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.repeat.RepeatInterceptor#after(org.springframework.batch.repeat.RepeatContext, org.springframework.batch.repeat.ExitStatus)
	 */
	public void after(RepeatContext context, RepeatStatus result) {
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.repeat.RepeatInterceptor#before(org.springframework.batch.repeat.RepeatContext)
	 */
	public void before(RepeatContext context) {
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.repeat.RepeatInterceptor#close(org.springframework.batch.repeat.RepeatContext)
	 */
	public void close(RepeatContext context) {
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.repeat.RepeatInterceptor#open(org.springframework.batch.repeat.RepeatContext)
	 */
	public void open(RepeatContext context) {
	}

}
