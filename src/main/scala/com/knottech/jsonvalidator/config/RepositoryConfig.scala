/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.config

import com.knottech.jsonvalidator.repo.RepositoryProvider
import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import pureconfig._
import pureconfig.generic.semiauto._

/**
  * The json schema repository configuration.
  *
  * @param provider The configured repo provider filesystem/database.
  */
final case class RepositoryConfig(
    provider: RepositoryProvider,
    filesystem: FilesystemConfig,
    database: DatabaseConfig
)

object RepositoryConfig {

  // The default configuration key to lookup the filesystem directory configuration.
  final val CONFIG_KEY: ConfigKey = "repository"

  implicit val configReader: ConfigReader[RepositoryConfig] = deriveReader[RepositoryConfig]

}
