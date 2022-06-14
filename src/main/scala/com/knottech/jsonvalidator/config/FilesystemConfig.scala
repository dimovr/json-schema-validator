/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.config

import com.knottech.jsonvalidator.repo.DirectoryName
import eu.timepit.refined.auto._
import pureconfig._
import eu.timepit.refined.pureconfig._
import pureconfig.generic.semiauto._

/**
  * The filesystem storage configuration.
  *
  * @param directory The configured directory in which to store schemas.
  */
final case class FilesystemConfig(directory: DirectoryName)

object FilesystemConfig {

  // The default configuration key to lookup the filesystem directory configuration.
  final val CONFIG_KEY: ConfigKey = "filesystem"

  implicit val configReader: ConfigReader[FilesystemConfig] = deriveReader[FilesystemConfig]

}
