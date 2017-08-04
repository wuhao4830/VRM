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

import com.lsh.base.data.utils.GenVals;
import com.lsh.base.data.utils.LoadCommodityProps;
import com.lsh.base.data.utils.LoadProps;
import com.thoughtworks.xstream.mapper.Mapper;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lsh.base.data.bean.Article;
import com.lsh.base.data.ArticleWriteDao;
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
public class HibernateArticleDao implements
		ArticleWriteDao, RepeatListener {
	private List<Throwable> errors = new ArrayList<Throwable>();
	private SessionFactory sessionFactory;
	private static final Logger logger = LoggerFactory.getLogger(HibernateArticleDao.class);
	private static Map isChangeVals= LoadCommodityProps.getIsChangeVals();
	private GenVals genVals;
	private JdbcTemplate jdbcTemplate;
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	public void setGenVals(GenVals genVals) {
		this.genVals = genVals;
	}
	private static String insertSql="insert into item_sku(sku_id,market_id,name,brand,tax,barcode,top_cat,second_cat,third_cat,properties,is_valid,status,created_by,created_at,updated_by,updated_at,pic_url,is_old)" +
			"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static String updateSql="update item_sku set name=?,brand=?,barcode=?,top_cat=?,second_cat=?,third_cat=?,updated_by=?,updated_at=?,is_old=? where sku_id=? and market_id=? and is_valid=1";
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
	public void writeArticle(Article article) throws Exception{
		logger.info(String.format("get article:%s",article.getSkuId()));
//		Session session;//hibernate会话
//		Transaction transaction; //hiberante事务
//		session=sessionFactory.openSession();
//		transaction = session.beginTransaction();
//
//		String selectHql="select articles from Article as articles where articles.skuId=? and articles.marketId=? and articles.isValid=?";
//		Query query=session.createQuery(selectHql);
//		query.setString(0, article.getSkuId());
//		query.setInteger(1, article.getMarketId());
//		query.setInteger(2, article.getIsValid());
//		List list=query.list();
//		if(list.isEmpty()){
//			logger.info(String.format("save article:%s",article.toString()));
//			session.save(article);
//		}else {
//			logger.info(String.format("update article:%s",article.toString()));
//				Article sqlArticle = (Article) list.get(0);
//				sqlArticle.setBarcode(article.getBarcode());
//				sqlArticle.setBrand(article.getBrand());
//				sqlArticle.setCreateAt(article.getCreateAt());
//				sqlArticle.setCreatedBy(article.getCreatedBy());
//				sqlArticle.setName(article.getName());
//				sqlArticle.setSecondCat(article.getSecondCat());
//				sqlArticle.setTax(article.getTax());
//				sqlArticle.setThirdCat(article.getThirdCat());
//				sqlArticle.setTopCat(article.getTopCat());
//				sqlArticle.setUpdatedAt(article.getUpdatedAt());
//				sqlArticle.setUpdatedBy(article.getUpdatedBy());
//				session.update(sqlArticle);
//		}
//		transaction.commit();
//		session.close();
		if (genVals.getIsChangeVal(article.getSkuId() + article.getMarketId()) == null) {
			if (genVals.getOldCommodityVals(article.getSkuId() + article.getMarketId()) == null) {
				logger.info(String.format("save article:%s", article.toString()));
				Object obj[] = {article.getSkuId(), article.getMarketId(), article.getName(), article.getBrand(), article.getTax(), article.getBarcode(),
						article.getTopCat(), article.getSecondCat(), article.getThirdCat(), article.getProperties(), article.getIsValid(), article.getStatus(), article.getCreatedBy(), article.getCreateAt(), article.getUpdatedBy(), article.getUpdatedAt(), article.getPicUrl(),article.getIsOld()};
				this.jdbcTemplate.update(insertSql, obj);
			} else {
				logger.info(String.format("update article:%s", article.toString()));
				Object obj[] = {article.getName(), article.getBrand(), article.getBarcode(), article.getTopCat(), article.getSecondCat(), article.getThirdCat(), article.getUpdatedBy(), article.getUpdatedAt(), article.getIsOld(),article.getSkuId(), article.getMarketId()};
				this.jdbcTemplate.update(updateSql, obj);

			}
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
		writeArticle((Article) output);
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
