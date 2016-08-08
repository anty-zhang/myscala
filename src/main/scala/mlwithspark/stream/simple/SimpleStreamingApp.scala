package mlwithspark.stream.simple

import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by xiaoqiang on 16-6-4.
  *
  * 简单的监听9999端口，并进行打印操作
  */
object SimpleStreamingApp {
  def main(args: Array[String]) {
    // create a StreamingContext
    val ssc = new StreamingContext("local[2]", "SimpleStreamingApp", Seconds(10))
    val stream = ssc.socketTextStream("localhost", 9999)

    // here we simply print out the first few elements of each batch
    stream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
