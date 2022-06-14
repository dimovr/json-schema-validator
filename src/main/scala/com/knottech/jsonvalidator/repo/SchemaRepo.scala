/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.repo

import com.knottech.jsonvalidator.config.RepositoryConfig
import com.knottech.jsonvalidator.models.{ JsonSchema, SchemaId }

trait SchemaRepo[F[_]] {

  def find(schemaId: SchemaId): F[Option[JsonSchema]]
  def upsert(schemaId: SchemaId, schema: JsonSchema): F[Unit]

}

object SchemaRepo {
  def apply[F[_]](
      repoConfig: RepositoryConfig
  ): SchemaRepo[F] = ???
}
