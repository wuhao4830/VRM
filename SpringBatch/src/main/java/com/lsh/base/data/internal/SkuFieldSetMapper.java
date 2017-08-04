package com.lsh.base.data.internal;

import com.alibaba.fastjson.JSON;
import com.lsh.base.data.bean.IssueBean;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.bean.SkuFileBean;
import com.lsh.base.data.jira.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

/**
 * Created by wuhao on 16/1/8.
 */
public class SkuFieldSetMapper implements FieldSetMapper<SkuFileBean> {
//    public static final int barCode = 20;
//    public static final int propers = 22;
private static final Logger logger = LoggerFactory.getLogger(SkuFieldSetMapper.class);
    private static final int value=0;

    @Override
    public SkuFileBean mapFieldSet(FieldSet fieldSet) {
        SkuFileBean skuFileBean = new SkuFileBean();
        try {
            String valStr=fieldSet.readRawString(value);
            String vals[]=valStr.split("\t");
            skuFileBean.setProperties(vals[22]);
            skuFileBean.setImgs(vals[23]);
            skuFileBean.setBarCode(vals[20]);
        }catch (Exception e){
            logger.error(String.format(" save article error %s",e.toString()));
            IssueInfo info=new IssueInfo();
            String desc="error:"+e.toString()+",value:"+ JSON.toJSON(skuFileBean).toString();
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

        return skuFileBean;
    }
}
