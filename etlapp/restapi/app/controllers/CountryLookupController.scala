package controllers

import java.util.concurrent.{ThreadLocalRandom, TimeUnit}

import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

@Singleton
class CountryLookupController @Inject()(cc: ControllerComponents,
                                        encoder: Encoder)
  extends AbstractController(cc) {

  def countryLookUp(): Action[AnyContent] = Action {
    TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10))
    Ok.sendResource("data/country-lookup.csv.gz")
  }

}
