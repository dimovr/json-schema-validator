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
import com.knottech.jsonvalidator.models.JsonSchemaResponse._
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

  implicit def decodeNonEmptyString[F[_]: Sync]: EntityDecoder[F, NonEmptyString] =
    EntityDecoder.text.map(NonEmptyString.unsafeFrom)

  // GET /schema/{schema_id}
  test("when parameter 'schema_id' is missing for GET /schema/{schema_id}") {
    val expectedStatusCode = HttpStatus.NotFound

    Uri.fromString("/schema") match {
      case Left(_) =>
        fail("Could not generate valid URI!")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaService.stub).routes)
        val request = Request[IO](
          method = Method.GET,
          uri = uri
        )
        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[String]
        } yield (result.status, body)
        test.assertEquals((expectedStatusCode, "Not found"))
    }
  }

  test("when parameter 'schema_id' is invalid for GET /schema/{schema_id}") {
    val expectedStatusCode = HttpStatus.NotFound

    Uri.fromString("/schema/") match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaService.stub).routes)
        val request = Request[IO](
          method = Method.GET,
          uri = uri
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

  test("when parameter 'schema_id' is valid for GET /schema/{schema_id}") {
    val expectedStatusCode = HttpStatus.Ok

    val schemaId: NonEmptyString = "config-json"
    val expectedSchemaResponse = Json.fromString(
      """{ "$schema": "http://json-schema.org/draft-04/schema#", "type": "object", "properties": { "source": { "type": "string" }, "destination": { "type": "string" }, "timeout": { "type": "integer", "minimum": 0, "maximum": 32767 }, "chunks": { "type": "object", "properties": { "size": { "type": "integer" }, "number": { "type": "integer" } }, "required": ["size"] } }, "required": ["source", "destination"] }"""
    )

    Uri.fromString(Uri.encode(s"/schema/$schemaId")) match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaService.stub[IO]).routes)
        val request = Request[IO](
          method = Method.GET,
          uri = uri
        )
        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[NonEmptyString]
        } yield (result.status, Json.fromString(body))
        test.assertEquals((expectedStatusCode, expectedSchemaResponse))
    }
  }


  // POST /schema/{schema_id}

  implicit def decodeUploadSuccess: EntityDecoder[IO, UploadSuccess] = jsonOf
  implicit def decodeUploadError: EntityDecoder[IO, UploadError] = jsonOf

  test("when parameter 'schema_id' is missing for POST /schema/{schema_id}") {
    val expectedStatusCode = HttpStatus.NotFound

    Uri.fromString("/schema") match {
      case Left(_) =>
        fail("Could not generate valid URI!")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaService.stub).routes)
        val request = Request[IO](
          method = Method.POST,
          uri = uri
        )
        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[String]
        } yield (result.status, body)
        test.assertEquals((expectedStatusCode, "Not found"))
    }
  }

  test("when parameter 'schema_id' is invalid for POST /schema/{schema_id}") {
    val expectedStatusCode = HttpStatus.NotFound

    Uri.fromString("/schema/") match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaService.stub).routes)
        val request = Request[IO](
          method = Method.POST,
          uri = uri
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

  // todo: add once validation is implemented
//  test("when parameter 'schema_id' is valid BUT body is INVALID for POST /schema/{schema_id}") {
//    val expectedStatusCode = HttpStatus.Ok
//
//    val schemaId: NonEmptyString = "config-json"
//    val schemaBody = "invalid-json"
//
//    Uri.fromString(Uri.encode(s"/schema/$schemaId")) match {
//      case Left(e) =>
//        fail(s"Could not generate valid URI: $e")
//      case Right(uri) =>
//        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaService.stub[IO]).routes)
//        val request = Request[IO](
//          method = Method.POST,
//          uri = uri,
//        ).withEntity(schemaBody)
//
//        val expectedResponse = UploadSuccess(schemaId)
//
//        val response = service.orNotFound.run(request)
//        val test = for {
//          result <- response
//          body   <- result.as[UploadSuccess]
//        } yield (result.status, body)
//        test.assertEquals((expectedStatusCode, expectedResponse))
//    }
//  }

  test("when parameter 'schema_id' is valid and body is valid for POST /schema/{schema_id}") {
    val expectedStatusCode = HttpStatus.Ok

    val schemaId: NonEmptyString = "config-json"
    val schemaBody =
      """{ "$schema": "http://json-schema.org/draft-04/schema#", "type": "object", "properties": { "source": { "type": "string" }, "destination": { "type": "string" }, "timeout": { "type": "integer", "minimum": 0, "maximum": 32767 }, "chunks": { "type": "object", "properties": { "size": { "type": "integer" }, "number": { "type": "integer" } }, "required": ["size"] } }, "required": ["source", "destination"] }"""

    Uri.fromString(Uri.encode(s"/schema/$schemaId")) match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaService.stub[IO]).routes)
        val request = Request[IO](
          method = Method.POST,
          uri = uri,
        ).withEntity(schemaBody)

        val expectedResponse = UploadSuccess(schemaId)

        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[UploadSuccess]
        } yield (result.status, body)
        test.assertEquals((expectedStatusCode, expectedResponse))
    }
  }

  // POST /validate/{schema_id}

  implicit def decodeValidationSuccess: EntityDecoder[IO, ValidationSuccess] = jsonOf
  implicit def decodeValidationError: EntityDecoder[IO, ValidationError] = jsonOf

  test("when parameter 'schema_id' is missing for POST /validate/{schema_id}") {
    val expectedStatusCode = HttpStatus.NotFound

    Uri.fromString("/schema") match {
      case Left(_) =>
        fail("Could not generate valid URI!")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaService.stub).routes)
        val request = Request[IO](
          method = Method.POST,
          uri = uri
        )
        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[String]
        } yield (result.status, body)
        test.assertEquals((expectedStatusCode, "Not found"))
    }
  }

  test("when parameter 'schema_id' is invalid for POST /validate/{schema_id}") {
    val expectedStatusCode = HttpStatus.NotFound

    Uri.fromString("/schema/") match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaService.stub).routes)
        val request = Request[IO](
          method = Method.POST,
          uri = uri
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

  // todo: add once validation is implemented
  //  test("when parameter 'schema_id' is valid BUT body is INVALID for POST /validate/{schema_id}") {
  //    val expectedStatusCode = HttpStatus.Ok
  //
  //    val schemaId: NonEmptyString = "config-json"
  //    val schemaBody = "invalid-json"
  //
  //    Uri.fromString(Uri.encode(s"/schema/$schemaId")) match {
  //      case Left(e) =>
  //        fail(s"Could not generate valid URI: $e")
  //      case Right(uri) =>
  //        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaService.stub[IO]).routes)
  //        val request = Request[IO](
  //          method = Method.POST,
  //          uri = uri,
  //        ).withEntity(schemaBody)
  //
  //        val expectedResponse = UploadSuccess(schemaId)
  //
  //        val response = service.orNotFound.run(request)
  //        val test = for {
  //          result <- response
  //          body   <- result.as[UploadSuccess]
  //        } yield (result.status, body)
  //        test.assertEquals((expectedStatusCode, expectedResponse))
  //    }
  //  }

  test("when parameter 'schema_id' is valid and body is valid for POST /validate/{schema_id}") {
    val expectedStatusCode = HttpStatus.Ok

    val schemaId: NonEmptyString = "config-json"
    val schemaBody =
      """{ "$schema": "http://json-schema.org/draft-04/schema#", "type": "object", "properties": { "source": { "type": "string" }, "destination": { "type": "string" }, "timeout": { "type": "integer", "minimum": 0, "maximum": 32767 }, "chunks": { "type": "object", "properties": { "size": { "type": "integer" }, "number": { "type": "integer" } }, "required": ["size"] } }, "required": ["source", "destination"] }"""

    Uri.fromString(Uri.encode(s"/schema/$schemaId")) match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaService.stub[IO]).routes)
        val request = Request[IO](
          method = Method.POST,
          uri = uri,
        ).withEntity(schemaBody)

        val expectedResponse = UploadSuccess(schemaId)

        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[UploadSuccess]
        } yield (result.status, body)
        test.assertEquals((expectedStatusCode, expectedResponse))
    }
  }

}
