package controllers

import java.util.concurrent.{ThreadLocalRandom, TimeUnit}

import javax.inject.{Inject, Singleton}
import play.api.http.HttpEntity
import play.api.libs.json.Json
import play.api.mvc._

import scala.collection.mutable


@Singleton
class UserMatchingController @Inject()(cc: ControllerComponents,
                                       encoder: Encoder,
                                       repository: Repository)
  extends AbstractController(cc) {

  import Models._

  private final val RATE_LIMIT_PER_SECOND = 1
  private final val rates = mutable.Map[AppUser, PerMinuteRateLimiter]()

  def matchUserId(id: Long): Action[AnyContent] =
    Action { implicit request =>
      request.headers.get(AUTHORIZATION)
        .map(encoder.decode)
        .flatMap(data => data.get("login"))
        .flatMap(repository.findUser)
        .map(user => respond(user) {
          Ok(Json.toJson(generateUser(id.toString)))
        })
        .getOrElse(Json.toJson(Message("Not authorized")))
    }

  def matchUserCookie(cookie: String): Action[AnyContent] =
    Action { implicit request =>
      request.session.get("login")
        .flatMap(repository.findUser)
        .map(user => if (isProbability(10)) {
          Result(header = ResponseHeader(MOVED_PERMANENTLY,
            Map(LOCATION -> s"/v2/users/cookie/$cookie")),
            body = HttpEntity.NoEntity)
        } else {
          handleUserCookieMatching(user, cookie)
        })
        .getOrElse(Json.toJson(Message("Not authorized")))
    }

  def handleUserCookieRedirect(cookie: String): Action[AnyContent] =
    Action { implicit request =>
      request.session.get("login")
        .flatMap(repository.findUser)
        .map(user => handleUserCookieMatching(user, cookie))
        .getOrElse(Json.toJson(Message("Not authorized")))
    }

  private def handleUserCookieMatching(user: AppUser, cookie: String)(implicit request: Request[AnyContent]): Result = {
    val userInfo = generateUser(cookie)
    respond(user) {
      Ok(<user>
        <key>
          {userInfo.key}
        </key>
        <sex>
          {userInfo.sex}
        </sex>
        <age>
          {userInfo.age}
        </age>
      </user>)
    }
  }

  private def isProbability(percent: Double): Boolean =
    percent / 100.0 >= ThreadLocalRandom.current.nextDouble

  private def respond(user: AppUser)(resultSupplier: => Result): Result = {
    val limiter = rates.getOrElseUpdate(user, new PerMinuteRateLimiter(RATE_LIMIT_PER_SECOND))
    if (limiter.hit().isExceedLimit) {
      TooManyRequests(s"You exceed your rate limit - $RATE_LIMIT_PER_SECOND rps")
    } else if (isProbability(10)) {
      InternalServerError("Oops")
    } else {
      if (isProbability(10)) {
        TimeUnit.SECONDS.sleep(5)
      }
      resultSupplier
    }
  }
}
