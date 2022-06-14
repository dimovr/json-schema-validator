/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.db

import cats.effect.Sync
import cats.implicits._
import com.knottech.jsonvalidator.models.{ JsonSchema, SchemaId }
import eu.timepit.refined.auto._

trait SchemaRepo[F[_]] {

  def find(schemaId: SchemaId): F[Option[JsonSchema]]
  def upsert(schemaId: SchemaId, schema: JsonSchema): F[Unit]

}

object SchemaRepo {

  def stub[F[_]: Sync]: SchemaRepo[F] =
    new SchemaRepo[F] {
      private val schemas: scala.collection.mutable.Map[SchemaId, JsonSchema] =
        scala.collection.mutable.Map.empty

      override def find(id: SchemaId): F[Option[JsonSchema]] = Sync[F].delay(schemas.get(id))

      override def upsert(id: SchemaId, schema: JsonSchema): F[Unit] =
        for {
          _ <- Sync[F].fromEither(io.circe.parser.parse(schema))
          _ <- Sync[F].delay(schemas += id -> schema)
        } yield ()

    }

}
