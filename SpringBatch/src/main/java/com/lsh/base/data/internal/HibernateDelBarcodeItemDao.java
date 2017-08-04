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

import com.lsh.base.data.BarcodeItemWriteDao;
import com.lsh.base.data.bean.BarcodeItem;
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
public class HibernateDelBarcodeItemDao implements
		BarcodeItemWriteDao, RepeatListener {
	private List<Throwable> errors = new ArrayList<Throwable>();
	private SessionFactory sessionFactory;
	private static final Logger logger = LoggerFactory.getLogger(HibernateDelBarcodeItemDao.class);
	private static Map isChangeVals= LoadCommodityProps.getIsChangeVals();
	private GenVals genVals;
	private JdbcTemplate jdbcTemplate;
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	public void setGenVals(GenVals genVals) {
		this.genVals = genVals;
	}

	private static String updateSql="update item_barcode_info set is_valid=0,updated_at=? where sku_id=? and market_id=? and is_valid=1 and barcode=?";
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
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
	public void writeBarcodeItem(BarcodeItem barcodeItem) {
		logger.info(String.format("get barcodeItem:%s",barcodeItem.getSkuId()));

		String key = "barcode:"+barcodeItem.getBarcode()+"market_id:"+barcodeItem.getMarketId()+"sku_id:"+barcodeItem.getSkuId();

//		Session session;//hibernate会话
//		Transaction transaction; //hiberante事务
//		session=sessionFactory.openSession();
//		transaction = session.beginTransaction();
//
//		String selectHql="select barcodeItems from BarcodeItem as barcodeItems where barcodeItems.skuId=? and barcodeItems.marketId=? and barcodeItems.barcode=? and barcodeItems.isValid=1";
//		Query query=session.createQuery(selectHql);
//		query.setString(0, barcodeItem.getSkuId());
//		query.setInteger(1, barcodeItem.getMarketId());
//		query.setString(2, barcodeItem.getBarcode());
//		List list=query.list();
//		if(list.isEmpty()){
////			logger.info(String.format("save article:%s",barcodeItem.toString()));
////			session.save(barcodeItem);
//		}else {
//
//			logger.info(String.format("update article:%s",barcodeItem.toString()));
//			barcodeItem
//		}
//		transaction.commit();
//		session.close();
		if (genVals.getOldBarcodeVals(key) == null) {
			logger.info(String.format("update barcodeItem:%s", barcodeItem.toString()));
			Object obj[] = {barcodeItem.getUpdatedAt(),barcodeItem.getSkuId(),barcodeItem.getMarketId(),barcodeItem.getBarcode()};
			this.jdbcTemplate.update(updateSql, obj);
		}
//		if(genVals.getIsChangeVal(article.getSkuId() + article.getMarketId())==null) {
//			if (genVals.getOldCommodityVals(article.getSkuId() + article.getMarketId())==null) {
//				logger.info(String.format("save article:%s",article.toString()));
//				Object obj[]={article.getSkuId(),article.getMarketId(),article.getName(),article.getBrand(),article.getTax(),article.getBarcode(),
//						article.getTopCat(),article.getSecondCat(),article.getThirdCat(),article.getProperties(),article.getIsValid(),article.getStatus(),article.getCreatedBy(),article.getCreateAt(),article.getUpdatedBy(),article.getUpdatedAt(),article.getPicUrl()};
//				this.jdbcTemplate.update(insertSql,obj);
//			}else {
//				logger.info(String.format("update article:%s",article.toString()));
//				Object obj[]={article.getName(),article.getBrand(),article.getBarcode(),article.getTopCat(),article.getSecondCat(),article.getThirdCat(),article.getUpdatedBy(),article.getUpdatedAt(),article.getSkuId(),article.getMarketId()};
//				this.jdbcTemplate.update(updateSql,obj);
//			}
//		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.io.OutputSource#write(java.lang.Object)
	 */
	public void write(Object output) throws Exception{
		writeBarcodeItem((BarcodeItem) output);
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
