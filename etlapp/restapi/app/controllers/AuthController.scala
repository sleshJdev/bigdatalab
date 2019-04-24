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

  def generateToke(): Action[AnyContent] =
    Action { implicit request =>
      request.headers.get(AUTHORIZATION)
        .map(_.split("""\s"""))
        .map({ case Array("Basic", loginAndPass) ⇒ loginAndPass })
        .map(encoder.fromBase64(_).split(":"))
        .map({ case Array(userLogin, userPassword) =>
          repository.findUser(userLogin) match {
            case Some(AppUser(login, passwordHash)) =>
              if (userLogin == login && passwordHash == encoder.toBase64(userPassword)) {
                Ok(encoder.encode(Map("login" -> login)))
              } else {
                Unauthorized(Json.toJson(Message("Login or password are incorrect")))
              }
            case _ => Unauthorized(Json.toJson(Message("User not found")))
          }
        }).getOrElse(Unauthorized)
    }

  def signUp(): Action[AppUser] =
    Action(parse.form(appUserForm)) { implicit request: Request[AppUser] =>
      val user = request.body
      repository.findUser(user.login) match {
        case Some(_) => UnprocessableEntity(Json.toJson(Message(s"User ${user.login} already exists")))
        case None =>
          val id = repository.saveUser(user)
          Ok(s"{id: $id}").withSession("login" -> user.login)
      }
    }

  def signIn(): Action[AppUser] =
    Action(parse.form(appUserForm)) { implicit request: Request[AppUser] =>
      val user = request.body
      repository.findUser(user.login) match {
        case Some(AppUser(login, passwordHash)) =>
          if (user.login == login && passwordHash == encoder.toBase64(user.password)) {
            Status(OK).withSession("login" -> login)
          } else {
            Unauthorized(Json.toJson(Message("Login or password are incorrect")))
          }
        case _ => Unauthorized(Json.toJson(Message("User not found")))
      }
    }

}
