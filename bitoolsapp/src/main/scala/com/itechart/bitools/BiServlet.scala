package com.itechart.bitools

import org.scalatra._

class BiServlet extends ScalatraServlet {

  get("/") {
    views.html.hello()
  }

}
