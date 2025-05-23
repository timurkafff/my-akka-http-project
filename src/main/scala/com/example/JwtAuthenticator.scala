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
  val secretKey = "Iw1t2hWPH5Rb69ZR0woGEWMTLF4Xp9fxIQwIhvwdNh8="
  val algorithm = JwtAlgorithm.HS256
  
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