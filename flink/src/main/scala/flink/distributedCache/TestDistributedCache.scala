package flink.distributedCache

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

import java.io.File
import scala.collection.parallel.immutable
import scala.io.Source

/**
 *  分布式缓存
 *
 * @author Lei
 * @date 2021/3/31
 */
object TestDistributedCache {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    // 注册分布式缓存
    env.registerCachedFile("hdfs://hadoop101/<path>/<fileName>", "cacheFile")

    env.socketTextStream("localhost", 7777)
      .filter(_.nonEmpty)
      .map(new RichMapFunction[(String), (String, String, Int, String)] {

        private var genderMap: Map[Int,Char] = _

        override def open(parameters: Configuration): Unit = {
          // 获取分布式缓存
          val cacheFile: File = getRuntimeContext.getDistributedCache.getFile("cacheFile")

          genderMap = Source.fromFile(cacheFile).getLines().toList
            .map(gender =>{
              val genderArr: Array[String] = gender.split(",")
              (genderArr(0).toInt, genderArr(1).trim.toCharArray()(0))
            }).toMap

        }

        override def map(value: String): (String, String, Int, String) = {

          val personArr: Array[String] = value.split(",")

          val gender = genderMap(personArr(2).toInt)
          (personArr(0), personArr(1), gender, personArr(3))
        }
      })


    env.execute(this.getClass.getSimpleName)
  }

}
