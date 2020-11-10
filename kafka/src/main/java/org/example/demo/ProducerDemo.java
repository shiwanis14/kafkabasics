package org.example.demo;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class ProducerDemo {

  public static void main(String[] args) {
    Properties properties = createKafkaProperties();

    //create producer
    KafkaProducer<String, String> producer = new KafkaProducer<String, String>(
        properties); //our key and value is string

    //create producer record
    ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic",
        "Hello world!"); //send to topic and value (not specified key)

    //send data asynchronous(in background)
    producer.send(record);

    //flush and close
    producer.flush();
    producer.close();

  }

  private static Properties createKafkaProperties() {
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        .getName()); //help the producer know what values you are sending to kafka, producer takes a string, serialises it and sends to kafka
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class.getName()); //how to serialise we need string serialiser
    return properties;
  }


}
