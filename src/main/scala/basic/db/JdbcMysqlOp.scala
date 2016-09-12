package basic.db

/**
  * Created by xiaoqiang on 16-7-20.
  */

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.types.{BooleanType, _}



object JdbcMysqlOp {
  def main(args: Array[String]) {
    val startTime = new Date().getTime
    val conf = new SparkConf().setAppName("JdbcMysqlRead").setMaster("local[5]")
    val sc = new SparkContext(conf)
    val sQLContext = new SQLContext(sc)

//    readAndInsertExp(sQLContext)

    val endTime = new Date().getTime

    println("spend time: %d" + (endTime - startTime))
    println("==================通过jdbc的方式插入到mysql=============================")
    val data = sc.parallelize(List(("wwww", 10), ("iteblog", 2), ("com", 30)))
    data.foreachPartition(sparkInsertByJDBC)

  }


  case class Blog(name: String, count: Int)
  /**
    * desc: 通过jdbc的方式,将spark的运算结果写入mysql等关系型数据库
    * @param iterrator
    *                  CREATE TABLE `blog` (
  `name` varchar(255) NOT NULL,
  `count` int(10) unsigned DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf-8
    */
  def sparkInsertByJDBC(iterrator: Iterator[(String, Int)]): Unit = {
    import java.sql.{DriverManager, PreparedStatement, Connection}
    var conn: Connection = null
    var ps: PreparedStatement = null
    val sql: String = "insert into blog (name, count) values (?, ?)"
    try {
      conn = DriverManager.getConnection("jdbc:mysql://xiaoqiang-zdm:3306/recommendDB", "root", "xxx")
      iterrator.foreach{data =>
        ps = conn.prepareStatement(sql)
        ps.setString(1, data._1)
        ps.setInt(2, data._2)
        ps.executeUpdate()
      }
    } catch {
      case e: Exception => println("Mysql Exception")
    } finally {
      if (null != ps) {
        ps.close()
      }

      if (null != conn) {
        conn.close()
      }
    }

  }

