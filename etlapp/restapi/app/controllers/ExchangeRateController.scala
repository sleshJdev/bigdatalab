package controllers

import java.time.LocalTime

import javax.inject.Inject
import javax.inject.Singleton
import play.api.http.HttpConfiguration.HttpConfigurationProvider
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.math.BigDecimal.RoundingMode

@Singleton
class ExchangeRateController @Inject()(cc: ControllerComponents,
                                       httConf: HttpConfigurationProvider)
  extends AbstractController(cc) {

  def exchangeRates(): Action[AnyContent] = Action {
    val xxxRate = BigDecimal.apply(1.2 + LocalTime.now().getMinute / 60.0)
    val yyyRate = BigDecimal.apply(3.4 + LocalTime.now().getMinute / 60.0)
    val zzzRate = BigDecimal.apply(5.6 + LocalTime.now().getMinute / 60.0)
    Ok(views.html.exchangerates(
      xxxRate.setScale(2, RoundingMode.HALF_DOWN),
      yyyRate.setScale(2, RoundingMode.HALF_DOWN),
      zzzRate.setScale(2, RoundingMode.HALF_DOWN)))
  }

}
