package com.example

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import scala.collection.concurrent.TrieMap
import scala.util.{Success, Failure}
import spray.json._
import scala.concurrent.ExecutionContextExecutor
import java.time.Clock

class StorageRoutes()(implicit system: ActorSystem[_]) {
  import JsonSupport._
  import com.example.Models._
  
  implicit val ec: ExecutionContextExecutor = system.executionContext
  // Явно импортируем Clock
  implicit val clock: Clock = Clock.systemUTC()

  private val store = new TrieMap[String, String]()

  private val loginRoute: Route = path("login") {
    post {
      entity(as[Credentials]) { creds =>
        if (creds.username == "admin" && creds.password == "password") {
          val claim = pdi.jwt.JwtClaim(subject = Some(creds.username))
            .issuedNow
            .expiresIn(3600)
          val token = pdi.jwt.JwtSprayJson.encode(claim, JwtAuthenticator.secretKey, JwtAuthenticator.algorithm)
          import spray.json.DefaultJsonProtocol._
          complete(StatusCodes.OK -> Map("token" -> token).toJson.toString)
        } else {
          complete(StatusCodes.Unauthorized -> "Invalid credentials")
        }
      }
    }
  }

  private val storageRoute: Route = pathPrefix("storage") {
    JwtAuthenticator.authenticate { username =>
      concat(
        get {
          path(Segment) { key =>
            store.get(key) match {
              case Some(value) => complete(Item(key, value))
              case None        => complete(StatusCodes.NotFound -> s"Key '$key' not found")
            }
          }
        },
        post {
          entity(as[Item]) { item =>
            store.put(item.key, item.value)
            complete(StatusCodes.Created -> s"Stored key '${item.key}'")
          }
        },
        delete {
          path(Segment) { key =>
            store.remove(key) match {
              case Some(_) => complete(StatusCodes.OK -> s"Deleted key '$key'")
              case None    => complete(StatusCodes.NotFound -> s"Key '$key' not found")
            }
          }
        }
      )
    }
  }

  val routes: Route = loginRoute ~ storageRoute
}