package com.itechart.bitools

import slick.jdbc.H2Profile.api._

object Db {
  val db = Database.forConfig("bidb")
}