  def readRecord(sQLContext: => SQLContext): Unit = {
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


  def readAndInsertExp(sQLContext: => SQLContext): Unit = {
    val df = sQLContext.read.format("jdbc").options(
      //      Map("url" -> "jdbc:mysql://xiaoqiang-zdm:3306/spark?useSSL=true",
      //      Map("url" -> "jdbc:mysql://localhost:3306/recommendDB",
      Map("url" -> "jdbc:mysql://xiaoqiang-zdm:3306/recommendDB",
        "dbtable" -> "recommend_feedback",
        "user" -> "root",
        "password" -> "xxx")
    ).load().select("device_id", "user_id", "like_label", "rating_level", "article_id", "article_channel_id",
      "article_channel_name", "content_url", "rs_id1", "rs_id2", "create_time").where("like_label='dislike'")

    val res = df.map{row =>
      val deviceId = row.getString(row.fieldIndex("device_id"))

      val newDeviceId = if  (deviceId == "8ef70faa2d65d378952aa45eb45c8b86") "8ef70faa2d65d378952aa45eb45c8b86_test" else deviceId

      Array(newDeviceId, row(1).toString,row(2).toString,row(3).toString,row(4).toString,row(5).toString,row(6).toString,row(7).toString,row(8).toString,row(9).toString,row(10).toString)
    }

    val rdd = res.filter(x=> x(0) == "8ef70faa2d65d378952aa45eb45c8b86_test")

//    val sqlStr: String = "device_id: string,user_id: string,like_label: string,rating_level: string," +
//      " article_id: string,article_channel_id: string,article_channel_name: string,content_url: string,rs_id1: string," +
//      " rs_id2: string,create_time: timestamp"
//    val schemaType = getSchemaStructType(sqlStr)

    val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val rowRdd = rdd.map{d =>
      val parsedDate = formatter.parse(d(10))
      Row(d(0).toString, d(1), d(2).toString, d(3).toString, d(4).toString, d(5).toString, d(6).toString, d(7).toString,
//        d(8).toString, d(9).toString, new java.sql.Timestamp(parsedDate.getTime()))
        d(8).toString, d(9).toString, d(10))
    }

    val schema = StructType(
      List(
        StructField("device_id", StringType, true),
        StructField("user_id", StringType, true),
        StructField("like_label", StringType, true),
        StructField("rating_level", StringType, true),
        StructField("article_id", StringType, true),
        StructField("article_channel_id", StringType, true),
        StructField("article_channel_name", StringType, true),
        StructField("content_url", StringType, true),
        StructField("rs_id1", StringType, true),
        StructField("rs_id2", StringType, true),
        StructField("create_time", StringType, true)
//        StructField("create_time", TimestampType, true)
      )
    )

    val resDataFrame = sQLContext.createDataFrame(rowRdd, schema)

    val prop = new Properties()
    prop.put("user", "root")
    prop.put("password", "xxx")

    // 追加
    resDataFrame.write.mode("append").jdbc("jdbc:mysql://xiaoqiang-zdm:3306/recommendDB", "recommend_feedback", prop)
    // 原来的记录全部覆盖了
//    resDataFrame.write.mode("overwrite").jdbc("jdbc:mysql://xiaoqiang-zdm:3306/recommendDB", "recommend_feedback", prop)

//    resDataFrame.write.format("jdbc").options(
//      Map("url" -> "jdbc:mysql://xiaoqiang-zdm:3306/recommendDB",
//        "dbtable" -> "recommend_feedback",
//        "user" -> "root",
//        "password" -> "xxx")
//    ).insertInto("recommend_feedback")
  }

  def constructRow(data: => Array[String], schema: => StructType): Row = {
    var list: collection.mutable.Seq[Any] = collection.mutable.Seq.empty[Any]
    var index = 0
    // 处理每一列
    data.foreach{value =>
      val valueType = schema.fields(index).dataType
      var tmp_value = value
      if (value == "" || value == "null" || value == "NULL" || value == null) {
        tmp_value = "-1"
      }
      val v = getValueByFieldType(tmp_value, valueType)
      list = list :+ v
      index += 1
    }
    val row = Row.fromSeq(list)
    row
  }

  /**
    * desc: 根据字段类型返回对应的值
    *
    * @param value  字段值
    * @param valueType  字段类型
    * @return 字段类型对应的值
    */
  def getValueByFieldType(value: => String, valueType: => DataType):Any = {
    var tmp_value = value
    if (value == null || value == "" || value == "null" || value == "NULL") {
      tmp_value = "-1"
    }
    valueType match {
      case IntegerType => tmp_value.trim.toInt
      case DoubleType => tmp_value.trim.toDouble
      case LongType => tmp_value.trim.toLong
      case FloatType => tmp_value.trim.toFloat
      case ByteType => tmp_value.trim.toByte
      case StringType => tmp_value.trim
      case TimestampType => tmp_value.trim
    }
  }

  def getSchemaStructType(schemaStr: => String): StructType = {
    val schema = StructType(schemaStr.split(",").map{ fieldName =>
      StructField(fieldName.trim.split(":")(0).trim, getFieldTypeInSchema(fieldName.trim.split(":")(1).trim), true)
    })
    schema
  }

  def getFieldTypeInSchema(ftype: => String): DataType = {
    ftype.trim.toLowerCase match {
      case "int" =>  IntegerType
      case "double" =>  DoubleType
      case "long" =>  LongType
      case "float" =>  FloatType
      case "byte" =>  ByteType
      case "string" =>  StringType
      case "date" =>  TimestampType
      case "timestamp" =>  StringType
      case "uuid" =>  StringType
      case "decimal" =>  DoubleType
      case "boolean" =>  BooleanType
      case "counter" =>  IntegerType
      case "bigint" =>  IntegerType
      case "text" =>  StringType
      case "ascii" =>  StringType
      case "varchar" =>  StringType
      case "varint" =>  IntegerType
      case "default" =>  StringType
    }
  }
}
