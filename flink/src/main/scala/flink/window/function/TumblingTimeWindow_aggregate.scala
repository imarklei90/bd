package flink.window.function

import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.spark.sql.catalyst.expressions.aggregate.AggregateFunction

/**
 * ReduceFunction
 *
 * @author Lei
 * @date 2021/4/1
 */
object TumblingTimeWindow_aggregate {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    env.socketTextStream("localhost", 7777)
      .filter(_.nonEmpty)
      .map( person => {
        val personArr: Array[String] = person.split(",")
        (personArr(0), personArr(1).toInt, personArr(2))
      })
      .keyBy(0)
      .timeWindow(Time.seconds(3))
      .aggregate()

    env.execute(this.getClass.getSimpleName)
  }

  private class MyAggregateFunction extends AggregateFunction[(String, Int, String), (String,Int), (String, Int)]{

  }

}
