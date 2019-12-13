package com.bitlrn.kafka.producer;

import com.bitlrn.kafka.util.MessageUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.concurrent.*;

@SpringBootApplication
public class ProducerApplication implements ApplicationRunner {
	private static final Logger logger = LoggerFactory.getLogger(ProducerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);

	}

	@Autowired
	private KafkaTemplate<String, String> template;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		logger.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
		logger.info("NonOptionArgs: {}", args.getNonOptionArgs());
		logger.info("OptionNames: {}", args.getOptionNames());

		for (String name : args.getOptionNames()){
			logger.info("arg-" + name + "=" + args.getOptionValues(name));
		}

		//TODO: use the passed parameters to dynamically change the config below

		//sending 1000 messages, 65KB, on topic test2 with dummyKey in single thread
		this.sendMessages(template,1000,65*1024,"test2","dummyKey",100);

		Thread.sleep(60000);

	}

	private void sendMessages(final KafkaTemplate<String, String> template,final int numOfMessages, final int sizeOfMessageInKb,
							  final String topicName, final String keyName, final int numberOfThread) throws InterruptedException {
		logger.info("Sending {} messages, sizeOfMessage {} to topicName {} with KeyName {} using threads {}..",numOfMessages,sizeOfMessageInKb,
				topicName,keyName,numberOfThread);
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThread);
		String message = MessageUtil.createDataSize(sizeOfMessageInKb);
		for ( int j=0; j <= numberOfThread; j++) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					int i = 1;
					while (i <= numOfMessages) {
						logger.info("Sending message {} of size {}", i, sizeOfMessageInKb);
						template.send(topicName, keyName, message);
						i++;
					}
				}
			});
		}
		logger.info("Executor with {} created. Waiting for {} ms",numberOfThread,numOfMessages);
		Thread.sleep(numOfMessages*numberOfThread);
		logger.info("Shutting down the executor");
		executor.shutdown();
		while (!executor.isTerminated()) {
			logger.debug("Pool size is now " + ((ThreadPoolExecutor) executor).getActiveCount()+
					" - queue size: "+ ((ThreadPoolExecutor) executor).getQueue().size()
			);
			TimeUnit.MILLISECONDS.sleep(500);
		}
		logger.info("Sending {} messages, sizeOfMessage {} to topicName {} with KeyName {} using threads {}..",numOfMessages,sizeOfMessageInKb,
				topicName,keyName,numberOfThread);
	}
}