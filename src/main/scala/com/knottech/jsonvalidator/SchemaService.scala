/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator

import cats.Id
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import cats.effect.IO
import com.github.fge.jsonschema.SchemaVersion
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.main.{JsonSchemaFactory, JsonValidator}

//trait SchemaService[F[_]] {
trait SchemaService {

  def findSchema(id: NonEmptyString): Option[String]
  def uploadSchema(): Unit
  def validate(): Unit

}

object SchemaService {

  private def validator(version: SchemaVersion): JsonValidator = {
    val config = ValidationConfiguration.newBuilder().setDefaultVersion(version).freeze
    JsonSchemaFactory.newBuilder().setValidationConfiguration(config).freeze.getValidator
  }

  def stub: SchemaService = new SchemaService {

    private val schemaId: NonEmptyString = "config-json"
    //  private val schema: NonEmptyString = """{ "$schema": "http://json-schema.org/draft-04/schema#", "type": "object", "properties": { "source": { "type": "string" }, "destination": { "type": "string" }, "timeout": { "type": "integer", "minimum": 0, "maximum": 32767 }, "chunks": { "type": "object", "properties": { "size": { "type": "integer" }, "number": { "type": "integer" } }, "required": ["size"] } }, "required": ["source", "destination"] }"""
    private val schema: String = """{ "$schema": "http://json-schema.org/draft-04/schema#", "type": "object", "properties": { "source": { "type": "string" }, "destination": { "type": "string" }, "timeout": { "type": "integer", "minimum": 0, "maximum": 32767 }, "chunks": { "type": "object", "properties": { "size": { "type": "integer" }, "number": { "type": "integer" } }, "required": ["size"] } }, "required": ["source", "destination"] }"""

    private val schemas: Map[NonEmptyString, String] = Map(schemaId -> schema)
    //  private val schemas: Map[NonEmptyString, NonEmptyString] = Map(schemaId -> schema)

    override def findSchema(id: NonEmptyString): Option[String] =
      schemas.get(id)

    override def uploadSchema(): Unit = ()
    override def validate():  Unit = ()
  }

}
