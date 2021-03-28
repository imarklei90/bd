package streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * DStream有状态计算
 */
object SparkStreaming08_state_window {

  def main(args: Array[String]): Unit = {

    // 创建ssc环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark Streaming")

    val ssc = new StreamingContext(sparkConf, Seconds(3))

    ssc.checkpoint("cp")

    val data = ssc.socketTextStream("192.168.28.8", 8888)

    val lines = data.map((_, 1))

    // 窗口的范围：采集周期的整数倍
    // 窗口是可以滑动的，默认一个采集周期进行滑动，可能会出现重复数据，解决方法：通过步长改变滑动间隔
    // val windowDS: DStream[(String, Int)] = lines.window(Seconds(6), Seconds(6))
    // 当窗口范围比较大，但是滑动幅度比较小，那么可以采用增加数据和删除数据的方式，无需重复计算
    val windowDS: DStream[(String, Int)] = lines.reduceByKeyAndWindow(
      (x:Int, y: Int) => {x + y},
      (x:Int, y: Int) => {x - y},
      Seconds(9),
      Seconds(6)
    )

    //val wordCount = windowDS.reduceByKey(_ + _)
    // 不会出现时间戳
    windowDS.foreachRDD(
      rdd => {
        // TODO
      }
    )


    // 1. 启动采集器
    ssc.start()

    // 2. 等待采集器关闭
    ssc.awaitTermination()
  }

}
