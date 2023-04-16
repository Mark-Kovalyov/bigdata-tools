# Json lines selector

* Transforms Complex JSON file into flat structure (JSON-lines) 
  to be able to load into Spark/Databricks. Like a flatMap function.
* Supports archives
  * GZip
  * BZip2
* Non-invasive by memory. Able to transform 2 Gb json file with 128Mb of mx memory.

## Usage:
```
java -jar json-lines-selector.jar [inputfile.json] [json-path-query] [outputfile.jsonl]
```
## Advanced usage with STDIN/STDOUT

Write to STDOUT
```
java -jar json-lines-selector.jar inputfile.json json-path-query -
```

Read from STDIN

## Advanced usage with gzip/bzip2 input format
```
java -jar json-lines-selector.jar - json-path-query out.jsonl
```

## Example:

### Assumed input file:
```json

```

### Output file will be:
```

```

How to do:
```
java -jar json-lines-selector.jar scott.json $.emp.* emp.jsonl
java -jar json-lines-selector.jar scott.json $.debt.* jsonl.jsonl
```

## Bencmarks, proof of non-invasive:

* Input file
  * Wireshark's pcap files tranformed into JSON.
* Size 
  * in bzip = 272 345 065 bytes (Raw size 1826M)

```bash
$ ls -lF

-rw-rw-r-- 1 mayton  mayton   272345065 Apr 15 21:46 dump-002.json.bz2
```

Sample:
```json
[
  {
    "_index": "packets-2023-04-15",
    "_type": "doc",
    "_score": null,
    "_source": {
      "layers": {
        "frame": {
          "frame.interface_id": "0",
          "frame.interface_id_tree": {
.....
]
```

| File              | Bzip2 / Raw Size | Json Objects | Processing time(s) | AVG Speed (rec/s) | Xmx  |
|-------------------|------------------|--------------|--------------------|-------------------|------|
| dump-002.json.bz2 | 259M / 1826M     | 322869       | 219                | 1474              | 128M |
| dump-002.json.bz2 | 259M / 1826M     | 322869       | 221                | 1460              | 256M |
| dump-002.json.bz2 | 259M / 1826M     | 322869       | 203                | 1590              | 512M |
| dump-002.json     | -  / 1826M       | 322869       | 27                 | 11958             | 128M |
| dump-002.json     | -  / 1826M       | 322869       | 28                 | 11531             | 512M |

```
$ java -Xmx128m -jar json-lines-selector.jar /bigdata/db/tcpdump/dump-002.json.bz2 '$.*' /bigdata/db/tcpdump/dump-exported.jsonl
```

### Output (cropped because of too large):
```
$ cut -c 1-280 dump-exported.jsonl | head
{"_index":"packets-2023-04-15","_type":"doc","_score":null,"_source":{"layers":{"frame":{"frame.interface_id":"0","frame.interface_id_tree":{"frame.interface_name":"unknown"},"frame.encap_type":"1","frame.time":"Apr 15, 2023 21:19:59.419299000 EEST","frame.offset_shift":"0.000000
{"_index":"packets-2023-04-15","_type":"doc","_score":null,"_source":{"layers":{"frame":{"frame.interface_id":"0","frame.interface_id_tree":{"frame.interface_name":"unknown"},"frame.encap_type":"1","frame.time":"Apr 15, 2023 21:19:59.419366000 EEST","frame.offset_shift":"0.000000
...
```
line count check
```
$ wc -l dump-exported.jsonl 
322869 dump-exported.jsonl
```


## Checking with spark-shell console:

