package basic.db

/**
  * Created by xiaoqiang on 16-7-20.
  */

import java.util.Date
import java.util.Properties
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Row, SQLContext, types}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}


object JdbcMysqlOp {
  def main(args: Array[String]) {
    val startTime = new Date().getTime
    val conf = new SparkConf().setAppName("JdbcMysqlRead").setMaster("local[5]")
    val sc = new SparkContext(conf)
    val sQLContext = new SQLContext(sc)

    val endTime = new Date().getTime

    println("spend time: %d" + (endTime - startTime))

  }


  def readRead(sQLContext: => SQLContext): Unit = {
    val res = sQLContext.read.format("jdbc").options(
      //      Map("url" -> "jdbc:mysql://xiaoqiang-zdm:3306/spark?useSSL=true",
      //      Map("url" -> "jdbc:mysql://localhost:3306/recommendDB",
      Map("url" -> "jdbc:mysql://xiaoqiang-zdm:3306/recommendDB",
        "dbtable" -> "recommend_feedback",
        "user" -> "root",
        "password" -> "xxx")
    ).load()

    res.collect().map(println)
  }
}
