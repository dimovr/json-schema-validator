/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.db

import cats.effect.IO
import eu.timepit.refined.auto._
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult

final class FlywayDatabaseMigrator {

  /**
    * Apply pending migrations to the database.
    *
    * @param url  A JDBC database connection url.
    * @param user The login name for the connection.
    * @param pass The password for the connection.
    * @return A migrate result object holding information about executed migrations and the schema. See the Java-Doc of Flyway for details.
    */
  def migrate(url: JDBCUrl, user: JDBCUsername, pass: JDBCPassword): IO[MigrateResult] =
    IO {
      val flyway: Flyway =
        Flyway.configure().dataSource(url, user, pass).load()
      flyway.migrate()
    }

}
