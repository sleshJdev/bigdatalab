package controllers

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.{MessageDigest, NoSuchAlgorithmException}
import java.util.Base64

import javax.inject.{Inject, Singleton}
import play.api.http.HttpConfiguration.HttpConfigurationProvider
import play.api.mvc.DefaultJWTCookieDataCodec

@Singleton
class Encoder @Inject()(httConf: HttpConfigurationProvider) {
  private final val jwtCodec = DefaultJWTCookieDataCodec(
    httConf.get.secret, httConf.get.session.jwt)

  def sha512(data: String): String = try {
    val digest = MessageDigest.getInstance("SHA-512")
    val bytes = digest.digest(data.getBytes(StandardCharsets.UTF_8))
    new BigInteger(bytes).toString(16)
  } catch {
    case e: NoSuchAlgorithmException =>
      throw new IllegalArgumentException(e)
  }

  def encode(data: Map[String, String]): String = jwtCodec.encode(data)

  def decode(data: String): Map[String, String] = jwtCodec.decode(data)

  def toBase64(data: String): String =
    Base64.getEncoder.encodeToString(data.getBytes(StandardCharsets.UTF_8))

  def fromBase64(data: String) =
    new String(Base64.getDecoder.decode(data), StandardCharsets.UTF_8)
}
