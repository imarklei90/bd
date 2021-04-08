package flink.statebackend

import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.time.Time
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

/**
 * StateBackend - RocksDBStateBackend
 *
 * @author Lei
 * @date 2021/4/1
 */
object TestBackend_rocksdb {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    val backend: RocksDBStateBackend = new RocksDBStateBackend("hdfs://hadoop/rocksdb_state", true)
    // 设置状态后端,开启增量Checkpoint
    env.setStateBackend(backend)

    // 设置RocksDB存储的路径: 使用绝对路径
    backend.setDbStoragePath("data/rocksdb")

    // 开启Checkpoint
    env.enableCheckpointing(100000)


    val result: SingleOutputStreamOperator[(String, Int)] = env.socketTextStream("localhost", 7777)
      .filter(_.nonEmpty)
      .map((_, 1))
      .keyBy(0)
      .sum(1)

    result.print()

    env.execute(this.getClass.getSimpleName)

    // 设置重庆策略
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, Time.seconds(5)))

  }

}
