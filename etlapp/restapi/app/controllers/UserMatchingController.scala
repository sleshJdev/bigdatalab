package controllers

import java.util.concurrent.ThreadLocalRandom

import javax.inject.{Inject, Singleton}
import play.api.http.HttpEntity
import play.api.libs.json.Json
import play.api.mvc._


@Singleton
class UserMatchingController @Inject()(cc: ControllerComponents,
                                       encoder: Encoder,
                                       repository: Repository)
  extends AbstractController(cc) {

  import Models._

  def matchUserId(id: Long): Action[AnyContent] =
    Action { implicit request =>
      request.headers.get(AUTHORIZATION)
        .map(encoder.decode)
        .flatMap(data => data.get("login"))
        .flatMap(repository.findUser)
        .map(user => respond {
          Ok(Json.toJson(generateUser(id.toString)))
        })
        .getOrElse(Unauthorized)
    }

  def matchUserCookie(cookie: String): Action[AnyContent] =
    Action { implicit request =>
      if (request.session.get("login").flatMap(repository.findUser).isEmpty) {
        Unauthorized
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
    respond {
      val user = generateUser(cookie)
      Ok(<user key="user.key" sex="{user.sex}" age="{user.age}"></user>)
    }
  }

  private def isProbability(percent: Double): Boolean =
    percent / 100.0 >= ThreadLocalRandom.current.nextDouble

  private def respond(task: => Result): Result = {
    if (isProbability(10)) {
      InternalServerError("Oops")
    } else {
      if (isProbability(10)) {
        TimeUnit.SECONDS.sleep(5)
      }
      task()
    }
  }
}
