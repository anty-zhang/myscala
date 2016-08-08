package sparkbasic

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by xiaoqiang on 16-6-30.
  */
object CogroupTest {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("CogroupTest").setMaster("local[5]")
    val sc = new SparkContext(conf)
    val data1 = sc.parallelize(List((1, "www"), (2, "bbs")))
    val data2 = sc.parallelize(List((1, "iteblog"), (2, "iteblog"), (3, "very")))
    val data3 = sc.parallelize(List((1, "com"), (2, "com"), (3, "good")))
    val result = data1.cogroup(data2, data3, data2)

  }
}
