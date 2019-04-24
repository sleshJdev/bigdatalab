package controllers

import javax.inject.{Inject, Singleton}
import play.api.db.Database
import play.api.http.SessionConfiguration
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */

@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               db: Database,
                               sessionConf: SessionConfiguration)
  extends AbstractController(cc) {

  def index(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.index())
  }

  def signIn(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.signin())
  }

  def signUp(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.signup())
  }

  def logOut(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.index()).discardingCookies(DiscardingCookie(sessionConf.cookieName))
  }

}
