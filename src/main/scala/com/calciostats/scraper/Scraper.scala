package com.calciostats.scraper

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{elementList => _, _}
/**
  * Created by daniele on 06/01/2017.
  */
object Scraper extends App{



  val browser = JsoupBrowser()
  val doc = browser.get("https://www.whoscored.com/Teams/75/Statistics/Italy-Inter")


  println (doc >> text("body"))

  val tables: List[Element] = doc >> elementList(".ws-list-grid")

  val items = tables.head >> elementList("tr")
    .map(_ >> elementList("td"))
    .map(e => (e.head, e(1)))

  items

}
