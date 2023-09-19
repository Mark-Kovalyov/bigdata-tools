package mayton

import java.io.{BufferedReader, StringReader}
import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.types._
import org.apache.spark.sql._

object SparkCsvMock {
  /**
   * How to use:
   * <pre>
   * val csv =
   * """|EMPNO  │ENAME    │JOB      │MGR   │HIREDATE   │SAL    │ COMM │ DEPTNO
   *    |───────┼─────────┼─────────┼──────┼───────────┼───────┼──────┼───────
   *    |7369   │SMITH    │CLERK    │7902  │17-DEC-1980│ 800   │      │20
   *    |7499   │ALLEN    │SALESMAN │7698  │20-FEB-1981│1600   │300   │30
   *    |7521   │WARD     │SALESMAN │7698  │22-FEB-1981│1250   │500   │30
   *    |7566   │JONES    │MANAGER  │7839  │02-APR-1981│2975   │      │20
   *    |7654   │MARTIN   │SALESMAN │7698  │28-SEP-1981│1250   │1400  │30
   *    |7698   │BLAKE    │MANAGER  │7839  │01-MAY-1981│2850   │      │30
   *    |""".stripMargin
   *
   * val df = SparkCsvMock.fromCSV(csv, '│', 2, SparkSession.builder.getOrCreate())
   * </pre>
   *
   * @param csv input CSV as plain text
   * @param delimiter for example : {',', ';', '|' ... } e.t.c.
   * @param startWith the first number of row, wich contains data
   * @param spark spark session
   * @return DataFrame
   */
  def fromCSV(csv: String, delimiter: Char, startWith : Int, spark: SparkSession): DataFrame = {
    val br: BufferedReader = new BufferedReader(new StringReader(csv))
    var res : String = null
    var row_count = 0
    var schema : StructType = null
    val data : ListBuffer[Row] = ListBuffer()
    while( { res = br.readLine() ; res != null } ) {
      if (row_count == 0) {
        val fieldNames = res.split(delimiter.toString)
        val schemaString = fieldNames.map(_ + " STRING").mkString(", ") // TODO: Add dynamic type inference
        schema = StructType.fromDDL(schemaString)
      } else if (row_count >= startWith) {
        val fields : Array[String] = res.split(delimiter.toString)
        data += Row.fromSeq(fields.toSeq)
      }
      row_count += 1
    }
    val rdd = spark.sparkContext.parallelize(data.toSeq)
    spark.createDataFrame(rdd, schema)
  }
}
