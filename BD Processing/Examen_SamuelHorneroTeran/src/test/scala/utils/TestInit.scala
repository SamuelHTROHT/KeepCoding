package utils

import org.apache.commons.io.FileUtils
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import java.io.File
import scala.reflect.io.Directory


case class TestInit() extends FlatSpec with Matchers with BeforeAndAfterAll with SparkSessionTestWrapper {

  lazy val testPath = "src/test/resources"

  override def beforeAll(): Unit = {
    super.beforeAll()
    //if (schemaSql.isSuccess) schemaSql.get.foreach(repairTableOrData(_, dropAndCreateTables))
  }

  override def afterAll(): Unit = {
    super.afterAll()
  }


  def newDf(datos:Seq[Row], schema: StructType): DataFrame = spark.createDataFrame(spark.sparkContext.parallelize(datos), schema)

  /**
   *
   * @param expected
   * @param actual
   */
  def checkDf(expected: DataFrame, actual: DataFrame): Unit = {
    expected.schema.toString() should be(actual.schema.toString())
    expected.collectAsList() should be(actual.collectAsList())
  }

  //No comprueba si una columna es nullable o no
  def checkDfIgnoreDefault(expected: DataFrame, actual: DataFrame): Unit = {
    setNullableStateForAllColumns(expected).schema.toString() should be(setNullableStateForAllColumns(actual).schema.toString())
    expected.collectAsList() should be(actual.collectAsList())
  }

  def setNullableStateForAllColumns(df: DataFrame, nullable: Boolean = true): DataFrame =
    df.sqlContext
      .createDataFrame(df.rdd, StructType(df.schema.map {
        case StructField(name, dataType, _, metadata) ⇒ StructField(name, dataType, nullable = nullable, metadata)
      }))

}

// Indica donde se van a crear los archivos temporales de spark
trait SparkSessionTestWrapper {
  FileUtils.deleteDirectory(new File("metastore_db"))
  new Directory(new File("src/test/resources/tmp")).deleteRecursively()


  /*
  Crear un a sesion de spark en local
   */
  implicit val spark: SparkSession = SparkSession.builder()
    .master("local[*]")
    .appName("spark-test")
    .config("spark.driver.bindAddress", "127.0.0.1")
    .getOrCreate()

  spark.sparkContext.setLogLevel("WARN")

}
