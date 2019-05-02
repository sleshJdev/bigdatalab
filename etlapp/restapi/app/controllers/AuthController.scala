package controllers

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms.{mapping, _}
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class AuthController @Inject()(cc: ControllerComponents,
                               encoder: Encoder,
                               repository: Repository)
  extends AbstractController(cc) {

  import Models._

  val appUserForm = Form(
    mapping(
      "login" -> nonEmptyText,
      "password" -> nonEmptyText
    )(AppUser.apply)(AppUser.unapply)
  )

  def generateToken(): Action[AnyContent] =
    Action { implicit request =>
      request.headers.get(AUTHORIZATION)
        .map(_.split("""\s"""))
        .map({ case Array("Basic", loginAndPass) ⇒ loginAndPass })
        .map(encoder.fromBase64(_).split(":"))
        .map({ case Array(login, password) =>
          repository.findUser(login) match {
            case Some(dbuser) =>
              if (dbuser == AppUser(login, encoder.sha512(password))) {
                Ok(encoder.encode(Map("login" -> login)))
              } else {
                Unauthorized(Json.toJson(Message("Login or password are incorrect")))
              }
            case _ => Unauthorized(Json.toJson(Message("User not found")))
          }
        }).getOrElse(Unauthorized(Json.toJson(Message("Not authorized"))))
    }

  def signUp(): Action[AppUser] =
    Action(parse.form(appUserForm)) { implicit request: Request[AppUser] =>
      val user = request.body
      repository.findUser(user.login) match {
        case Some(_) => UnprocessableEntity(Json.toJson(Message(s"User ${user.login} already exists")))
        case None =>
          val id = repository.saveUser(user)
          Redirect(routes.ExchangeRateController.exchangeRates(), SEE_OTHER)
            .withSession("login" -> user.login)
      }
    }

  def signIn(): Action[AppUser] =
    Action(parse.form(appUserForm)) { implicit request: Request[AppUser] =>
      val user = request.body
      repository.findUser(user.login) match {
        case Some(dbuser) =>
          if (dbuser == user.copy(password = encoder.sha512(user.password))) {
            Redirect(routes.ExchangeRateController.exchangeRates(), SEE_OTHER)
              .withSession("login" -> dbuser.login)
          } else {
            Unauthorized(Json.toJson(Message("Login or password are incorrect")))
          }
        case _ => Unauthorized(Json.toJson(Message("User not found")))
      }
    }

}
