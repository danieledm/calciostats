package com.calciostats.scraper

import java.nio.file.{Files, Paths}

import org.openqa.selenium.{WebDriver, WebElement}
import org.openqa.selenium.chrome.ChromeDriver
import org.scalatest.selenium.WebBrowser


/**
  * Created by daniele on 12/01/2017.
  */
object SeleniumScraper extends App with TeamStatistics {

  // italy, england, spain, germany, france, portugal, netherlands
  val allLeagues = (108, 5) :: (252, 2) :: (206, 4) :: (81, 3) :: (74, 22) :: (177, 21) :: (155, 13) :: Nil
//  val allLeagues = (108, 5) :: Nil

  // get teams url ids
  val teams = allLeagues.flatMap(league => {
    go to s"https://www.whoscored.com/Regions/${league._1}/Tournaments/${league._2}"

    findAll(className("team-link"))
      .map(x => x.attribute("href"))
      .filter(x => x.isDefined)
      .map(x => x.get)
      .toList.distinct
  })


  // for every team collect statistics in a map
  val results = teams.map(team => {
    go to s"${team}/Statistics"
    println(s"${team}/Statistics") //log

    val title = pageTitle
    val goalContent = getSituationStatistics("team-goals-content")
    val passesContent = getSituationStatistics("team-passes-content")
    val cardsContent = getSituationStatistics("team-cards-content")
    val general = collectGeneralStatistics()

    val teamName = title.split("-")(0).trim
    ("Team", teamName) :: goalContent ::: passesContent ::: cardsContent ::: general
  })

  val titles = results.head.map(_._1)
  val titleRow = titles.mkString(",")

  val listResultMap = results.map(x => x.toMap)
  val lines = listResultMap.map(resultMap => titles.map(title => resultMap.getOrElse(title, "n/a")).mkString(","))
  val linesWithTitle = titleRow :: lines

  import scala.collection.JavaConverters._
  Files.write(Paths.get("team-statistics.csv"), linesWithTitle.asJava)

  close()

  println("END!")
}

trait TeamStatistics extends WebBrowser {

  System.setProperty("webdriver.chrome.driver", "./chromedriver")

  implicit val webDriver: WebDriver = new ChromeDriver()

  def getSituationStatistics(elementId: String ): List[(String, String)] = {
    val table: List[Element] = findAll(id(elementId)).toList

    val situationsStats = Utils.findChildElementByName(table.head.underlying, "tr")
      .map(Utils.findChildElementByName(_, "td").toList)
      .filter(_.size == 3)
      .flatMap(x => List((x.head.getText, x(1).getText), (x.head.getText + " %", x(2).getText)))

    situationsStats
  }


  def collectGeneralStatistics(): List[(String, String)] = {

    def toText(element: WebElement): String = {
      val spanElements = Utils.findChildElementByName(element, "span")
      if (spanElements.length == 3) {
        spanElements.tail.map(_.getText).mkString(" - ")
      } else {
        element.getText
      }
    }

    val eles: Iterator[Element] = findAll(className("ws-list"))
    val statsList = eles.flatMap(el => Utils.findChildElementByName(el.underlying, "td").map(toText)).toList

    val values = statsList.zipWithIndex.filter(e => (e._2 + 1) % 2 == 0).map(_._1)
    val keys = statsList.zipWithIndex.filter(e => (e._2 + 1) % 2 == 1).map(_._1)

    keys.zip(values)
  }
}


object Utils {
  def findChildElementByName(element: WebElement, name: String): List[WebElement] = {
    import scala.collection.JavaConversions._
    element.findElements(org.openqa.selenium.By.tagName(name)).toList
  }

}
