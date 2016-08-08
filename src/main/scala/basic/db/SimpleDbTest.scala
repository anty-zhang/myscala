package basic.db

/**
  * Created by xiaoqiang on 16-5-24.
  * Illustrates writing data over db
  */

import org.apache.spark.{SparkConf, SparkContext}
import java.sql.{PreparedStatement, ResultSet}

import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapred.lib.db.{DBConfiguration, DBOutputFormat, DBWritable}


object SimpleDbTest {
  def main(args: Array[String]) {
    operation_db()
  }

  def operation_db(): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("SimpleDbTest")
    val sc = new SparkContext(conf)
    val data = sc.parallelize(List(("cat1", 10)))     // 元组列表

    //foreach partition method
    data.foreachPartition { records =>
      records.foreach(record => println("fake db write: " + record))
    }

    // DBOutputFormat approach
    // why the end is null ????? TODO
    val records = data.map(x=> (CatRecord(x._1, x._2), null))
    val tableName = "cate"
    val fields = Array("name", "age")
    val jobConf = new JobConf()
    DBConfiguration.configureDB(jobConf, "com.mysql.jdbc.Driver", "jdbc:mysql://localhost/spark?user=root", "root", "xxx")
    DBOutputFormat.setOutput(jobConf, tableName, fields: _*)
    records.saveAsHadoopDataset(jobConf)
  }


  // data object
  // extends DBWritable
  // PreparedStatement
  // ResultSet
  case class CatRecord(name: String, age: Int) extends DBWritable {
    override def readFields(resultSet: ResultSet): Unit = {

    }

    override def write(s: PreparedStatement): Unit = {
      s.setString(1, name)
      s.setInt(2, age)
    }
  }
}
