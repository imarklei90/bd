package flink.window.function

import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

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
    /**
     * 初始化当前的累加器，开启一个新的聚合操作
     * @return
     */
    override def createAccumulator(): (String, Int) = ("", 1)

    /**
     * 将当前元素的值添加到累加器中，并且返回一个新的累加器
     * @param value
     * @param accumulator
     * @return
     */
    override def add(value: (String, Int, String), accumulator: (String, Int)): (String, Int) = {
      val count = accumulator._2 + 1
      (value._1, count)
    }

    /**
     * 从累加器中获取最终的值
     * @param accumulator
     * @return
     */
    override def getResult(accumulator: (String, Int)): (String, Int) = {
      ("", accumulator._2)
    }

    /**
     * 将两个累加器的值进行合并，返回合并后的累加器的值
     * @param acc1
     * @param acc2
     * @return
     */
    override def merge(acc1: (String, Int), acc2: (String, Int)): (String, Int) = {
      val mergeCount = acc1._2 + acc2._2
      ("", mergeCount)
    }
  }

}
