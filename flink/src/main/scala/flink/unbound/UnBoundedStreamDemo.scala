package flink.unbound

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

/**
 * 无界流
 *
 * @author Lei
 * @date 2021/3/26
 */
object UnBoundedStreamDemo {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    import org.apache.flink.api.scala._

    env.sockT

    env.readTextFile("data")
      .flatMap(_.split(""))
      .map((_, 1))
      .keyBy(0)
      .sum(1)
      .setParallelism(1)
      .print()

    env.execute("Unbounded Stream")

  }

}
