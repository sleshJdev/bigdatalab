package com.itechart.bitools

import com.typesafe.config.{Config, ConfigFactory}
import slick.util.ClassLoaderUtil

object ConfigProvider {
  val config: Config = ConfigFactory.load(ClassLoaderUtil.defaultClassLoader)
}
