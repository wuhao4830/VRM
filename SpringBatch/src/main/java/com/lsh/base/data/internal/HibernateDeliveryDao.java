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

import com.lsh.base.data.DeliveryWriterDao;
import com.lsh.base.data.bean.Delivery;
import com.lsh.base.data.utils.SmsUtils;
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
import java.util.Map;

/**
 * @author wuhao
 *
 */
public class HibernateDeliveryDao implements
		DeliveryWriterDao, RepeatListener {
	private List<Throwable> errors = new ArrayList<Throwable>();
	private SessionFactory sessionFactory;
	private static final Logger logger = LoggerFactory.getLogger(HibernateDeliveryDao.class);
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
	 * @see org.springframework.batch.sample.domain.trade.internal.DeliveryWriterDao#write(org.springframework.batch.sample.domain.Article)
	 */
	public Map writeDelivery(Delivery delivery,Map errorMap) throws Exception{
		logger.info(String.format("get delivery:%s",delivery.toString()));
		Session session;//hibernate会话
		Transaction transaction; //hiberante事务
		String selectHql="select deliveries from Delivery as deliveries where deliveries.skuId=? and deliveries.marketId=? and deliveries.werks=?";
		session=sessionFactory.getCurrentSession();
		transaction = session.beginTransaction();
		Query query=session.createQuery(selectHql);
		query.setString(0, delivery.getSkuId());
		query.setInteger(1, delivery.getMarketId());
		query.setString(2,delivery.getWerks());
		List list=query.list();
		if(list.isEmpty()){
			session.save(delivery);
		}else {
			Delivery sqlDelivery=(Delivery)list.get(0);
			if(sqlDelivery.getLbkum().intValue()!=0) {
				double saleRet=delivery.getLbkum().subtract(sqlDelivery.getLbkum()).divide(sqlDelivery.getLbkum(),3).doubleValue();
				logger.info("商品码:"+delivery.getSkuId()+",原库存:"+sqlDelivery.getLbkum().intValue()+",目前库存:"+delivery.getLbkum().intValue()+",售罄率:"+saleRet);
//				if ( saleRet> 0.1) {
//					logger.info("error delivery:"+sqlDelivery.toString());
//					errorMap.put(delivery.getSkuId(),"库存售罄率超过10%"+"原库存:"+sqlDelivery.getLbkum().intValue()+"目前库存:"+delivery.getLbkum().intValue());
//				}
			}
			sqlDelivery.setKunnr(delivery.getKunnr());
			sqlDelivery.setWerks(delivery.getWerks());
			sqlDelivery.setMaktx(delivery.getMaktx());
			sqlDelivery.setMmstaTxt(delivery.getMmstaTxt());
			sqlDelivery.setZkhspbm(delivery.getZkhspbm());
			sqlDelivery.setLbkum(delivery.getLbkum());
			sqlDelivery.setMeins(delivery.getMeins());
			sqlDelivery.setZdate(delivery.getZdate());
			sqlDelivery.setZtime(delivery.getZtime());
			sqlDelivery.setZuname(delivery.getZuname());

			session.update(sqlDelivery);
		}
		transaction.commit();
		return errorMap;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.io.OutputSource#write(java.lang.Object)
	 */
	public void write(Object output,Map errorMap) throws Exception{
		writeDelivery((Delivery) output,errorMap);
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
