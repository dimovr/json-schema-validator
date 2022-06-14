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
import com.knottech.jsonvalidator.SchemaService
import com.knottech.jsonvalidator.models.JsonSchemaResponse.UploadSuccess
import com.knottech.jsonvalidator.models._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import sttp.model._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.codec.refined._
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._
import sttp.tapir.openapi.circe.yaml._

final class JsonSchemaAPI[F[_]: Concurrent: ContextShift: Timer](
    service: SchemaService
) extends Http4sDsl[F] {

  implicit def decodeJsonSchemaResponse: EntityDecoder[F, JsonSchemaResponse] = jsonOf
  implicit def encodeJsonSchemaResponse: EntityEncoder[F, JsonSchemaResponse] = jsonEncoderOf

  private val getSchema: HttpRoutes[F] = Http4sServerInterpreter[F]().toRoutes(JsonSchemaAPI.getSchema) { id =>

    val maybeSchema = service.findSchema(id).map(DummyResponse.apply)

    Sync[F].delay(maybeSchema.fold(StatusCode.BadRequest.asLeft[DummyResponse])(_.asRight[StatusCode]))
  }

  val routes: HttpRoutes[F] = getSchema

}

object JsonSchemaAPI {

//  val getSchema: Endpoint[NonEmptyString, StatusCode, String, Any] =
  val getSchema: Endpoint[NonEmptyString, StatusCode, DummyResponse, Any] =
    endpoint.get
      .in("schema")
      .in(path[NonEmptyString]("schema_id"))
      .errorOut(statusCode)
//      .out(stringBody.description("A JSON schema object"))
      .out(jsonBody[DummyResponse].description("A JSON schema object"))
      .description(
        "Returns a JSON object representing the schema if it's found for the provided id"
      )

  val uploadSchema: Endpoint[(NonEmptyString, NonEmptyString), StatusCode, JsonSchemaResponse, Any] =
    endpoint.post
      .in("schema")
      .in(path[NonEmptyString]("schema_id"))
      .in(plainBody[NonEmptyString].description("A JSON schema object"))
      .errorOut(statusCode)
      .out(
        jsonBody[JsonSchemaResponse].description("A JSON upload response").example(UploadSuccess(id = "config-json"))
      )
      .description(
        "Returns a simple JSON response representing the status of the action (success/error)"
      )

  val validateSchema: Endpoint[NonEmptyString, StatusCode, JsonSchemaResponse, Any] =
    endpoint.post
      .in("validate")
      .in(path[NonEmptyString]("schema_id"))
      .errorOut(statusCode)
      .out(
        jsonBody[JsonSchemaResponse].description("A JSON validation response").example(JsonSchemaResponse.ValidationError("config-json", "missing field"))
      )
      .description(
        "Returns a simple JSON response representing the status of the action (success/error)"
      )

  val endpoints = List(getSchema, uploadSchema, validateSchema)

  lazy val openApiDocs: String =
    OpenAPIDocsInterpreter().toOpenAPI(endpoints, "json-validator", "1.0.0").toYaml

}
