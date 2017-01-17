name := "calciostats"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.0"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.5.0"

libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.35.0"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "3.0.1"

libraryDependencies += "org.seleniumhq.selenium" % "selenium-chrome-driver" % "2.39.0"

mainClass in (Compile, run) := Some("com.calciostats.scraper.SeleniumScraper")

packageName in Docker := "whoscoredstats"

enablePlugins(JavaAppPackaging, DockerPlugin)
