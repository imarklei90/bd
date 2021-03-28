package producer;

import constants.KafkaConstants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.Properties;

public class MyInterceptorProducer {

    public static void main(String[] args) {

        // 1. Kafka集群配置
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop101:9092, hadoop102:9092, hadoop103:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaConstants.KAFKA_KEY_STRING_SERIALIZER);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaConstants.KAFKA_VALUE_STRING_SERIALIZER);
        ArrayList<String> interceptorLists = new ArrayList<>();
        interceptorLists.add("interceptor.TimeInterceptor");
        interceptorLists.add("interceptor.CounterInterceptor");
        // 添加自定义拦截器
        properties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptorLists);

        // 2. 创建生产者对象
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        // 3. 发送数据
        for (int i = 0; i < 10; i++) {
            producer.send(new ProducerRecord<>("first", "key" + i, "message_" + i));
        }

        // 4. 关闭资源
        producer.close();
    }
}
