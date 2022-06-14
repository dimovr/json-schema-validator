/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.models

import enumeratum.values.{StringCirceEnum, StringEnum, StringEnumEntry}

sealed abstract class Status(val value: String) extends StringEnumEntry
object Status extends StringEnum[Status] with StringCirceEnum[Status] {
  case object Success  extends Status("success")
  case object Error    extends Status("error")

  val values = findValues
}