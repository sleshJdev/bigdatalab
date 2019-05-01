package controllers

import java.sql.Statement

import javax.inject.{Inject, Singleton}
import play.api.db.Database

import scala.util.{Failure, Success, Try}

@Singleton
class Repository @Inject()(db: Database, encoder: Encoder) {

  def findUser(login: String): Option[AppUser] = db.withTransaction(conn => {
    loan(conn.prepareStatement("SELECT LOGIN, PASSWORD FROM APPUSER WHERE LOGIN = ?")) { stmt =>
      stmt.setString(1, login)
      loan(stmt.executeQuery()) { rs =>
        if (!rs.next()) {
          None
        } else {
          val user = AppUser(
            rs.getString("LOGIN"),
            rs.getString("PASSWORD"))
          if (rs.next()) {
            throw new IllegalStateException("No uniq result")
          }
          Some(user)
        }
      }
    }
  })

  def saveUser(user: AppUser): Long = db.withTransaction(conn => {
    loan(conn.prepareStatement(
      "INSERT INTO APPUSER(LOGIN, PASSWORD) VALUES(?, ?)",
      Statement.RETURN_GENERATED_KEYS)) { stmt =>
      stmt.setString(1, user.login)
      stmt.setString(2, encoder.sha512(user.password))
      stmt.executeUpdate()
    }
  })

  private def loan[A <: AutoCloseable, B](resource: A)(block: A => B): B = {
    Try(block(resource)) match {
      case Success(result) =>
        resource.close()
        result
      case Failure(e) =>
        resource.close()
        throw e
    }
  }
}
