package com.itechart.bitools

import java.sql.Timestamp
import java.time.Instant

import org.scalatra.{MultiParams, forms}
import org.scalatra.forms.{MappingValueType, double, number, optional, text}
import org.scalatra.i18n.Messages

object Mappings {
  val orderFormMapping: MappingValueType[Order] = forms.mapping(
    "area" -> optional(text()),
    "country" -> optional(text()),
    "description" -> optional(text()),
    "name" -> optional(text()),
    "width" -> optional(number()),
    "height" -> optional(number()),
    "length" -> optional(number()),
    "weight" -> optional(number()),
    "price" -> optional(double()),
    "units" -> optional(number()),
    "ordermethod" -> optional(text()),
    "ip" -> optional(text()),
    "age" -> optional(number()),
    "sex" -> optional(text()),
    "userId" -> optional(text()),
    "userCookieId" -> optional(text()),
    "currency" -> optional(text())
  )((area, country, description,
     name, width, height, length, weight,
     price, units, ordermethod, ip, age, sex,
     userId, userCookieId, currency) =>
    Order(Timestamp.from(Instant.now()),
      area, country, description,
      name, width, height, length, weight,
      price, units, ordermethod, ip, age, sex,
      userId, userCookieId, currency, Option.empty))

  def order(params: MultiParams): Order =
    orderFormMapping.convert("", params, Messages())
}
