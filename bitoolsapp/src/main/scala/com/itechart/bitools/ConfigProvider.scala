package com.itechart.bitools

import com.typesafe.config.{Config, ConfigFactory}

object ConfigProvider {
  val config: Config = ConfigFactory.load()
}
