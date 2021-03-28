package sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.{Encoder, Encoders, Row, SparkSession, functions}
import org.apache.spark.sql.expressions.{Aggregator, MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, LongType, StructField, StructType}

object SparkSQL_02 {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("UDF")

    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    import spark.implicits._

    val df = spark.read.json("data/user.json")

    df.createOrReplaceTempView("user")

    // UDF
    spark.udf.register("prefix_name", (name: String) => {
      "Name: " + name
    })

    // UDAF: 弱类型
    spark.udf.register("my_avg", new MyUDAF)

    // UDAF: 强类型
    spark.udf.register("my_avg2", functions.udaf(new MyUDAF2()))


    spark.sql("select my_avg2(age) from user").show()

    spark.close()

  }

  /**
   * 自定义UDAF函数: 弱类型
   */
  class MyUDAF extends UserDefinedAggregateFunction{
    // 输入数据的结构
    override def inputSchema: StructType = {
      StructType(
        Array(
          StructField("age", LongType)
        )
      )
    }

    // 缓冲区数据的结构
    override def bufferSchema: StructType = {
      StructType{
        Array(
          StructField("total", LongType),
          StructField("count", LongType)
        )
      }
    }

    // 函数计算结果的输出类型
    override def dataType: DataType = LongType

    // 函数稳定性
    override def deterministic: Boolean = true

    // 缓冲器初始化
    override def initialize(buffer: MutableAggregationBuffer): Unit = {
      // buffer(0) = 0L
      // buffer(1) = 0L

      buffer.update(0, 0L)
      buffer.update(1, 0L)
    }

    // 根据输入的值更新缓冲区
    override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
      buffer.update(0, buffer.getLong(0) + input.getLong(0))
      buffer.update(1, buffer.getLong(1) + 1)
    }

    // 缓冲器数据合并
    override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
      buffer1.update(0, buffer1.getLong(0) + buffer2.getLong(0))
      buffer1.update(1, buffer1.getLong(1) + buffer2.getLong(1))
    }

    // 计算平均值
    override def evaluate(buffer: Row): Any = {
      buffer.getLong(0) / buffer.getLong(1)
    }
  }

  /**
   * 自定义UDAF函数: 强类型
   *
   */

  case class Buff(var total: Long, var count: Long)

  class MyUDAF2 extends Aggregator[Long, Buff, Long] {
    // 初始值或者零值
    // 缓冲器初始化
    override def zero: Buff = {
      Buff(0L, 0L)
    }

    // 根据输入的数据更新缓冲区的数据
    override def reduce(buff: Buff, in: Long): Buff = {
      buff.total = buff.total + in
      buff.count = buff.count + 1
      buff
    }

    // 合并缓冲区
    override def merge(buff1: Buff, buff2: Buff): Buff = {
      buff1.total = buff1.total + buff2.total
      buff1.count = buff1.count + buff2.count
      buff1
    }

    // 计算结果
    override def finish(buff: Buff): Long = {
      buff.total / buff.count
    }

    // 缓冲区的编码操作
    override def bufferEncoder: Encoder[Buff] = Encoders.product

    // 输出编码操作
    override def outputEncoder: Encoder[Long] = Encoders.scalaLong
  }

}
