package com.jira;

import com.jira.entity.ErrorMsg;
import com.jira.service.JiraService;
import com.jira.serviceImpl.JiraServiceImpl;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wuhao on 15/12/18.
 */
public class JiraConsumer {

    private final ConsumerConnector consumer;
    private static final String TOPIC="JIRA";
    private JiraService jiraService =new JiraServiceImpl();
    private static final Logger logger = LoggerFactory.getLogger(JiraConsumer.class);

    private JiraConsumer() throws Exception{
        Properties props = new Properties();
        //zookeeper 配置
        props.put("zookeeper.connect", "123.59.4.51:2180");

        //group 代表一个消费组
        props.put("group.id", "jira");

        //zk连接超时
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "smallest");
        //序列化类
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.load(ClassLoader.getSystemResourceAsStream("kafka.properties"));
        ConsumerConfig config = new ConsumerConfig(props);

        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);
    }

    void consume() {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(TOPIC, new Integer(1));

        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        Map<String, List<KafkaStream<String, String>>> consumerMap =
                consumer.createMessageStreams(topicCountMap,keyDecoder,valueDecoder);
        KafkaStream<String, String> stream = consumerMap.get(TOPIC).get(0);
        ConsumerIterator<String, String> it = stream.iterator();
        while (it.hasNext()) {
           String message=it.next().message();
            logger.info(String.format("get message:%s",message));
            try {
                jiraService.newJiraIssue(message);
            } catch (Exception e) {
                logger.error(String.format("fail to save jira :%s，error message:%s", message, e.toString()));
                ErrorMsg errorMsg=new ErrorMsg();
                errorMsg.setContent(message);
                errorMsg.setErrorinfo(e.getMessage());
                jiraService.saveError(errorMsg);
            }
        }
    }

    public static void main(String args[])throws Exception{
        new JiraConsumer().consume();
    }

}
