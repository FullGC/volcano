package com.fullgc.api

/**
  * Created by dani on 24/09/18.
  */
import com.fullgc.client.Send._
import com.fullgc.client.{GET, Send}
import dispatch.url


object RequestDispatcher {
  def createUser(user: String, password: String) = {
    val response = Send a VolcanoRequest(
      requestType = GET,
      path = VolcanoPath.Admin.Account.signup,
      queryParams = List(("user", Some(user)), ("password", Some(password))),
      headers = List(("accept", "application/json" + "; charset=UTF-8")),
      basicAuth = None
    )
    response
  }

  def addFriend(token: String, user: String, friend: String) = {
    val response = Send a VolcanoRequest(
      requestType = GET,
      path = VolcanoPath.Admin.Network.addFriend,
      queryParams = List(("token", Some(token)),("user", Some(user)), ("friend", Some(friend))),
      headers = List(("accept", "application/json" + "; charset=UTF-8")),
      basicAuth = None
    )
    response
  }

  def findFriend(token: String, user: String, friend: String) = {
    val response = Send a VolcanoRequest(
      requestType = GET,
      path = VolcanoPath.Admin.Network.findFriend,
      queryParams = List(("token", Some(token)),("user", Some(user)), ("friend", Some(friend))),
      headers = List(("accept", "application/json" + "; charset=UTF-8")),
      basicAuth = None
    )
    response
  }

  def logIn(user: String, password: String) = {
    val response = Send a VolcanoRequest(
      requestType = GET,
      path = VolcanoPath.Admin.Account.login,
      queryParams = List(("user", Some(user)), ("password", Some(password))),
      headers = List(("accept", "application/json" + "; charset=UTF-8")),
      basicAuth = None
    )
    response
  }
  def changePassword(token: String, user: String, oldPassword: String, newPassword: String) = {
    val response = Send a VolcanoRequest(
      requestType = GET,
      path = VolcanoPath.Admin.Account.password,
      queryParams = List(("token", Some(token)),("user", Some(user)), ("oldPassword", Some(oldPassword)), ("newPassword", Some(newPassword))),
      headers = List(("accept", "application/json" + "; charset=UTF-8")),
      basicAuth = None
    )
    response
  }


}

trait RESTfulWSPath {

  import RESTfulWSPath._

  val ROOT_PATH = url(HTTP + LOCALHOST + ":" + PORT)
}

object RESTfulWSPath {
  val offset = "offset"
  val accept = "accept"
  val limit = "limit"
  val HTTP = "http://"
  val PORT = "8083"
  val LOCALHOST = "localhost"
}

object VolcanoPath extends RESTfulWSPath {

  final val PATH = ROOT_PATH / "volcano"

  object Admin {
    val users = PATH / "users"
    object Account{
      val signup = users / "signup"
      val login = users / "login"
      val password = users / "password"
    }
    object Network{
      val addFriend = users / "addFriend"
      val findFriend = users / "findFriend"
    }


  }
}

