package basic.stream

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.DStream

/**
  * Created by xiaoqiang on 16-6-4.
  * 一个简单的spark streaming程序
  *
  * apt install netcat
  * client: nc -l 7777 -k -c datagen.cmd
  */


object StreamBasicExp {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("StreamBasicExp").setMaster("local[5]")

    // 1. create a StreamingContext with a 1 seconds batch
    // 直接通过conf创建StreamingContext
    val ssc = new StreamingContext(conf, Seconds(10))

    // 2. create a DStream from all the input on port 7777
    val lines = ssc.socketTextStream("xiaoqiang-zdm", 7777)
    val errLines = processLines(lines)
    errLines.print()

    // 3. start our streaming context and wait for it to 'finish'
    ssc.start()

    // 4. Wait for 30 seconds then exit. To run forever call without a timeout
    ssc.awaitTerminationOrTimeout(300 * 1000)

    ssc.stop()
  }

  def processLines(lines: DStream[String]) = {
    // filter our DStream for lines with error
    lines.filter(_.contains("error"))
  }
}
