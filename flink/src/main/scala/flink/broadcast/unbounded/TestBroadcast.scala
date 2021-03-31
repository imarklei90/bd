package flink.broadcast.unbounded

import org.apache.flink.api.common.state.MapStateDescriptor
import org.apache.flink.api.common.typeutils.base.{CharValueSerializer, IntSerializer}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.datastream.BroadcastStream
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction
import org.apache.flink.streaming.api.scala.{BroadcastConnectedStream, DataStream, StreamExecutionEnvironment}
import org.apache.flink.types.CharValue
import org.apache.flink.util.Collector

/**
 * 广播变量 - 无界流
 *
 * @author Lei
 * @date 2021/3/30
 */
object TestBroadcast {

  def main(args: Array[String]): Unit = {

    // 创建执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    // 获取两个无界流
      val genderInfoDS: DataStream[(Int, Char)] = env.socketTextStream("localhost", 7777)
      .filter(_.nonEmpty)
      .map {
        genderInfo =>
          val genderInfoArr: Array[String] = genderInfo.split(",")
          (genderInfoArr(0).trim.toInt, genderInfoArr(1).trim.charAt(0))
      }

    val personInfoDS: DataStream[(Int, String, Int, String)] = env.socketTextStream("localhost", 8888)
      .filter(_.nonEmpty)
      .map {
        personInfo =>
          val personInfoArr: Array[String] = personInfo.split(",")
          (personInfoArr(0).toInt, personInfoArr(1), personInfoArr(2).toInt, personInfoArr(3))
      }

    // 准备MapStateDescriptor实例,用于给BroadcastStream中的每个元素添加一个标识
    val genderInfoMapStateDesc: MapStateDescriptor[Integer, CharValue] = new MapStateDescriptor(
      "genderInfoMapStateDesc", // 命名MapStateDescriptor
      new IntSerializer(), // 维护Key状态使用的序列化器
      new CharValueSerializer() // 维护Value状态使用的序列化器
    )

    // 将数据量少的DataStream封装成广播无界流
    val genderInfoBroadcastDS: BroadcastStream[(Int, Char)] = genderInfoDS.broadcast(genderInfoMapStateDesc)

    // connect，转换成ConnectedStream
    val genderInfoBCConnectedStream: BroadcastConnectedStream[(Int, String, Int, String), (Int, Char)] = personInfoDS.connect(genderInfoBroadcastDS)

    // 对广播连接流进行计算
    val resultDS: DataStream[(Int, String, Char, String)] = genderInfoBCConnectedStream.process(new MyBroadcastProcessFunction(genderInfoMapStateDesc))
    resultDS.print()


    // 执行
    env.execute(this.getClass.getSimpleName)

  }

  /**
   * <IN1> – The input type of the non-broadcast side. 非广播流一侧的DataStream中每个元素的类型
   * <IN2> – The input type of the broadcast side. 广播流一侧的DataStream中的每个元素的类型
   * <OUT> – The output type of the operator. 输出的DataStream类型
   */
  private class MyBroadcastProcessFunction(genderInfoMapStateDesc: MapStateDescriptor[Integer, CharValue]) extends BroadcastProcessFunction[(Int, String, Int, String), (Int, Char), (Int, String, Char, String)]{
    /**
     * 处理非广播流一侧的数据
     * @param value
     * @param ctx
     * @param out
     */
    override def processElement(value: (Int, String, Int, String),
                                ctx: BroadcastProcessFunction[(Int, String, Int, String), (Int, Char), (Int, String, Char, String)]#ReadOnlyContext,
                                out: Collector[(Int, String, Char, String) ]): Unit = {
      // 从mapStateDescriptor获得BroadcastState的实例,然后获取性别的值
      val genderTemp: CharValue = ctx.getBroadcastState(genderInfoMapStateDesc).get(value._3)

      val gender: Char = if(genderTemp == null) '*' else genderTemp.getValue
      // 返回数据
      out.collect((value._1, value._2, gender, value._4))
    }

    /**
     * 处理广播流一侧的数据
     * @param value
     * @param ctx
     * @param out
     */
    override def processBroadcastElement(value: (Int, Char),
                                         ctx: BroadcastProcessFunction[(Int, String, Int, String), (Int, Char), (Int, String, Char, String)]#Context,
                                         out: Collector[(Int, String, Char, String)]): Unit = {
      // 将当前广播流中的数据封装到BroadcastState中存储
      ctx.getBroadcastState(genderInfoMapStateDesc).put(value._1, value._2)
    }
  }


}

