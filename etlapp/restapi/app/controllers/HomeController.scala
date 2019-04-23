package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */

@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               encoder: Encoder)
  extends AbstractController(cc) {

  def index(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.index())
  }

}
