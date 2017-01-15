package com.calciostats.scraper

import org.openqa.selenium.{WebDriver, WebElement}
import org.openqa.selenium.chrome.ChromeDriver
import org.scalatest.selenium.WebBrowser


/**
  * Created by daniele on 12/01/2017.
  */
object BlogSpec  extends App with WebBrowser {

  System.setProperty("webdriver.chrome.driver", "/Users/daniele/dev/chromedriver")
  implicit val webDriver: WebDriver = new ChromeDriver()

  go to "https://www.whoscored.com/Teams/75/Statistics/Italy-Inter"

  val title = pageTitle


//  getSituationStatistics("team-goals-content").foreach(println)
//  getSituationStatistics("team-passes-content").foreach(println)
//  getSituationStatistics("team-cards-content").foreach(println)
//  collectGeneralStatistics().foreach(println)

  go to "https://www.whoscored.com/stagestatfeed/?against=0&field=0&stageId=14014&teamId=75&type=6"

  val body = pageSource
  println(body)

  close()

  def getSituationStatistics(elementId: String ): List[(String, String)] = {
    val table: List[_root_.com.calciostats.scraper.BlogSpec.Element] = findAll(id(elementId)).toList

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

    val eles: Iterator[_root_.com.calciostats.scraper.BlogSpec.Element] = findAll(className("ws-list"))

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

  def findChildElementByXPath(element: WebElement, xpath: String): List[WebElement] = {
    import scala.collection.JavaConversions._
    element.findElements(org.openqa.selenium.By.xpath(xpath)).toList
  }

  def findChildElementById(element: WebElement, id: String): List[WebElement] = {
    import scala.collection.JavaConversions._
    element.findElements(org.openqa.selenium.By.id(id)).toList
  }
}
