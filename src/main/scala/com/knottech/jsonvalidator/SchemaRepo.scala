/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator

import eu.timepit.refined.auto._

import cats.effect.Sync
import cats.implicits._
import com.knottech.jsonvalidator.models.{JsonSchema, SchemaId}

trait SchemaRepo[F[_]] {

  def find(schemaId: SchemaId): F[Option[JsonSchema]]
  def upsert(schemaId: SchemaId, schema: JsonSchema): F[Unit]

}

object SchemaRepo {

  def stub[F[_]: Sync]: SchemaRepo[F] = new SchemaRepo[F] {

    private val schemaId: SchemaId = "config-json"
    private val schema: JsonSchema = """{ "$schema": "http://json-schema.org/draft-04/schema#", "type": "object", "properties": { "source": { "type": "string" }, "destination": { "type": "string" }, "timeout": { "type": "integer", "minimum": 0, "maximum": 32767 }, "chunks": { "type": "object", "properties": { "size": { "type": "integer" }, "number": { "type": "integer" } }, "required": ["size"] } }, "required": ["source", "destination"] }"""

    import scala.collection.mutable.{Map => MutMap}
    private val schemas: MutMap[SchemaId, JsonSchema] = MutMap(schemaId -> schema)

    override def find(id: SchemaId): F[Option[JsonSchema]] =
      Sync[F].delay(schemas.get(id))

    override def upsert(id: SchemaId, schema: JsonSchema): F[Unit] =
      for {
        _ <- Sync[F].fromEither(io.circe.parser.parse(schema))
        _ <- Sync[F].delay(schemas += id -> schema)
      } yield ()

  }

}