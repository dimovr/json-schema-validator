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
import eu.timepit.refined.types.string.NonEmptyString
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonschema.SchemaVersion
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.main.{JsonSchemaFactory, JsonValidator}

trait SchemaService[F[_]] {

  def findSchema(id: NonEmptyString): F[Option[NonEmptyString]]
  def uploadSchema(id: NonEmptyString, schema: NonEmptyString): F[Unit]
  def validate(schemaId: NonEmptyString, jsonString: NonEmptyString): F[Boolean]

}

object SchemaService {

  private def validator(version: SchemaVersion): JsonValidator = {
    val config = ValidationConfiguration.newBuilder().setDefaultVersion(version).freeze
    JsonSchemaFactory.newBuilder().setValidationConfiguration(config).freeze.getValidator
  }

  def stub[F[_]: Sync]: SchemaService[F] = new SchemaService[F] {

    private val schemaId: NonEmptyString = "config-json"
      private val schema: NonEmptyString = """{ "$schema": "http://json-schema.org/draft-04/schema#", "type": "object", "properties": { "source": { "type": "string" }, "destination": { "type": "string" }, "timeout": { "type": "integer", "minimum": 0, "maximum": 32767 }, "chunks": { "type": "object", "properties": { "size": { "type": "integer" }, "number": { "type": "integer" } }, "required": ["size"] } }, "required": ["source", "destination"] }"""

    private val schemas: Map[NonEmptyString, NonEmptyString] = Map(schemaId -> schema)

    override def findSchema(id: NonEmptyString): F[Option[NonEmptyString]] =
      Sync[F].delay(schemas.get(id))

    override def uploadSchema(id: NonEmptyString, schema: NonEmptyString): F[Unit] =
      Sync[F].unit

    override def validate(schemaId: NonEmptyString, jsonString: NonEmptyString): F[Boolean] =
      for {
        maybeSchema <- findSchema(schemaId)
        schema      <- Sync[F].fromOption(maybeSchema, new RuntimeException(s"No schema for id $schemaId"))
        jsonNode = (new ObjectMapper).readTree(jsonString)
        schemeNode = (new ObjectMapper).readTree(schema)
        result = validator(SchemaVersion.DRAFTV4).validate(jsonNode, schemeNode)
      } yield result.isSuccess

  }

}
