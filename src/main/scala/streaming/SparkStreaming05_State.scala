package streaming

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * DStream有状态计算
 */
object SparkStreaming05_State {

  def main(args: Array[String]): Unit = {

    // 创建ssc环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark Streaming")

    val ssc = new StreamingContext(sparkConf, Seconds(3))

    ssc.checkpoint("cp")

    // 无状态数据操作，只对当前的采集周期内的数据进行处理
    val data = ssc.socketTextStream("192.168.28.8", 8888)

//    val wordCount = data.flatMap(_.split(" "))
//      .map((_, 1))
//      .reduceByKey(_ + _)


    // 有状态数据操作:updateStateByKey根据key对数据的状态进行更新
    // 参数1：表示相同key的value数据
    // 参数2：表示缓冲区中相同key的value数据
    // 使用有状态操作时需要设定checkpoint目录
    val stateValue: DStream[(String, Int)] = data.flatMap(_.split(" "))
      .map((_, 1))
      .updateStateByKey(
        (seq: Seq[Int], buff: Option[Int]) => {
          val newCount = buff.getOrElse(0) + seq.sum
          Option(newCount)
        }
      )

    stateValue.print()



    // 1. 启动采集器
    ssc.start()

    // 2. 等待采集器关闭
    ssc.awaitTermination()
  }

}
