/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.repo

import cats.effect.Sync
import com.knottech.jsonvalidator.models.{ JsonSchema, SchemaId }
import eu.timepit.refined.auto._

class SchemaRepoStub[F[_]: Sync] extends SchemaRepo[F] {
  private val schemas: scala.collection.mutable.Map[SchemaId, JsonSchema] =
    scala.collection.mutable.Map.empty

  override def find(id: SchemaId): F[Option[JsonSchema]] =
    Sync[F].delay(schemas.get(id))

  override def upsert(id: SchemaId, schema: JsonSchema): F[Unit] =
    Sync[F].delay(schemas += id -> schema)

}