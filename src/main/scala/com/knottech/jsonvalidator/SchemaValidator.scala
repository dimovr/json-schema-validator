/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator

import cats.data.Validated
import cats.effect.Sync
import cats.implicits._
import eu.timepit.refined.auto._
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonschema.SchemaVersion
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.{ JsonSchemaFactory, JsonValidator }
import com.knottech.jsonvalidator.models.{ JsonDocument, JsonSchema }
import io.circe.ParsingFailure

import scala.jdk.CollectionConverters._

trait SchemaValidator[F[_]] {

  def validate(schema: JsonSchema, document: JsonDocument): F[Validated[List[String], Unit]]

}

object SchemaValidator {

  def stub[F[_]: Sync](version: SchemaVersion): SchemaValidator[F] =
    new SchemaValidator[F] {

      private lazy val jsonSchemaValidator: JsonValidator = {
        val config = ValidationConfiguration.newBuilder().setDefaultVersion(version).freeze
        JsonSchemaFactory.newBuilder().setValidationConfiguration(config).freeze.getValidator
      }

      override def validate(schema: JsonSchema, document: JsonDocument): F[Validated[List[String], Unit]] =
        for {
          cleanedUpDoc <- Sync[F].fromEither(cleanup(document))
          report <- Sync[F].delay {
            val documentJson = (new ObjectMapper).readTree(cleanedUpDoc)
            val schemaJson   = (new ObjectMapper).readTree(schema)
            jsonSchemaValidator.validate(schemaJson, documentJson)
          }
        } yield handleResult(report)

      private def handleResult(report: ProcessingReport) =
        (report.iterator().asScala.toList.map(_.getMessage), report.isSuccess) match {
          case (Nil, true)     => Validated.Valid(())
          case (errors, false) => Validated.Invalid(errors)
          case (errors, status) =>
            Validated.Invalid(List(s"Got errors [${errors.mkString(",")}], but status is $status"))
        }

      private def cleanup(document: JsonDocument): Either[ParsingFailure, String] =
        io.circe.parser.parse(document).map(_.deepDropNullValues.toString())

    }

}
