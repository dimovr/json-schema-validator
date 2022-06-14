/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.config

import com.github.fge.jsonschema.SchemaVersion
import eu.timepit.refined.auto._
import pureconfig._
import pureconfig.generic.semiauto._

/**
  * The JSON schema validation configuration.
  *
  * @param schemaVersion The configured schema version to validate against.
  */
final case class ValidationConfig(schemaVersion: SchemaVersion)

object ValidationConfig {
  // The default configuration key to lookup the schema validation configuration.
  final val CONFIG_KEY: ConfigKey = "validation"

  implicit val configReader: ConfigReader[ValidationConfig] = deriveReader[ValidationConfig]

}


