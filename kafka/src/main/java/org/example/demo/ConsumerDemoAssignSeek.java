package org.example.demo;

import java.sql.Array;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemoAssignSeek {

  public static void main(String[] args) {

    final Logger logger = LoggerFactory
        .getLogger(ConsumerDemoAssignSeek.class);//get logger for my class

    Properties properties = createKafkaConsumerProperties();

    //create consumer
    KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(
        properties); //our key and value is string

    //assign and seek instead of poll
    //it replays data or fetches a specific message(specify partition and offset to read data)

    TopicPartition partitionToReadFrom = new TopicPartition("first_topic", 0);
    consumer.assign(Arrays.asList(partitionToReadFrom));

    long offSetToReadFrom = 15L;
    consumer.seek(partitionToReadFrom, offSetToReadFrom);

    int numberOfMessages = 5;
    int currentCount = 0;
    boolean read = true;

    while (read) {
      //poll every second
      ConsumerRecords<String, String> records =
          consumer
              .poll(Duration.ofMillis(100)); // new in Kafka 2.0.0, force maven to use JAVA 8 here

      for (ConsumerRecord currentRecord : records) {
        currentCount++;
        logger.info("Key: " + currentRecord.key() + ", Value: " + currentRecord.value());
        logger
            .info("Partition: " + currentRecord.partition() + ", Offset:" + currentRecord.offset());
        if (currentCount >= numberOfMessages) {
          read = false;
          break;
        }
      }
      logger.info("Exiting consumer read");
    }


  }

  private static Properties createKafkaConsumerProperties() {
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    //when bytes received from kafka, consumer has to create a string from it
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    //earliest(read from beginning of topic), latest(read only new msgs), none(throws error when no offset is set)
    return properties;
  }


}
