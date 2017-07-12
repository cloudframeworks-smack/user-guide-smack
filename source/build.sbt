val scalaVer = "2.11.7"
val akkaVersion = "2.4.17"

organization := "com.goodrain"
name := "smack"
version := "1.0"

scalaVersion := scalaVer
autoScalaLibrary := false

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/"

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-language:postfixOps",
  "-target:jvm-1.8")

parallelExecution in ThisBuild := false

parallelExecution in Test := false

logBuffered in Test := false

unmanagedBase := baseDirectory.value / "project/lib"

//assemblyJarName := s"$name-$version.jar"

libraryDependencies ++= 
  Seq(
    "com.typesafe.akka" % "akka-actor_2.11" % akkaVersion % Provided,
    "com.typesafe.akka" % "akka-cluster_2.11" % akkaVersion % Provided,
    "com.typesafe.akka" % "akka-contrib_2.11" % akkaVersion % Provided,
    "com.typesafe.akka" % "akka-kernel_2.11" % akkaVersion % Provided,
    "com.typesafe.akka" % "akka-protobuf_2.11" % akkaVersion % Provided,
    "com.typesafe.akka" % "akka-remote_2.11" % akkaVersion % Provided,
    "com.typesafe.akka" % "akka-slf4j_2.11" % akkaVersion % Provided,
    "com.typesafe.akka" % "akka-stream_2.11" % akkaVersion % Provided,
    "com.typesafe.akka" % "akka-stream-kafka_2.11" % "0.16" % Provided,
    "com.datastax.cassandra" % "cassandra-driver-corer" % "3.2.0" % Provided,
    "com.datastax.cassandra" % "commons-collections" % "3.2.2" % Provided
    "org.apache.commons" % "commons-configuration" % "1.10" % Provided
    "org.apache.commons" % "commons-configuration2" % "2.1.1" % Provided
    "commons-lang" % "commons-lang" % "2.6" % Provided,
    "commons-lang" % "commons-lang3" % "3.5" % Provided,
    "commons-logging" % "commons-logging" % "1.2" % Provided,
    "org.joda" % "joda-convert" % "1.6" Provided,
    "joda-time" % "joda-time" % "2.9.9" Provided,
    "org.apache.kafka" % "kafka_2.11" % "0.10.2.1" % Provided,
    "org.apache.kafka" % "kafka-clients" % "0.10.2.1" % Provided,
    "log4j" % "log4j" % "1.2.17" % Provided,
    "com.yammer.metrics" % "metrics-core" % "2.2.0" % Provided,
    "com.codahale.metrics" % "metrics-core" % "3.0.2" % Provided,
    "org.jvnet.mimepull" % "mimepull" % "1.9.4" % Provided,
    "com.typesafe" % "config" % "1.3.0" % Provided,
    "com.google.guava" % "guava" % "19.0" % Provided,
    "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.8" % Provided,
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.8.1" % Provided,
    "com.fasterxml.jackson.core" % "jackson-core" % "2.8.8" % Provided,
    "com.fasterxml.jackson.module" % "jackson-module-jsonSchema" % "2.8.8" % Provided,
    "com.fasterxml.jackson.module" % "jackson-module-paranamer" % "2.8.8" % Provided,
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.8" % Provided,
    "com.esotericsoftware" % "minlog" % "1.3.0" % Provided,
    "io.netty" % "netty" % "3.10.6.Final" % Provided,
    "io.netty" % "netty-all" % "4.1.11.Final.jar" % Provided,
    "io.netty" % "netty-handler" % "4.1.11.Final.jar" % Provided,
    "io.netty" % "netty-transport-native-epoll" % "4.1.11.Final.jar" % Provided,
    "org.objenesis" % "objenesis" % "2.5.1" % Provided,
    "com.thoughtworks.paranamer" % "paranamer" % "2.8" % Provided,
    "org.parboiled" % "parboiled-core" % "1.1.8",
    "org.parboiled" % "parboiled-scala_2.11" % "1.1.8" % Provided,
    "com.outworkers" % "phantom-connectors_2.11" % "2.9.2" % Provided,
    "com.outworkers" % "phantom-streams_2.11" % "2.9.2" % Provided,
    "com.google.protobuf" % "protobuf-java" % "2.6.1" % Provided,
    "org.reactivestreams" % "reactive-streams" % "1.0.0" % Provided,
    "org.scala-lang.modules" % "scala-parser-combinators_2.11" % "1.0.5" % Provided,
    "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.5" % Provided,
    "com.chuusai" % "shapeless_2.11" % "1.0.5" % Provided,
    "com.chuusai" % "shapeless_2.11" % "2.3.2" % Provided,
    "org.slf4j" % "slf4j-api" % "1.7.19" % Provided,
    "org.slf4j" % "slf4j-log4j12" % "1.7.24" % Provided,
    "io.spray" % "spray-caching_2.11" % "1.3.4" % Provided,
    "io.spray" % "spray-can_2.11" % "1.3.4" % Provided,
    "io.spray" % "spray-http_2.11" % "1.3.4" % Provided,
    "io.spray" % "spray-httpx_2.11" % "1.3.4" % Provided,
    "io.spray" % "spray-io_2.11" % "1.3.4" % Provided,
    "io.spray" % "spray-json_2.11" % "1.3.4" % Provided,
    "io.spray" % "spray-routing-shapeless2_2.11" % "1.3.3" % Provided,
    "io.spray" % "spray-util_2.11" % "1.3.4" % Provided
    
    "org.apache.hadoop" % "hadoop-auth" % "2.8.0" % Provided
    "com.datastax.spark" % "spark-cassandra-connector-embedded_2.11" % "2.0.2" % Provided
    "org.apache.spark" % "spark-core_2.11" % "2.1.1" % Provided
    "org.apache.spark" % "spark-network-common_2.11" % "2.1.1" % Provided
    "org.apache.spark" % "spark-sql_2.11" % "2.1.1" % Provided
    "org.apache.spark" % "spark-streaming_2.11" % "2.1.1" % Provided
    "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.1.1" % Provided
    "org.apache.spark" % "spark-tags_2.11" % "2.1.1" % Provided
    
    
    "org.apache.flink" % "flink-connector-kafka-base_2.11" % "1.2.1"
    "org.apache.flink" % "flink-core" % "1.2.1"
    "org.apache.flink" % "flink-connector-kafka-0.9_2.11" % "1.2.1"
    "org.apache.flink" % "flink-streaming-java_2.11" % "1.2.1"
    "org.apache.flink" % "flink-runtime_2.11" % "1.2.1"
    "org.apache.flink" % "flink-clients_2.11" % "1.2.1"
    "org.apache.flink" % "flink-java" % "1.2.1"
    "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.8.0"
    "org.apache.flink" % "flink-metrics-core" % "1.2.1"
    "org.apache.flink" % "flink-annotations" % "1.2.1"
    "org.apache.flink" % "flink-optimizer_2.11" % "1.2.1"
    
    "com.github.scopt" % "scopt_2.11" % "3.2.0"
    "com.data-artisans" % "flakka-remote_2.11" % "2.3-custom"
    "com.data-artisans" % "flakka-slf4j_2.11" % "2.3-custom"
    "com.data-artisans" % "flakka-actor_2.11" % "2.3-custom"
    "org.clapper" % "grizzled-slf4j_2.11" % "1.3.1"
  )