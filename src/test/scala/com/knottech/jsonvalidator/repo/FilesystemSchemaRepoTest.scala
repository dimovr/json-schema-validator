/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.repo

import eu.timepit.refined.auto._
import com.knottech.jsonvalidator.config.FilesystemConfig
import com.knottech.jsonvalidator.models.{JsonSchema, SchemaId}
import eu.timepit.refined.api.Refined
import munit.FunSuite

import java.util.UUID

final class FilesystemSchemaRepoTest extends FunSuite {

  private val directory: Directory = ".schemas"
  private val config = FilesystemConfig(directory)
  private val repo = FilesystemSchemaRepo(config).unsafeRunSync()

  test("should return None if no file has NOT been stored yet") {
    val id: SchemaId = Refined.unsafeApply(UUID.randomUUID().toString)
    assert(
      repo.find(id).unsafeRunSync().isEmpty
    )
  }

  test("should return Some if a file has been stored yet") {
    val id: SchemaId = "234"
    val schema: JsonSchema = "abc"

    assert {
      repo.upsert(id, schema).unsafeRunSync()
      repo.find(id).unsafeRunSync().get.value == "abc"
    }
  }

}
