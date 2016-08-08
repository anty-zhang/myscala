package basic.collect.treemap

/**
  * Created by xiaoqiang on 16-6-17.
  * https://dzone.com/articles/custom-ordering-scala-treemap
  */

import scala.collection.immutable.TreeMap
object Simple {
  def main(args: Array[String]) {
    val dtm = TreeMap( "a" -> 1, "bc" -> 2, "def" -> 3 )
    dtm.map(println)

    println("===============================")
    val dtm1 = TreeMap( "a" -> 1, "bc" -> 2, "def" -> 3 )(VarNameOrdering)
    dtm1.map(println)

  }
}


object VarNameOrdering extends Ordering[String] {
  def compare(a:String, b:String) = b.length compare a.length
}

