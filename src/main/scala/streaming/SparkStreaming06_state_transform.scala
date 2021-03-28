package streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * DStream有状态计算
 */
object SparkStreaming06_state_transform {

  def main(args: Array[String]): Unit = {

    // 创建ssc环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark Streaming")

    val ssc = new StreamingContext(sparkConf, Seconds(3))

    val data = ssc.socketTextStream("192.168.28.8", 8888)

    // 可以将底层的RDD获取到之后进行操作
    // 使用场景：1. DStream功能不完善；2. 需要代码周期性执行
    // Code: Driver端
    val ds: DStream[String] = data.transform(
      rdd => {
        // Code: Driver端（周期性执行）
        rdd.map(
          str => {
            // Code: Executor端
            str
          }
        )
      }
    )

    // Code: Driver端
    data.map(
      data => {
        // Code: Executor端
        data
      }
    )

    // 1. 启动采集器
    ssc.start()

    // 2. 等待采集器关闭
    ssc.awaitTermination()
  }

}
