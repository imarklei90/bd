package flink.window.function

import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.{ProcessWindowFunction, RichWindowFunction}
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

/**
 * AggregateFunction + ProcessFunction
 *
 * @author Lei
 * @date 2021/4/1
 */
object TumblingTimeWindow_aggregate_process {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    env.socketTextStream("localhost", 7777)
      .filter(_.nonEmpty)
      .map(person => {
        val personArr: Array[String] = person.split(",")
        (personArr(0), personArr(1).toInt, personArr(2))
      })
      .keyBy(0)
      .window(TumblingEventTimeWindows.of(10))
      .aggregate(new MyAggregateFunction(), new MyWindowFunction())


    env.execute(this.getClass.getSimpleName)
  }

  private class MyAggregateFunction extends AggregateFunction[(String, Int, String), (String,Int), (String, Int)]{
    override def createAccumulator(): (String, Int) = ???

    override def add(value: (String, Int, String), accumulator: (String, Int)): (String, Int) = ???

    override def getResult(accumulator: (String, Int)): (String, Int) = ???

    override def merge(a: (String, Int), b: (String, Int)): (String, Int) = ???
  }

  private class MyWindowFunction extends ProcessWindowFunction[String, Int, Tuple, TimeWindow]{
    override def process(key: Tuple, context: Context, elements: Iterable[String], out: Collector[Int]): Unit ={
      context.window.getStart
      context.window.getEnd
    }
  }

  private class MyRichWindowFunction extends RichWindowFunction[String, Int, Tuple, TimeWindow]{
    override def apply(key: Tuple, window: TimeWindow, input: Iterable[String], out: Collector[Int]): Unit = ???
  }

}
