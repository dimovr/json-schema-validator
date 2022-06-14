/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.api

import cats.effect._
import com.knottech.jsonvalidator.SchemaService
import com.knottech.jsonvalidator.models._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Json
import munit._
import org.http4s._
import org.http4s.{Status => HttpStatus}
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.server.Router

class JsonSchemaAPITest extends CatsEffectSuite {

  implicit def decodeJsonSchemaResponse: EntityDecoder[IO, JsonSchemaResponse] = jsonOf
  implicit def decodeDummyResponse: EntityDecoder[IO, DummyResponse] = jsonOf

  test("when parameter 'schema_id' is missing") {
    val expectedStatusCode = HttpStatus.NotFound

    Uri.fromString("/schema") match {
      case Left(_) =>
        fail("Could not generate valid URI!")
      case Right(u) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaService.stub).routes)
        val request = Request[IO](
          method = Method.GET,
          uri = u
        )
        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[String]
        } yield (result.status, body)
        test.assertEquals((expectedStatusCode, "Not found"))
    }
  }

  test("when parameter 'schema_id' is invalid") {
    val expectedStatusCode = HttpStatus.NotFound

    Uri.fromString("/schema/") match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(u) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaService.stub).routes)
        val request = Request[IO](
          method = Method.GET,
          uri = u
        )
        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[String]
        } yield (result.status, body)
        test.assertEquals(
          (expectedStatusCode, "Not found")
        )
    }
  }

  test("when parameter 'schema_id' is valid") {
    val expectedStatusCode = HttpStatus.Ok

    val schemaId: NonEmptyString = "config-json"
    val expectedSchemaResponse = Json.fromString(
      """{ "$schema": "http://json-schema.org/draft-04/schema#", "type": "object", "properties": { "source": { "type": "string" }, "destination": { "type": "string" }, "timeout": { "type": "integer", "minimum": 0, "maximum": 32767 }, "chunks": { "type": "object", "properties": { "size": { "type": "integer" }, "number": { "type": "integer" } }, "required": ["size"] } }, "required": ["source", "destination"] }"""
    )

    Uri.fromString(Uri.encode(s"/schema/$schemaId")) match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(u) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaService.stub[IO]).routes)
        val request = Request[IO](
          method = Method.GET,
          uri = u
        )
        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[DummyResponse]
        } yield (result.status, Json.fromString(body.schema))
        test.assertEquals((expectedStatusCode, expectedSchemaResponse))
    }
  }
}
