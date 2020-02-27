name := "ask-bot"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies += "com.bot4s" %% "telegram-core" % "4.4.0-RC1"

libraryDependencies += "org.typelevel" %% "cats-effect" % "1.3.0"

libraryDependencies += "com.monovore" %% "decline" % "1.0.0"

libraryDependencies ++= Seq(
  "biz.enef" %% "slogging-slf4j" % "0.6.1",
  "org.slf4j" % "slf4j-simple" % "1.7.+"
)