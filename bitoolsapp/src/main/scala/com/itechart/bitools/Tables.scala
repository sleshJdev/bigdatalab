package com.itechart.bitools

import java.sql.Timestamp

import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

case class Order(fulldate: Timestamp,
                 area: Option[String] = None,
                 country: Option[String] = None,
                 description: Option[String] = None,
                 name: Option[String] = None,
                 width: Option[Int] = None,
                 height: Option[Int] = None,
                 length: Option[Int] = None,
                 weight: Option[Int] = None,
                 price: Option[Double] = None,
                 units: Option[Int] = None,
                 ordermethod: Option[String] = None)

object Tables {

  class Orders(tag: Tag) extends Table[Order](tag, "sales_raw") {
    def fulldate = column[Timestamp]("fulldate")

    def area = column[Option[String]]("area")

    def country = column[Option[String]]("country")

    def description = column[Option[String]]("description")

    def name = column[Option[String]]("name")

    def width = column[Option[Int]]("width")

    def height = column[Option[Int]]("height")

    def length = column[Option[Int]]("length")

    def weight = column[Option[Int]]("weight")

    def price = column[Option[Double]]("price")

    def units = column[Option[Int]]("units")

    def ordermethod = column[Option[String]]("ordermethod")

    def * = (fulldate,
      area, country, description,
      name, width, height, length, weight,
      price, units,
      ordermethod) <> (Order.tupled, Order.unapply)
  }

  object orders extends TableQuery(new Orders(_)) {
    def save(order: Order): Future[Int] = DatabaseProvider.db.run(this.+=(order))
  }

}
