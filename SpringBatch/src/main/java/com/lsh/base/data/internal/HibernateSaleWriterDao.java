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
import com.lsh.base.data.bean.Sale;
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
import com.lsh.base.data.SaleWriterDao;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wuhao
 *
 */
public class HibernateSaleWriterDao implements
		SaleWriterDao, RepeatListener {
	private List<Throwable> errors = new ArrayList<Throwable>();
	private SessionFactory sessionFactory;
	private GenVals genVals;
	private JdbcTemplate jdbcTemplate;
	private static final Logger logger = LoggerFactory.getLogger(HibernateSaleWriterDao.class);
	private static Map isChangeVals= LoadCommodityProps.getIsChangeVals();
	private static String insertSql="insert into item_sale (sku_id,market_id,sup_no,net_price,min_qty,max_qty,refundable,is_valid,due_day,unit,effected_at,end_at,properties,created_at,updated_at) values" +
			"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static String updateSql="update item_sale set net_price=?,min_qty=?,max_qty=?,refundable=?,due_day=?,unit=?,effected_at=?,end_at=?,updated_at=? where sku_id=? and market_id=? and sup_no=? and is_valid =1 ";

	public void setGenVals(GenVals genVals) {
		this.genVals = genVals;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
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
	 * @see org.springframework.batch.sample.domain.trade.internal.CustomerCreditWriter#write(org.springframework.batch.sample.domain.Eina)
	 */
	public void writeSale(Sale sale) throws Exception{
		logger.info(String.format("get sale:%s",sale.getSkuId()));
		if(sale.getSupNo().startsWith("D")){
			return;
		}
//		Session session;//hibernate会话
//		Transaction transaction; //hiberante事务
//		session=sessionFactory.openSession();
//		transaction = session.beginTransaction();
//
//		String selectHql="select sales from Sale as sales where sales.skuId=? and sales.marketId=? and sales.isValid=? and sales.supNo=?";
//		Query query=session.createQuery(selectHql);
//		query.setString(0,sale.getSkuId());
//		query.setInteger(1, sale.getMarketId());
//		query.setInteger(2, sale.getIsValid());
//		query.setString(3, sale.getSupNo());
//		List list=query.list();
//		if(list.isEmpty()){
//			logger.info(String.format("save sale:%s",sale.toString()));
//			session.save(sale);
//		}else{
//			String key=sale.getSkuId()+sale.getMarketId();
//			if (isChangeVals.get(key)==null) {
//				logger.info(String.format("update sale:%s",sale.toString()));
//				Sale sqlSale=(Sale)list.get(0);
//				sqlSale.setDueDay(sale.getDueDay());
//				sqlSale.setEffectedAt(sale.getEffectedAt());
//				sqlSale.setEndAt(sale.getEndAt());
//				sqlSale.setMaxQty(sale.getMaxQty());
//				sqlSale.setMinQty(sale.getMinQty());
//				sqlSale.setNetPrice(sale.getNetPrice());
//				sqlSale.setRefundable(sale.getRefundable());
//				sqlSale.setSupNo(sale.getSupNo());
//				sqlSale.setUnit(sale.getUnit());
//				sqlSale.setUpdateAt(sale.getUpdateAt());
//				session.update(sqlSale);
//			}
//		}
//		transaction.commit();
//		session.close();
		if (genVals.getIsChangeVal(sale.getSkuId()+sale.getMarketId())==null){
			if (genVals.getOldSaleCommodityVals(sale.getSkuId()+sale.getMarketId()+sale.getSupNo())==null){
				logger.info(String.format("save sale:%s",sale.toString()));
				Object obj[]={sale.getSkuId(),sale.getMarketId(),sale.getSupNo(),sale.getNetPrice(),sale.getMinQty(),sale.getMarketId(),sale.getRefundable(),sale.getIsValid(),
						sale.getDueDay(),sale.getUnit(),sale.getEffectedAt(),sale.getEndAt(),sale.getProperties(),sale.getCreateAt(),sale.getUpdateAt()};
				this.jdbcTemplate.update(insertSql,obj);
			}else {
				logger.info(String.format("update sale:%s",sale.toString()));
				Object obj[]={sale.getNetPrice(),sale.getMinQty(),sale.getMaxQty(),sale.getRefundable(),sale.getDueDay(),sale.getUnit(),sale.getEffectedAt(),sale.getEndAt(),sale.getUpdateAt(),sale.getSkuId(),sale.getMarketId(),sale.getSupNo()};
				this.jdbcTemplate.update(updateSql,obj);

			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.io.OutputSource#write(java.lang.Object)
	 */
	public void write(Object output) throws Exception{
		writeSale((Sale) output);
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
