package flink.broadcast.bounded

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration

/**
 * 广播变量 - 有界流
 *
 * @author Lei
 * @date 2021/3/30
 */
object TestBroadcast {

  def main(args: Array[String]): Unit = {

    // 导入Scala中隐式的成员方法
    import org.apache.flink.api.scala._

    // 创建执行环境
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    // 获取数据集
    val genderDS: DataSet[(Int, Char)] = env.fromElements((1, '男'), (2, '女'))

    val personDS: DataSet[(Int, String, Int, String)] = env.fromElements(
      (1, "张三", 1, "安徽合肥"),
      (2, "李四", 0, "北京"),
      (3, "王五", 1, "上海")
    )

    val BROADCAST_NAME = "genderBC"

    // 将数据量小的数据集封装成广播变量，据此来分析数据量大的数据集\
    // 需要根据上下文信息获取都广播变量的值，所以需要实现RichMapFunction,可以获取上下文信息
    val resultDS: DataSet[(Int, String, Char, String)] = personDS.map(new MyMapFunction(BROADCAST_NAME))
      .withBroadcastSet(genderDS, BROADCAST_NAME)

    resultDS.print()

  }

  /**
   *
   * @param broadcastName 广播变量名称
   */
  private class MyMapFunction(broadcastName: String) extends RichMapFunction[(Int, String, Int, String), (Int, String, Char, String)] {
    // 存储广播变量中存放的值
    var broadcastInfo: Map[Int, Char] = _

    /**
     * 初始化方法: 可以用来获取广播变量的数据集
     *
     * @param parameters
     */
    override def open(parameters: Configuration): Unit = {
      import scala.collection.JavaConversions._
      val broadcastInfoTemp = this.getRuntimeContext.getBroadcastVariable(broadcastName).
      broadcastInfo = broadcastInfoTemp.toMap
    }

    /**
     * DataSet中每个元素都会执行一次map
     *
     * @param in
     * @return
     */
    override def map(value: (Int, String, Int, String)): (Int, String, Char, String) = {
      val gender: Int = value._3
      // 根据性别标识值从广播变量中获取值
      val genderInfo: Char = broadcastInfo(gender)
      (value._1, value._2, genderInfo, value._4)
    }

    /**
     *
     */
    override def close(): Unit = {

    }
  }

}
