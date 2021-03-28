package streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * DStream有状态计算
 */
object SparkStreaming07_state_join {

  def main(args: Array[String]): Unit = {

    // 创建ssc环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark Streaming")

    val ssc = new StreamingContext(sparkConf, Seconds(3))

    val data1 = ssc.socketTextStream("192.168.28.8", 8888)
    val data2 = ssc.socketTextStream("192.168.28.8", 9999)

    val mappedValue1: DStream[(String, Int)] = data1.map((_, 1))
    val mappedValue2: DStream[(String, Int)] = data2.map((_, 1))

    // 底层是RDD的Join
    val joinedValue: DStream[(String, (Int, Int))] = mappedValue1.join(mappedValue2)

    joinedValue.print()


    // 1. 启动采集器
    ssc.start()

    // 2. 等待采集器关闭
    ssc.awaitTermination()
  }

}
