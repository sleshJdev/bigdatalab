package controllers

import scala.collection.mutable

object InMemoryStorage {
  val users: mutable.HashMap[String, AppUser] = mutable.HashMap[String, AppUser]()
  val rates: mutable.HashMap[Nothing, Nothing] = mutable.HashMap()
}
