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

  eles.map(el => findChildElementByName(el.underlying, "td").map(toText)).foreach(println)



  def toText(element: WebElement): String = {
//    findChildElementByName(element, "span").map(_.getText).mkString(" - ")
    element.getText
  }


  def findChildElementByName(element: WebElement, name: String): List[WebElement] = {
    import scala.collection.JavaConversions._
    element.findElements(org.openqa.selenium.By.tagName("td")).toList
  }


}
