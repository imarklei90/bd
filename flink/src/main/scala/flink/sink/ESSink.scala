package flink.sink

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest

import scala.collection.JavaConversions._

/**
 * Sink之 ElasticSearch
 *
 * @author Lei
 * @date 2021/3/28
 */
object ESSink {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    val httpHosts: List[HttpHost] = List(
      new HttpHost("hadoop101", 9200),
      new HttpHost("hadoop102", 9201)
    )
    val elasticsearchSinkFunction: ElasticsearchSinkFunction[String] = new MyElasticsearchSinkFunction()

    val builder: ElasticsearchSink.Builder[String] = new ElasticsearchSink.Builder[String](httpHosts, elasticsearchSinkFunction)

    // 用于设置客户端缓存中有多少条数据后一起向远程的ES分布式集群中发送，如果不设置，处理后的数据不能实时落地到ES分布式集群中
    builder.setBulkFlushMaxActions(1)
    val esSink: ElasticsearchSink[String] = builder.build()

    // Source
    env.socketTextStream("localhost", 7777)
      .addSink(esSink)

    env.execute(this.getClass.getSimpleName)

  }

  /**
   * 自定义ElasticsearchSinkFunction实例
   */
    private class MyElasticsearchSinkFunction extends ElasticsearchSinkFunction[String]{
    /**
     *
     * @param element 将要处理的数据
     * @param ctx 和Sink实例相关的运行时的上下文信息
     * @param indexer 将Request添加到实例中
     */
    override def process(element: String, ctx: RuntimeContext, indexer: RequestIndexer): Unit = {

      // id:每条数据的唯一标识
      val id = element + "---" + element

      // 准备document的内容
      val content: Map[String, String] = Map(
        "key" -> "value"
      )
      val javaMap: java.util.Map[String, String] = content

      // 构建IndexRequest实例
      val indexRequest = new IndexRequest("indexName", "type", id)
        .source(javaMap)

      // 将当前document信息添加到索引库中
      indexer.add(indexRequest)
    }
  }


}
