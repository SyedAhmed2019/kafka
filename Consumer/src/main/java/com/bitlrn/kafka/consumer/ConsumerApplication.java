package com.bitlrn.kafka.consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;

@SpringBootApplication
public class ConsumerApplication implements ApplicationRunner {
	private static final Logger logger = LoggerFactory.getLogger(ConsumerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);

	}

	@Autowired
	private KafkaTemplate<String, String> template;

	private ArrayList<String> messageCache = new ArrayList<>();
	int i = 0;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		logger.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
		logger.info("NonOptionArgs: {}", args.getNonOptionArgs());
		logger.info("OptionNames: {}", args.getOptionNames());

		for (String name : args.getOptionNames()){
			logger.info("arg-" + name + "=" + args.getOptionValues(name));
		}

		//TODO: use the passed parameters to dynamically change the config below
		int numOfMessages = 1000;

		while (i != numOfMessages){
			Thread.sleep(1);
		}

		logger.info("After {} ExpectedMessages count {}, ReceivedMessages Count {}",i, numOfMessages, messageCache.size());

	}
	@KafkaListener(topics={"test2"})
	public void receivedMessage (String message){
		logger.info("Recevied message {} of size {}", ++i,message.length());
		messageCache.add(message);
	}
}