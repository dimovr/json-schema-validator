/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.api

import cats.effect._
import com.github.fge.jsonschema.SchemaVersion
import com.knottech.jsonvalidator.SchemaValidator
import com.knottech.jsonvalidator.db.SchemaRepo
import com.knottech.jsonvalidator.models.{JsonSchema, SchemaId}
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

final class JsonSchemaAPITest extends CatsEffectSuite {

  implicit def decodeNonEmptyString[F[_]: Sync]: EntityDecoder[F, NonEmptyString] =
    EntityDecoder.text.map(NonEmptyString.unsafeFrom)

  private def schemaVersion = SchemaVersion.DRAFTV4
  private def validator = SchemaValidator[IO](schemaVersion)
  
  // GET /schema/{schema_id}
  test("when parameter 'schema_id' is missing for GET /schema/{schema_id}") {
    val expectedStatusCode = HttpStatus.NotFound

    Uri.fromString("/schema") match {
      case Left(_) =>
        fail("Could not generate valid URI!")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaRepo.stub, validator).routes)
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
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaRepo.stub, validator).routes)
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

    val schemaId: SchemaId = "config-json"
    val schema: JsonSchema =
      """{ "$schema": "http://json-schema.org/draft-04/schema#", "type": "object", "properties": { "source": { "type": "string" }, "destination": { "type": "string" }, "timeout": { "type": "integer", "minimum": 0, "maximum": 32767 }, "chunks": { "type": "object", "properties": { "size": { "type": "integer" }, "number": { "type": "integer" } }, "required": ["size"] } }, "required": ["source", "destination"] }"""
    val expectedSchemaResponse = Json.fromString(schema)

    Uri.fromString(Uri.encode(s"/schema/$schemaId")) match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(uri) =>
        val repo = SchemaRepo.stub[IO]
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](repo, validator).routes)
        val request = Request[IO](
          method = Method.GET,
          uri = uri
        )
        val response = service.orNotFound.run(request)
        val test = for {
          _ <- repo.upsert(schemaId, schema)
          result <- response
          body   <- result.as[JsonSchema]
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
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaRepo.stub, validator).routes)
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
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaRepo.stub, validator).routes)
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

  test("when parameter 'schema_id' is valid BUT body is INVALID for POST /schema/{schema_id}") {
    val expectedStatusCode = HttpStatus.BadRequest

    val schemaId: SchemaId= "config-json"
    val schemaBody = "invalid-json"

    Uri.fromString(Uri.encode(s"/schema/$schemaId")) match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaRepo.stub, validator).routes)
        val request = Request[IO](
          method = Method.POST,
          uri = uri,
        ).withEntity(schemaBody)

        val expectedResponse = UploadError(schemaId, "upload failed")

        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[UploadError]
        } yield (result.status, body)
        test.assertEquals((expectedStatusCode, expectedResponse))
    }
  }

  test("when parameter 'schema_id' is valid and body is valid for POST /schema/{schema_id}") {
    val expectedStatusCode = HttpStatus.Created

    val schemaId: SchemaId= "config-json"
    val schemaBody =
      """{ "$schema": "http://json-schema.org/draft-04/schema#", "type": "object", "properties": { "source": { "type": "string" }, "destination": { "type": "string" }, "timeout": { "type": "integer", "minimum": 0, "maximum": 32767 }, "chunks": { "type": "object", "properties": { "size": { "type": "integer" }, "number": { "type": "integer" } }, "required": ["size"] } }, "required": ["source", "destination"] }"""

    Uri.fromString(Uri.encode(s"/schema/$schemaId")) match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaRepo.stub, validator).routes)
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

    Uri.fromString("/validate") match {
      case Left(_) =>
        fail("Could not generate valid URI!")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaRepo.stub, validator).routes)
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

    Uri.fromString("/validate/") match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaRepo.stub, validator).routes)
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

  test("when parameter 'schema_id' is valid and body is NOT a valid json for POST /validate/{schema_id}") {
    val expectedStatusCode = HttpStatus.BadRequest

    val schemaId: SchemaId= "config-json"
    val schemaBody = "abc"

    Uri.fromString(Uri.encode(s"/validate/$schemaId")) match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(uri) =>
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](SchemaRepo.stub, validator).routes)
        val request = Request[IO](
          method = Method.POST,
          uri = uri,
        ).withEntity(schemaBody)

        val expectedResponse = ValidationError(schemaId, "validation failed")

        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[ValidationError]
        } yield (result.status, body)
        test.assertEquals((expectedStatusCode, expectedResponse))
    }
  }
  test("when parameter 'schema_id' is valid and body is valid for POST /validate/{schema_id}") {
    val expectedStatusCode = HttpStatus.Created

    val schemaId: SchemaId = "config-json"
    val document = """{ "source": "/home/alice/image.iso", "destination": "/mnt/storage", "timeout": null, "chunks": { "size": 1024, "number": null } }"""

    Uri.fromString(Uri.encode(s"/validate/$schemaId")) match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(uri) =>
        val repo = SchemaRepo.stub[IO]
        def service: HttpRoutes[IO] = Router("/" -> new JsonSchemaAPI[IO](repo, validator).routes)
        val request = Request[IO](
          method = Method.POST,
          uri = uri,
        ).withEntity(document)

        val expectedResponse = ValidationSuccess(schemaId)

        val response = service.orNotFound.run(request)
        val schema: JsonSchema =
          """{ "$schema": "http://json-schema.org/draft-04/schema#", "type": "object", "properties": { "source": { "type": "string" }, "destination": { "type": "string" }, "timeout": { "type": "integer", "minimum": 0, "maximum": 32767 }, "chunks": { "type": "object", "properties": { "size": { "type": "integer" }, "number": { "type": "integer" } }, "required": ["size"] } }, "required": ["source", "destination"] }"""

        val test = for {
          _      <- repo.upsert(schemaId, schema)
          result <- response
          body   <- result.as[ValidationSuccess]
        } yield (result.status, body)
        test.assertEquals((expectedStatusCode, expectedResponse))
    }
  }

}
