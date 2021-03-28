package streaming

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, InputDStream, ReceiverInputDStream}
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable
import scala.util.Random

/**
 * 自定义数据采集器
 */
object SparkStreaming03_DIY {

  def main(args: Array[String]): Unit = {

    // 创建ssc环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark Streaming")

    val ssc = new StreamingContext(sparkConf, Seconds(3))

    // 使用自定义的Receiver
    val inputStream: ReceiverInputDStream[String] = ssc.receiverStream(new MyReceiver)
    inputStream.print()

    // 1. 启动采集器
    ssc.start()

    // 2. 等待采集器关闭
    ssc.awaitTermination()
  }

  /**
   * 继承 abstract class Receiver[T](val storageLevel: StorageLevel)
   */
  class MyReceiver extends Receiver[String](StorageLevel.MEMORY_ONLY){

    private var flag = true

    override def onStart(): Unit = {
      new Thread(new Runnable {
        override def run(): Unit = {
          while (flag){
            val message = new Random().nextInt(10).toString
            store(message)
            Thread.sleep(500)
          }
        }
      }).start()
    }

    override def onStop(): Unit = {
      flag = false
    }
  }

}
