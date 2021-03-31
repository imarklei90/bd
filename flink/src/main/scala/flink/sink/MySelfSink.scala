package flink.sink

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import java.sql.{Connection, DriverManager, PreparedStatement}

/**
 * 自定义Sink - JDBC
 *
 * @author Lei
 * @date 2021/3/29
 */
object MySelfSink {

  def main(args: Array[String]): Unit = {

    // 执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    // 业务
    env.socketTextStream("localhost", 7777)
      .addSink(new MySelfSink())

    // 执行
    env.execute(this.getClass.getSimpleName)

  }

  /**
   * 自定义Sink Function
   */
  private class MySelfSink extends RichSinkFunction[String]{

    // 用于连接
    var conn: Connection = _

    // 用于更新的PreparedStatement
    private var updateStatement: PreparedStatement = _

    // 用于插入的PreparedStatement
    private var insertStatement: PreparedStatement = _

    // 用于更新和插入
    private var insertOrUpdateStatement: PreparedStatement = _

    /**
     * 用于初始化操作
     * @param parameters
     */
    override def open(parameters: Configuration): Unit = {\
      // 获取连接实例
      conn = DriverManager.getConnection("jdbc:mysql://hadoop101:3306/<database>?serverTimezone=Asia/Shanghai&characterEncoding=utf-8", "username", "password")

      // 初始化全局变量
      updateStatement = conn.prepareStatement("update sql")
      insertStatement = conn.prepareStatement("insert sql")

      // 可以替换掉上面两个statement
      insertOrUpdateStatement = conn.prepareStatement(
        """
          | insert
          | into
          | <table>("", "", "", "", "")
          | values(?,?,?,?,?)
          | on duplicate key update set
          | key=?,key=?.....
          |
        """.stripMargin)

      // SQL 优化
      String sql = ""

    }

    /**
     * DataStream中每次过来一个数据，invoke()调用一次
     * @param value
     * @param context
     */
    override def invoke(value: String, context: SinkFunction.Context): Unit = {

      // 获取数据

      // 更新
      updateStatement.setInt(1, 1)
      updateStatement.setString(2, "")

      // 执行
      updateStatement.executeUpdate()

      // 更新结果
      if (updateStatement.getUpdateCount == 0) {
        // 更新失败，则插入数据
        insertStatement.setInt(1, 1)
        insertStatement.setString(2, "")
        // 执行
        insertStatement.executeUpdate()
      }

      // 可以替换上面的执行语句
      insertOrUpdateStatement.executeUpdate()
    }

      /**
       * 释放资源
       */
      override def close(): Unit = {
        // 关闭PreparedStatement
        if(insertStatement != null){
          insertStatement.close()
        }

        if(updateStatement != null){
          insertStatement.close()
        }

        if(insertOrUpdateStatement != null){
          insertOrUpdateStatement.close()
        }

        if(conn != null){
          conn.close()
        }

      }

  }

}
