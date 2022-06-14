/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator

import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.cats._
import eu.timepit.refined.string._
import eu.timepit.refined.types.string.NonEmptyString

package object db {
  type JDBCDriverName =
    String Refined MatchesRegex[W.`"^\\\\w+\\\\.[\\\\w\\\\d\\\\.]+[\\\\w\\\\d]+$"`.T]
  object JDBCDriverName extends RefinedTypeOps[JDBCDriverName, String] with CatsRefinedTypeOpsSyntax

  type JDBCUrl = String Refined MatchesRegex[W.`"^jdbc:[a-zA-z0-9]+:.*"`.T]
  object JDBCUrl extends RefinedTypeOps[JDBCUrl, String] with CatsRefinedTypeOpsSyntax

  type JDBCUsername = NonEmptyString
  object JDBCUsername extends RefinedTypeOps[JDBCUsername, String] with CatsRefinedTypeOpsSyntax

  type JDBCPassword = NonEmptyString
  object JDBCPassword extends RefinedTypeOps[JDBCPassword, String] with CatsRefinedTypeOpsSyntax
}
