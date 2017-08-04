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

import com.lsh.base.data.ArticleWriteDao;
import com.lsh.base.data.bean.Article;
import com.lsh.base.data.utils.GenVals;
import com.lsh.base.data.utils.LoadCommodityProps;
import com.lsh.base.data.utils.LoadProps;
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
public class HibernateArticleProDao implements
		ArticleWriteDao, RepeatListener {
	private List<Throwable> errors = new ArrayList<Throwable>();
	private SessionFactory sessionFactory;
	private static final Logger logger = LoggerFactory.getLogger(HibernateArticleProDao.class);;
	private Map changeValMap = LoadCommodityProps.getCommodityVals();
	private JdbcTemplate jdbcTemplate;
	private GenVals genVals;

	public void setGenVals(GenVals genVals) {
		this.genVals = genVals;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	private static String updateSql="update item_sku set properties=? where sku_id=? and market_id=? and is_valid=1";
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
//		if(changeValMap.get(article.getSkuId())!=null) {
//			if (genVals.getIsChangeVal(article.getSkuId() + article.getMarketId()) == null) {
				Session session;//hibernate会话
				Transaction transaction; //hiberante事务
				session = sessionFactory.openSession();
				transaction = session.beginTransaction();

				String selectHql = "select articles from Article as articles where articles.skuId=? and articles.marketId=? ";
				Query query = session.createQuery(selectHql);
				query.setString(0, article.getSkuId());
				query.setInteger(1, article.getMarketId());
				List<Article> list = query.list();
				if (!list.isEmpty()) {
					for(Article sqlArticle:list) {
						logger.info(String.format("update article:%s", article.toString()));
						String newPro = GenVals.changgePro(sqlArticle.getProperties(), article.getProperties());
						sqlArticle.setProperties(newPro);
						sqlArticle.setTax(article.getTax());
						session.update(sqlArticle);
					}
				}
				transaction.commit();
				session.close();
			}
//		}
//		}
//		if(changeValMap.get(article.getSkuId())!=null) {
//			logger.info(String.format("update article:%s", article.toString()));
//			Object obj[] = {article.getName(), article.getBrand(), article.getBarcode(), article.getTopCat(), article.getSecondCat(), article.getThirdCat(), article.getUpdatedBy(), article.getUpdatedAt(), article.getSkuId(), article.getMarketId()};
//			this.jdbcTemplate.update(updateSql, obj);
//		}
//	}

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
