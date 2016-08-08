package sparkbasic

/**
  * Created by xiaoqiang on 16-6-29.
  */
import org.apache.spark.{SparkConf, SparkContext}


object MapPartitions {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("MapPartitions").setMaster("local[5]")
    val sc = new SparkContext(conf)

    val data = Array[(String, Int)](("A1", 1), ("A2", 2),
      ("B1", 1), ("B2", 4),
      ("C1", 3), ("C2", 4)
    )
    val pairs = sc.parallelize(data, 3)

    val finalRDD = pairs.mapPartitions(iter => iter.filter(_._2 >= 2))

    finalRDD.collect().map(println)

  }
}
