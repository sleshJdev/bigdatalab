package controllers

import java.time.Instant

import scala.collection.mutable

class PerMinuteRateLimiter(private val limit: Float) {
  private var hits = mutable.ListBuffer.empty[Long]

  def hit(): PerMinuteRateLimiter = {
    hits += Instant.now().toEpochMilli
    this
  }

  def isExceedLimit: Boolean = {
    val bottomLine = Instant.now().minusSeconds(60).toEpochMilli
    hits --= hits.filter(_ < bottomLine)
    hits.length.toFloat / 60 > limit
  }
}