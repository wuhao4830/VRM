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

import com.lsh.base.data.SiteArticleWriterDao;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lsh.base.data.bean.Sitearticle;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatListener;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuhao
 *
 */
public class HibernateSiteWriterDao implements
		SiteArticleWriterDao, RepeatListener {
	private List<Throwable> errors = new ArrayList<Throwable>();
	private SessionFactory sessionFactory;
	private static final Logger logger = LoggerFactory.getLogger(HibernateSiteWriterDao.class);

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
	 * @see org.springframework.batch.sample.domain.trade.internal.CustomerCreditWriter#write(org.springframework.batch.sample.domain.Sitearticle)
	 */
	public void writeSiteArticle(Sitearticle sitearticle) throws Exception{
		logger.info(String.format("save sitearticle:%s",sitearticle.toString()));
		Session session;//hibernate会话
		Transaction transaction; //hiberante事务
		String selectHql="select sitearticles from Sitearticle as sitearticles where sitearticles.skuId=? and sitearticles.marketId=? and sitearticles.shopId=?";
		session=sessionFactory.getCurrentSession();
		transaction = session.beginTransaction();
		Query query=session.createQuery(selectHql);
		query.setString(0, sitearticle.getSkuId());
		query.setInteger(1, sitearticle.getMarketId());
		query.setString(2, sitearticle.getShopId());
		List list=query.list();
		if(list.isEmpty()){
			session.save(sitearticle);
		}else {
			Sitearticle sqlSiteArticle=(Sitearticle)list.get(0);
			sqlSiteArticle.setUpdateAt(sitearticle.getUpdateAt());
			sqlSiteArticle.setCreateAt(sitearticle.getCreateAt());
			sqlSiteArticle.setDcId(sitearticle.getDcId());
			sqlSiteArticle.setDeliveryType(sitearticle.getDeliveryType());
			sqlSiteArticle.setDueDate(sitearticle.getDueDate());
			session.update(sqlSiteArticle);
		}
		transaction.commit();


	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.io.OutputSource#write(java.lang.Object)
	 */
	public void write(Object output)throws Exception{
		writeSiteArticle((Sitearticle) output);
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
