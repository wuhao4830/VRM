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
import com.lsh.base.data.SaleDcWriterDao;
import com.lsh.base.data.bean.IssueBean;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.bean.SaleDc;
import com.lsh.base.data.jira.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Delegates actual writing to a custom DAO. 
 * 
 * @author wuhao
 */
public class SaleDcItemWriter implements ItemWriter<SaleDc> {

    private SaleDcWriterDao saleDcWriterDao;

    public SaleDcWriterDao getSaleDcWriterDao() {
        return saleDcWriterDao;
    }



    private static final Logger logger = LoggerFactory.getLogger(SaleDcItemWriter.class);
    /**
     * Public setter for the {@link SaleDcWriterDao}.
     * @param saleDcWriterDao the {@link SaleDcWriterDao} to set
     */
    public void setSaleDcWriterDao(SaleDcWriterDao saleDcWriterDao) {
        this.saleDcWriterDao = saleDcWriterDao;
    }




    public void write(List<? extends SaleDc> einaCredits)  {
        for (SaleDc sale : einaCredits) {
            try {
                saleDcWriterDao.writeSale(sale);
            }catch (Exception e){
                logger.error(String.format("sale error:%s", e.toString()));
                //发消息
                IssueInfo info=new IssueInfo();
                String desc="error:"+e.toString()+",value:"+ JSON.toJSON(sale).toString();
                IssueBean bean=new IssueBean();
                bean.setDescription(desc);
                info.setIssue(bean);
                try {
                    KafkaProducer kafkaProducer= KafkaProducer.getInstance();
                    kafkaProducer.produce(info);
                }catch (Exception error){
                    logger.error(error.getMessage(),error);
                }
            }
        }
    }

}
