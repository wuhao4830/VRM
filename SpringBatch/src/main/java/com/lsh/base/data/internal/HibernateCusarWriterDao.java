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

import com.lsh.base.data.CusarticleWriterDao;
import com.lsh.base.data.SiteArticleWriterDao;
import com.lsh.base.data.bean.Cusarticle;
import com.lsh.base.data.bean.Sitearticle;
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
import java.util.List;

/**
 * @author wuhao
 *
 */
public class HibernateCusarWriterDao implements
		CusarticleWriterDao, RepeatListener {
	private List<Throwable> errors = new ArrayList<Throwable>();
	private SessionFactory sessionFactory;
	private static final Logger logger = LoggerFactory.getLogger(HibernateSaleWriterDao.class);

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
	public void writeCusarticle(Cusarticle cusarticle) throws Exception{
		logger.info(String.format("save cusarticle:%s",cusarticle.toString()));
		Session session;//hibernate会话
		Transaction transaction; //hiberante事务
		String selectHql="select cusarticles from Cusarticle as cusarticles where cusarticles.skuId=? and cusarticles.vkorg=? and cusarticles.kunnr=?";
		session=sessionFactory.getCurrentSession();
		transaction = session.beginTransaction();
		Query query=session.createQuery(selectHql);
		query.setString(0, cusarticle.getSkuId());
		query.setString(1,cusarticle.getVkorg());
		query.setString(2,cusarticle.getKunnr());
		List list=query.list();
		if(list.isEmpty()){
			session.save(cusarticle);
		}else {
			Cusarticle sqlCusarticle=(Cusarticle)list.get(0);
			sqlCusarticle.setMandt(cusarticle.getMandt());
			sqlCusarticle.setVkorg(cusarticle.getVkorg());
			sqlCusarticle.setVtweg(cusarticle.getVtweg());
			sqlCusarticle.setKunnr(cusarticle.getKunnr());
			sqlCusarticle.setKdmat(cusarticle.getKdmat());
			sqlCusarticle.setKdptx(cusarticle.getKdptx());
			sqlCusarticle.setKbetr(cusarticle.getKbetr());
			sqlCusarticle.setMmsta(cusarticle.getMmsta());
			session.update(sqlCusarticle);
		}
		transaction.commit();


	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.io.OutputSource#write(java.lang.Object)
	 */
	public void write(Object output)throws Exception{
		writeCusarticle((Cusarticle) output);
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
