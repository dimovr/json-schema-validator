/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.api

import cats.effect._
import cats.implicits._
import com.knottech.jsonvalidator.models._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import sttp.model._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.codec.refined._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._

final class HelloWorld[F[_]: Concurrent: ContextShift: Timer] extends Http4sDsl[F] {
  final val message: NonEmptyString = "This is a fancy message directly from http4s! :-)"

  implicit def decodeGreetings: EntityDecoder[F, Greetings] = jsonOf
  implicit def encodeGreetings: EntityEncoder[F, Greetings] = jsonEncoderOf

  private val sayHello: HttpRoutes[F] = Http4sServerInterpreter[F]().toRoutes(HelloWorld.greetings) { name =>
    val greetings = (
      NonEmptyString.from(s"Hello ${name.show}!").toOption,
      NonEmptyString.from(s"Hello ${name.show}, live long and prosper!").toOption
    ).mapN { case (title, headings) =>
      Greetings(
        title = title,
        headings = headings,
        message = message
      )
    }
    Sync[F].delay(greetings.fold(StatusCode.BadRequest.asLeft[Greetings])(_.asRight[StatusCode]))
  }

  val routes: HttpRoutes[F] = sayHello

}

object HelloWorld {
  val example = Greetings(
    title = "Hello Kirk!",
    headings = "Hello Kirk, live long and prosper!",
    message = "This is some demo message..."
  )

  val greetings: Endpoint[NonEmptyString, StatusCode, Greetings, Any] =
    endpoint.get
      .in("hello")
      .in(query[NonEmptyString]("name"))
      .errorOut(statusCode)
      .out(jsonBody[Greetings].description("A JSON object demo").example(example))
      .description(
        "Returns a simple JSON object using the provided query parameter 'name' which must not be empty."
      )
}
