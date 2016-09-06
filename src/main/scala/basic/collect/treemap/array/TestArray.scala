package basic.collect.treemap.array

import scala.collection.mutable.HashMap

/**
  * Created by xiaoqiang on 16-8-11.
  */
object TestArray {
  def main(args: Array[String]) {
    val articleDistinctMap = new HashMap[String, Array[String]]

    val key = "test_key"
    var mapValue = articleDistinctMap.getOrElse(key, Array("0"))
    println(mapValue.toSeq)

    if (mapValue(0) == "0") {
      articleDistinctMap.update(key, Array("1", "2", "3"))
    } else {
      articleDistinctMap.update(key, Array("1", "2", "3"))
    }

    mapValue = articleDistinctMap.getOrElse(key, Array("0"))

    println(mapValue.toSeq)

    if (mapValue(0) == "0") {
      println("=====1======")
      articleDistinctMap.update(key, Array("1", "2", "3"))
    } else {
      println("=====2======")
      articleDistinctMap.update(key, Array("1", "4", "3"))
    }


    mapValue = articleDistinctMap.getOrElse(key, Array("0"))

    println(mapValue.toSeq)

  }
}
