/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.models

import enumeratum.values.{ StringCirceEnum, StringEnum, StringEnumEntry }

sealed abstract class Action(val value: String) extends StringEnumEntry
object Action extends StringEnum[Action] with StringCirceEnum[Action] {
  case object UploadSchema     extends Action("uploadSchema")
  case object ValidateDocument extends Action("validateDocument")

  val values = findValues
}
