package flink.accumulator

import org.apache.flink.api.common.JobExecutionResult
import org.apache.flink.api.common.accumulators.IntCounter
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration

/**
 * 累加器 - 有界流
 *
 * @author Lei
 * @date 2021/3/31
 */
object TestAccumulator {

  def main(args: Array[String]): Unit = {

    import org.apache.flink.api.scala._

    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    env.readTextFile("data/data.txt")
      .map(person => {
          val personArr: Array[String] = person.split(",")
          (personArr(0), personArr(1), personArr(2).toInt, personArr(3))
        }).map(new MyRichMapFunction)
      .print()

    val result: JobExecutionResult = env.execute(this.getClass.getSimpleName)
    // 获取执行后的结果中累加器的值
    result.getAccumulatorResult("normalAcc")
    result.getAccumulatorResult("unnormalAcc")


  }

  private class MyRichMapFunction extends RichMapFunction[(String, String, Int, String), String]{

    private var normal: IntCounter = _
    private var unnormal: IntCounter = _


    override def open(parameters: Configuration): Unit = {
      // 初始化累加器
      normal = new IntCounter()
      unnormal = new IntCounter()

      // 注册累加器
      val context = getRuntimeContext
      context.addAccumulator("normalAcc", normal)
      context.addAccumulator("unnormalAcc", unnormal)

    }

    override def map(value: (String, String, Int, String)): String = {

        // 使用累加器
       if(value._3> 16.2 && value._3 < 37.2){
         normal.add(1)
         s"${value._1}正常"
       }else{
         unnormal.add(1)
         s"${value._1}异常"
       }

      null
    }

    override def close(): Unit = {

    }
  }

}