```
$ spark-shell
Setting default log level to "WARN".
To adjust logging level use sc.setLogLevel(newLevel). For SparkR, use setLogLevel(newLevel).
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 3.3.2
      /_/
         
Using Scala version 2.13.8 (OpenJDK 64-Bit Server VM, Java 17)
Type in expressions to have them evaluated.
Type :help for more information.
Spark context Web UI available at http://localhost:4040
Spark context available as 'sc' (master = local[*], app id = local-1681642344682).
Spark session available as 'spark'.

scala> val df = spark.read.json("/bigdata/db/tcpdump/dump-exported.jsonl")
val df: org.apache.spark.sql.DataFrame = [_index: string, _score: string ... 2 more fields]

scala> df.count()
val res1: Long = 322869

scala> df.printSchema
warning: 1 deprecation (since 2.13.3); for details, enable `:setting -deprecation` or `:replay -deprecation`
root
 |-- _index: string (nullable = true)
 |-- _score: string (nullable = true)
 |-- _source: struct (nullable = true)
 |    |-- layers: struct (nullable = true)
 |    |    |-- _ws.malformed: struct (nullable = true)
 |    |    |    |-- _ws.expert: struct (nullable = true)
 |    |    |    |    |-- _ws.expert.group: string (nullable = true)
 |    |    |    |    |-- _ws.expert.message: string (nullable = true)
 |    |    |    |    |-- _ws.expert.severity: string (nullable = true)
 |    |    |    |    |-- _ws.malformed.expert: string (nullable = true)
 |    |    |    |-- _ws.malformed: string (nullable = true)
 |    |    |-- data: struct (nullable = true)
 |    |    |    |-- data.data: string (nullable = true)
 |    |    |    |-- data.len: string (nullable = true)
 |    |    |-- ecatf: struct (nullable = true)
 |    |    |    |-- ecatf.length: string (nullable = true)
 |    |    |    |-- ecatf.reserved: string (nullable = true)
 |    |    |    |-- ecatf.type: string (nullable = true)
 |    |    |-- eth: struct (nullable = true)
 |    |    |    |-- eth.dst: string (nullable = true)
 |    |    |    |-- eth.dst_tree: struct (nullable = true)
 |    |    |    |    |-- eth.addr: string (nullable = true)
 |    |    |    |    |-- eth.addr.oui: string (nullable = true)
 |    |    |    |    |-- eth.addr.oui_resolved: string (nullable = true)
 |    |    |    |    |-- eth.addr_resolved: string (nullable = true)
 |    |    |    |    |-- eth.dst.ig: string (nullable = true)
 |    |    |    |    |-- eth.dst.lg: string (nullable = true)
 |    |    |    |    |-- eth.dst.oui: string (nullable = true)
 |    |    |    |    |-- eth.dst.oui_resolved: string (nullable = true)
 |    |    |    |    |-- eth.dst_resolved: string (nullable = true)
 |    |    |    |    |-- eth.ig: string (nullable = true)
 |    |    |    |    |-- eth.lg: string (nullable = true)
 |    |    |    |-- eth.src: string (nullable = true)
 |    |    |    |-- eth.src_tree: struct (nullable = true)
 |    |    |    |    |-- eth.addr: string (nullable = true)
 |    |    |    |    |-- eth.addr.oui: string (nullable = true)
 |    |    |    |    |-- eth.addr.oui_resolved: string (nullable = true)
 |    |    |    |    |-- eth.addr_resolved: string (nullable = true)
 |    |    |    |    |-- eth.ig: string (nullable = true)
 |    |    |    |    |-- eth.lg: string (nullable = true)
 |    |    |    |    |-- eth.src.ig: string (nullable = true)
 |    |    |    |    |-- eth.src.lg: string (nullable = true)
 |    |    |    |    |-- eth.src.oui: string (nullable = true)
 |    |    |    |    |-- eth.src.oui_resolved: string (nullable = true)
 |    |    |    |    |-- eth.src_resolved: string (nullable = true)
 |    |    |    |-- eth.type: string (nullable = true)
 |    |    |-- frame: struct (nullable = true)
 |    |    |    |-- frame.cap_len: string (nullable = true)
 |    |    |    |-- frame.coloring_rule.name: string (nullable = true)
 |    |    |    |-- frame.coloring_rule.string: string (nullable = true)
 |    |    |    |-- frame.encap_type: string (nullable = true)
 |    |    |    |-- frame.ignored: string (nullable = true)
 |    |    |    |-- frame.interface_id: string (nullable = true)
 |    |    |    |-- frame.interface_id_tree: struct (nullable = true)
 |    |    |    |    |-- frame.interface_name: string (nullable = true)
 |    |    |    |-- frame.len: string (nullable = true)
 |    |    |    |-- frame.marked: string (nullable = true)
 |    |    |    |-- frame.number: string (nullable = true)
 |    |    |    |-- frame.offset_shift: string (nullable = true)
 |    |    |    |-- frame.protocols: string (nullable = true)
 |    |    |    |-- frame.time: string (nullable = true)
 |    |    |    |-- frame.time_delta: string (nullable = true)
 |    |    |    |-- frame.time_delta_displayed: string (nullable = true)
 |    |    |    |-- frame.time_epoch: string (nullable = true)
 |    |    |    |-- frame.time_relative: string (nullable = true)
 |    |    |-- ioraw: struct (nullable = true)
 |    |    |    |-- ioraw.data: string (nullable = true)
 |    |    |    |-- ioraw.header: string (nullable = true)
 |    |    |-- ip: struct (nullable = true)
 |    |    |    |-- ip.addr: string (nullable = true)
 |    |    |    |-- ip.checksum: string (nullable = true)
 |    |    |    |-- ip.checksum.status: string (nullable = true)
 |    |    |    |-- ip.dsfield: string (nullable = true)
 |    |    |    |-- ip.dsfield_tree: struct (nullable = true)
 |    |    |    |    |-- ip.dsfield.dscp: string (nullable = true)
 |    |    |    |    |-- ip.dsfield.ecn: string (nullable = true)
 |    |    |    |-- ip.dst: string (nullable = true)
 |    |    |    |-- ip.dst_host: string (nullable = true)
 |    |    |    |-- ip.flags: string (nullable = true)
 |    |    |    |-- ip.flags_tree: struct (nullable = true)
 |    |    |    |    |-- ip.flags.df: string (nullable = true)
 |    |    |    |    |-- ip.flags.mf: string (nullable = true)
 |    |    |    |    |-- ip.flags.rb: string (nullable = true)
 |    |    |    |-- ip.frag_offset: string (nullable = true)
 |    |    |    |-- ip.hdr_len: string (nullable = true)
 |    |    |    |-- ip.host: string (nullable = true)
 |    |    |    |-- ip.id: string (nullable = true)
 |    |    |    |-- ip.len: string (nullable = true)
 |    |    |    |-- ip.proto: string (nullable = true)
 |    |    |    |-- ip.src: string (nullable = true)
 |    |    |    |-- ip.src_host: string (nullable = true)
 |    |    |    |-- ip.ttl: string (nullable = true)
 |    |    |    |-- ip.version: string (nullable = true)
 |    |    |-- nxp_802154_sniffer: struct (nullable = true)
 |    |    |    |-- nxp_802154_sniffer.channel: string (nullable = true)
 |    |    |    |-- nxp_802154_sniffer.id: string (nullable = true)
 |    |    |    |-- nxp_802154_sniffer.length: string (nullable = true)
 |    |    |    |-- nxp_802154_sniffer.lqi: string (nullable = true)
 |    |    |    |-- nxp_802154_sniffer.timestamp: string (nullable = true)
 |    |    |-- uaudp: struct (nullable = true)
 |    |    |    |-- uaudp.opcode: string (nullable = true)
 |    |    |-- udp: struct (nullable = true)
 |    |    |    |-- Timestamps: struct (nullable = true)
 |    |    |    |    |-- udp.time_delta: string (nullable = true)
 |    |    |    |    |-- udp.time_relative: string (nullable = true)
 |    |    |    |-- udp.checksum: string (nullable = true)
 |    |    |    |-- udp.checksum.status: string (nullable = true)
 |    |    |    |-- udp.dstport: string (nullable = true)
 |    |    |    |-- udp.length: string (nullable = true)
 |    |    |    |-- udp.port: string (nullable = true)
 |    |    |    |-- udp.srcport: string (nullable = true)
 |    |    |    |-- udp.stream: string (nullable = true)
 |    |    |-- wg: struct (nullable = true)
 |    |    |    |-- wg.encrypted_static: string (nullable = true)
 |    |    |    |-- wg.encrypted_timestamp: string (nullable = true)
 |    |    |    |-- wg.ephemeral: string (nullable = true)
 |    |    |    |-- wg.ephemeral_tree: struct (nullable = true)
 |    |    |    |    |-- wg.ephemeral.known_privkey: string (nullable = true)
 |    |    |    |-- wg.mac1: string (nullable = true)
 |    |    |    |-- wg.mac2: string (nullable = true)
 |    |    |    |-- wg.reserved: string (nullable = true)
 |    |    |    |-- wg.sender: string (nullable = true)
 |    |    |    |-- wg.stream: string (nullable = true)
 |    |    |    |-- wg.type: string (nullable = true)
 |    |    |-- wpan: struct (nullable = true)
 |    |    |    |-- _ws.expert: struct (nullable = true)
 |    |    |    |    |-- _ws.expert.group: string (nullable = true)
 |    |    |    |    |-- _ws.expert.message: string (nullable = true)
 |    |    |    |    |-- _ws.expert.severity: string (nullable = true)
 |    |    |    |    |-- wpan.dst_invalid: string (nullable = true)
 |    |    |    |-- _ws.malformed: string (nullable = true)
 |    |    |    |-- wpan.fcf: string (nullable = true)
 |    |    |    |-- wpan.fcf_tree: struct (nullable = true)
 |    |    |    |    |-- wpan.ack_request: string (nullable = true)
 |    |    |    |    |-- wpan.dst_addr_mode: string (nullable = true)
 |    |    |    |    |-- wpan.fcf.reserved: string (nullable = true)
 |    |    |    |    |-- wpan.frame_type: string (nullable = true)
 |    |    |    |    |-- wpan.ie_present: string (nullable = true)
 |    |    |    |    |-- wpan.pan_id_compression: string (nullable = true)
 |    |    |    |    |-- wpan.pending: string (nullable = true)
 |    |    |    |    |-- wpan.security: string (nullable = true)
 |    |    |    |    |-- wpan.seqno_suppression: string (nullable = true)
 |    |    |    |    |-- wpan.src_addr_mode: string (nullable = true)
 |    |    |    |    |-- wpan.version: string (nullable = true)
 |    |    |    |-- wpan.frame_length: string (nullable = true)
 |    |    |    |-- wpan.seq_no: string (nullable = true)
 |-- _type: string (nullable = true)

```

## See also

* JSonPath : https://github.com/json-path/JsonPath
