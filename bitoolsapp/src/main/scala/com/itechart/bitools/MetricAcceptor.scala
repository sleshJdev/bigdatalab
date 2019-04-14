package com.itechart.bitools

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import com.codahale.metrics.MetricFilter
import com.codahale.metrics.graphite.{Graphite, GraphiteReporter}
import com.codahale.metrics.jvm.{GarbageCollectorMetricSet, MemoryUsageGaugeSet, ThreadStatesGaugeSet}
import com.itechart.bitools.ConfigProvider.config
import nl.grons.metrics4.scala.DefaultInstrumented

object MetricAcceptor extends DefaultInstrumented {
  val metricsPrefix: String = config.getString("metrics.prefix")
  val metricsHost: String = config.getString("metrics.server.address")
  val metricsPort: Int = config.getInt("metrics.server.port")

  metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet())
  metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet())
  metricRegistry.register("jvm.thread.state", new ThreadStatesGaugeSet())

  val graphite = new Graphite(new InetSocketAddress(metricsHost, metricsPort))
  val reporter: GraphiteReporter = GraphiteReporter.forRegistry(metricRegistry)
    .prefixedWith(s"$metricsPrefix.${java.net.InetAddress.getLocalHost.getHostName}")
    .convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .filter(MetricFilter.ALL)
    .build(graphite)

  reporter.start(1, TimeUnit.SECONDS)

  def inc(name: String): Unit = metrics.counter(name).inc()

  def onApiVersion(apiVersion: String = "v1", httpMethod: String = "get"): Unit =
    inc(s"request.$apiVersion.$httpMethod.all")

  def onException(apiVersion: String, httpMethod: String = "get"): Unit =
    inc(s"request.$apiVersion.$httpMethod.exceptions")

  def onLatency(latency: Long, apiVersion: String = "v1", httpMethod: String = "get"): Unit =
    metrics.meter(s"request.$apiVersion.$httpMethod.latency").mark(latency)

  def onTimer(apiVersion: String = "v1", httpMethod: String = "get")(action: => Any): Unit =
    metrics.timer(s"request.$apiVersion.$httpMethod.latency").time(action)
}
