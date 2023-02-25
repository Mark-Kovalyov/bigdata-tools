package mayton.bigdata

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.sftp.*
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import net.schmizz.sshj.xfer.*
import org.apache.commons.io.IOUtils
import org.slf4j.profiler.{Profiler, TimeInstrument}
import org.slf4j.{Logger, LoggerFactory}
import org.yaml.snakeyaml.Yaml

import java.io.*
import java.nio.ByteBuffer
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId, ZoneOffset}
import java.util
import java.util.regex.Pattern
import java.util.zip.ZipInputStream
import java.util.{Properties, UUID}
import scala.collection.mutable
import scala.jdk.CollectionConverters.*


object FtpSync {

  val BLOCK_SIZE : Int = 128 * 1024 * 1024

  val logger : Logger = LoggerFactory.getLogger("ftp-sync")
  val reportLogger: Logger = LoggerFactory.getLogger("report")

  def createSftpClient(host:String, port:Int, user:String, pwd:String) : SFTPClient = {
    logger.info(s"connecting to ${host}")
    val sshClient = new SSHClient()
    sshClient.addHostKeyVerifier(new PromiscuousVerifier)
    sshClient.connect(host, port)
    sshClient.authPassword(user, pwd)
    val client = sshClient.newSFTPClient()
    logger.info("connected")
    client
  }

  def writeBlob(dest : SFTPClient, path : String, blob : Array[Byte]) : Long = {
    logger.info("writting blob to {}", path)
    val inMemorySourceFileImpl = new InMemorySourceFile {
      override def getName: String = path
      override def getLength: Long = blob.length
      override def getInputStream: InputStream = new ByteArrayInputStream(blob)
    }
    dest.put(inMemorySourceFileImpl, path)
    blob.length
  }

  def getBlob(source : SFTPClient, path : String) : Array[Byte] = {
    logger.info("getting blob from {}", path)
    val remoteFile: RemoteFile = source.open(path)
    val len = remoteFile.length
    assert(len < Integer.MAX_VALUE)
    val resultBuf = ByteBuffer.allocate(len.toInt)
    val buf : Array[Byte] = Array.fill[Byte](BLOCK_SIZE)(0)
    var offset = 0
    var read: Int = 0
    var iteration: Int = 0
    while ( { read = remoteFile.read(offset, buf, 0, BLOCK_SIZE); read } > 0) {
      offset = offset + read
      logger.debug("Read ftp block with length {} bytes, iteration {}", read, iteration)
      resultBuf.put(buf, 0, read)
      iteration = iteration + 1
    }
    val arr : Array[Byte] = resultBuf.array()
    if (props.getProperty("dumplocal") == "yes") {
      val os = new FileOutputStream(UUID.randomUUID().toString)
      IOUtils.write(arr, os)
      os.close()
    }
    arr
  }

  def listFiles(source : SFTPClient, listFolder : String) : List[String] = {
    val res = source.ls(listFolder).asScala.map(x => x.getName).toList
    res.foreach(item => logger.debug("listFiles :: {}", item))
    res
  }

  def sync(source : SFTPClient, dest : SFTPClient, rootFolder : String,
           pathFilterFunction : String => Boolean,
           pathSelectorFunction : List[String] => List[String],
           remapFileNameFunction : String => String
          ) : List[String] = {
    logger.info("call sync")
    val list = listFiles(source, rootFolder)
    logger.info("received list of {} items", list.length)
    val filteredList = list.filter(pathFilterFunction)
    logger.info("list of items after filtering : {}", filteredList.length)
    filteredList.foreach(x => logger.debug("sync :: filtered :: {}", x))
    val selected = pathSelectorFunction(filteredList)
    selected.foreach(x => logger.debug("sync :: selected :: {}", x))
    logger.info("list of items after selection : {}", selected.length)

    for(filename <- selected) {
      val ftpPath = rootFolder + "/" + filename
      val res = getBlob(source, ftpPath)
      val destFtpPath = rootFolder + "/" + remapFileNameFunction(filename)
      writeBlob(dest, destFtpPath, res)
      logger.info("OK")
      reportLogger.info("Saved file : {}", destFtpPath)
    }
    list
  }

  def go(props : Properties) : Unit = {

    def createSftpConnectionFromConfig(prefix: String): SFTPClient = {
      createSftpClient(
        props.getProperty(prefix + "host"),
        (props.getProperty(prefix + "port")).toInt,
        props.getProperty(prefix + "user"),
        props.getProperty(prefix + "pwd"))
    }

    val source = createSftpConnectionFromConfig("source.")
    val dest = createSftpConnectionFromConfig("dest.")

    def processGroup(groupNum : Int): Unit = {
      logger.info("processing group : {}", groupNum)
      val root = props.getProperty(s"group.${groupNum}.root")
      val patternText = props.getProperty(s"group.${groupNum}.pattern")
      logger.info("pattern (regex) : {}", patternText)
      import java.util.regex.Pattern.*
      val patternObject = Pattern.compile(patternText)
      val fileNameFilter = (sample:String) => patternObject.matcher(sample).matches()
      val numoffiles = props.getProperty(s"group.${groupNum}.numoffiles").toInt
      logger.info("numoffiles : {}", numoffiles)
      val ordered = props.getProperty(s"group.${groupNum}.ordered")
      logger.info("ordered = {}", ordered)
      val sorter: List[String] => List[String] = (input: List[String]) => {
        ordered match {
          case "asc" => input.sortWith((a, b) => a < b)
          case "desc" => input.sortWith((a, b) => a >= b)
          case "_" => throw new RuntimeException("Unknown ordering " + ordered)
        }
      }
      val selector: List[String] => List[String] = (input: List[String]) => sorter(input).take(numoffiles)
      val destpattern = props.getProperty(s"group.${groupNum}.destpattern")
      logger.debug("destpattern = {}", destpattern)
      val instant = Instant.now()
      val zone = props.getProperty("zone")
      val zdt = instant.atZone(ZoneId.of(zone))
      logger.trace("zoned date time now = {}", zdt)
      val localDate: LocalDateTime = zdt.toLocalDateTime
      val datetimeformat = props.getProperty("datetimeformat")
      val fileMapper = (input : String) =>
        String.format(destpattern, localDate.format(DateTimeFormatter.ofPattern(datetimeformat)))
      sync(source, dest, root, fileNameFilter, selector, fileMapper)
    }

    val n = props.getProperty("groups").toInt

    for(i <- 1 to n) {
      processGroup(i)
    }

    reportLogger.info("Processed groups : {}", n)

  }

  var props = new Properties()

  def main(args : Array[String]) : Unit = {
    val configName = if (args.length > 0) args(0) else "config.properties"
    props.load(new FileInputStream(configName))
    go(props)
  }

}