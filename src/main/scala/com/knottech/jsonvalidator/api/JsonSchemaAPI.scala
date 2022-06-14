/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.api

import cats.data.Validated
import cats.effect._
import cats.implicits._
import com.knottech.jsonvalidator.SchemaValidator
import com.knottech.jsonvalidator.repo.SchemaRepo
import com.knottech.jsonvalidator.models._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.parser.parse
import org.http4s._
import org.http4s.dsl._
import sttp.model._
import sttp.tapir.{ ValidationError => TapirValidationError, _ }
import sttp.tapir.generic.auto._
import sttp.tapir.codec.refined._
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._
import sttp.tapir.openapi.circe.yaml._

final class JsonSchemaAPI[F[_]: Concurrent: ContextShift: Timer](
    repo: SchemaRepo[F],
    validator: SchemaValidator[F]
) extends Http4sDsl[F] {

  private val getSchema: HttpRoutes[F] = Http4sServerInterpreter[F]()
    .toRoutes(JsonSchemaAPI.getSchema) { id =>
      for {
        maybeSchema <- repo.find(id)
        result <- Sync[F].delay(
          maybeSchema.fold(StatusCode.NotFound.asLeft[NonEmptyString])(_.asRight[StatusCode])
        )
      } yield result
    }

  private val uploadSchema: HttpRoutes[F] = Http4sServerInterpreter[F]()
    .toRoutes(JsonSchemaAPI.uploadSchema) { case (id, schema) =>
      val result = for {
        _ <- Sync[F].fromEither(parse(schema))
        _ <- repo.upsert(id, schema)
      } yield UploadSuccess(id).asRight[UploadError]

      result.recoverWith { case _: Throwable =>
        Sync[F].pure(Left(UploadError(id, "upload failed")))
      }
    }

  private val validate: HttpRoutes[F] = Http4sServerInterpreter[F]()
    .toRoutes(JsonSchemaAPI.validate) { case (id, document) =>
      def validationSuccess            = ValidationSuccess(id).asRight[ValidationError]
      def validationError(msg: String) = ValidationError(id, msg).asLeft[ValidationSuccess]

      val result = for {
        schemaOpt <- repo.find(id)
        schema    <- Sync[F].fromOption(schemaOpt, new RuntimeException("missing schema"))
        result <- validator.validate(schema, document).map {
          case Validated.Valid(_)        => validationSuccess
          case Validated.Invalid(errors) => validationError(errors.mkString("\n"))
        }
      } yield result

      result.recoverWith { case _: Throwable =>
        Sync[F].pure(validationError("validation failed"))
      }
    }

  val routes: HttpRoutes[F] = getSchema <+> uploadSchema <+> validate

}

object JsonSchemaAPI {

  val getSchema: Endpoint[SchemaId, StatusCode, JsonSchema, Any] =
    endpoint.get
      .in("schema")
      .in(path[SchemaId]("schema_id"))
      .errorOut(statusCode)
      .out(
        plainBody[JsonSchema].description("A JSON schema object")
      )
      .description(
        "Returns a JSON object representing the schema if it's found for the provided id"
      )

  val uploadSchema: Endpoint[(SchemaId, JsonSchema), UploadError, UploadSuccess, Any] =
    endpoint.post
      .in("schema")
      .in(path[SchemaId]("schema_id"))
      .in(plainBody[JsonSchema].description("A JSON schema object"))
      .out(statusCode(StatusCode.Created))
      .errorOut(statusCode(StatusCode.BadRequest))
      .out(
        jsonBody[UploadSuccess]
          .description("successful upload response")
          .example(UploadSuccess(id = "config-json"))
      )
      .errorOut(
        jsonBody[UploadError]
          .description("A JSON validation error")
          .example(UploadError("config-json", "invalid scheme"))
      )
      .description(
        "Returns a simple JSON response representing the status of the action (success/error)"
      )

  val validate: Endpoint[
    (SchemaId, JsonDocument),
    ValidationError,
    ValidationSuccess,
    Any
  ] =
    endpoint.post
      .in("validate")
      .in(path[SchemaId]("schema_id"))
      .in(plainBody[JsonDocument].description("A JSON object to be validated"))
      .out(
        jsonBody[ValidationSuccess]
          .description("Successful JSON schema validation response")
      )
      .out(statusCode(StatusCode.Created))
      .errorOut(statusCode(StatusCode.BadRequest))
      .errorOut(
        jsonBody[ValidationError]
          .description("A JSON validation error")
          .example(ValidationError("config-json", "missing field"))
      )
      .description(
        "Returns a simple JSON response representing the status of the action (success/error)"
      )

  private val endpoints = List(getSchema, uploadSchema, validate)

  lazy val openApiDocs: String =
    OpenAPIDocsInterpreter().toOpenAPI(endpoints, "json-schema-validator", "0.0.1").toYaml

}
