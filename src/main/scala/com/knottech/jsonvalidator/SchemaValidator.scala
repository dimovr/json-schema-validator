/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator

import cats.effect.Sync
import eu.timepit.refined.auto._
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonschema.SchemaVersion
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.main.{JsonSchemaFactory, JsonValidator}
import com.knottech.jsonvalidator.models.{JsonObject, JsonSchema}

trait SchemaValidator[F[_]] {

  def validate(schema: JsonSchema, document: JsonObject): F[Boolean]

}

object SchemaValidator {

  def stub[F[_]: Sync](version: SchemaVersion): SchemaValidator[F] = new SchemaValidator[F] {

    private val validator: JsonValidator = {
      val config = ValidationConfiguration.newBuilder().setDefaultVersion(version).freeze
      JsonSchemaFactory.newBuilder().setValidationConfiguration(config).freeze.getValidator
    }

    override def validate(schema: JsonSchema, document: JsonObject): F[Boolean] =
      Sync[F].delay {
        val documentJson    = (new ObjectMapper).readTree(document)
        val schemaJson  = (new ObjectMapper).readTree(schema)
        val result      = validator.validate(schemaJson, documentJson)

        result.isSuccess
      }

  }

}
