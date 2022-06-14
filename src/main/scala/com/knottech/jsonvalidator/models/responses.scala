/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.models

import eu.timepit.refined.auto._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.generic.auto._
import io.circe.refined._

/**
  * A response model for successful schema upload.
  *
  * @param action uploadSchema
  * @param id The id of the json schema uploaded.
  * @param status success
  */
case class UploadSuccess private (action: Action, id: SchemaId, status: Status)
object UploadSuccess {
  def apply(id: SchemaId): UploadSuccess = UploadSuccess(Action.UploadSchema, id, Status.Success)

  implicit val encoder: Encoder[UploadSuccess] = deriveEncoder
  implicit val decoder: Decoder[UploadSuccess] = deriveDecoder
}

/**
  * A response model for successful schema upload.
  *
  * @param action uploadSchema
  * @param id The id of the json schema uploaded.
  * @param status error
  * @param message  An error message if status is 'error' message for the user.
  */
case class UploadError private (action: Action, id: SchemaId, status: Status, message: ErrorMessage)
object UploadError {
  def apply(id: SchemaId, message: ErrorMessage): UploadError =
    UploadError(Action.UploadSchema, id, Status.Error, message)

  implicit val encoder: Encoder[UploadError] = deriveEncoder
  implicit val decoder: Decoder[UploadError] = deriveDecoder
}

/**
  * A response model for successful schema upload.
  *
  * @param action validateDocument
  * @param id The id of the json schema uploaded.
  * @param status success
  */
case class ValidationSuccess private (action: Action, id: SchemaId, status: Status)
object ValidationSuccess {
  def apply(id: SchemaId): ValidationSuccess = ValidationSuccess(Action.ValidateDocument, id, Status.Success)

  implicit val encoder: Encoder[ValidationSuccess] = deriveEncoder
  implicit val decoder: Decoder[ValidationSuccess] = deriveDecoder
}

/**
  * A response model for successful schema upload.
  *
  * @param action validateDocument
  * @param id The id of the json schema uploaded.
  * @param status error
  * @param message An error message if status is 'error' message for the user.
  */
case class ValidationError private (action: Action, id: SchemaId, status: Status, message: String)
object ValidationError {
  def apply(id: SchemaId, message: String): ValidationError =
    ValidationError(Action.ValidateDocument, id, Status.Error, message)

  implicit val encoder: Encoder[ValidationError] = deriveEncoder
  implicit val decoder: Decoder[ValidationError] = deriveDecoder
}
