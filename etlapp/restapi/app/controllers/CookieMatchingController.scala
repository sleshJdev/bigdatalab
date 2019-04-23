package controllers

import java.util.concurrent.ThreadLocalRandom

import javax.inject.Inject
import javax.inject.Singleton
import play.api.http.HttpEntity
import play.api.mvc._


@Singleton
class CookieMatchingController @Inject()(cc: ControllerComponents,
                                         encoder: Encoder)
  extends AbstractController(cc) {

  import InMemoryStorage._
  import Models._

  def matchUserCookie(cookie: String): Action[AnyContent] =
    Action { implicit request =>
      if (!request.session.get("login").exists(users.contains)) {
        Status(UNAUTHORIZED)
      } else if (isProbability(10)) {
        Result(
          header = ResponseHeader(MOVED_PERMANENTLY,
            Map(LOCATION -> s"/v2/users/cookie/$cookie")),
          body = HttpEntity.NoEntity)
      } else {
        handleUserCookieMatching(cookie)
      }
    }

  def handleUserCookieRedirect(cookie: String): Action[AnyContent] =
    Action { implicit request =>
      handleUserCookieMatching(cookie)
    }

  private def handleUserCookieMatching(cookie: String)(implicit request: Request[AnyContent]): Result = {
    if (isProbability(10)) {
      InternalServerError("Oops")
    } else {
      val user = generateUser(cookie)
      Ok(<user key="user.key">
        <sex>
          {user.sex}
        </sex>
        <age>
          {user.age}
        </age>
      </user>)
    }
  }

  private def isProbability(percent: Double): Boolean =
    percent / 100.0 >= ThreadLocalRandom.current.nextDouble
}
