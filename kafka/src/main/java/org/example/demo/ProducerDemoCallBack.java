package org.example.demo;

import java.util.Properties;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemoCallBack {

  public static void main(String[] args) {
    final Logger logger = LoggerFactory
        .getLogger(ProducerDemoCallBack.class);//get logger for my class

    Properties properties = createKafkaProperties();

    //create producer
    KafkaProducer<String, String> producer = new KafkaProducer<String, String>(
        properties); //our key and value is string

    //send data asynchronous(in background)
    for (int i = 0; i < 10; i++) {
      //create producer record
      ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic",
          "Hello world- " + i);
      sendData(logger, producer, record);
    }

    //flush and close
    producer.flush();
    producer.close();

  }

  private static void sendData(final Logger logger, KafkaProducer<String, String> producer,
      ProducerRecord<String, String> record) {
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
    });
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
