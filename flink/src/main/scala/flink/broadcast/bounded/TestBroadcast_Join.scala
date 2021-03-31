package flink.broadcast.bounded

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration

/**
 * 有界流 使用内连接的方式实现
 *
 * @author Lei
 * @date 2021/3/30
 */
object TestBroadcast_Join {

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

    // 使用内连接
    val resultDS: DataSet[(Int, String, Char, String)] = genderDS.join(personDS)
      .where(0)
      .equalTo(2) {
        (l, r) => (r._1, r._2, l._2, r._4)
      }

    resultDS.print()


  }
}
