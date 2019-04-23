package controllers

import java.time.LocalTime

import com.google.inject.{Inject, Singleton}
import play.api.http.HttpConfiguration.HttpConfigurationProvider
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.math.BigDecimal.RoundingMode

@Singleton
class ExchangeRateController @Inject()(cc: ControllerComponents,
                                       httConf: HttpConfigurationProvider)
  extends AbstractController(cc) {

  def exchangeRates(): Action[AnyContent] = Action {
    val rate = BigDecimal.apply(3 + LocalTime.now().getMinute / 60.0)
    Ok(views.html.exchangerates(rate.setScale(2, RoundingMode.HALF_DOWN)))
  }

}
