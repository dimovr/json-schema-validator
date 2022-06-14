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
import java.nio.file.{ Files, Path, Paths }
import scala.util.Try

object FilesystemSchemaRepo {
  def apply(
      filesystemConfig: FilesystemConfig
  ): IO[SchemaRepo[IO]] =
    for {
      path <- IO.fromTry(Try(Paths.get(filesystemConfig.directory.value)))
      _    <- IO(if (Files.notExists(path)) Files.createDirectory(path).toFile)
    } yield new Impl(filesystemConfig)

  private class Impl(
      filesystemConfig: FilesystemConfig
  ) extends SchemaRepo[IO] {

    import filesystemConfig.directory

    override def find(schemaId: SchemaId): IO[Option[JsonSchema]] =
      if (Files.exists(path(schemaId))) {
        def acquire: IO[FileInputStream] =
          for {
            file <- IO.fromTry(Try(new File(path(schemaId).toUri)))
            fis  <- IO.delay(new FileInputStream(file))
          } yield fis

        def release(inStream: FileInputStream): IO[Unit] = IO.delay(inStream.close()).handleErrorWith(_ => IO.unit)

        Resource
          .make(acquire)(release)
          .use(fis => IO.delay(fis.readAllBytes()))
          .map { bytes =>
            def schema: JsonSchema = Refined.unsafeApply(new String(bytes))

            if (bytes.nonEmpty) Some(schema) else None
          }
          .recoverWith { case _: FileNotFoundException =>
            IO.pure(None)
          }
      } else {
        IO.pure(None)
      }

    override def upsert(schemaId: SchemaId, schema: JsonSchema): IO[Unit] = {

      def acquire: IO[FileOutputStream] =
        for {
          path <- IO.fromTry(Try(path(schemaId)))
          _    <- IO.delay(if (Files.exists(path)) Files.delete(path))
          file <- IO.fromTry(Try(Files.createFile(path).toFile))
          fis  <- IO.delay(new FileOutputStream(file))
        } yield fis

      def release(outStream: FileOutputStream) = IO.delay(outStream.close()).handleErrorWith(_ => IO.unit)

      Resource
        .make(acquire)(release)
        .use(fos => IO.delay(fos.write(schema.value.getBytes())))
        .map(_ => IO.unit)
    }

    private def path(schemaId: SchemaId): Path = Paths.get(s"$directory/$schemaId")

  }
}
