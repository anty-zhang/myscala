package mlwithspark.stream.simple

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.streaming.{Seconds, StreamingContext}
/**
  * Created by xiaoqiang on 16-6-4.
  * A more complex Steaming app,
  * which computes statistics
  * and print the results for each batch in a DStream
  *
  * 只是统计每个batch的
  */
object StreamingAnalyticsApp {
  def main(args: Array[String]) {
    val ssc = new StreamingContext("local[4]", "StreamingAnalyticsApp", Seconds(10))
    val stream = ssc.socketTextStream("localhost", 9999)

    // create stream of events from raw text elements
    val events = stream.map{record =>
      val event = record.split(",")
      (event(0), event(1), event(2))
    }

    /*
      We compute and print out stats for each batch.
      Since each batch is an RDD, we call forEeachRDD on the DStream, and apply the usual RDD functions
      we used in Chapter 1.
     */
    events.foreachRDD{ (rdd, time) =>
      val numPurchases = rdd.count()      // 购买商品总量
      val uniqueUsers = rdd.map{ case (user, _, _) => user}.distinct().count()    //唯一用户数
      val totalRevenue = rdd.map{ case (_, _, price) => price.toDouble}.sum().formatted("%.4f") // 购买总金额
      // 畅销产品
      val productsByPopularity = rdd.map{ case (user, product, price) => (product, 1)}
                            .reduceByKey(_ + _)
                            .collect()
                            .sortBy(-_._2)    // 降序

      // 最畅销产品
      val mostProduct = productsByPopularity(0)

      // print
      val formatter = new SimpleDateFormat()
      val datastr = formatter.format(new Date(time.milliseconds))
      println(s"===Batch start time: $datastr ===")
      println("Total purchases: " + numPurchases)
      println("Unique users: " + uniqueUsers)
      println("Total revenue: " + totalRevenue)
      println("Most popular product: %s with %s products".format(mostProduct._1, mostProduct._2))

    }



    // start the context
    ssc.start()
    ssc.awaitTermination()
  }
}
