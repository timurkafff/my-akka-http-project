package com.example

import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.RootJsonFormat

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  import com.example.Models._
  
  implicit val itemFormat: RootJsonFormat[Item] = jsonFormat2(Item)
  implicit val credentialsFormat: RootJsonFormat[Credentials] = jsonFormat2(Credentials)
}