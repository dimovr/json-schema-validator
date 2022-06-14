/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator

import cats.effect.Sync
import cats.implicits._
import eu.timepit.refined.auto._
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonschema.SchemaVersion
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.main.{JsonSchemaFactory, JsonValidator}
import com.knottech.jsonvalidator.models.{JsonObject, JsonSchema, SchemaId}

trait SchemaService[F[_]] {

  def findSchema(id: SchemaId): F[Option[JsonSchema]]
  def uploadSchema(id: SchemaId, schema: JsonSchema): F[Unit]
  def validate(schemaId: SchemaId, jsonToValidate: JsonObject): F[Boolean]

}

object SchemaService {

  def stub[F[_]: Sync](version: SchemaVersion): SchemaService[F] = new SchemaService[F] {

    private val validator: JsonValidator = {
      val config = ValidationConfiguration.newBuilder().setDefaultVersion(version).freeze
      JsonSchemaFactory.newBuilder().setValidationConfiguration(config).freeze.getValidator
    }

    private val schemaId: SchemaId = "config-json"
    private val schema: JsonSchema = """{ "$schema": "http://json-schema.org/draft-04/schema#", "type": "object", "properties": { "source": { "type": "string" }, "destination": { "type": "string" }, "timeout": { "type": "integer", "minimum": 0, "maximum": 32767 }, "chunks": { "type": "object", "properties": { "size": { "type": "integer" }, "number": { "type": "integer" } }, "required": ["size"] } }, "required": ["source", "destination"] }"""

    import scala.collection.mutable.{Map => MutMap}
    private val schemas: MutMap[SchemaId, JsonSchema] = MutMap(schemaId -> schema)

    override def findSchema(id: SchemaId): F[Option[JsonSchema]] =
      Sync[F].delay(schemas.get(id))

    override def uploadSchema(id: SchemaId, schema: JsonSchema): F[Unit] =
      for {
        _ <- Sync[F].fromEither(io.circe.parser.parse(schema))
        _ <- Sync[F].delay(schemas.addOne(id -> schema))
      } yield ()

    override def validate(schemaId: SchemaId, jsonToValidate: JsonObject): F[Boolean] =
      for {
        maybeSchema <- findSchema(schemaId)
        schema      <- Sync[F].fromOption(maybeSchema, new RuntimeException(s"No schema for id $schemaId"))
        jsonNode    = (new ObjectMapper).readTree(jsonToValidate)
        schemeNode  = (new ObjectMapper).readTree(schema)
        result      = validator.validate(jsonNode, schemeNode)
      } yield result.isSuccess

  }

}
