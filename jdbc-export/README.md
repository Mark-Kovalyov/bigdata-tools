# JDBC-export

JDBC-export is tiny and flexible CLI to to export tables from JDBC compatible databases into external files (csv, json, etc)

Fieatures:
- supports databases:
  - Oracle
  - DB2 
  - PostgreSQL
  - MS-SQL Server (not tested yet!)
  - mysql / maria (the same driver)
  - SQLite
  - H2
- supports output type files
  - CSV (via FastCSV)
  - JSONLines (JSONL), text files with independent JSON in each Line
  - XML (via Woodstox XML)
  - AVRO
- not supported yet, but planned:
  - Google Protobuf Binary file
  - Apache Parquet and Delta Table

# Exapmple

## 0) Help:
```
$ java -jar jdbc-export.jar
    _  ____  ____  ____        ________  _ ____  ____  ____ _____
   / |/  _ \/  __\/   _\      /  __/\  \///  __\/  _ \/  __Y__ __\
   | || | \|| | //|  /  _____ |  \   \  / |  \/|| / \||  \/| / \
/\_| || |_/|| |_\\|  \__\____\|  /_  /  \ |  __/| \_/||    / | |
\____/\____/\____/\____/      \____\/__/\\\_/   \____/\_/\_\ \_/

 -c,--compression <arg>   Optional parameter for Apache AVRO compression
                          ex: snappy|deflate|bzip2
 -f,--format <arg>        Export format: {csv|jsonl|xml|avro|protobuf}
 -o,--outputfile <arg>    Output file name (ex: emp.csv)
 -q,--where <arg>         WHERE expression (ex: id > 1000)
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

## 4) Export with binary AVRO output format, and set 'snappy' compression
```bash
java -jar jdbc-export.jar ..... --format avro --compression snappy
```

## 5) Export from MariaDb table with filtering 
```sh
java -jar jdbc-export.jar jdbc:mariadb://localhost:3306/testdb --query "select name, id from clients where category = '141'" .....
```

# Specifications and examples:

## JDBC driver urls for network connection for different types of DBMS:
| DBMS          | Driver                          | Connection string examples                                              | Desc                    |
|---------------|---------------------------------|-------------------------------------------------------------------------|-------------------------|
| Oracle        | oracle.jdbc.driver.OracleDriver | jdbc:oracle:thin:scott/tiger@localhost:1521/ORCL                        | ORCL is a test database 
| MySQL/MariaDb |                                 | dbc:mariadb://localhost:3306/testdb                                     |
| IBM/Db2       |                                 | jdbc:db2:STLEC1:user=dbadm;password=dbadm                               |
| PostgreSQL    |                                 | jdbc:postgresql://localhost:5455/your_database_name                     |     
| PostgreSQL    |                                 | jdbc:postgresql://buh.account.org:5455/sklad??user=elena&password=***** | Credentials             |  

## JDBC driver url for file connection
| DBMS   | Driver | Connection string examples           | Desc                    |
|--------|--------|--------------------------------------|-------------------------|
| SQLite |        | jdbc:sqlite:/folder1/database_folder |
| H2     |        | jdbc:h2:~/test                       |

## Compression types for Apache AVRO:
| Compression |
|-------------|
| snappy      |
| bzip2       |
| deflate     |

See the different sizes for compression codecs to compare. We would recommend to use snappy because of 
the good balance between size and time overhead.

```
08/23/2025  09:20 PM       193,132,303 books.avro
08/23/2025  09:19 PM       142,405,348 books.snappy.avro
08/23/2025  09:18 PM        97,498,771 books.deflate.avro
08/23/2025  09:20 PM        83,081,037 books.bzip2.avro
```

# Pitfalls

## Naive JSON library

We use Jsonitier library to write JSON files. We are not sure is it fully covers ECMA-404 specification. 
The main goal - to speed up the writing stream of chars much more quickly, so some Unicode characters can be incorrectly wrapped.

## Custom JDBC drivers

You can use any JDBC-compatible driver, but carefully with complex data types, like Date/Time, Objects, Geometry, Structures e.t.c.
We do not test all of them in a combination of different databases and drivers.

You can report issues here https://github.com/Mark-Kovalyov/bigdata-tools/issues

## Security statement:

The technical ability to unload business-information from the database in huge volumes 
does not allow you the privileges to do this. Consult with your project manager
or security officer about!

Many organizations follow the principle of explicit permission to act. Remember it!

## Typical file extensions for different output formats:

You're able to use any file extension, but typically used are:

| Format   | Typical file extension |
|----------|-----------------------|
| CSV      | .csv                  |
| JSONL    | .jsonl                |
| XML      | .xml                  |
| AVRO     | .avro                 |        
| Protobuf | .pb                   |


# Related links:

- Woodstox https://github.com/FasterXML/woodstox
- AVRO https://github.com/apache/avro
- FastCSV https://fastcsv.org/ (Fastest library to write CSV)
- Jsonitier https://jsoniter.com/api.htmlusage:


