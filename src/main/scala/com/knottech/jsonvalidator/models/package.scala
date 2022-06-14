/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator

import eu.timepit.refined.api._
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString

package object models {

  type SchemaId = NonEmptyString
  object SchemaId extends RefinedTypeOps[SchemaId, String] with CatsRefinedTypeOpsSyntax

  type JsonSchema = NonEmptyString
  object JsonSchema extends RefinedTypeOps[JsonSchema, String] with CatsRefinedTypeOpsSyntax

  type JsonDocument = NonEmptyString
  object JsonDocument extends RefinedTypeOps[JsonDocument, String] with CatsRefinedTypeOpsSyntax

  type ErrorMessage = NonEmptyString
  object ErrorMessage extends RefinedTypeOps[ErrorMessage, String] with CatsRefinedTypeOpsSyntax

}
