package com.itechart.bitools

import com.itechart.bitools.MetricAcceptor._
import org.scalatra._

class BiServlet extends ScalatraServlet {

  get("/") {
    redirect("/v1")
  }

  get("/:apiVersion") {
    val apiVersion = params("apiVersion")
    onApiVersion(apiVersion)

    if (params.contains("exception")) {
      val exception = params.getOrElse("exception", "")
      onException(apiVersion)
    }
    if (params.contains("latency")) {
      val latency = params("latency").toLong
      onLatency(latency)
    }

    views.html.hello()
  }
}
