/*
 * Copyright (c) 2022 Knot Tech
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.knottech.jsonvalidator.models

import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe._
import io.circe.generic.semiauto._
import io.circe.refined._

/**
  * A simple model for our hello world greetings.
  *
  * @param action An action performed: uploadSchema or validateSchema.
  * @param id The id of the json schema uploaded.
  * @param status The status of the action representing the outcome: success or error.
  * @param message  An error message if status is 'error' message for the user.
  */
sealed abstract class JsonSchemaResponse(
    action: Action,
    id: SchemaId,
    status: Status,
    message: Option[ErrorMessage]
)

object JsonSchemaResponse {

  case class UploadSuccess(id: SchemaId)
    extends JsonSchemaResponse(Action.UploadSchema, id, Status.Success, None)
  object UploadSuccess {
    implicit val encoder: Encoder[UploadSuccess] = deriveEncoder
    implicit val decoder: Decoder[UploadSuccess] = deriveDecoder
  }

  case class UploadError(id: SchemaId, message: ErrorMessage)
    extends JsonSchemaResponse(Action.UploadSchema, id, Status.Error, Some(message))
  object UploadError {
    implicit val encoder: Encoder[UploadError] = deriveEncoder
    implicit val decoder: Decoder[UploadError] = deriveDecoder
  }

  case class ValidationSuccess(id: SchemaId)
    extends JsonSchemaResponse(Action.ValidateDocument, id, Status.Success, None)
  object ValidationSuccess {
    implicit val encoder: Encoder[ValidationSuccess] = deriveEncoder
    implicit val decoder: Decoder[ValidationSuccess] = deriveDecoder
  }

  case class ValidationError(id: SchemaId, message: ErrorMessage)
    extends JsonSchemaResponse(Action.ValidateDocument, id, Status.Error, Some(message))
  object ValidationError {
    implicit val encoder: Encoder[ValidationError] = deriveEncoder
    implicit val decoder: Decoder[ValidationError] = deriveDecoder
  }

  implicit val decoder: Decoder[JsonSchemaResponse] = deriveDecoder[JsonSchemaResponse]
  implicit val encoder: Encoder[JsonSchemaResponse] = deriveEncoder[JsonSchemaResponse]

}
