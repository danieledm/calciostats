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

  val eles: Iterator[_root_.com.calciostats.scraper.BlogSpec.Element] = findAll(className("ws-list"))

  val statsList = eles.flatMap(el => findChildElementByName(el.underlying, "td").map(toText)).toList

  val values = statsList.zipWithIndex.filter(e => (e._2 + 1) % 2 == 0).map(_._1)
  val keys = statsList.zipWithIndex.filter(e => (e._2 + 1) % 2 == 1).map(_._1)

  val keyValueStats = keys.zip(values).foreach(e => println(e._1 + " = " + e._2))


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


}
