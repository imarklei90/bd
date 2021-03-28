package flink.unbound

import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

/**
 * 自定义Source
 *
 * @author Lei
 * @date 2021/3/24
 */
object MySource {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 自定义Source
    import org.apache.flink.api.scala._
    env.addSource(new SourceFunction[String] {

      private var isRunning = true

      override def run(ctx: SourceFunction.SourceContext[String]): Unit = {
        while (isRunning){
          ctx.collect("")
        }
      }

      override def cancel(): Unit = {
        isRunning = false
      }
    })

    env.execute(this.getClass.getSimpleName)


  }

}
