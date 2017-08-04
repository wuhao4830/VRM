package com.lsh.base.data.internal;

import com.alibaba.fastjson.JSON;
import com.lsh.base.data.bean.IssueBean;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.bean.PackBean;
import com.lsh.base.data.jira.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuhao on 16/1/8.
 */
public class PackSizeFieldSetMapper implements FieldSetMapper<PackBean> {

private static final Logger logger = LoggerFactory.getLogger(PackSizeFieldSetMapper.class);
    private static final int name=0;
    private static final int skuId=0;
    private static final int packLength=10;
    private static final int packWidth=12;
    private static final int packHeight=11;
    private static final DecimalFormat df=new DecimalFormat("000000000000000000");

    @Override
    public PackBean mapFieldSet(FieldSet fieldSet) {
        PackBean packBean = new PackBean();
        try {
            String val=fieldSet.readRawString(name);
            String vals[]=val.split(",");
            String valStr=vals[skuId];
            valStr=valStr.replace("'", "");
            String skuId=valStr.replace("(", "");
            packBean.setSkuId(df.format(Integer.parseInt(skuId)));
            Map<String,String> packSizeMap=new HashMap<>();
            packSizeMap.put("pack_length",vals[packLength]);
            packSizeMap.put("pack_height",vals[packHeight]);
            packSizeMap.put("pack_width",vals[packWidth]);
            packBean.setPackSizeMap(packSizeMap);
        }catch (Exception e){
            logger.error(String.format(" update ugroup error %s",e.toString()));
            IssueInfo info=new IssueInfo();
            String desc="error:"+e.toString()+",value:"+ JSON.toJSON(packBean).toString();
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

        return packBean;
    }
}
