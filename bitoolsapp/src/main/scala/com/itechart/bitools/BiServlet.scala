package com.itechart.bitools

import java.sql.Timestamp
import java.time.Instant

import com.itechart.bitools.MetricAcceptor._
import org.scalatra._
import org.scalatra.forms.{MappingValueType, SingleValueType, double, number, optional, text}
import org.scalatra.i18n.Messages

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class BiServlet extends ScalatraServlet with FutureSupport {
  implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val form: MappingValueType[Order] = forms.mapping(
    "fulldate" -> new SingleValueType[Timestamp]() {
      def convert(value: String, messages: Messages): Timestamp = Timestamp.from(Instant.now())
    },
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
    "ordermethod" -> optional(text())
  )(Order.apply)

  get("/") {
    redirect("/v1")
  }

  get("/:apiVersion") {
    val apiVersion = params.getOrElse("apiVersion", "v1")
    writeMetrics(apiVersion)
    views.html.order(apiVersion)
  }

  post("/:apiVersion/orders") {
    val apiVersion = params.getOrElse("apiVersion", "v1")
    writeMetrics(apiVersion)
    val order = form.convert("", multiParams, Messages())

    Tables.orders.save(order) onComplete {
      case Success(n) => {
        views.html.order(apiVersion)
      }
      case Failure(e) => {
        e.printStackTrace()
      }
    }
  }

  private def writeMetrics(apiVersion: String): Unit = {
    onApiVersion(apiVersion, request.getMethod.toLowerCase())

    if (params.contains("exception")) {
      val exception = params.getOrElse("exception", "")
      onException(apiVersion, request.getMethod.toLowerCase())
    }
    if (params.contains("latency")) {
      val latency = params("latency").toLong
      onLatency(latency, request.getMethod.toLowerCase())
    }
  }
}
