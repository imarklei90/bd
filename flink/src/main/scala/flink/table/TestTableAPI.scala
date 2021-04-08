package flink.table

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * Table API
 *
 * @author Lei
 * @date 2021/4/7
 */
object TestTableAPI {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    env.readTextFile("")
      .filter(_.nonEmpty)
      .map( data => {
        val dataArr: Array[String] = data.split(",")
        (dataArr(0), dataArr(1))
      })

    // 创建表的执行环境
    StreamTableEnvironment.create(env)
  }

}
