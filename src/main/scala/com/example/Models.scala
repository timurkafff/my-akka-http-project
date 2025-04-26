package com.example

object Models {
  final case class Item(key: String, value: String)
  final case class Credentials(username: String, password: String)
}