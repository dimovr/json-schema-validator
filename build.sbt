// *****************************************************************************
// Build settings
// *****************************************************************************

inThisBuild(
  Seq(
    scalaVersion := "2.13.8",
    organization := "com.knottech",
    organizationName := "Knot Tech",
    startYear := Some(2022),
    licenses += ("MPL-2.0", url("https://www.mozilla.org/en-US/MPL/2.0/")),
    testFrameworks += new TestFramework("munit.Framework"),
    Test / parallelExecution := false,
    dynverSeparator   := "_", // the default `+` is not compatible with docker tags
    scalacOptions ++= Seq(
      "-deprecation",
      "-explaintypes",
      "-feature",
      "-language:higherKinds",
      "-unchecked",
      "-Xcheckinit",
      //"-Xfatal-warnings", // Should be enabled if feasible.
      "-Xlint:adapted-args",
      "-Xlint:constant",
      "-Xlint:delayedinit-select",
      "-Xlint:doc-detached",
      "-Xlint:inaccessible",
      "-Xlint:infer-any",
      "-Xlint:missing-interpolator",
      "-Xlint:nullary-unit",
      "-Xlint:option-implicit",
      "-Xlint:package-object-classes",
      "-Xlint:poly-implicit-overload",
      "-Xlint:private-shadow",
      "-Xlint:stars-align",
      "-Xlint:type-parameter-shadow",
      "-Yrangepos", // Needed to make `clue` work correctly (munit).
      "-Ywarn-dead-code",
      "-Ywarn-extra-implicit",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused:implicits",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:params",
      "-Ywarn-unused:patvars",
      "-Ywarn-unused:privates",
      "-Ywarn-value-discard",
      "-Ycache-plugin-class-loader:last-modified",
      "-Ycache-macro-class-loader:last-modified",
    ),
    Compile / console / scalacOptions --= Seq(
      "-Xfatal-warnings",
      "-Ywarn-unused-import",
      "-Ywarn-unused:implicits",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:params",
      "-Ywarn-unused:patvars",
      "-Ywarn-unused:privates"
    ),
    Compile / compile / wartremoverWarnings ++= Warts.unsafe.filterNot(_ == Wart.Any), // Disable the "Any" wart due to too many false positives.
    Test / console / scalacOptions --= Seq(
      "-Xfatal-warnings",
      "-Ywarn-unused-import",
      "-Ywarn-unused:implicits",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:params",
      "-Ywarn-unused:patvars",
      "-Ywarn-unused:privates"
    )
  )
)

// *****************************************************************************
// Projects
// *****************************************************************************

