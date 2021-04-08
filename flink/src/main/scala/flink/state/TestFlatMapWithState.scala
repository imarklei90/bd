package flink.state

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

/**
 * Keyed Stream - FlatMapWithState
 *
 * @author Lei
 * @date 2021/3/31
 */
object TestFlatMapWithState {

  def main(args: Array[String]): Unit = {
    import org.apache.flink.api.scala._

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    env.socketTextStream("localhost", 7777)
      .filter(_.nonEmpty)
      .map(personInfo => {
        val personArr: Array[String] = personInfo.split(",")
        (personArr(0), personArr(1))
      }).keyBy("")
      .flatMapWithState[(String, String), String]{
        case (("", ""), None) =>{
          (List(("", "")), Some(""))
        }
      }

    env.execute(this.getClass.getSimpleName)

  }

}
