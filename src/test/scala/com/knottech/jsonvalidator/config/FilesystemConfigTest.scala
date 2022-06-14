/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.config

import com.typesafe.config.ConfigFactory
import eu.timepit.refined.auto._
import munit._
import pureconfig._

class FilesystemConfigTest extends FunSuite {

  test("FilesystemConfig must load the default application.conf correctly") {
    val cfg = ConfigFactory.load(getClass().getClassLoader())
    ConfigSource.fromConfig(cfg).at(RepositoryConfig.CONFIG_KEY + "." + FilesystemConfig.CONFIG_KEY).load[FilesystemConfig] match {
      case Left(e)  => fail(e.toList.mkString(", "))
      case Right(conf) => assert(conf.directory.value == ".schemas")
    }
  }

}
