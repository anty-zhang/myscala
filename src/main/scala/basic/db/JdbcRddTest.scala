package basic.db

/**
  * Created by xiaoqiang on 16-6-3.
  * rdd write to mysql
  */
import java.util.Properties
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Row, SQLContext, types}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

object JdbcRddTest {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("JdbcRddTest").setMaster("local[5]")
    val sc = new SparkContext(conf)
    val sQLContext = new SQLContext(sc)

    // 通过并行化创建RDD
    val personRDD = sc.parallelize(Array("1 tom 5", "2 jerry 3", "3 kitty 6")).map(_.split(" "))
    // 1. 通过StructType直接指定每个字段的schema
    val schema = StructType(
      List(
        StructField("id", IntegerType, true),
        StructField("name", StringType, true),
        StructField("age", IntegerType, true)
      )
    )

    // 2. 将RDD映射为rowRdd
    val rowRDD = personRDD.map(p=> Row(p(0).toInt, p(1).trim, p(2).toInt))

    // 3. 将schema信息映射到rowRDD上
    val personDataFrame = sQLContext.createDataFrame(rowRDD, schema)

    // 4. 创建Properties存储数据库相关属性
    val prop = new Properties()
    prop.put("user", "root")
    prop.put("password", "xxx")

    // 5. 将数据追加到数据库
    val jdbcUrl = "jdbc:mysql://xiaoqiang-zdm:3306/spark?useSSL=true"
    val table = "person"
    personDataFrame.write.mode("append").jdbc(jdbcUrl, table, prop)

    // stop SparkContext
    sc.stop()
  }
}
