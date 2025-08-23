# JDBC-export

- tiny and flexible to to export tables from JDBC compatible databases into external files
- support internally
  - Oracle
  - DB2
  - mysql / maria
  - SQLite
- support output type files
  - CSV
  - BigData JSONL (text files with independent JSON in each Line)
  - XML
- not supported yet, but planned:
  - Apache Avro
  - Apache ORC
  - Apache Parquet
  - Delta table


# Exapmple

## 0) Help:
```
$ java -jar jdbc-export.jar
usage:
   ____                  __                                      __
  / __ \_________ ______/ /__        ___  _  ______  ____  _____/ /_
 / / / / ___/ __ `/ ___/ / _ \______/ _ \| |/_/ __ \/ __ \/ ___/ __/
/ /_/ / /  / /_/ / /__/ /  __/_____/  __/>  </ /_/ / /_/ / /  / /_
\____/_/   \__,_/\___/_/\___/      \___/_/|_/ .___/\____/_/   \__/
                                           /_/
 -f,--format <arg>       Export format: csv|jsonl|xml
 -h,--help               Print this help
 -o,--outputfile <arg>   Output file name (ex: emp.csv)
 -q,--query <arg>        SELECT-expression (ex: SELECT * FROM EMP)
 -s,--schema <arg>       Schema name
 -t,--table <arg>        Table or View name
 -u,--url <arg>          JDBC url. (ex:jdbc:oracle:thin@localhost:1521/XE
```

## 1) Export Oracle table scott.emp into CSV file:

```bash
java -jar jdbc-export.jar -u "jdbc:oracle:thin:scott/tiger@localhost:1521/ORCL" --schema scott --table emp --outputfile emp.csv --format csv
```

## 2) Export SQLite table using custom query and JSON as output format

```bash
java -jar jdbc-export.jar -u "jdbc:sqlite:books" --query "select name, sha1, size from books" --outputfile books.jsonl --format jsonl
```

## 3) Export with XML output format
```bash
java -jar jdbc-export.jar ..... --format xml
```

# Bugs and pitfalls:

## Naive JSON library

We use Jsonitier library to write JSON files. We are not sure is it fully covers ECMA-404 specification.
The main goal - to speedup writing stream of chars as much more quicly, so, some Unicode charaters can
be incorrectly wrapped.

## Custom JDBC drivers

You can use any JDBC compatible driver, but carefully with complex data types, 
like Date/Time, Objects, Geometry, Structures e.t.c. We not tested yet all off them
in combination of different Databases and drivers.

You can report issues here https://github.com/Mark-Kovalyov/bigdata-tools/issues