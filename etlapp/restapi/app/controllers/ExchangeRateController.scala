package controllers

import java.time.LocalTime

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.math.BigDecimal.RoundingMode

@Singleton
class ExchangeRateController @Inject()(cc: ControllerComponents,
                                       repository: Repository)
  extends AbstractController(cc) {

  import Models._

  def exchangeRates(): Action[AnyContent] = Action { implicit request =>
    if (request.session.get("login").flatMap(repository.findUser).isEmpty) {
      Unauthorized(Json.toJson(Message("Not authorized")))
    } else {
      val xxxRate = BigDecimal(1.2 + LocalTime.now().getMinute / 60.0)
      val yyyRate = BigDecimal(3.4 + LocalTime.now().getMinute / 60.0)
      val zzzRate = BigDecimal(5.6 + LocalTime.now().getMinute / 60.0)
      Ok(views.html.exchangerates(
        xxxRate.setScale(2, RoundingMode.HALF_DOWN),
        yyyRate.setScale(2, RoundingMode.HALF_DOWN),
        zzzRate.setScale(2, RoundingMode.HALF_DOWN)))
    }
  }

}
