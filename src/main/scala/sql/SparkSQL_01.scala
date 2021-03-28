package sql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object SparkSQL_01 {

  def main(args: Array[String]): Unit = {

    // 创建SparkSQL环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkSQL")
    // 创建SparkSession对象
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    // 导入隐式转换(使用DataFrame时，如果涉及到转换操作，需要引入抓换规则)
    import spark.implicits._

    // 创建DataFrame
    val df: DataFrame = spark.read.json("data/user.json")
    df.show()

    // SQL DSL
    df.createOrReplaceTempView("user")
    // SQL
    spark.sql("select * from user").show()
    // DSL
    df.select($"age" + 1).show()

    // 创建DataSet
    val seq = Seq(1,2,3)
    val ds: Dataset[Int] = seq.toDS()
    ds.show()

    // RDD
    val userRDD = spark.sparkContext.makeRDD(List(("zhangsan", 1), ("lisi", 2)))

    // RDD转换为DF
    val newDF = userRDD.toDF("username", "age")
    println(newDF)

    // DF转换为RDD
    val newRDD = newDF.rdd
    println(newRDD)

    // DF转换为DS
    val newDS: Dataset[User] = newDF.as[User]
    println(newDS)

    // DS转换为RDD
    val rddFromDS = newDS.rdd
    println(rddFromDS)

    // DS转换为DF
    val dfFromDS = newDS.toDF()
    println(dfFromDS)

    // RDD转换为DS
    val newDSFromRDD: Dataset[User] = userRDD.map {
      case (username, age) => {
        User(username, age)
      }
    }.toDS()



    // 关闭
    spark.close()

  }


  case class User(username: String, age: Int)
}
