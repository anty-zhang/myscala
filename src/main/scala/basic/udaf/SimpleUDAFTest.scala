package basic.udaf

/**
  * Created by xiaoqiang on 16-6-10.
  * 自定义UDAF，需要extends  org.apache.spark.sql.expressions.UserDefinedAggregateFunction，并实现接口中的8个方法
  */

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.types.{DataType, DoubleType, StructField, StructType, LongType}
import org.apache.spark.sql.expressions.UserDefinedAggregateFunction
import org.apache.spark.sql.expressions.MutableAggregationBuffer

class NumsAvg extends UserDefinedAggregateFunction {
  override def bufferSchema: StructType = StructType(
      StructField("cnt", LongType) ::
        StructField("avg", DoubleType) :: Nil)

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0) = buffer.getAs[Long](0) + 1
    buffer(1) = buffer.getAs[Double](1) + input.getAs[Double](0)
  }

  override def inputSchema: StructType =
    StructType(StructField("nums", DoubleType):: Nil)

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getAs[Long](0) + buffer2.getAs[Long](0)
    buffer1(1) = buffer1.getAs[Double](1) + buffer2.getAs[Double](1)
  }

  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0l
    buffer(1) = 0.0
  }

  override def deterministic: Boolean = true

  override def evaluate(buffer: Row): Any = {
    val t = buffer.getDouble(1) / buffer.getLong(0)
    f"$t%1.5f".toDouble
  }

  override def dataType: DataType = DoubleType
}



object SimpleUDAFTest {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[4]").setAppName("SimpleUDAFTest")
    val sc = new SparkContext(conf)

    val sqlContext = new SQLContext(sc)
//    import org.apache.spark.sql.functions._

    val nums = List(4.5, 2.4, 1.8)
    val numsRDD = sc.parallelize(nums, 1)
    val numsRowRDD = numsRDD.map { x => Row(x) }
    val structType = StructType(Array(StructField("num", DoubleType, true)))

    val numsDF = sqlContext.createDataFrame(numsRowRDD, structType)
    numsDF.registerTempTable("numtest")
    sqlContext.sql("select avg(num) from numtest ").collect().foreach { x => println(x) }

    // 注册自定义函数
    sqlContext.udf.register("numsAvg", new NumsAvg)
    sqlContext.sql("select numsAvg(num) from numtest ").collect().foreach { x => println(x) }

  }
}
