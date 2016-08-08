package basic.sparksql

/**
  * Created by xiaoqiang on 16-5-29.
  */

import org.apache.spark. {SparkConf, SparkContext}
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.SaveMode

object HiveSqlTest {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("HiveSqlTest")
      .setMaster("spark://xiaoqiang-zdm:7077")
//          .setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext = new HiveContext(sc)

    sqlContext.sql("from user select id ").collect().foreach(println)

//    val rdd = sc.parallelize((1 to 100))


    val res = sqlContext.sql("select * from user_save where ds = '20160607'")
    res.registerTempTable("myuser")
    res.write.mode(SaveMode.Overwrite).partitionBy("ds").saveAsTable("user_save_1")
//    res.write.mode(SaveMode.Overwrite).partitionBy("ds").saveAsTable("user_save.ds=20160607")
    sqlContext.sql("insert into user_save partition(ds='20160608') select * from myuser")
    sc.stop()
  }



}
