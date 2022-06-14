/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.api

import io.circe._
import io.circe.generic.semiauto._

final case class DummyResponse(schema: String)
object DummyResponse {
  implicit val encoder: Encoder[DummyResponse] = deriveEncoder
  implicit val decoder: Decoder[DummyResponse] = deriveDecoder
}
