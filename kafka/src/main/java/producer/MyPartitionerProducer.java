package producer;

import constants.KafkaConstants;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

/**
 * 带自定义分区器的生产者
 */
public class MyPartitionerProducer {

    public static void main(String[] args) {

        // 1. kafka集群配置
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop101:9092, hadoop102:9092, hadoop103:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaConstants.KAFKA_KEY_STRING_SERIALIZER);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaConstants.KAFKA_VALUE_STRING_SERIALIZER);
        // 添加自定义分区器
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "partitioner.MyPartitioner");

        // 2. 创建生产者对象
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        // 3. 发送数据
        for (int i = 0; i < 10; i++) {
            producer.send(new ProducerRecord<>("first", "message_" + i), (metadata, exception) -> {
                if (exception == null){
                    System.out.println(metadata.partition() + " ---- " + metadata.offset());
                }else {
                    exception.printStackTrace();
                }
            });
        }

        // 4. 关闭资源
        producer.close();

    }
}
