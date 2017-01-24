package com.calciostats.scraper

import java.nio.file.{Files, Paths}

import com.calciostats.scraper.Utils.{findChildElementByName, mergeTables, toText, writeResultOnCsvFile}
import org.openqa.selenium.{WebDriver, WebElement}
import org.openqa.selenium.chrome.ChromeDriver
import org.scalatest.selenium.WebBrowser
import org.scalatest.time.{Second, Span}

/**
  * Created by daniele on 23/01/2017.
  */
object ScraperByLeague extends App with WebBrowser {

  System.setProperty("webdriver.chrome.driver", "./chromedriver")

  implicit val webDriver: WebDriver = new ChromeDriver()

  go to "https://www.whoscored.com/Regions/108/Tournaments/5/Seasons/6461/Stages/14014/TeamStatistics/Italy-Serie-A-2016-2017"

  val summary = getStatAttributes

//  click on xpath("""//*[@id="stage-team-stats-options"]/li[2]/a""")
//  Thread.sleep(500)
//  val defensive = getStatAttributes
//
//  click on xpath("""//*[@id="stage-team-stats-options"]/li[3]/a""")
//  Thread.sleep(500)
//  val offensive = getStatAttributes


  val goalTypes = getStatGoalAttributes

//  click on xpath("""//*[@id="stage-situation-stats-options"]/li[2]/a""")
//  Thread.sleep(500)
//  val passTypes = getStatGoalAttributes
//
//  click on xpath("""//*[@id="stage-situation-stats-options"]/li[3]/a""")
//  Thread.sleep(500)
//  val cardSituations = getStatGoalAttributes


  val attackSides = getPositionalStatAttributes

//  click on xpath("""//*[@id="stage-pitch-stats-options"]/li[2]/a""")
//  Thread.sleep(500)
//  val shotDirections = getPositionalStatAttributes
//
//  click on xpath("""//*[@id="stage-pitch-stats-options"]/li[3]/a""")
//  Thread.sleep(500)
//  val shotZones = getPositionalStatAttributes
//
//  click on xpath("""//*[@id="stage-pitch-stats-options"]/li[4]/a""")
//  Thread.sleep(500)
//  val actionZones = getPositionalStatAttributes

  val results = List(summary, goalTypes, attackSides).reduceLeft { mergeTables }

  writeResultOnCsvFile(results)

  print("END")

  close()



  def getPositionalStatAttributes: List[Map[String, String]] = {
    val elementTitles: List[Element] = findAll(xpath("""//*[@id="stage-touch-channels-grid"]/thead/tr/th""")).toList
    val elementRows: List[Element] = findAll(xpath("""//*[@id="stage-touch-channels-content"]/tr""")).toList

    getAtttributes(elementTitles, elementRows)
  }

  def getStatAttributes: List[Map[String, String]] = {
    val elementTitles: List[Element] = findAll(xpath("""//*[@id="top-team-stats-summary-grid"]/thead/tr/th""")).toList
    val elementRows: List[Element] = findAll(xpath("""//*[@id="top-team-stats-summary-content"]/tr""")).toList

    getAtttributes(elementTitles, elementRows)
  }

  def getStatGoalAttributes: List[Map[String, String]] = {
    val elementTitles: List[Element] = findAll(xpath("""//*[@id="stage-goals-grid"]/thead/tr/th""")).toList
    val elementRows: List[Element] = findAll(xpath("""//*[@id="stage-goals-content"]/tr""")).toList

    getAtttributes(elementTitles, elementRows)
  }

  def getAtttributes(elementTitles: List[_root_.com.calciostats.scraper.ScraperByLeague.Element], elementRows: List[_root_.com.calciostats.scraper.ScraperByLeague.Element]): List[Map[String, String]] = {
    val teamAttributes = elementRows
      .map(row => findChildElementByName(row.underlying, "td").map(value => toText(value)).filter(_.nonEmpty)).filter(_.nonEmpty)
    val titles = elementTitles.map(_.text).filter(_.nonEmpty)

    teamAttributes.map(attributes => (titles zip attributes).toMap)
  }
}


object Utils {
  def toText(element: WebElement): String = {
    val spanElements = findChildElementByName(element, "span")
    if (spanElements.length == 3) {
      spanElements.tail.map(_.getText).mkString(" - ")
    } else {
      element.getText
    }
  }

  def findChildElementByName(element: WebElement, name: String): List[WebElement] = {
    import scala.collection.JavaConversions._
    element.findElements(org.openqa.selenium.By.tagName(name)).toList
  }

  def findChildElementById(element: WebElement, name: String): List[WebElement] = {
    import scala.collection.JavaConversions._
    element.findElements(org.openqa.selenium.By.xpath(name)).toList
  }

  def mergeMap[K,V](map1: Map[K,V], map2: Map[K,V]): Map[K,V] = {
    (map1.toList ::: map2.toList).toMap
  }
  def mergeTables(tableRows1: List[Map[String, String]], tableRows2: List[Map[String, String]]): List[Map[String, String]] = {
    val byTeam1 = tableRows1.groupBy(_("Team"))
    val byTeam2 = tableRows2.groupBy(_("Team"))
    byTeam1.map { case (k,v) => mergeMap(v.head,  byTeam2.get(k).get.head) }.toList
  }

  def writeResultOnCsvFile(results: List[Map[String, String]]): Unit = {
    println("saving results ...")
    val titles = results.head.keys
    val titleRow = titles.mkString(",")
    val lines = results.map(resultMap => titles.toSeq.map(title => resultMap.getOrElse(title, "n/a")).mkString(","))

    val linesWithTitle = titleRow :: lines

    import scala.collection.JavaConverters._
    Files.write(Paths.get("team-statistics.csv"), linesWithTitle.asJava)
  }
}
