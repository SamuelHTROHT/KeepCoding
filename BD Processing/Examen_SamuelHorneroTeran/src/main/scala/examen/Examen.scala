package examen


  import org.apache.spark.rdd.RDD
  import org.apache.spark.sql.{DataFrame, SparkSession, functions}
  import org.apache.spark.sql.functions._

object Examen {


    /** Ejercicio 1: Crear un DataFrame y realizar operaciones básicas
     * Pregunta: Crea un DataFrame a partir de una secuencia de tuplas que contenga información sobre
     * estudiantes (nombre, edad, calificación).
     * Realiza las siguientes operaciones:
     *
     * Muestra el esquema del DataFrame.
     * Filtra los estudiantes con una calificación mayor a 8.
     * Selecciona los nombres de los estudiantes y ordénalos por calificación de forma descendente.
     */
    def ejercicio1(estudiantes: DataFrame)(implicit spark: SparkSession): DataFrame = {
      /**imprimimos el esquema */
      println(estudiantes.schema)

      /** seleccionamos el nombre ordenado de forma descendente
       * de los alumnos con calificacion mayor que 8 */
      estudiantes
        .filter("calificacion > 8.0")
        .sort(desc("calificacion"))
        .select("nombre")
    }

    /** Ejercicio 2: UDF (User Defined Function)
     * Pregunta: Define una función que determine si un número es par o impar.
     * Aplica esta función a una columna de un DataFrame que contenga una lista de números.
     */

    /* Si lo quisieses como lista
    def parImpar(ls: List[Int]): List[String] = ls match{
      case Nil => Nil
      case head :: tail =>
        val tipo = if (head % 2 == 0) "Par" else "Impar"
        tipo :: parImpar(tail)
    }*/

    // creamos la funcion UDF para poder usarla mas tarde indicando que es una UDF
    val parImparUDF = udf((n: Int) =>
      if (n % 2 == 0) "Par" else "Impar"
    )
    def ejercicio2(numeros: DataFrame)(implicit spark: SparkSession): DataFrame = {
      /*
      Si lo quisieses como lista
      import spark.implicits._
      val lista = numeros.as[Int].collect().toList
      parImpar(lista)*/

      //llamamos a la funcion para el DF y seleccionamos la columna creada
      numeros.withColumn("tipo", parImparUDF(col("numero")))
        .select("tipo")
    }

    /** Ejercicio 3: Joins y agregaciones
     * Pregunta: Dado dos DataFrames,
     * uno con información de estudiantes (id, nombre)
     * y otro con calificaciones (id_estudiante, asignatura, calificacion),
     * realiza un join entre ellos y calcula el promedio de calificaciones por estudiante.
     */
    def ejercicio3(estudiantes: DataFrame, calificaciones: DataFrame): DataFrame = {

      /** Ambas soluciones son equivalentes.
       * Aunque podría parecer que hacer un select previo mejora el rendimiento,
       * Spark (Catalyst optimizer) elimina automáticamente las columnas no utilizadas,
       * por lo que en este caso no hay diferencia significativa.*/

      /*
      estudiantes.alias("est")
        .join(calificaciones.alias("cal"),
      col("est.id") === col("cal.id_estudiante"))
        .select(col("est.id"),
          col("est.nombre"),
          col("cal.calificacion"))
        .groupBy(col("est.id"), col("est.nombre"))
        .agg(avg(col("cal.calificacion")))*/

      /** forma mas limpia en código, en la que hacemos el join
       * agrupamos  por id y nombre y calculamos la media de las notas:
       */

      estudiantes.alias("est")
        .join(calificaciones.alias("cal"),
          col("est.id") === col("cal.id_estudiante"))
        .groupBy(col("est.id"), col("est.nombre"))
        .agg(avg(col("cal.calificacion")).alias("media"))
    }


    /** Ejercicio 4: Uso de RDDs
     * Pregunta: Crea un RDD a partir de una lista de palabras y cuenta la cantidad de ocurrencias de cada palabra.
     */

    def ejercicio4(palabras: List[String])(implicit spark: SparkSession): RDD[(String, Int)] = {
      /** hacemos un map con x,1 para que cuente el numero de ocurrencias
       * mediante reduceByKey agrupamos por la palabra y lo ordenamos por palabra */

      val rdd = spark.sparkContext.parallelize(palabras)
      rdd.map(x=>(x,1))
        .reduceByKey(_ + _)
        .sortByKey()
    }

    /**
     * Ejercicio 5: Procesamiento de archivos
     * Pregunta: Carga un archivo CSV que contenga información sobre
     * ventas (id_venta, id_producto, cantidad, precio_unitario)
     * y calcula el ingreso total (cantidad * precio_unitario) por producto,
     * ordenados por id_producto
     */
    def ejercicio5(ventas: DataFrame)(implicit spark: SparkSession): RDD[(Int,Double)] = {
      /** casteamos el id a int para que coincida con lo que tenemos que devolver y
       * creamos una nueva columna de precio total y agrupamos por producto, lo ordenamos
       * y sumamos para que nos de el total por producto */

      val df_final = ventas
        .withColumn("id_producto", col("id_producto").cast("int"))
        .withColumn("precio_total", col("cantidad") * col("precio_unitario"))
        .groupBy(col("id_producto"))
        .sum("precio_total")
        .orderBy(asc("id_producto"))

      /** convertirmos el DataFrame a RDD con los tipados correctos para que coincidan
       * con el assert que nos manda el profesor */

      df_final.rdd.map(row => (
        row.getInt(0),
        row.getDouble(1)
      ))
    }
}


