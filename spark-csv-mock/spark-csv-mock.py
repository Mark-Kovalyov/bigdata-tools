from pyspark.sql import SparkSession
from pyspark.sql import Row
from pyspark.sql.types import StructType
from pyspark.sql import SparkSession, Row, DataFrame
from pyspark.sql.types import StructType, StringType

csv ="""
EMPNO  │ENAME    │JOB      │MGR   │HIREDATE   │SAL    │ COMM │ DEPTNO
───────┼─────────┼─────────┼──────┼───────────┼───────┼──────┼───────
7369   │SMITH    │CLERK    │7902  │17-DEC-1980│ 800   │      │20
7499   │ALLEN    │SALESMAN │7698  │20-FEB-1981│1600   │300   │30
7521   │WARD     │SALESMAN │7698  │22-FEB-1981│1250   │500   │30
7566   │JONES    │MANAGER  │7839  │02-APR-1981│2975   │      │20
7654   │MARTIN   │SALESMAN │7698  │28-SEP-1981│1250   │1400  │30
7698   │BLAKE    │MANAGER  │7839  │01-MAY-1981│2850   │      │30
"""



from pyspark.sql import SparkSession
from pyspark.sql import SparkSession, DataFrame
from pyspark.sql.types import StructType
import pyspark.sql.types as T

def from_csv(csv : str, delimiter : str, spark: SparkSession) -> DataFrame:
    csv_lines : list = csv.split("\n")
    row_count : int = 0
    data : list = []
    for line in csv_lines:
      if line == "":
        continue
      if row_count == 0:
        field_names : list = line.split(delimiter)
        schema_string = ",".join(list(map(lambda s: s.strip(" ") + " STRING", field_names)))
        schema = T._parse_datatype_string(schema_string)
      if row_count >= 2:
        fields = line.split(delimiter)
        cleaned_fields : list = list(map(lambda s: s.strip(" "), fields))
        data.append(tuple(cleaned_fields))
      row_count = row_count + 1
    return spark.createDataFrame(spark.sparkContext.parallelize(data), schema)


df = from_csv(csv, "│", spark) 

display(df)
