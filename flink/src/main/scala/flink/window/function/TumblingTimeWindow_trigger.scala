package flink.window.function

import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.triggers.{Trigger, TriggerResult}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

/**
 * ReduceFunction
 *
 * @author Lei
 * @date 2021/4/1
 */
object TumblingTimeWindow_trigger {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    env.socketTextStream("localhost", 7777)
      .filter(_.nonEmpty)
      .map( person => {
        val personArr: Array[String] = person.split(",")
        (personArr(0), personArr(1).toInt, personArr(2))
      })
      .keyBy(0)
      .window(TumblingEventTimeWindows.of(Time.seconds(3)))
      .trigger(new MyTrigger(3))

    env.execute(this.getClass.getSimpleName)
  }

  private class MyTrigger(i: Int) extends Trigger[(String, Int), TimeWindow]{
    /**
     * 窗口中每进入一个数据，就触发执行一次
     * @param element 输入的数据
     * @param timestamp 数据到达的时间戳
     * @param window 窗口
     * @param ctx trigger的上下文信息
     * @return
     */
    override def onElement(element: (String, Int), timestamp: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = ???

    /**
     * 计时触发
     * @param time ProcessingTime
     * @param window
     * @param ctx
     * @return
     */
    override def onProcessingTime(time: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = {
      TriggerResult.FIRE_AND_PURGE
    }

    /**
     * 计时触发
     * @param time
     * @param window
     * @param ctx
     * @return
     */
    override def onEventTime(time: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = {
      TriggerResult.CONTINUE
    }

    /**
     * 清空窗口中的元素
     * @param window 窗口
     * @param ctx trigger的上下文
     */
    override def clear(window: TimeWindow, ctx: Trigger.TriggerContext): Unit = ???
  }

}
