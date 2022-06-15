# json-schema-validator #

A REST-service for validating JSON documents against JSON Schemas.

This REST-service allows users to upload JSON Schemas and store them at unique URI and then validate JSON documents against these URIs.

Additionally, this service will "cleans" every JSON document before validation: removes keys for which the value is `null`.

The validation is performed using a 3-rd pary library [json-schema-validator](https://github.com/java-json-tools/json-schema-validator). 

## Pre-requisites

## System requirements ##

- Java 11
- Scala 2.13.8
- sbt 1.6.12

## Deployment guide ##

In order to start the service execute the following command in the project root directory:
``` sbt "compile; test; run```

If you see the following log lines the service is ready to use:
```
[info] running com.knottech.jsonvalidator.Server 
2022-06-14 20:02:36,148 INFO  EmberServerBuilder - Ember-Server service bound to address: /127.0.0.1:8080
2022-06-14 20:02:36,150 INFO  Server$ - Server started at /127.0.0.1:8080
```


## API Specification ##
The primary interface of application is REST (JSON over HTTP).

### Endpoints  ###
```
POST    /schema/SCHEMAID        - Upload a JSON Schema with unique `SCHEMAID`
GET     /schema/SCHEMAID        - Download a JSON Schema with unique `SCHEMAID`

POST    /validate/SCHEMAID      - Validate a JSON document against the JSON Schema identified by `SCHEMAID`
```
### Responses  ###
All possible responses should be valid JSON documents.

#### Valid JSON Schema Upload ####
This should contain Schema id, action and status.
```
{
    "action": "uploadSchema",
    "id": "config-schema",
    "status": "success"
}
```
#### Invalid JSON Schema Upload ####
It isn't necessary to check whether the uploaded JSON is a valid JSON Schema v4 (many validation libraries dont allow it), but it is required to check whether the document is valid JSON.
```
{
    "action": "uploadSchema",
    "id": "config-schema",
    "status": "error",
    "message": "Invalid JSON"
}
```
#### JSON document was successfully validated ####
```
{
    "action": "validateDocument",
    "id": "config-schema",
    "status": "success"
}
```

#### JSON document is invalid against JSON Schema ####
The returned message should contain a human-readable string or machine-readable JSON document indicating the error encountered. The exact format can be chosen based on the validator library's features.
```
{
    "action": "validateDocument",
    "id": "config-schema",
    "status": "error",
    "message": "Property '/root/timeout' is required"
}
```

### Use case ###
Schema validation
The potential user has a configuration JSON file config.json like the following:
```
{
  "source": "/home/alice/image.iso",
  "destination": "/mnt/storage",
  "timeout": null,
  "chunks": {
    "size": 1024,
    "number": null
  }
}
```
And expects it conforms to the following JSON Schema config-schema.json:
```
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "type": "object",
  "properties": {
    "source": {
      "type": "string"
    },
    "destination": {
      "type": "string"
    },
    "timeout": {
      "type": "integer",
      "minimum": 0,
      "maximum": 32767
    },
    "chunks": {
      "type": "object",
      "properties": {
        "size": {
          "type": "integer"
        },
        "number": {
          "type": "integer"
        }
      },
      "required": ["size"]
    }
  },
  "required": ["source", "destination"]
}
```

To check that it really fits the schema:

The user should upload the JSON Schema:
 ```curl http://localhost:8080/schema/config-schema -X POST -d @config-schema.json```

The server should respond with:
`{"action": "uploadSchema", "id": "config-schema", "status": "success"}` and status code `201`.

The user should upload the JSON document to validate it
 `curl http://localhost:8080/validate/config-schema -X POST -d @config.json`

The server should "clean" the uploaded JSON document to remove keys for which the value is `null`:
```{
  "source": "/home/alice/image.iso",
  "destination": "/mnt/storage",
  "chunks": {
    "size": 1024
  }
}
```

The server should respond with: `{"action": "validateDocument", "id": "config-schema", "status": "success"}` and status code `200`


## License & Copyright ##

The project was created based on the following [gitter template](https://codeberg.org/wegtam/http4s-app.g8).

This code is licensed under the Mozilla Public License Version 2.0, see the
[LICENSE](LICENSE) file for details.

