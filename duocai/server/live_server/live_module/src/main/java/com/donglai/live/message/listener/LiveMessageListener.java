package com.donglai.live.message.listener;

import com.alibaba.fastjson.JSON;
import com.donglai.live.message.dispatcher.LiveMessageDispatcher;
import com.donglai.protocol.message.KafkaMessage.TopicMessage;
import lombok.extern.java.Log;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

//消息订阅转发
@Component
@Log
public class LiveMessageListener {

    @KafkaListener(topics = "#{'${kafka.listener_topics}'.split(',')}")
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            TopicMessage message = JSON.parseObject(kafkaMessage.get().toString(), TopicMessage.class);
            // 转发
            LiveMessageDispatcher.dispatcher(message);
        }
    }
/*
	@KafkaListener(groupId = "live-consumer-group",topicPartitions = {@TopicPartition(topic = "demo1",partitions  = {"0"})})
	public void listenerA(String data){
		System.out.println("消费分区0     "+ data);
	}

	@KafkaListener(groupId = "live-consumer-group",topicPartitions = {@TopicPartition(topic = "demo1",partitions  = {"1"})})
	public void listenerB(String data){
		System.out.println("消费分区1     "+ data);
	}*/
/*
	@KafkaListener(topicPartitions = {@TopicPartition(topic = "topic1", partitions = { "0" })})
	public void listen0(String data) {
		System.out.println("消费分区0     "+ data);
	}

	@KafkaListener(topicPartitions = {@TopicPartition(topic = "topic1", partitions = { "1" })})
	public void listen1(String data) {
		System.out.println("消费分区1     "+ data);
	}*/
}
