package com.itechart.bitools

import com.itechart.bitools.Mappings._
import com.itechart.bitools.Tables._
import org.scalatra._
import org.scalatra.forms.FormSupport
import org.scalatra.i18n.I18nSupport
import org.scalatra.metrics.MetricsSupport
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class BiServlet extends ScalatraServlet
  with FutureSupport with MetricsSupport
  with FormSupport with I18nSupport {

  implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val logger: Logger = LoggerFactory.getLogger(getClass)

  get("/") {
    redirect("/v1")
  }

  get("/:apiVersion") {
    views.html.order(params("apiVersion"))
  }

  post("/:apiVersion/orders") {
    val apiVersion = params("apiVersion")
    val method = request.getMethod.toLowerCase()
    orders.save(order(multiParams)) onComplete {
      case Success(count) =>
        writeOrdersMetric(count, apiVersion, method)
        views.html.order(apiVersion)
      case Failure(e) =>
        logger.error(s"Error when saving order", e)
    }
  }

  get("/:apiVersion/orders/generate") {
    val count = params.getOrElse("count", "1").toInt
    val apiVersion = params("apiVersion")
    val method = request.getMethod.toLowerCase()
    orders.generate(count) onComplete {
      case Success(orders) =>
        writeOrdersMetric(count, apiVersion, method)
        logger.info(s"$count orders were generated: $orders")
      case Failure(e) =>
        logger.error(s"Error when generating orders", e)
    }
  }

  private def writeMetrics(): Unit = {
    val apiVersion = params.getOrElse("apiVersion", "v1")
    if (params.contains("exception")) {
      val exception = params.getOrElse("exception", "")
      logger.error("Fake exception", new Exception(exception))
      onException()
    }
    if (params.contains("latency")) {
      val latency = params("latency").toLong
      writeLatencyMetric(latency)
    }
  }

  override def error(handler: ErrorHandler): Unit = {
    super.error({
      case t =>
        logger.error("Unexpected error occurred", t)
        onException()
        throw t
    })
  }

  before() {
    logger.info(s"$request. Params: $multiParams")
  }

  after() {
    onRequest()
    writeMetrics()
  }

  def onRequest(): Unit =
    counter(s"request.${params.getOrElse("apiVersion", "v1")}.${request.getMethod.toLowerCase()}.all").inc()

  def onException(): Unit =
    counter(s"request.${params.getOrElse("apiVersion", "v1")}.${request.getMethod.toLowerCase()}.exceptions").inc()

  def writeLatencyMetric(latency: Long): Unit =
    meter(s"request.${params.getOrElse("apiVersion", "v1")}.${request.getMethod.toLowerCase()}.latency").mark(latency)

  def writeOrdersMetric(orders: Long, apiVersion: String, method: String): Unit =
    meter(s"orders.$apiVersion.$method.all").mark(orders)

  def onTimer(apiVersion: String = "v1", httpMethod: String = "get")(action: => Any): Unit =
    timer(s"request.$apiVersion.$httpMethod.latency")(action)
}
