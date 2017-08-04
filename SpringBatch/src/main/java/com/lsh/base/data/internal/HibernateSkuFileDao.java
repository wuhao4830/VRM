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
import com.lsh.base.data.bean.Article;
import com.lsh.base.data.bean.PropertiesBean;
import com.lsh.base.data.bean.SkuFileBean;
import com.lsh.base.data.bean.SkuFilePropertiesBean;
import net.sf.json.JSONArray;
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
public class HibernateSkuFileDao implements
		SkuFileWriteDao, RepeatListener {
	private List<Throwable> errors = new ArrayList<Throwable>();
	private SessionFactory sessionFactory;
	private static final Logger logger = LoggerFactory.getLogger(HibernateSkuFileDao.class);
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
	 * @see org.springframework.batch.sample.domain.trade.internal.CustomerCreditWriter#write(org.springframework.batch.sample.domain.Article)
	 */
	public void writeSkuFileBean(SkuFileBean skuFileBean) throws Exception{
		logger.info(String.format("save skuFileBean:%s",skuFileBean.toString()));
		Session session;//hibernate会话
		Transaction transaction; //hiberante事务
		String selectHql="select articles from Article as articles where articles.barcode=?";
		session=sessionFactory.getCurrentSession();
		transaction = session.beginTransaction();
		Query query=session.createQuery(selectHql);
		query.setString(0, skuFileBean.getBarCode());
		List<Article> list=query.list();
		if(!list.isEmpty()){
			Map<String,String> map=new HashMap<String,String>();
			List<SkuFilePropertiesBean> skuFilePropertiesList= JSON.parseArray(skuFileBean.getProperties(), SkuFilePropertiesBean.class);
			for(SkuFilePropertiesBean bean:skuFilePropertiesList){
				if(bean.getName().equals("vender_place")){
					map.put("product_supply",bean.getValue());
				}else {
					map.put(bean.getName(), bean.getValue());
				}
			}
			for(Article sqlArticle:list) {
				//更新策略
				String properties=sqlArticle.getProperties();
				List<PropertiesBean> propertiesBeanList=JSON.parseArray(properties,PropertiesBean.class);
				for(PropertiesBean bean:propertiesBeanList){
					if(map.containsKey(bean.getName())){
						bean.setValue(map.get(bean.getName()));
					}
				}
				JSONArray object = JSONArray.fromObject(propertiesBeanList);
				sqlArticle.setProperties(object.toString());
				sqlArticle.setPicUrl(skuFileBean.getImgs());
				logger.info(String.format("update sku:%s", sqlArticle.toString()));
				session.update(sqlArticle);
			}
		}
		transaction.commit();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.io.OutputSource#write(java.lang.Object)
	 */
	public void write(Object output) throws Exception{
		writeSkuFileBean((SkuFileBean) output);
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
