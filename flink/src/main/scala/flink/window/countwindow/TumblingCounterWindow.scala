package flink.window.countwindow

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

/**
 * Window - CounterWindow - Tumbling
 *
 * @author Lei
 * @date 2021/4/1
 */
object TumblingCounterWindow {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    env.socketTextStream("localhost", 7777)
      .filter(_.nonEmpty)
      .map(person => {
        val personArr: Array[String] = person.split(",")
        (personArr(0), personArr(1).toInt, personArr(2))
      })
      .keyBy(0)
      .countWindow(3)
      .maxBy(1)
      .print()

    env.execute(this.getClass.getSimpleName)
  }

}
