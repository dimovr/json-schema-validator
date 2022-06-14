import sbt._

object Dependencies {
  val jsonValidator     = "com.github.java-json-tools"  % "json-schema-validator"     % Versions.jsonValidator
  val catsCore          = "org.typelevel"               %% "cats-core"                % Versions.cats
  val circeCore         = "io.circe"                    %% "circe-core"               % Versions.circe
  val circeGeneric      = "io.circe"                    %% "circe-generic"            % Versions.circe
  val circeRefined      = "io.circe"                    %% "circe-refined"            % Versions.circe
  val circeParser       = "io.circe"                    %% "circe-parser"             % Versions.circe
  val doobieCore        = "org.tpolecat"                %% "doobie-core"              % Versions.doobie
  val doobieHikari      = "org.tpolecat"                %% "doobie-hikari"            % Versions.doobie
  val doobiePostgres    = "org.tpolecat"                %% "doobie-postgres"          % Versions.doobie
  val doobieRefined     = "org.tpolecat"                %% "doobie-refined"           % Versions.doobie
  val doobieScalaTest   = "org.tpolecat"                %% "doobie-scalatest"         % Versions.doobie
  val enumeratumCirce   = "com.beachape"                %% "enumeratum-circe"         % Versions.enumeratum
  val flywayCore        = "org.flywaydb"                %  "flyway-core"              % Versions.flyway
  val http4sCirce       = "org.http4s"                  %% "http4s-circe"             % Versions.http4s
  val http4sDsl         = "org.http4s"                  %% "http4s-dsl"               % Versions.http4s
  val http4sEmberServer = "org.http4s"                  %% "http4s-ember-server"      % Versions.http4s
  val http4sEmberClient = "org.http4s"                  %% "http4s-ember-client"      % Versions.http4s
  val logback           = "ch.qos.logback"              %  "logback-classic"          % Versions.logback
  val munit             = "org.scalameta"               %% "munit"                    % Versions.munit
  val munitCatsEffect   = "org.typelevel"               %% "munit-cats-effect-2"      % Versions.munitCatsEffect
  val munitScalaCheck   = "org.scalameta"               %% "munit-scalacheck"         % Versions.munit
  val postgresql        = "org.postgresql"              %  "postgresql"               % Versions.postgresql
  val pureConfig        = "com.github.pureconfig"       %% "pureconfig"               % Versions.pureConfig
  val refinedCore       = "eu.timepit"                  %% "refined"                  % Versions.refined
  val refinedCats       = "eu.timepit"                  %% "refined-cats"             % Versions.refined
  val refinedPureConfig = "eu.timepit"                  %% "refined-pureconfig"       % Versions.refined
  val refinedScalaCheck = "eu.timepit"                  %% "refined-scalacheck"       % Versions.refined
  val scalaCheck        = "org.scalacheck"              %% "scalacheck"               % Versions.scalaCheck
  val tapirCats         = "com.softwaremill.sttp.tapir" %% "tapir-cats"               % Versions.tapir
  val tapirCirce        = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % Versions.tapir
  val tapirCore         = "com.softwaremill.sttp.tapir" %% "tapir-core"               % Versions.tapir
  val tapirEnumeratum   = "com.softwaremill.sttp.tapir" %% "tapir-enumeratum"         % Versions.tapir
  val tapirHttp4s       = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % Versions.tapir
  val tapirOpenApiDocs  = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % Versions.tapir
  val tapirOpenApiYaml  = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Versions.tapir
  val tapirRefined      = "com.softwaremill.sttp.tapir" %% "tapir-refined"            % Versions.tapir
  val tapirSwaggerUi    = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % Versions.tapir
  
  val all = Seq(
    jsonValidator,
    catsCore,
    circeCore,
    circeGeneric,
    circeRefined,
    circeParser,
    doobieCore,
    doobieHikari,
    doobiePostgres,
    doobieRefined,
    enumeratumCirce,
    flywayCore,
    http4sCirce,
    http4sDsl,
    http4sEmberClient,
    http4sEmberServer,
    logback,
    postgresql,
    pureConfig,
    refinedCats,
    refinedCore,
    refinedPureConfig,
    tapirCats,
    tapirCirce,
    tapirCore,
    tapirEnumeratum,
    tapirHttp4s,
    tapirOpenApiDocs,
    tapirOpenApiYaml,
    tapirRefined,
    tapirSwaggerUi,
    munit             % IntegrationTest,
    munitCatsEffect   % IntegrationTest,
    munitScalaCheck   % IntegrationTest,
    refinedScalaCheck % IntegrationTest,
    scalaCheck        % IntegrationTest,
    munit             % Test,
    munitCatsEffect   % Test,
    munitScalaCheck   % Test,
    refinedScalaCheck % Test,
    scalaCheck        % Test
  )
}
