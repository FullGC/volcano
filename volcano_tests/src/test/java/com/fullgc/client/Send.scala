package com.fullgc.client

import com.fasterxml.jackson.databind.ObjectMapper
import dispatch._
import org.apache.http.HttpHeaders

import scala.concurrent.ExecutionContext.Implicits.global

object Send {
  val httpClient: Http = Http()
  val mapper = new ObjectMapper()
  //HTTP client
  def a(requestInfo: (dispatch.Req, HttpReqTypes, Boolean)) = {
    val (request, reqType, bodyAsStream) = requestInfo
    val response = httpClient(reqType match {
      case post: POST =>
        if (!bodyAsStream) {
          val json = if (post.body != null) mapper.writeValueAsString(post.body) else null
          val req = request << json
          req.POST OK as.Response(r => r)
        } else {
          request.underlying(_.setBody(post.body.asInstanceOf[Array[Byte]])).POST OK as.Response(r => r)
        }
      case GET => {
        request.GET OK as.Response(r => r)
      }
      case put: PUT =>
        val json = if (put.body != null) mapper.writeValueAsString(put.body) else null
        val req = request << json
        req.PUT OK as.Response(r => r)
      case DELETE => request.DELETE OK as.Response(r => r)

    })
    response()
  }

  // Request builder
  def VolcanoRequest(
                      requestType: HttpReqTypes = GET,
                      path: Req,
                      body: Option[String] = None,
                      queryParams: List[(String, Option[String])] = List(),
                      headers: List[(String, String)] = List(),
                      basicAuth: Option[(String, String)] = None,
                      bodyAsStream: Boolean = false,
                      maybeSessionId: Option[com.ning.http.client.cookie.Cookie] = None

                 ) = {
    //queryParams.foreach(queryParam => completePath = completePath.addQueryParameter(name = queryParam._1, value = queryParam._2))
    val queryParamsMap: Map[String, Seq[String]] = queryParams.filter(_._2.isDefined).toMap.mapValues(value => Seq(value.get))
    val headersMap: Map[String, Seq[String]] = headers.toMap.mapValues(value => Seq(value))
    val completePath = {
      val tempPath = path.setQueryParameters(queryParamsMap).setHeaders(headersMap)
      val maybeContentType: Option[Seq[String]] = headersMap.get(HttpHeaders.CONTENT_TYPE)
      val reqWithContent = if (maybeContentType.nonEmpty) tempPath.setContentType(maybeContentType.get.head, "UTF-8") else tempPath.setContentType("application/json", "UTF-8").addHeader("Accept","application/json; charset=UTF-8")
      if (maybeSessionId.isDefined) reqWithContent.addCookie(maybeSessionId.get) else reqWithContent
    }
    if (basicAuth.nonEmpty) (completePath.as_!(basicAuth.get._1, basicAuth.get._2), requestType, bodyAsStream) else (completePath, requestType, bodyAsStream)
  }
}


