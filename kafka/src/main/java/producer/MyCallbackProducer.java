package producer;

import constants.KafkaConstants;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

/**
 * 带回调函数的Producer
 */
public class MyCallbackProducer {

    public static void main(String[] args) {

        // 1. kafka集群配置
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop101:9092, hadoop102:9092, hadoop103:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaConstants.KAFKA_KEY_STRING_SERIALIZER);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaConstants.KAFKA_VALUE_STRING_SERIALIZER);

        // 2. 创建生产者
        KafkaProducer<Object, Object> producer = new KafkaProducer<>(properties);

        // 3. 发送数据
//        for (int i = 0; i < 10; i++) {
//            producer.send(new ProducerRecord<>("first", "message_" + i), new Callback() {
//                @Override
//                public void onCompletion(RecordMetadata metadata, Exception exception) {
//                    if (exception == null){
//                        System.out.println(metadata.partition());
//                        System.out.println(metadata.offset());
//                    }
//                }
//            });
//        }

        for (int i = 0; i < 10; i++) {
            // 使用Lambda表达式
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
