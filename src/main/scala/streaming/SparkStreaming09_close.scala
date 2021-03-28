package streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext, StreamingContextState}

/**
 * DStream有状态计算
 */
object SparkStreaming09_close {

  def main(args: Array[String]): Unit = {

    // 创建ssc环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark Streaming")

    val ssc = new StreamingContext(sparkConf, Seconds(3))

    val data = ssc.socketTextStream("192.168.28.8", 8888)

    val wordToOne = data.map((_, 1))


    // 1. 启动采集器
    ssc.start()

    // 2. 等待采集器关闭
    ssc.awaitTermination()

    // 如果要关闭采集器，那么需要创建新的线程; 而且需要在第三方程序中增加关闭状态，如：mysql、redis、zookeeper、hdfs
    new Thread(
      new Runnable {
        override def run(): Unit = {
          // 优雅地关闭: 计算节点不再接收新的数据，而是将现有的数据处理完毕，然后关闭
          while(true){
            if(true){ // 读取第三方数据
              // 获取Spark Streaming的状态
              val state: StreamingContextState = ssc.getState()
              if(state == StreamingContextState.ACTIVE) {
                ssc.stop(true, true)
              }
            }
            Thread.sleep(5000)
            System.exit(0)
          }
        }
      }
    ).start()

  }

}
