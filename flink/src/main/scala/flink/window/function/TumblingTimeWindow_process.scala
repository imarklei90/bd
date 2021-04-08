package flink.window.function

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.util.Collector
import org.apache.spark.sql.catalyst.expressions.aggregate.AggregateFunction

/**
 * ProcessFunction
 *
 * @author Lei
 * @date 2021/4/1
 */
object TumblingTimeWindow_process {

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
      .process(new MyProcessFunction)

    env.execute(this.getClass.getSimpleName)
  }

  private class MyProcessFunction extends ProcessWindowFunction{
    /**
     * 窗口关闭时，process会被触发执行
     * @param key
     * @param context
     * @param elements
     * @param out
     */
    override def process(key: Nothing, context: Context, elements: Iterable[Nothing], out: Collector[Nothing]): Unit = {


    }
  }

}
