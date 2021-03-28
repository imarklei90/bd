package flink.sink

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.apache.flink.util.Collector

import java.util.Properties

/**
 * 自定义Kafka Sink
 * 需求：Flink从Socket中获取数据读到Kafka，再将数据写到kafka不同的topic中
 *
 * @author Lei
 * @date 2021/3/26
 */
object KafkaSink {


  var props: Properties = new Properties()
  props.load(this.getClass.getClassLoader.getResourceAsStream("consumer.properties"))

  def main(args: Array[String]): Unit = {

    // 执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    // Source
    import org.apache.flink.api.scala._
    val sourceDataStream: DataStream[String] = env.addSource(new FlinkKafkaConsumer[String]("sourceData", new SimpleStringSchema(), props))
      .filter(_.nonEmpty)

    // Sink
   // sourceDataStream.addSink(new FlinkKafkaProducer[String]("targetTopic", new SimpleStringSchema(), props, FlinkKafkaProducer.Semantic.EXACTLY_ONCE))

    sourceDataStream.addSink(new FlinkKafkaProducer[String]("", new SimpleStringSchema(), props))

    // 执行
    env.execute(this.getClass.getSimpleName)

  }

}
