package flink.window.function

import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

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

  /**
   * IN: 待处理的DataStream中每个元素的类型
   * OUT: 输出的DataStream中每个元素的类型
   * KEY: KeyBy中指定的Key的类型
   * W: 窗口的类型
   */
  private class MyProcessFunction extends ProcessWindowFunction[String, Int, Tuple, TimeWindow]{
    /**
     * 窗口关闭时，process会被触发执行
     * @param key ID
     * @param context 窗口的上下文实例
     * @param elements 窗口中带计算的元素
     * @param out 输出结果DataStream
     */
    override def process(key: Nothing, context: Context, elements: Iterable[Nothing], out: Collector[Nothing]): Unit = {


    }
  }

}
