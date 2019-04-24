package controllers

import java.util.concurrent.{ThreadLocalRandom, TimeUnit}

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

@Singleton
class CountryLookupController @Inject()(cc: ControllerComponents,
                                        repository: Repository)
  extends AbstractController(cc) {

  import Models._

  def countryLookUp(): Action[AnyContent] = Action { implicit request =>
    if (request.session.get("login").flatMap(repository.findUser).isEmpty) {
      Unauthorized(Json.toJson(Message("Not authorized")))
    } else {
      TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10))
      Ok.sendResource("data/country-lookup.csv.gz")
    }
  }

}
