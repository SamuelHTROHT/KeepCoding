package sparkutils

import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkUtils {
  def runSparkSession(appName: String): SparkSession =
    SparkSession.builder().appName(appName).master("local[*]")
      .config("spark.driver.bindAddress", "127.0.0.1").getOrCreate()

  def lecturaCSVDF(path:String,delimiter:String = ",")(implicit ss:SparkSession): DataFrame = {
    ss.read.option("header", true).option("delimiter",delimiter).csv(path)
  }
}
