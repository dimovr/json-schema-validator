/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator

import eu.timepit.refined.auto._
import cats.effect.IO
import com.knottech.jsonvalidator.models._
import munit.FunSuite

final class SchemaValidator extends FunSuite {

  private val validator = SchemaValidator.stub[IO]

  test("validation should fail for invalid json schema and valid document") {
    val document: JsonDocument = """{}"""
  }

}
