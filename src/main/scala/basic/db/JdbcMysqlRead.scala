package basic.db

/**
  * Created by xiaoqiang on 16-7-20.
  */

import java.util.Properties
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Row, SQLContext, types}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}


object JdbcMysqlRead {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("JdbcMysqlRead").setMaster("local[5]")
    val sc = new SparkContext(conf)
    val sQLContext = new SQLContext(sc)

    val res = sQLContext.read.format("jdbc").options(
      Map("url" -> "jdbc:mysql://xiaoqiang-zdm:3306/spark?useSSL=true",
      "dbtable" -> "person",
      "user" -> "root",
      "password" -> "xxx")
    ).load()

    res.collect().map(println)

    val data = res.map(x => (x(0).toString, x(1).toString))

    println("=============")
    data.collect().map(println)
  }
}
