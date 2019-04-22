import org.apache.spark.sql._

object Main extends App {

  val spark = SparkSession.builder.appName("Simple Application").getOrCreate()
  val logData = spark.read.csv("smallMuseum1.csv")
  val numAs = logData.groupBy("ObjectName").pivot("model")
  println(numAs)
  spark.stop()

}
