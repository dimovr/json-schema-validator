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
  * A simple model for our hello world greetings.
  *
  * @param action An action performed: uploadSchema or validateDocument.
  * @param id The id of the json schema uploaded.
  * @param status The status of the action representing the outcome: success or error.
  * @param message  An error message if status is 'error' message for the user.
  */

sealed trait JsonSchemaResponse

object JsonSchemaResponse {

  case class UploadSuccess private (action: Action, id: SchemaId, status: Status) extends JsonSchemaResponse
  object UploadSuccess {
    def apply(id: SchemaId): UploadSuccess =
      UploadSuccess(Action.UploadSchema, id, Status.Success)

    implicit val encoder: Encoder[UploadSuccess] = deriveEncoder
    implicit val decoder: Decoder[UploadSuccess] = deriveDecoder
  }

  case class UploadError private (action: Action, id: SchemaId, status: Status, message: ErrorMessage) extends JsonSchemaResponse
  object UploadError {
    def apply(id: SchemaId, message: ErrorMessage): UploadError =
      UploadError(Action.UploadSchema, id, Status.Error, message)

    implicit val encoder: Encoder[UploadError] = deriveEncoder
    implicit val decoder: Decoder[UploadError] = deriveDecoder
  }

  case class ValidationSuccess private (action: Action, id: SchemaId, status: Status) extends JsonSchemaResponse
  object ValidationSuccess {
    def apply(id: SchemaId): ValidationSuccess =
      ValidationSuccess(Action.ValidateDocument, id, Status.Success)

    implicit val encoder: Encoder[ValidationSuccess] = deriveEncoder
    implicit val decoder: Decoder[ValidationSuccess] = deriveDecoder
  }

  case class ValidationError private (action: Action, id: SchemaId, status: Status, message: ErrorMessage) extends JsonSchemaResponse
  object ValidationError {
    def apply(id: SchemaId, message: ErrorMessage): ValidationError =
      ValidationError(Action.ValidateDocument, id, Status.Error, message)

    implicit val encoder: Encoder[ValidationError] = deriveEncoder
    implicit val decoder: Decoder[ValidationError] = deriveDecoder
  }

}
