package streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext, StreamingContextState}

/**
 * DStream有状态计算
 */
object SparkStreaming09_resume {

  def main(args: Array[String]): Unit = {

    val ssc = StreamingContext.getOrCreate("cp", () => { // 创建ssc环境
      val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark Streaming")

      val ssc = new StreamingContext(sparkConf, Seconds(3))

      val data = ssc.socketTextStream("192.168.28.8", 8888)

      val wordToOne = data.map((_, 1))
      wordToOne.print()

      ssc
    }
    )

    ssc.checkpoint("cp")


    // 1. 启动采集器
    ssc.start()

    // 2. 等待采集器关闭
    ssc.awaitTermination()

  }

}
