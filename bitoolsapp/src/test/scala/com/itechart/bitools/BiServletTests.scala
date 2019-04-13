package com.itechart.bitools

import org.scalatra.test.scalatest._

class BiServletTests extends ScalatraFunSuite {

  addServlet(classOf[BiServlet], "/*")

  test("GET / on BiServlet should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

}
