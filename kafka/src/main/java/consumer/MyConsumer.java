package consumer;

import constants.KafkaConstants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class MyConsumer {

    public static void main(String[] args) {

        // 1. Kafka集群配置
        Properties properties = new Properties();
        // kafka集群
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop101:9092, hadoop102:9092, hadoop103:9092");
        // 开启自动提交
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        // 自动提交的延时
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        // key的反序列化
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaConstants.KAFKA_KEY_STRING_DESERIALIZER);
        // Value的反序列化
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaConstants.KAFKA_VALUE_STRING_DESERIALIZER);
        // 消费者组
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "kafkaData1");
        // 重置消费者的offset
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        // 2. 创建消费者对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        // 3. 订阅主题
        consumer.subscribe(Arrays.asList("first", "second"));

        while (true) {
            // 4. 获取数据
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(1));

            // 5. 解析数据
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                System.out.println(consumerRecord.key() + " ---- " + consumerRecord.value());
            }
        }

        // 6. 关闭连接

    }
}
