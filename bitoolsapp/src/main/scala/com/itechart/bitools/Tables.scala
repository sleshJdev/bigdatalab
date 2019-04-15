package com.itechart.bitools

import java.sql.Timestamp
import java.time.Instant
import java.util.concurrent.ThreadLocalRandom

import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

case class Order(fulldate: Timestamp,
                 area: Option[String],
                 country: Option[String],
                 description: Option[String],
                 name: Option[String],
                 width: Option[Int],
                 height: Option[Int],
                 length: Option[Int],
                 weight: Option[Int],
                 price: Option[Double],
                 units: Option[Int],
                 ordermethod: Option[String])

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

    import DatabaseProvider.db

    implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

    def save(order: Order): Future[Int] = db.run(this.+=(order))

    def generate(count: Int = 1): Future[Seq[Order]] = {
      val r = for {
        areas <- db.run(this.map(_.area).distinct.result)
        countries <- db.run(this.map(_.country).distinct.result)
        ordermethods <- db.run(this.map(_.ordermethod).distinct.result)
        products <- db.run(this.map(_.name).distinct.result)
      } yield (areas, countries, ordermethods, products)

      r.flatMap {
        case (areas, countries, ordermethods, products) =>
          val fakeOrders = for (i <- 1 to count) yield Order(
            Timestamp.from(Instant.now()),
            areas(ThreadLocalRandom.current().nextInt(areas.size)),
            countries(ThreadLocalRandom.current().nextInt(countries.size)),
            Some("Description" + ThreadLocalRandom.current().nextInt(5)),
            products(ThreadLocalRandom.current().nextInt(products.size)),
            Some(ThreadLocalRandom.current().nextInt(120)),
            Some(ThreadLocalRandom.current().nextInt(130)),
            Some(ThreadLocalRandom.current().nextInt(140)),
            Some(ThreadLocalRandom.current().nextInt(50)),
            Some(ThreadLocalRandom.current().nextInt(150)),
            Some(ThreadLocalRandom.current().nextInt(20)),
            ordermethods(ThreadLocalRandom.current().nextInt(ordermethods.size)))

          db.run(this.++=(fakeOrders)).map(_ => fakeOrders)

      }
    }
  }

}
