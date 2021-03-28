package flink.bound

import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import scala.io.Source

/**
 * 有界数据流
 *  单词计数
 *
 * @author Lei
 * @date 2021/3/22
 */
object BoundStreamDemo {

  def main(args: Array[String]): Unit = {

    // 创建执行环境
    val environment: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    Source.fromFile("").getLines()

    // 读取文件，并封装到DataSet中
    val data: DataSet[String] = environment.readTextFile("flink/data")

    // 单词计数
    import org.apache.flink.api.scala._ // 导入Flink的scala单例类
    val result: DataSet[(String, Int)] = data.flatMap(_.split("\\s+")) // 匹配空格
      .filter(_.trim.nonEmpty) // 过滤掉空数据
      .map((_, 1))
      .groupBy(0) // 根据单词进行分组
      .sum(1) // 单词出现的次数进行累计
      .sortPartition(1, Order.DESCENDING) // 降序排序
      .setParallelism(1) // 将前一个算子设置并行度，通过一个线程进行排序， 就是一个线程，达到全局有序的目的


    // 输出
    result.print()

    // 触发执行
    environment.execute(this.getClass.getSimpleName)

  }

}
