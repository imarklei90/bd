package flink.broadcast.unbounded

import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

/**
 *
 * 无界数据流 - Join操作
 *
 * @author Lei
 * @date 2021/3/31
 */
object TestJoin {

  def main(args: Array[String]): Unit = {

    import org.apache.flink.api.scala._

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    // 模拟两个无界数据流
    val genderDS: DataStream[(Int, Char)] = env.addSource(new MySourceFunction)
    val personDS: DataStream[(Int, String, Int, String)] = env.socketTextStream("localhost", 7777)
      .filter(_.nonEmpty)
      .map(person => {
        val personArr: Array[String] = person.split(",")
        (personArr(0).toInt, personArr(1), personArr(2).toInt, personArr(3))
      }
      )

    // Join
    genderDS.join(personDS)
      .where(_._1)
      .equalTo(_._3)
      .window[TimeWindow](TumblingEventTimeWindows.of(Time.seconds(3)))
      .apply[(Int, String, Int, String)]((left, right) => (right._1, right._2, left._2, right._4))
      .print()

  }

  private class MySourceFunction extends SourceFunction[(Int, Char)]{

    private var isRunning = true

    override def run(ctx: SourceFunction.SourceContext[(Int, Char)]): Unit = {

      val data: Array[(Int, Char)] = Array((1, '男'), (2, '女'))

      while (isRunning){
        data.foreach(ele => ctx.collect((ele._1, ele._2)))
        Thread.sleep(3000)
      }

    }

    override def cancel(): Unit = isRunning = false
  }

}
