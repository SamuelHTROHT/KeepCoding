ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.15"

lazy val root = (project in file("."))
  .settings(
    name := "Examen_SamuelHorneroTeran"
  )
libraryDependencies ++= Seq(

  // Anadir libreria de testInit
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  // Anadir libreria de Spark
  "org.apache.spark" %% "spark-core" % "3.5.6",
  "org.apache.spark" %% "spark-sql" % "3.5.6" % "provided",
  "org.apache.spark" %% "spark-avro" % "2.4.7"
)
