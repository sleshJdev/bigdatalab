
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/slesh/workspace/bigdatalab/bigdatalab/etlapp/restapi/conf/routes
// @DATE:Mon Apr 22 03:38:28 MSK 2019


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
