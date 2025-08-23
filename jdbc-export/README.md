# JDBC-export

- tiny and flexible CLI to to export tables from JDBC compatible databases into external files
- supports internally
  - Oracle
  - DB2 
  - mysql / maria
  - SQLite
  - H2
- support output type files
  - CSV
  - BigData JSONL (text files with independent JSON in each Line)
  - XML
  - AVRO
- not supported yet, but planned:
  - Google Protobuf Binary file


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
 -c,--compression <arg>   Optional parameter for Apache AVRO compression
                          ex: snappy|deflate|bzip2
 -f,--format <arg>        Export format: csv|jsonl|xml|avro
 -h,--help                Print this help
 -o,--outputfile <arg>    Output file name (ex: emp.csv)
 -q,--query <arg>         SELECT-expression (ex: SELECT * FROM EMP)
 -s,--schema <arg>        Schema name
 -t,--table <arg>         Table or View name
 -u,--url <arg>           JDBC url. (ex:jdbc:oracle:thin@localhost:1521/XE
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

## 3) Export with binary AVRO output format, and set 'snappy' compression
```bash
java -jar jdbc-export.jar ..... --format avro --compression snappy
```

# Bugs and pitfalls:

## Compression types for Apache AVRO:
| Compression |
|-------------|
| snappy      |
| bzip2 |
| deflate |

See the different size for compression codecs to compare. We would recoomend to use snappy
because of good balance between size and time overhead.

```
08/23/2025  09:20 PM       193,132,303 books.avro
08/23/2025  09:19 PM       142,405,348 books.snappy.avro
08/23/2025  09:18 PM        97,498,771 books.deflate.avro
08/23/2025  09:20 PM        83,081,037 books.bzip2.avro
```

## Naive JSON library

We use Jsonitier library to write JSON files. We are not sure is it fully covers ECMA-404 specification.
The main goal - to speedup writing stream of chars as much more quicly, so, some Unicode charaters can
be incorrectly wrapped.

## Custom JDBC drivers

You can use any JDBC compatible driver, but carefully with complex data types, 
like Date/Time, Objects, Geometry, Structures e.t.c. We not tested yet all off them
in combination of different Databases and drivers.

You can report issues here https://github.com/Mark-Kovalyov/bigdata-tools/issues