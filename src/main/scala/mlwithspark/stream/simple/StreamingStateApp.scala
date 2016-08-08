package mlwithspark.stream.simple

import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by xiaoqiang on 16-6-4.
  *
  * 有状态的流计算
  * 计算每个用户的购买总次数和总收入
  */
object StreamingStateApp {
  def main(args: Array[String]) {
    val ssc = new StreamingContext("local[5]", "StreamingStateApp", Seconds(10))

    // for stateful operations, we need to set a checkpoint location
    // 有状态的操作需要设置一个检查点
    ssc.checkpoint("/tmp/sparkstreaming/")
    val stream = ssc.socketTextStream("localhost", 9999)

    // create streams of events from raw text elements
    val events = stream.map{record =>
      val event = record.split(",")
      (event(0), event(1), event(2).toDouble)
    }

    val users = events.map{ case (user, product, price) => (user, (product, price))}
    val revenuePerUser = users.updateStateByKey(updateState)      // updateStateByKey 使用

    users.saveAsTextFiles("/home/xiaoqiang/data/zdm/ouput/stream/1")

    // print
    revenuePerUser.print()

    ssc.start()
    ssc.awaitTermination()
  }

  // 计算每个用户的购买总数量和总收入，是全局变量
  // Option, Some
  def updateState(user: Seq[(String, Double)], currentTotal: Option[(Int, Double)]) = {
    val currentRevenue = user.map(_._2).sum    // 总收入
    val currentNumberPurchase = user.size      // 购买总次数

    val state = currentTotal.getOrElse((0, 0.0))
    Some((currentNumberPurchase + state._1, currentRevenue + state._2))
  }
}
