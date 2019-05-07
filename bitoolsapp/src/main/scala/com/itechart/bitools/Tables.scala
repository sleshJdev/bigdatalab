package com.itechart.bitools

import java.nio.charset.StandardCharsets
import java.sql.Timestamp
import java.time.Instant
import java.util.Base64
import java.util.concurrent.ThreadLocalRandom

import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

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
                 ordermethod: Option[String],
                 ip: Option[String],
                 age: Option[Int],
                 sex: Option[String],
                 userId: Option[String],
                 userCookieId: Option[String],
                 currency: Option[String])

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

    def ordermethod = column[Option[String]]("ordermethod")

    def units = column[Option[Int]]("units")

    def ip = column[Option[String]]("ip")

    def age = column[Option[Int]]("age")

    def sex = column[Option[String]]("sex")

    def userId = column[Option[String]]("user_id")

    def userCookieId = column[Option[String]]("user_cookie_id")

    def currency = column[Option[String]]("currency")

    def * = (
      fulldate, area, country, description,
      name, width, height, length, weight,
      price, units, ordermethod, ip, age, sex,
      userId, userCookieId, currency) <> (Order.tupled, Order.unapply)
  }

  val sex = Array("male", "female")
  case class UserInfo(key: String, sex: String, age: Int)

  object orders extends TableQuery(new Orders(_)) {

    import DatabaseProvider.db

    implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

    def save(order: Order): Future[Int] = db.run(this.+=(order))

    def generateUser(key: String): UserInfo = {
      val hash = key.hashCode.abs
      val usersex = sex(hash % sex.length)
      var age = 18 + hash % 60
      UserInfo(key, usersex, age)
    }

    def generate(count: Int = 1): Future[Seq[Order]] = {
      val r = for {
        areas <- db.run(this.map(_.area).distinct.result)
        ips <- db.run(this.map(_.ip).distinct.result)
        countries <- db.run(this.map(_.country).distinct.result)
        ordermethods <- db.run(this.map(_.ordermethod).distinct.result)
        products <- db.run(this.map(_.name).distinct.result)
        currencies <- db.run(this.map(_.currency).distinct.result)
      } yield (areas, countries, ips, ordermethods, products, currencies)

      r.flatMap {
        case (areas, countries, ips, ordermethods, products, currencies) =>
          val fakeOrders = for (i <- 1 to count) yield {
            val random = ThreadLocalRandom.current()
            val orderMethod = ordermethods(random.nextInt(ordermethods.size))
            val webCookie = orderMethod.exists(_.equalsIgnoreCase("web"))

            val id = random.nextInt(100).toString
            val key = if (!webCookie) id
            else Base64.getEncoder.encodeToString(id.getBytes(StandardCharsets.UTF_8))
            val userInfo = generateUser(key)

            Order(
              Timestamp.from(Instant.now()),
              areas(ThreadLocalRandom.current().nextInt(areas.size)),
              countries(ThreadLocalRandom.current().nextInt(countries.size)),
              Some("Description" + random.nextInt(10)),
              products(random.nextInt(products.size)),
              Some(random.nextInt(10)),
              Some(random.nextInt(10)),
              Some(random.nextInt(10)),
              Some(random.nextInt(10)),
              Some(random.nextInt(10)),
              Some(random.nextInt(10)),
              orderMethod,
              ips(random.nextInt(ips.size)),
              Some(userInfo.age),
              Some(userInfo.sex),
              if (webCookie) Option.empty else Some(userInfo.key),
              if (webCookie) Some(random.nextDouble().toString) else Option.empty,
              currencies(random.nextInt(currencies.size))
            )
          }

          db.run(this.++=(fakeOrders)).map(_ => fakeOrders)
      }
    }
  }
}
