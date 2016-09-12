package es

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.EsSpark
import org.apache.spark.sql.hive.HiveContext
import org.elasticsearch.spark.sql._

/**
  * desc: elasticsearch 简单实例
  * Created by xiaoqiang on 16-8-19.
  */

object SimpleEsTest {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("SimpleEsTest").setMaster("local[5]")
    conf.set("es.nodes", "localhost")
//    conf.set("es.resource", "test")
    conf.set("es.port", "9200")
//    conf.set("es.index.auto.create", "true")
    val sc = new SparkContext(conf)

//    SimpleEsTest.write_rdd_by_document_id(sc)
    SimpleEsTest.write_rdd(sc)
  }


  // 基于RDD导入数据,并制定document_id
  def write_rdd_by_document_id(sc: => SparkContext):Unit = {
    case class Trip(oid: String, departure: String, arrival: String)
    val upcomingTrip = Trip("1", "OTP", "SFO")
    val lastWeekTrip = Trip("2", "MUC", "OTP")
    val rdd = sc.makeRDD(Seq(upcomingTrip, lastWeekTrip))
    EsSpark.saveToEs(rdd, "test/ext", Map("es.mapping.id" -> "oid"))
  }

  // 基于RDD导入数据
  // 这种保存会自动生成document_id, 可通过spark/ext/_search 查询
  def write_rdd(sc: => SparkContext): Unit = {
    val numbers = Map("one1" -> 1, "two1" -> 2, "three1" -> 3)
//    val airports = Map("arrival" -> "Otopeni", "SFO" -> "San Fran")
//    sc.makeRDD(Seq(numbers, airports)).saveToEs("spark/ext")
    val res = sc.makeRDD(Seq(numbers))

    res.saveToEs("spark/ext")
  }

  // 基于spark sql的dataframe导入数据
  def write_rdd_spark_sql(sc: => SparkContext): Unit = {
    val hiveContext = new HiveContext(sc)
//    val sqlContext = new SQLContext(sc)
    val df = hiveContext.sql("select * from tmp.z_wo_order limit 50")
    df.saveToEs("z_wo_order/record", Map("es.mapping.id" -> "order_id"))
  }
}
