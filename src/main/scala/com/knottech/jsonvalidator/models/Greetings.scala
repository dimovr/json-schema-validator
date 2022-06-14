/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.models

import eu.timepit.refined.auto._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.refined._

/**
  * A simple model for our hello world greetings.
  *
  * @param title    A generic title.
  * @param headings Some header which might be presented prominently to the user.
  * @param message  A message for the user.
  */
final case class Greetings(title: GreetingTitle, headings: GreetingHeader, message: GreetingMessage)

object Greetings {

  implicit val decoder: Decoder[Greetings] = deriveDecoder[Greetings]
  implicit val encoder: Encoder[Greetings] = deriveEncoder[Greetings]

}
