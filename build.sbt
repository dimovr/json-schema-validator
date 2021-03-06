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
      "-Xlog-implicits",
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

lazy val jsonSchemaValidator =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .configs(IntegrationTest)
    .settings(
      name := "json-schema-validator"
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
      libraryDependencies ++= Dependencies.all
    )

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

