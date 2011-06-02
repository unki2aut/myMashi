import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) { //with ProguardProject {
  val liftVersion = "2.3"

  // uncomment the following if you want to use the snapshot repo
  // val scalatoolsSnapshot = ScalaToolsSnapshots

  // If you're using JRebel for Lift development, uncomment
  // this line
  // override def scanDirectories = Nil

  override def libraryDependencies = Set(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-widgets" % liftVersion % "compile->default",
    "org.mortbay.jetty" % "jetty" % "6.1.22" % "test->default",
    "junit" % "junit" % "4.5" % "test->default",
    "ch.qos.logback" % "logback-classic" % "0.9.26",
    "org.scala-tools.testing" %% "specs" % "1.6.6" % "test->default",
    "org.scala-tools.testing" %% "scalacheck" % "1.8" % "test->default",
    "org.scalatest" %% "scalatest" % "1.5.RC2" % "test->default",
    "rome" % "rome" % "0.9",
    "org.apache.lucene" % "lucene-core" % "3.1.0",
    "mysql" % "mysql-connector-java" % "5.1.16",
    "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2"
  ) ++ super.libraryDependencies

  /* override def proguardOptions = List(
    "-dontshrink -dontoptimize -dontobfuscate -dontpreverify -dontnote -ignorewarnings",
    proguardKeepAllScala
  ) */
}
