package controllers

import java.util.concurrent.ThreadLocalRandom

import com.google.inject._
import play.api.libs.json._
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */

@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               encoder: Encoder)
  extends AbstractController(cc) {

  import InMemoryStorage._
  import Models._

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.index())
  }

  def generateToke(): Action[AnyContent] =
    Action { implicit request =>
      request.headers.get(AUTHORIZATION)
        .map(_.split("""\s"""))
        .map({ case Array("Basic", loginAndPass) ⇒ loginAndPass })
        .map(encoder.fromBase64(_).split(":"))
        .map({ case Array(userLogin, userPassword) => {
          users.get(userLogin) match {
            case Some(AppUser(login, passwordHash)) =>
              if (userLogin == login && passwordHash == encoder.toBase64(userPassword)) {
                Ok(encoder.encode(Map("login" -> login)))
              } else {
                Unauthorized(Json.toJson(Message("Login or password are incorrect")))
              }
            case _ => Unauthorized(Json.toJson(Message("User not found")))
          }
        }}).getOrElse(Unauthorized)
    }

  def matchUserId(id: Long): Action[AnyContent] =
    Action { implicit request =>
      request.headers.get(AUTHORIZATION)
        .map(encoder.decode)
        .flatMap(data => data.get("login"))
        .flatMap(users.get)
        .map(user => {
          if (isProbability(10)) {
            InternalServerError("Oops")
          } else {
            Ok(Json.toJson(generateUser(id.toString)))
          }
        }).getOrElse(Unauthorized)
    }

  private def isProbability(percent: Double): Boolean =
    percent / 100.0 >= ThreadLocalRandom.current.nextDouble

}
