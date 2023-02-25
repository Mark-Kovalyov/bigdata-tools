val scala3Version = "3.2.1"

scalacOptions ++= Seq("-explain")

Compile / packageBin / packageOptions +=
  Package.ManifestAttributes(
    "Class-Path" ->
      List(
        "lib/slf4j-api-2.0.3.jar",
        "lib/slf4j-ext-2.0.3.jar",
        "lib/jsch-0.2.0.jar",
        "lib/sshj-0.31.0.jar",
        "lib/logback-classic-1.4.4.jar",
        "lib/logback-core-1.4.4.jar",
        "lib/scala-library-2.13.10.jar",
        "lib/scala3-library_3-3.2.1.jar",
        "lib/commons-csv-1.9.0.jar",
        "lib/bcpkix-jdk15on-1.68.jar",
        "lib/bcprov-jdk15on-1.68.jar",
        "lib/eddsa-0.3.0.jar",
        "lib/commons-io-2.11.0.jar"
        )
        .mkString(" "))

lazy val root = project
  .in(file("."))
  .settings(
    exportJars := true,
    name := "ftp-sync",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-ext" % "2.0.3",
      "org.yaml" % "snakeyaml" % "1.33",
      "org.apache.commons" % "commons-csv" % "1.9.0",
      "commons-codec" % "commons-codec" % "1.15",
      "commons-io" % "commons-io" % "2.11.0",
      "com.github.mwiede" % "jsch" % "0.2.0",
      "com.hierynomus" % "sshj" % "0.31.0",
      "ch.qos.logback" % "logback-classic" % "1.4.4"
    )
  )


