package org.example.demo;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemoKeys {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    final Logger logger = LoggerFactory
        .getLogger(ProducerDemoKeys.class);//get logger for my class

    Properties properties = createKafkaProperties();

    //create producer
    KafkaProducer<String, String> producer = new KafkaProducer<String, String>(
        properties); //our key and value is string

    //send data synchronous(bad practice)
    for (int i = 0; i < 10; i++) {

      //create producer record
      String topic = "first_topic";
      String key = "id_" + i;
      String value = "Hello world-" + i;
      ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, value);

      logger.info("Key:" + key);
      //same key will always go to same partition for a fixed number of partitions
      // no matter how many times its run, this ensures ordering

      sendData(logger, producer, record);
    }

    //flush and close
    producer.flush();
    producer.close();

  }

  private static void sendData(final Logger logger, KafkaProducer<String, String> producer,
      ProducerRecord<String, String> record) throws ExecutionException, InterruptedException {
    producer.send(record, new Callback() {
      public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        // executes every time a record is successfully sent or an exception is thrown
        if (e == null) {
          // the record was successfully sent
          logger.info("Received new metadata. \n" +
              "Topic:" + recordMetadata.topic() + "\n" +
              "Partition: " + recordMetadata.partition() + "\n" +
              "Offset: " + recordMetadata.offset() + "\n" +
              "Timestamp: " + recordMetadata.timestamp());
        } else {
          logger.error("Error while producing", e);
        }
      }
    }).get(); //block the send to make it synchronous
  }

  private static Properties createKafkaProperties() {
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        .getName()); //help the producer know what values you are sending to kafka
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class.getName()); //how to serialise we need string serialiser
    return properties;
  }


}
