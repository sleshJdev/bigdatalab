package controllers

import java.nio.charset.StandardCharsets
import java.util.Base64

import javax.inject.{Inject, Singleton}
import play.api.http.HttpConfiguration.HttpConfigurationProvider
import play.api.mvc.DefaultJWTCookieDataCodec

@Singleton
class Encoder @Inject()(httConf: HttpConfigurationProvider) {
  private final val jwtCodec = DefaultJWTCookieDataCodec(
    httConf.get.secret, httConf.get.session.jwt)

  def encode(data: Map[String, String]): String = jwtCodec.encode(data)

  def decode(data: String): Map[String, String] = jwtCodec.decode(data)

  def toBase64(data: String): String =
    Base64.getEncoder.encodeToString(data.getBytes(StandardCharsets.UTF_8))

  def fromBase64(data: String) =
    new String(Base64.getDecoder.decode(data), StandardCharsets.UTF_8)
}
