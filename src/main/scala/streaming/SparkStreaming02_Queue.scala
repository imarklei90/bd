package streaming

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable


object SparkStreaming02_Queue {

  def main(args: Array[String]): Unit = {

    // 创建ssc环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark Streaming")

    val ssc = new StreamingContext(sparkConf, Seconds(3))

    // 创建RDD队列
    val rddQueue = new mutable.Queue[RDD[Int]]()

    // 创建QueueInputStream
    val inputStream : InputDStream[Int] = ssc.queueStream(rddQueue, oneAtATime = false)

    // 处理队列中的数据
    val mappedStream: DStream[(Int, Int)] = inputStream.map((_, 1))
    val reducedStream: DStream[(Int, Int)] = mappedStream.reduceByKey(_ + _)

    reducedStream.print()

    // 1. 启动采集器
    ssc.start()

    // 循环向queue中存放数据
    for(i <- 1 to 5){
      rddQueue += ssc.sparkContext.makeRDD(1 to 300, 10)
      Thread.sleep(2000)
    }

    // 2. 等待采集器关闭
    ssc.awaitTermination()
  }

}
