package com.example

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.model.StatusCodes
import com.typesafe.config.ConfigFactory
import pdi.jwt.{JwtSprayJson, JwtAlgorithm}
import scala.util.{Success, Failure}
import java.time.Clock

object JwtAuthenticator {
  private val config = ConfigFactory.load()
  // Делаем публичным для доступа из StorageRoutes
  val secretKey = "your-secret-key-here" // Hard-coded for now
  val algorithm = JwtAlgorithm.HS256
  
  // Явно указываем Clock для методов, которые требуют его
  implicit val clock: Clock = Clock.systemUTC()

  def authenticate: Directive1[String] = 
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(header) if header.startsWith("Bearer ") =>
        val token = header.substring("Bearer ".length)
        JwtSprayJson.decode(token, secretKey, Seq(algorithm)) match {
          case Success(claim) => provide(claim.subject.getOrElse(""))
          case Failure(_) => 
            complete(StatusCodes.Unauthorized -> "Invalid or expired token")
        }
      case _ => 
        complete(StatusCodes.Unauthorized -> "Authorization token missing or malformed")
    }
}