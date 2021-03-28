package streaming

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming01 {

  def main(args: Array[String]): Unit = {

    // 创建ssc环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark Streaming")

    val ssc = new StreamingContext(sparkConf, Seconds(3))

    val lines = ssc.socketTextStream("192.168.28.8", 8888, StorageLevel.DISK_ONLY)

    val wordCount: DStream[(String, Int)] = lines.flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)

    wordCount.print()

    // 关闭
    // 采集器是长期执行的任务，不能直接关闭
    // main执行完毕，应用程序也会自动结束，所以main不能执行完毕
    // ssc.stop()

    // 1. 启动采集器
    ssc.start()

    // 2. 等待采集器关闭
    ssc.awaitTermination()
  }

}
