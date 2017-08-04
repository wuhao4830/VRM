package com.lsh.base.data.internal;

import com.alibaba.fastjson.JSON;
import com.lsh.base.data.bean.IssueBean;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.bean.UgroupBean;
import com.lsh.base.data.jira.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import java.text.DecimalFormat;

/**
 * Created by wuhao on 16/1/8.
 */
public class UgroupFieldSetMapper implements FieldSetMapper<UgroupBean> {

private static final Logger logger = LoggerFactory.getLogger(UgroupFieldSetMapper.class);
    private static final int value=0;
    private static final DecimalFormat df=new DecimalFormat("000000000000000000");

    @Override
    public UgroupBean mapFieldSet(FieldSet fieldSet) {
        UgroupBean ugroupBean = new UgroupBean();
        try {
            String valStr=fieldSet.readRawString(value);
            valStr=valStr.replace("'", "");
            String vals[]=valStr.split("\t");
            String skuId=vals[0].replace("(", "");
            ugroupBean.setSkuId(df.format(Integer.parseInt(skuId)));
            ugroupBean.setShelfNum(vals[1]);
        }catch (Exception e){
            logger.error(String.format(" update ugroup error %s",e.toString()));
            IssueInfo info=new IssueInfo();
            String desc="error:"+e.toString()+",value:"+ JSON.toJSON(ugroupBean).toString();
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

        return ugroupBean;
    }
}
