/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.db

import eu.timepit.refined.auto._
import cats.effect.IO
import com.knottech.jsonvalidator.models.JsonSchema
import io.circe.ParsingFailure
import munit.FunSuite

final class SchemaRepoTest extends FunSuite {

  private val repo = SchemaRepo.stub[IO]

  test("repo should get None if there is no schema for given id") {
    assert(
      repo.find("some-id").unsafeRunSync().isEmpty
    )
  }

  test("repo should fail to insert schema if it's not a valid json") {
    val invalidJson: JsonSchema = "{123}"
    intercept[ParsingFailure](
      repo.upsert("some-id", invalidJson).unsafeRunSync()
    )
  }

  test("repo should insert schema and get it successfully afterwards") {
    val schema: JsonSchema = "{}"
    repo.upsert("some-id", schema).unsafeRunSync()
    assert(
      repo.find("some-id").unsafeRunSync().get == schema
    )
  }

}
