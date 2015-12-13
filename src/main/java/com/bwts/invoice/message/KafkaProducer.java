package com.bwts.invoice.message;

import java.util.Properties;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

@Component
public class KafkaProducer implements DisposableBean {
    
    private final Producer producer;
    private final String topic;
    
    @Autowired
    public KafkaProducer(
            @Value("${producer.zookeeper.connect}") String url,
            @Value("${producer.topic}") String topic,
            @Value("${producer.broker.list}") String brokerList) {
        
        Properties producerProps = new Properties();
        producerProps.put("serializer.class", "kafka.serializer.StringEncoder");
        producerProps.put("zk.connect", "localhost:8888");
        producerProps.put("metadata.broker.list", brokerList);
        
        producer = new Producer<String, String>(new ProducerConfig(producerProps));
        this.topic = topic;
    }
    
    public void send(String message) {
        KeyedMessage<String, String> keyedMessage = new KeyedMessage<String, String>(topic, message);
        producer.send(keyedMessage);
    }

    @Override
    public void destroy() throws Exception {
        if (producer != null) {
            producer.close();
        }
    }
}
