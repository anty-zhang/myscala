package basic.db

/**
  * Created by xiaoqiang on 16-6-10.
  * 通过java的方式讲数据写入DB
  *
  *
CREATE TABLE `blog` (
  `name` varchar(255) NOT NULL,
  `count` int(10) unsigned DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8

  */

import java.sql.{DriverManager, PreparedStatement, Connection}
import org.apache.spark.{SparkContext, SparkConf}


object RDDToMysql {
  class Blog (name: String, count: Int)

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("RDDToMysql").setMaster("local[5]")
    val sc = new SparkContext(conf)

    val data = sc.parallelize(List(("www", 10), ("iteblog", 20), ("com", 50)))
    data.foreachPartition(updateMysqlFun)
  }

  def updateMysqlFun(iterator: Iterator[(String, Int)]): Unit = {
    var conn: Connection = null
    var ps: PreparedStatement = null
    val sql = "insert into blog(name, count) values (?, ?)"

    // 可以转换为批量计算
    try {
      conn = DriverManager.getConnection("jdbc:mysql://xiaoqiang-zdm:3306/spark", "root", "xxx")
      iterator.foreach(data=> {
        ps = conn.prepareStatement(sql)
        ps.setString(1, data._1)
        ps.setInt(2, data._2)
        ps.executeUpdate()
      })

    }catch {
      case e: Exception => println("Mysql Exception")
    } finally {
      if (ps != null) {
        ps.close()
      }

      if (conn != null){
        conn.close()
      }
    }
  }
}
