package com.example.demo.controller;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@PropertySource("classpath:kafka.properties")
public class MyController {
	@Value("${kafka.bootstrap.servers}")
	private String servers;
	@Value("${kafka.serializer}")
	private String serializer;
	@Value("${kafka.topic}")
	private String topic;
	
	@PutMapping("/produce/{message}")
	public long producer(@PathVariable String message) throws InterruptedException, ExecutionException {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializer);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer);
		
		Producer<String, String> producer = new KafkaProducer<>(props);
		Future<RecordMetadata> data = producer.send(new ProducerRecord<String, String>(topic, message));
		producer.close();
		return data.get().timestamp();
		
	}
}
