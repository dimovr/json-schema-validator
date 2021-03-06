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
import eu.timepit.refined.string._
import eu.timepit.refined.types.string.NonEmptyString

package object config {

  type ConfigKey = NonEmptyString
  object ConfigKey extends RefinedTypeOps[ConfigKey, String] with CatsRefinedTypeOpsSyntax

  type IP = String Refined IPv4
  object IP extends RefinedTypeOps[IP, String] with CatsRefinedTypeOpsSyntax

}
