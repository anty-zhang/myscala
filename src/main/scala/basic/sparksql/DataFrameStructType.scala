package basic.sparksql

/**
  * Created by xiaoqiang on 16-8-8.
  * http://stackoverflow.com/questions/31397845/how-to-create-data-frame-from-csv-in-sparkusing-scala-when-the-first-line-is-t
  */

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types.{IntegerType, StringType, DoubleType, LongType,FloatType, ByteType,TimestampType,
StructField, BooleanType, StructType, DataType}
import org.apache.spark.sql.Row


object DataFrameStructType {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("DataFrameStructType").setMaster("local[5]")
    val sc = new SparkContext(conf)
    val sqlcon = new SQLContext(sc)

//    var schemaString = "Id:int,FirstName:text,LastName:text,Email:string,Country:text"
    val schemaString = "name:string,age:int"
    val schema = StructType(schemaString.split(",").map(fieldName => StructField(fieldName.split(":")(0), getFieldTypeInSchema(fieldName.split(":")(1)), true)))

    val rdd = sc.textFile("/home/xiaoqiang/Documents/spark-1.6.1-bin-hadoop2.6/examples/src/main/resources/people.txt")

//    val rowRdd = rdd.map(_.split(",")).map{attr =>
//      Row(attr(0).trim, attr(1).trim.toInt)
//    }

    val rowRdd = rdd.map{x =>
      var list: collection.mutable.Seq[Any] = collection.mutable.Seq.empty[Any]
      var index = 0
      // 处理每一列
      x.split(",").foreach{value =>
        val valType = schema.fields(index).dataType
        var returnVal: Any = null
        valType match {
          case IntegerType => returnVal = value.trim.toInt
          case DoubleType => returnVal = value.trim.toDouble
          case LongType => returnVal = value.trim.toLong
          case FloatType => returnVal = value.trim.toFloat
          case ByteType => returnVal = value.trim.toByte
          case StringType => returnVal = value.trim
          case TimestampType => returnVal = value.trim
        }
        list = list :+ returnVal
        index += 1
      }
      val res = Row.fromSeq(list)
      res
    }

    val peopleDF = sqlcon.createDataFrame(rowRdd, schema)
//    val peopleDF = sqlcon.applySchema(rowRdd, schema)      // deprecated
    peopleDF.registerTempTable("people")
//    peopleDF.createOrReplaceTempView("people")

    val res = sqlcon.sql("select name, age from people")
    res.map(attributes => ("Name: " + attributes(0), "age: " + attributes(1).toString)).collect().map(println)
//    val result = res.map[(String, java.lang.Integer)](x => (x.getAs[String](0), x.getAs[java.lang.Integer](1)))
//    result.take(1).map(println)
  }

//  def getFieldTypeInSchema(ftype: String): DataType = {
//    ftype match {
//      case "int" => return IntegerType
//      case "double" => return DoubleType
//      case "long" => return LongType
//      case "float" => return FloatType
//      case "byte" => return ByteType
//      case "string" => return StringType
//      case "date" => return TimestampType
//      case "timestamp" => return StringType
//      case "uuid" => return StringType
//      case "decimal" => return DoubleType
//      case "boolean" => return BooleanType
//      case "counter" => return IntegerType
//      case "bigint" => return IntegerType
//      case "text" => return StringType
//      case "ascii" => return StringType
//      case "varchar" => return StringType
//      case "varint" => return IntegerType
//      case "default" => return StringType
//    }
//  }

  def getFieldTypeInSchema(ftype: => String): DataType = {
    ftype match {
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
