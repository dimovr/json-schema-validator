/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.repo

import cats.effect.{ IO, Resource }
import cats.implicits._
import com.knottech.jsonvalidator.config.FilesystemConfig
import com.knottech.jsonvalidator.models.{ JsonSchema, SchemaId }
import eu.timepit.refined.api.Refined

import java.io._
import scala.util.Try

private class FilesystemSchemaRepo(
    filesystemConfig: FilesystemConfig
) extends SchemaRepo[IO] {

  override def find(schemaId: SchemaId): IO[Option[JsonSchema]] =
    for {
      file <- IO.fromTry(Try(file(schemaId)))
      bytes <- Resource
        .make {
          IO.delay(new FileInputStream(file))
        } { inStream =>
          IO.delay(inStream.close()).handleErrorWith(_ => IO.unit)
        }
        .use(fis => IO.delay(fis.readAllBytes()))
    } yield {
      def schema: JsonSchema = Refined.unsafeApply(new String(bytes))
      if (bytes.nonEmpty) Some(schema) else None
    }

  override def upsert(schemaId: SchemaId, schema: JsonSchema): IO[Unit] =
    for {
      file <- IO.fromTry(Try(file(schemaId)))
      _ <- Resource
        .make {
          IO.delay(new FileOutputStream(file))
        } { outStream =>
          IO.delay(outStream.close()).handleErrorWith(_ => IO.unit)
        }
        .use(fos => IO.delay(fos.write(schema.value.getBytes())))
    } yield ()

  private def file(schemaId: SchemaId): File = new File(s"${filesystemConfig.directory}/$schemaId")

}
