package flink.sink

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.redis.RedisSink

/**
 * Redis Sink实现
 *
 * @author Lei
 * @date 2021/3/26
 */
object RedisSink {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    env.socketTextStream("lcoalhost", 6666)
      .filter(_.nonEmpty)
      .addSink(new RedisSink(flinkJedisCOnfig, redisSinkMapper))
    env.execute(this.getClass.getSimpleName)

  }

}
