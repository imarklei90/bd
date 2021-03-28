package flink.unbound

import org.apache.flink.api.common.serialization.{DeserializationSchema, SimpleStringSchema}
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer

import java.util.Properties
import scala.io.Source

/**
 * 从Kafka获取数据
 *
 * @author Lei
 * @date 2021/3/24
 */
object KafkaSource {

  def main(args: Array[String]): Unit = {

    // 执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    // 从kafka获取数据
    val topic = "flink"
    val valueDeserializer: DeserializationSchema[String] = new SimpleStringSchema()
    val props: Properties = new Properties()
    props.load(this.getClass.getClassLoader.getResourceAsStream("config.properties"))
    import org.apache.flink.api.scala._
    env.addSource(new FlinkKafkaConsumer[String](topic, valueDeserializer, props))
      .filter(_.trim.nonEmpty)
      .map(travelInfo => {

      })





    // 开启执行
    env.execute(this.getClass.getSimpleName)

  }

}
