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

import com.lsh.base.data.SiteWriterDao;
import com.lsh.base.data.bean.Site;
import com.lsh.base.data.utils.GenVals;
import com.lsh.base.data.utils.LoadCommodityProps;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatListener;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wuhao
 *
 */
public class HibernateSiteNewWriterDao implements
		SiteWriterDao, RepeatListener {
	private List<Throwable> errors = new ArrayList<Throwable>();
	private SessionFactory sessionFactory;
	private GenVals genVals;
	private JdbcTemplate jdbcTemplate;
	private Map changeValMap = LoadCommodityProps.getCommodityVals();
	private static final Logger logger = LoggerFactory.getLogger(HibernateSiteNewWriterDao.class);
	private static Map isChangeVals= LoadCommodityProps.getIsChangeVals();
	private static String insertSql="insert into item_site_article (bwscl,prerf,rbzul,pprice,sprice,scagr,sku_id,market_id,dc_id,pack_size,is_valid,status,created_at,updated_at,kwdht) values" +
			"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static String updateSql="update item_site_article set bwscl=?,prerf=?,rbzul=?,pprice=?,sprice=?,scagr=?,dc_id=?,updated_at=? where sku_id=? and market_id=? and is_valid=1";

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setGenVals(GenVals genVals) {
		this.genVals = genVals;
	}

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
	public void writeSite(Site site)  {
		logger.info(String.format("get site:%s", site.getSkuId()));
//		if(changeValMap.get(site.getSkuId())!=null) {
		if (genVals.getIsChangeVal(site.getSkuId() + site.getMarketId()) == null) {
			Session session;//hibernate会话
			Transaction transaction; //hiberante事务
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			String selectHql = "select sites from Site as sites where sites.skuId=? and sites.marketId=? and sites.isValid=?";
			Query query = session.createQuery(selectHql);
			query.setString(0, site.getSkuId());
			query.setInteger(1, site.getMarketId());
			query.setInteger(2, site.getIsValid());
			List list = query.list();
			if (list.isEmpty()) {
				logger.info(String.format("save site:%s", site.toString()));
				session.save(site);
			} else {
				logger.info(String.format("update site:%s", site.toString()));
				Site sqlsite = (Site) list.get(0);
				sqlsite.setBwscl(site.getBwscl());
				sqlsite.setDcId(site.getDcId());
				sqlsite.setScagr(site.getScagr());
				sqlsite.setRbzul(site.getRbzul());
				sqlsite.setPrerf(site.getPrerf());
				sqlsite.setPprice(site.getPprice());
				sqlsite.setSprice(site.getSprice());
				sqlsite.setUpdatedAt(site.getUpdatedAt());
				session.update(sqlsite);
			}
			transaction.commit();
     		session.close();
		}
	}
//		Session session;//hibernate会话
//		Transaction transaction; //hiberante事务
//		session=sessionFactory.openSession();
//		transaction = session.beginTransaction();
//		String selectHql="select sites from Site as sites where sites.skuId=? and sites.marketId=? and sites.isValid=?";
//		Query query=session.createQuery(selectHql);
//		query.setString(0, site.getSkuId());
//		query.setInteger(1, site.getMarketId());
//		query.setInteger(2,site.getIsValid());
//		List list=query.list();
//		if(list.isEmpty()){
//			logger.info(String.format("save site:%s", site.toString()));
//			session.save(site);
//		}else{
//			String key =site.getSkuId()+site.getMarketId();
//			if (isChangeVals.get(key)==null){
//				logger.info(String.format("update site:%s", site.toString()));
//				Site sqlsite = (Site) list.get(0);
//				sqlsite.setBwscl(site.getBwscl());
//				sqlsite.setDcId(site.getDcId());
//				sqlsite.setScagr(site.getScagr());
//				sqlsite.setRbzul(site.getRbzul());
//				sqlsite.setPrerf(site.getPrerf());
//				sqlsite.setPprice(site.getPprice());
//				sqlsite.setSprice(site.getSprice());
//				sqlsite.setUpdatedAt(site.getUpdatedAt());
//				session.update(sqlsite);
//			}
//		}
//
//		transaction.commit();
//		session.close();
//		if (genVals.getIsChangeVal(site.getSkuId() + site.getMarketId()) == null) {
//			if (genVals.getOldCommodityVals(site.getSkuId() + site.getMarketId()) != null) {
//				logger.info(String.format("update site:%s", site.toString()));
//				Object obj[] = {site.getBwscl(), site.getPrerf(), site.getRbzul(), site.getPprice(), site.getSprice(), site.getScagr(), site.getDcId(), site.getUpdatedAt(), site.getSkuId(), site.getMarketId()};
//				this.jdbcTemplate.update(updateSql, obj);
//			} else {
//				logger.info(String.format("save site:%s", site.toString()));
//				Object obj[] = {site.getBwscl(), site.getPrerf(), site.getRbzul(), site.getPprice(), site.getSprice(), site.getScagr(), site.getSkuId(), site.getMarketId(), site.getDcId(), site.getPackSize(), site.getIsValid(), site.getStatus(), site.getCreatedAt(), site.getUpdatedAt(), site.getKwdht()};
//				this.jdbcTemplate.update(insertSql, obj);
//			}
//
//			transaction.commit();
//			session.close();
//		}
//		if (genVals.getIsChangeVal(site.getSkuId() + site.getMarketId()) == null) {
//			if (genVals.getOldCommodityVals(site.getSkuId() + site.getMarketId()) != null) {
//				logger.info(String.format("update site:%s", site.toString()));
//				Object obj[] = {site.getBwscl(), site.getPrerf(), site.getRbzul(), site.getPprice(), site.getSprice(), site.getScagr(), site.getDcId(), site.getUpdatedAt(), site.getSkuId(), site.getMarketId()};
//				this.jdbcTemplate.update(updateSql, obj);
//			} else {
//				logger.info(String.format("save site:%s", site.toString()));
//				Object obj[] = {site.getBwscl(), site.getPrerf(), site.getRbzul(), site.getPprice(), site.getSprice(), site.getScagr(), site.getSkuId(), site.getMarketId(), site.getDcId(), site.getPackSize(), site.getIsValid(), site.getStatus(), site.getCreatedAt(), site.getUpdatedAt(), site.getKwdht()};
//				this.jdbcTemplate.update(insertSql, obj);
//			}
//		}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.io.OutputSource#write(java.lang.Object)
	 */
	public void write(Object output)throws Exception{
		writeSite((Site) output);
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
