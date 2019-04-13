package com.itechart.bitools

import slick.jdbc.H2Profile.api._

object DatabaseProvider {
  val db = Database.forConfig("bidb", ConfigProvider.config)
}
