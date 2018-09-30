package com.fullgc.client

/**
 * User: Daniel.Shemesh
 * Date: 16/02/2015
 */
sealed trait HttpReqTypes

case class POST(body: Object) extends HttpReqTypes

case object GET extends HttpReqTypes

case class PUT(body: Object) extends HttpReqTypes

case object DELETE extends HttpReqTypes