lazy val jsonvalidator =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .configs(IntegrationTest)
    .settings(
      name := "json-validator"
    )
    .settings(settings)
    .settings(
      Defaults.itSettings,
      headerSettings(IntegrationTest),
      inConfig(IntegrationTest)(scalafmtSettings),
      IntegrationTest / console / scalacOptions --= Seq(
        "-Xfatal-warnings",
        "-Ywarn-unused-import",
        "-Ywarn-unused:implicits",
        "-Ywarn-unused:imports",
        "-Ywarn-unused:locals",
        "-Ywarn-unused:params",
        "-Ywarn-unused:patvars",
        "-Ywarn-unused:privates"
      ),
      IntegrationTest / parallelExecution := false
    )
    .settings(
      libraryDependencies ++= Seq(
        library.catsCore,
        library.circeCore,
        library.circeGeneric,
        library.circeRefined,
        library.circeParser,
        library.doobieCore,
        library.doobieHikari,
        library.doobiePostgres,
        library.doobieRefined,
        library.enumeratumCirce,
        library.flywayCore,
        library.http4sCirce,
        library.http4sDsl,
        library.http4sEmberClient,
        library.http4sEmberServer,
        library.logback,
        library.postgresql,
        library.pureConfig,
        library.refinedCats,
        library.refinedCore,
        library.refinedPureConfig,
        library.tapirCats,
        library.tapirCirce,
        library.tapirCore,
        library.tapirEnumeratum,
        library.tapirHttp4s,
        library.tapirOpenApiDocs,
        library.tapirOpenApiYaml,
        library.tapirRefined,
        library.tapirSwaggerUi,
        library.munit             % IntegrationTest,
        library.munitCatsEffect   % IntegrationTest,
        library.munitScalaCheck   % IntegrationTest,
        library.refinedScalaCheck % IntegrationTest,
        library.scalaCheck        % IntegrationTest,
        library.munit             % Test,
        library.munitCatsEffect   % Test,
        library.munitScalaCheck   % Test,
        library.refinedScalaCheck % Test,
        library.scalaCheck        % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val cats            = "2.7.0"
      val circe           = "0.14.2"
      val doobie          = "0.13.4"
      val enumeratum      = "1.7.0"
      val flyway          = "8.5.12"
      val http4s          = "0.22.8"
      val logback         = "1.2.11"
      val munit           = "0.7.29"
      val munitCatsEffect = "1.0.7"
      val postgresql      = "42.4.0"
      val pureConfig      = "0.17.1"
      val refined         = "0.9.29"
      val scalaCheck      = "1.15.4"
      val tapir           = "0.18.3"
    }
    val catsCore          = "org.typelevel"               %% "cats-core"                % Version.cats
    val circeCore         = "io.circe"                    %% "circe-core"               % Version.circe
    val circeGeneric      = "io.circe"                    %% "circe-generic"            % Version.circe
    val circeRefined      = "io.circe"                    %% "circe-refined"            % Version.circe
    val circeParser       = "io.circe"                    %% "circe-parser"             % Version.circe
    val doobieCore        = "org.tpolecat"                %% "doobie-core"              % Version.doobie
    val doobieHikari      = "org.tpolecat"                %% "doobie-hikari"            % Version.doobie
    val doobiePostgres    = "org.tpolecat"                %% "doobie-postgres"          % Version.doobie
    val doobieRefined     = "org.tpolecat"                %% "doobie-refined"           % Version.doobie
    val doobieScalaTest   = "org.tpolecat"                %% "doobie-scalatest"         % Version.doobie
    val enumeratumCirce   = "com.beachape"                %% "enumeratum-circe"         % Version.enumeratum
    val flywayCore        = "org.flywaydb"                %  "flyway-core"              % Version.flyway
    val http4sCirce       = "org.http4s"                  %% "http4s-circe"             % Version.http4s
    val http4sDsl         = "org.http4s"                  %% "http4s-dsl"               % Version.http4s
    val http4sEmberServer = "org.http4s"                  %% "http4s-ember-server"      % Version.http4s
    val http4sEmberClient = "org.http4s"                  %% "http4s-ember-client"      % Version.http4s
    val logback           = "ch.qos.logback"              %  "logback-classic"          % Version.logback
    val munit             = "org.scalameta"               %% "munit"                    % Version.munit
    val munitCatsEffect   = "org.typelevel"               %% "munit-cats-effect-2"      % Version.munitCatsEffect
    val munitScalaCheck   = "org.scalameta"               %% "munit-scalacheck"         % Version.munit
    val postgresql        = "org.postgresql"              %  "postgresql"               % Version.postgresql
    val pureConfig        = "com.github.pureconfig"       %% "pureconfig"               % Version.pureConfig
    val refinedCore       = "eu.timepit"                  %% "refined"                  % Version.refined
    val refinedCats       = "eu.timepit"                  %% "refined-cats"             % Version.refined
    val refinedPureConfig = "eu.timepit"                  %% "refined-pureconfig"       % Version.refined
    val refinedScalaCheck = "eu.timepit"                  %% "refined-scalacheck"       % Version.refined
    val scalaCheck        = "org.scalacheck"              %% "scalacheck"               % Version.scalaCheck
    val tapirCats         = "com.softwaremill.sttp.tapir" %% "tapir-cats"               % Version.tapir
    val tapirCirce        = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % Version.tapir
    val tapirCore         = "com.softwaremill.sttp.tapir" %% "tapir-core"               % Version.tapir
    val tapirEnumeratum   = "com.softwaremill.sttp.tapir" %% "tapir-enumeratum"         % Version.tapir
    val tapirHttp4s       = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % Version.tapir
    val tapirOpenApiDocs  = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % Version.tapir
    val tapirOpenApiYaml  = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Version.tapir
    val tapirRefined      = "com.softwaremill.sttp.tapir" %% "tapir-refined"            % Version.tapir
    val tapirSwaggerUi    = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % Version.tapir
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  scalafmtSettings ++
  scoverageSettings

lazy val commonSettings =
  Seq(
    libraryDependencies ++= (
      if (scalaVersion.value.startsWith("2")) {
        Seq(
          compilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
          compilerPlugin("org.typelevel" % "kind-projector"      % "0.13.2" cross CrossVersion.full)
        )
      } else {
        Seq()
      }
    )
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := false,
  )

lazy val scoverageSettings =
  Seq(
    coverageMinimumStmtTotal := 60,
    coverageFailOnMinimum := false,
    coverageHighlighting := true
  )

