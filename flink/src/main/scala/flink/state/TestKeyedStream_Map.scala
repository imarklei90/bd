package flink.state

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

/**
 * State - KeyedStream 使用Map
 *
 * @author Lei
 * @date 2021/3/31
 */
object TestKeyedStream_Map {

  def main(args: Array[String]): Unit = {

    import org.apache.flink.api.scala._

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    env.socketTextStream("localhost", 7777)
      .filter(_.nonEmpty)
      .map(personInfo => {
        val personArr: Array[String] = personInfo.split(",")
        (personArr(0), personArr(1))
      }).keyBy("").map(new RichMapFunction[(String, String), (String, String)] {

      // 存储上次的数据信息
      var valueState: ValueState[String] = _

      override def open(parameters: Configuration): Unit = {
        val stateProperties = new ValueStateDescriptor[String]("personState", classOf[String])
        valueState = getRuntimeContext.getState[String](stateProperties)
      }

      override def map(value: (String, String)): (String, String) = {

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

        retValue
      }
    })

    env.execute(this.getClass.getSimpleName)

  }

}
