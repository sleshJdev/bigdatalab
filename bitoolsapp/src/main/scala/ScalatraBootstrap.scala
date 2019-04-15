import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import com.codahale.metrics.MetricFilter
import com.codahale.metrics.graphite.{Graphite, GraphiteReporter}
import com.codahale.metrics.jvm.{GarbageCollectorMetricSet, MemoryUsageGaugeSet, ThreadStatesGaugeSet}
import com.itechart.bitools.ConfigProvider.config
import com.itechart.bitools.DatabaseProvider.db
import com.itechart.bitools._
import javax.servlet.ServletContext
import org.scalatra._
import org.scalatra.metrics.MetricsBootstrap

class ScalatraBootstrap extends LifeCycle with MetricsBootstrap {
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

  override def init(context: ServletContext) {
    context.mount(new BiServlet, "/*")

    reporter.start(1, TimeUnit.SECONDS)
  }

  override def destroy(context: ServletContext): Unit = {
    db.close()
    reporter.stop()
  }
}
