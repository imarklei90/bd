package eCommerce

import org.apache.spark.{SparkConf, SparkContext}

object HotCategoryTop10 {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("HotCategoryTop10")

    val sc = new SparkContext(sparkConf)

    // 读取数据
    val data = sc.textFile("data/user_visit_action.txt")

    // 统计品类的点击数量
    val clickActionRDD = data.filter(
      data => {
        val datas = data.split(" ")
        datas(6) != "-1"
      }
    )

    clickActionRDD.map(
      data => {
        val datas = data.split("_")
        (datas(6), 1)
      }
    )

    clickActionRDD.map(
      action => {
        val datas = action.split("_")
        (datas(6), 1)
      }
    )


    // 统计品类的下单数量

    // 统计品类的支付数量

    // 将品类进行排序，取前10


    sc.stop

  }
}
