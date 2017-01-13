package com.calciostats.scraper

import java.text.SimpleDateFormat

import org.json4s._
import org.json4s.jackson.JsonMethods._
import dispatch._
import Defaults._
import com.ning.http.client.FluentCaseInsensitiveStringsMap
import com.ning.http.client.cookie.Cookie

import scala.util.{Failure, Success}
import scala.collection.JavaConversions._



/**
  * Created by daniele on 07/01/2017.
  */
object StatisticsGetter extends App{

  implicit lazy val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  }

  def pageHeaders(): Future[List[Cookie]] = {
    val svc = url("https://www.whoscored.com/Teams/75")
    svc.setHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36")
    val response = Http(svc.HEAD)
    response.map(resp => resp.getCookies.toList)
  }


  def getStats(cookies: List[Cookie]): Future[String] = {
//    val svc = url("https://google.com")
    val svc = url("https://www.whoscored.com/StatisticsFeed/1/GetTeamStatistics?category=summaryteam&subcategory=summary&statsAccumulationType=0&field=Overall&tournamentOptions=&timeOfTheGameStart=&timeOfTheGameEnd=&teamIds=75&stageId=14014&sortBy=Rating&sortAscending=&page=&numberOfTeamsToPick=&isCurrent=true&formation=")
    val hcookie =cookies.map(c => s"${c.getName}=${c.getValue}").mkString(" ;")
    svc.setHeader("cookie", hcookie)
//    cookies.foreach(svc.addCookie)
    //    headers.get("Set-Cookie").toList.foreach( value =>
//      svc.addCookie("cookie", value)
//    )
    svc.setHeader("x-requested-with", "XMLHttpRequest")
    svc.setHeader("accept-language", "en-US,en;q=0.8,it;q=0.6")
    svc.setHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36")
    svc.setHeader("accept", "application/json, text/javascript, */*; q=0.01")
    svc.setHeader("referer", "https://www.whoscored.com/Teams/75/Statistics/Italy-Inter")
    svc.setHeader("authority", "www.whoscored.com")
    svc.setHeader("accept-encoding:", "gzip, deflate, sdch, br")

    val response = Http(svc OK as.String)
    val result = response.map(parse(_).extract[String])
    result onFailure { case _ => println("Failed to get statistics")}
    result
  }



  def getExampe() = {
    //    val svc = url("https://google.com")
    val svc = url("https://www.whoscored.com/StatisticsFeed/1/GetTeamStatistics?category=summaryteam&subcategory=summary&statsAccumulationType=0&field=Overall&tournamentOptions=&timeOfTheGameStart=&timeOfTheGameEnd=&teamIds=75&stageId=14014&sortBy=Rating&sortAscending=&page=&numberOfTeamsToPick=&isCurrent=true&formation=")
    svc.setHeader("x-requested-with", "XMLHttpRequest")
    svc.setHeader("accept-language", "en-US,en;q=0.8,it;q=0.6")
    svc.setHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36")
    svc.setHeader("accept", "application/json, text/javascript, */*; q=0.01")
    svc.setHeader("referer", "https://www.whoscored.com/Teams/75/Statistics/Italy-Inter")
    svc.setHeader("authority", "www.whoscored.com")
    val cookie = "visid_incap_774904=rXFhLD4ATz2C0Fj9PyqyvrD2ZFgAAAAAQUIPAAAAAACArF/RkqXDMd/WVQS0W08g; incap_ses_476_774904=urHCVOWIu0erwsiXtBebBtu0d1gAAAAAOpObSD+n9sx2j5M7yFhffA==; _gat=1; _ga=GA1.2.374741354.1483011766"
    svc.setHeader("cookie", cookie)
    svc.setHeader("modei-last-mode", "FsY6Oz9sleS1LRqfJJXEEOrGg6zqlSBxPvpMBU0FD7k=")


    val response = Http(svc OK as.String)
    val result = response.map(parse(_).extract[String])
    result onFailure { case _ => println("Failed to get statistics")}
    result
  }


//  pageHeaders().flatMap(headers => getStats(headers)).onComplete {
//    case Success(posts) => println("RISULTATO "+posts)
//    case Failure(t) => println("Eroorre accaduto osti: " + t.getMessage)
//  }

  getExampe().onComplete {
    case Success(posts) => println("RISULTATO "+posts)
    case Failure(t) => println("Eroorre accaduto osti: " + t.getMessage)
  }

  Thread.sleep(10000)




}
