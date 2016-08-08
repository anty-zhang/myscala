import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by xiaoqiang on 16-6-16.
  */
object Test {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("AggregateAvgApp")
    val sc = new SparkContext(conf)
//    val input = sc.parallelize(Array(("did1", (1, 2, 0.1)), ("did1", (1, 2, 0.4)), ("did1", (10, 3, 0.4)),
//      ("did2", (1, 2, 0.1)), ("did3", (1, 2, 0.4))))
//
//
//    input.collect().map(println)
//
//    val res = input.map(x=> (x._1, Map(x._2._2 -> (x._2._1, x._2._3))))
//      val res1 = res.reduceByKey{ (x, y) =>
//                  val y_keys_array = y.keys.toArray
//                  val y_key = y_keys_array(0)
//                  val y_value = y.get(y_key).toArray
//                  if ( x.getOrElse(y_key, 0) != 0) {
//                    val x_value = x.get(y_key).toArray
//                    if (x_value(0)._2 < y_value(0)._2) {
////                      x.get(y_key) = y_value
//                      x -> Map(1-> (2, 3), 2-> (4,5))
//                    }
//                  }
//
//                  x -> Map(1-> (2, 3), 2-> (4,5))
//                }


//    val inputData = sc.parallelize(List((1, 2.0), (2, 3.0), (3, 4.0), (4, 5.0)))
//    val tData = inputData.map(x=> x._2)
//    val max =tData.max()
//    val min = tData.min()
//    println("max: " + max)
//    println("min: " + min)
//
//    val res1 = inputData.map{x=>
//      val conf = (x._2 - min) / (max - min)
//      println("x._1:" + x._1 + "conf:" + conf)
//      (x._1, conf)
//    }
//
//    res1.collect().map(println)
    val rdd1 = sc.parallelize(List((1, (2, 3)), (2, (2, 4)), (3, (5, 6))))
    val rdd2 =sc.parallelize(List((1, None)))
    val res = rdd1.subtractByKey(rdd2)

    res.collect().map(println)
  }
}
