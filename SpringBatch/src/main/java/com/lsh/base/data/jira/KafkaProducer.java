package com.lsh.base.data.jira;
import java.util.Properties;

import com.lsh.base.data.bean.IssueBean;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lsh.base.data.bean.IssueInfo;
import com.alibaba.fastjson.JSON;
/**
 * Created by wuhao on 15/12/19.
 */
public class KafkaProducer
{
    private final Producer<String, String> producer;
    public final static String TOPIC = "JIRA";
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private static KafkaProducer kafkaProducer;

    private KafkaProducer()throws Exception{
        Properties props = new Properties();
        props.load(ClassLoader.getSystemResourceAsStream("kafka-producer.properties"));
        producer = new Producer<String, String>(new ProducerConfig(props));
    }
    public static KafkaProducer getInstance()throws Exception{
        if(kafkaProducer==null){
            kafkaProducer=new KafkaProducer();
        }
        return kafkaProducer;
    }
    public void produce(IssueInfo info) {
        String error = JSON.toJSON(info).toString();
        logger.info(String.format("send error msg:%s", error));
        producer.send(new KeyedMessage<String, String>(TOPIC, error));

    }
    public static void main(String args[])throws Exception{
        IssueInfo info=new IssueInfo();
        IssueBean bean=new IssueBean();
        bean.setProjectKey("TEST");
        bean.setSummary("呵呵哒");
        bean.setDescription("呵呵哒");
        info.setIssue(bean);
        KafkaProducer kafkaProducer=KafkaProducer.getInstance();
        kafkaProducer.produce(info);
    }

}