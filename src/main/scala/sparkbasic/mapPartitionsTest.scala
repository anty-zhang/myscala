package sparkbasic

/**
  * Created by xiaoqiang on 16-6-29.
  */

import org.apache.spark.{SparkConf, SparkContext}

object mapPartitionsTest {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("mapPartitionsTest").setMaster("local[5]")
    val sc = new SparkContext(conf)
    val rdd1 =  sc.parallelize(
      List("yellow", "red", "blue", "cyan", "black"),
      3
    )

    val mapped = rdd1.mapPartitionsWithIndex{
      // 'index' represents the Partition No
      // 'iter' to iterate through all elements
      // in the partition
      (index, iter) => {
        println("Called in Partition -> " + index)
        val myList = iter.toList
        val res = myList.map(x => x + " -> " + index).iterator
        res
      }
    }

    var i = -1
    val resRdd1 = rdd1.map{x =>
      if (i == 2) {
        i = -1
      }
      i += 1
      println("i= " + i)
      (i, x(0))

    }
    resRdd1.collect().map(println)

  }
}
