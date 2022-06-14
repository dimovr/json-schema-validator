/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.config

import com.knottech.jsonvalidator.repo._
import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import pureconfig._
import pureconfig.generic.semiauto._

/**
  * The configuration for a database connection.
  *
  * @param driver The class name of the JDBC driver.
  * @param url    A JDBC URL.
  * @param user   The username for the connection.
  * @param pass   The password for the connection.
  */
final case class DatabaseConfig(
    driver: JDBCDriverName,
    url: JDBCUrl,
    user: JDBCUsername,
    pass: JDBCPassword
)

object DatabaseConfig {
  // The default configuration key to lookup the database configuration.
  final val CONFIG_KEY: ConfigKey = "database"

  implicit val configReader: ConfigReader[DatabaseConfig] = deriveReader[DatabaseConfig]

}
