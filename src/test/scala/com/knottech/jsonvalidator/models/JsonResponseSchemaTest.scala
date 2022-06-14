/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.models

import io.circe.syntax._
import io.circe.parser.decode
import eu.timepit.refined.auto._
import com.knottech.jsonvalidator.models._
import munit.FunSuite

final class JsonResponseSchemaTest extends FunSuite {

  // UploadSuccess
  test("created UploadSuccess from json") {
    val uploadSuccess = UploadSuccess("some-id")
    val jsonString = """{"action":"uploadSchema","id":"some-id","status":"success"}"""

    assert(
      decode[UploadSuccess](jsonString).toOption.get == uploadSuccess
    )
  }

  test("convert UploadSuccess to json") {
    val uploadSuccess = UploadSuccess("some-id")
    val jsonString = """{"action":"uploadSchema","id":"some-id","status":"success"}"""

    assert(
      uploadSuccess.asJson.noSpaces == jsonString
    )
  }

  // UploadError
  test("created UploadError from json") {
    val uploadError = UploadError("some-id", "msg")
    val jsonString = """{"action":"uploadSchema","id":"some-id","status":"error","message":"msg"}"""

    assert(
      decode[UploadError](jsonString).toOption.get == uploadError
    )
  }

  test("convert UploadError to json") {
    val uploadError = UploadError("some-id", "msg")
    val jsonString = """{"action":"uploadSchema","id":"some-id","status":"error","message":"msg"}"""

    assert(
      uploadError.asJson.noSpaces == jsonString
    )
  }

  // ValidationSuccess
  test("created ValidationSuccess from json") {
    val uploadSuccess = ValidationSuccess("some-id")
    val jsonString = """{"action":"validateDocument","id":"some-id","status":"success"}"""

    assert(
      decode[ValidationSuccess](jsonString).toOption.get == uploadSuccess
    )
  }

  test("convert ValidationSuccess to json") {
    val validationSuccess = ValidationSuccess("some-id")
    val jsonString = """{"action":"validateDocument","id":"some-id","status":"success"}"""

    assert(
      validationSuccess.asJson.noSpaces == jsonString
    )
  }

  // ValidationError
  test("created ValidationError from json") {
    val validationError = ValidationError("some-id", "msg")
    val jsonString = """{"action":"validateDocument","id":"some-id","status":"error","message":"msg"}"""

    assert(
      decode[ValidationError](jsonString).toOption.get == validationError
    )
  }

  test("convert ValidationError to json") {
    val validationError = ValidationError("some-id", "msg")
    val jsonString = """{"action":"validateDocument","id":"some-id","status":"error","message":"msg"}"""

    assert(
      validationError.asJson.noSpaces == jsonString
    )
  }

}
