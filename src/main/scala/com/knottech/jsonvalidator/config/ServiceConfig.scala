/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.config

import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import eu.timepit.refined.types.net.PortNumber
import pureconfig._
import pureconfig.generic.semiauto._

/**
  * The service configuration.
  *
  * @param ip   The ip address the service will listen on.
  * @param port The port number the service will listen on.
  */
final case class ServiceConfig(ip: IP, port: PortNumber)

object ServiceConfig {
  // The default configuration key to lookup the service configuration.
  final val CONFIG_KEY: ConfigKey = "service"

  implicit val serviceConfigReader: ConfigReader[ServiceConfig] = deriveReader[ServiceConfig]

}
