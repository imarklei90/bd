package flink.sink

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisClusterConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

import java.net.InetSocketAddress
import scala.collection.JavaConversions._
/**
 * Redis Sink实现
 *
 * @author Lei
 * @date 2021/3/26
 */
object RedisSink {

  val flinkJedisConfig = new FlinkJedisClusterConfig.Builder().setNodes(nodes).build()

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    env.socketTextStream("lcoalhost", 6666)
      .filter(_.nonEmpty)
      .addSink(new RedisSink(flinkJedisConfig, new MyRedisMapper))
    env.execute(this.getClass.getSimpleName)

  }

  private val nodes: Predef.Set[InetSocketAddress] = Set(
    new InetSocketAddress("hadoop101", 6379),
    new InetSocketAddress("hadoop102", 6380)
  )

  private class MyRedisMapper extends RedisMapper[String]{
    /**
     * @return 返回定义数据类型的描信息
     */
    override def getCommandDescription: RedisCommandDescription = {
      new RedisCommandDescription(RedisCommand.HSET, "allTempException")
    }

    /**
     * 从参数指定的信息中获取key
     * @param t
     * @returnS
     */
    override def getKeyFromData(t: String): String = {
      return t.toString
    }

    /**
     * 从参数指定的信息中获取Value
     * @param t
     * @return
     */
    override def getValueFromData(t: String): String = {
      return t.toString
    }
  }

}
