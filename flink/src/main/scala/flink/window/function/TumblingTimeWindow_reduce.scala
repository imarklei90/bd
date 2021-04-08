package flink.window.function

import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

/**
 * ReduceFunction
 *
 * @author Lei
 * @date 2021/4/1
 */
object TumblingTimeWindow_reduce {

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
      .reduce(new ReduceFunction[(String, Int, String)] {
        override def reduce(value1: (String, Int, String), value2: (String, Int, String)): (String, Int, String) = {
          if(value1._2 > value2._2){
            value1
          }else{
            value2
          }
        }
      }).print()

    env.execute(this.getClass.getSimpleName)
  }

}
