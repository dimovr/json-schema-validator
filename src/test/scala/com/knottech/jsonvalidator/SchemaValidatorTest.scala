/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator

import cats.data.Validated
import eu.timepit.refined.auto._
import cats.effect.IO
import com.fasterxml.jackson.core.JsonParseException
import com.github.fge.jsonschema.SchemaVersion
import com.knottech.jsonvalidator.models.{JsonDocument, JsonSchema}
import munit.FunSuite

final class SchemaValidatorTest extends FunSuite {

  private val validator = SchemaValidator.stub[IO](SchemaVersion.DRAFTV4)

  test("fail if the schema is not a valid json ") {
    val document: JsonDocument = """{ "source": "/home/alice/image.iso", "destination": "/mnt/storage", "timeout": null, "chunks": { "size": 1024, "number": null } }"""
    val schema: JsonSchema = "{123}"

    intercept[JsonParseException](
      validator.validate(schema, document).unsafeRunSync()
    )
  }

  test("fail if schema is valid but the document not") {
    val document: JsonDocument = """{ "source": "/home/alice/image.iso", "timeout": null, "chunks": { "size": 1024, "number": null } }"""
    val schema: JsonSchema = """{ "$schema": "http://json-schema.org/draft-04/schema#", "type": "object", "properties": { "source": { "type": "string" }, "destination": { "type": "string" }, "timeout": { "type": "integer", "minimum": 0, "maximum": 32767 }, "chunks": { "type": "object", "properties": { "size": { "type": "integer" }, "number": { "type": "integer" } }, "required": ["size"] } }, "required": ["source", "destination"] }"""

    assert(
      validator.validate(schema, document).unsafeRunSync() == Validated.Invalid(
        List("""object has missing required properties (["destination"])""")
      )
    )
  }

  test("validate successfully if the document matches the schema") {
    val document: JsonDocument = """{ "source": "/home/alice/image.iso", "destination": "/mnt/storage", "timeout": null, "chunks": { "size": 1024, "number": null } }"""
    val schema: JsonSchema = """{ "$schema": "http://json-schema.org/draft-04/schema#", "type": "object", "properties": { "source": { "type": "string" }, "destination": { "type": "string" }, "timeout": { "type": "integer", "minimum": 0, "maximum": 32767 }, "chunks": { "type": "object", "properties": { "size": { "type": "integer" }, "number": { "type": "integer" } }, "required": ["size"] } }, "required": ["source", "destination"] }"""

    assert(
      validator.validate(schema, document).unsafeRunSync() == Validated.Valid(())
    )
  }


}
