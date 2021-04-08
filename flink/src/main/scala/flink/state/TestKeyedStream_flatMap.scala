package flink.state

import org.apache.flink.api.common.functions.{RichFlatMapFunction, RichMapFunction}
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.typeutils.base.StringSerializer
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.util.Collector

/**
 * State - KeyedStream 使用FlatMap
 *
 * @author Lei
 * @date 2021/3/31
 */
object TestKeyedStream_flatMap {

  def main(args: Array[String]): Unit = {

    import org.apache.flink.api.scala._

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    env.socketTextStream("localhost", 7777)
      .filter(_.nonEmpty)
      .map(personInfo => {
        val personArr: Array[String] = personInfo.split(",")
        (personArr(0), personArr(1))
      }).keyBy("").map(new RichFlatMapFunction[(String, String), (String, String)] {

      var valueState: ValueState[String] = _

      override def open(parameters: Configuration): Unit = {
        val stateProperties: ValueStateDescriptor[String] = new ValueStateDescriptor[String]("valueState", new StringSerializer())
        valueState = getRuntimeContext.getState[String](stateProperties)
      }

      override def flatMap(value: (String, String), out: Collector[(String, String)]): Unit = {
        // 先获取到存储的值
        val lastValue: String = valueState.value()
        var retValue: (String, String) = (null, null)
        var tmpValue = _
        if (value!= null){
          tmpValue = value._2 > lastValue
          retValue = (value._1, "")
        }else{
          retValue = (value._1, value._2)
        }

        // 更新ValueState
        valueState.update(tmpValue)

        // 发送结果
        out.collect(retValue)
      }
    })

    env.execute(this.getClass.getSimpleName)

  }

}
