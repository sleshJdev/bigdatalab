package controllers

import play.api.libs.json.{Json, OWrites, Reads}

case class AppUser(login: String, password: String)

case class UserInfo(key: String, sex: String, age: Int)

case class Message(message: String)

object Models {
  val sex = Array("male", "female")

  implicit val appUserReader: Reads[AppUser] = Json.reads[AppUser]
  implicit val userWrites: OWrites[UserInfo] = Json.writes[UserInfo]
  implicit val messageWrites: OWrites[Message] = Json.writes[Message]

  def generateUser(key: String): UserInfo = {
    val hash = key.hashCode.abs
    val usersex = sex(hash % sex.length)
    var age = 18 + hash % 60
    UserInfo(key, usersex, age)
  }
}