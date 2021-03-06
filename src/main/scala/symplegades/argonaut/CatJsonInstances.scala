package symplegades.argonaut

import argonaut._

import cats.Show

object CatJsonInstances {
  implicit val JsonInstances: Show[Json] =
    new Show[Json] {
      override def show(a: Json): String = Show.fromToString.show(a)
    }
}