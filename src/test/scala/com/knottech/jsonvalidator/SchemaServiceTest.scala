/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator

import cats.effect.IO
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.auto._
import munit.FunSuite


final class SchemaServiceTest extends FunSuite {

  private val existingSchemaId: NonEmptyString = "1"
  private val jsonSchema: NonEmptyString = "{}"

  private val schemaService = new SchemaService[IO] {
    override def findSchema(id: NonEmptyString): IO[Option[NonEmptyString]] =
      IO(if (id == existingSchemaId) Some(jsonSchema) else None)

    override def uploadSchema(id: NonEmptyString, schema: NonEmptyString): IO[Unit] = IO.unit

    override def validate(jsonString: NonEmptyString, schemaId: NonEmptyString): IO[Boolean] = IO.pure(true)
  }

//  test("get schema for existing id should return success response") {
//
//
//  }

}
