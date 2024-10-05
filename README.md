# Violations Command Line

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.bjurr.violations/violations-command-line/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.bjurr.violations/violations-command-line)
[![NPM](https://img.shields.io/npm/v/violations-command-line.svg?style=flat-square) ](https://www.npmjs.com/package/violations-command-line)
[![NPM Downloads](https://img.shields.io/npm/dm/violations-command-line.svg?style=flat)](https://www.npmjs.com/package/violations-command-line)
[![Docker Pulls](https://badgen.net/docker/pulls/tomasbjerre/violations-command-line?icon=docker&label=pulls)](https://hub.docker.com/r/tomasbjerre/violations-command-line/)

This is a command line tool that will find report files from static code analysis, present and optionally fail the command. It uses the [Violations Lib](https://github.com/tomasbjerre/violations-lib).

- The runnable can be found in [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22violations-command-line%22)
- or [NPM](https://www.npmjs.com/package/violations-command-line).
- The `Docker` image can be found in [Dockerhub](https://hub.docker.com/r/tomasbjerre/violations-command-line)
  - Can used like `docker run --mount src="$(pwd)",target=/home/violations-command-line,type=bind tomasbjerre/violations-command-line:a.b.c -v "FINDBUGS" src/test/resources/findbugs/ ".*main\.xml$" "Spotbugs"`.
  - Or open a shell to have a look `docker run --rm -it --entrypoint sh tomasbjerre/violations-command-line:a.b.c`

| Version           | Java Version |
| ------------------| ------------ |
| version < 2.0.0   | 8            |
| 2.0.0 <= version  | 11           |

Run it with:

```bash
npx violations-command-line -s ERROR -mv 0 \
 -v "CHECKSTYLE" "." ".*checkstyle/main\.xml$" "Checkstyle" \
 -v "JSLINT" "." ".*jshint/report\.xml$" "JSHint"
```

It can parse results from static code analysis and:

- Report violations in the build log.
- Export to a normalized JSON format.

```bash
npx violations-command-line -vf violations-report.json \
 -v "CHECKSTYLE" "." ".*checkstyle/main\.xml$" "Checkstyle"
```

- Export to CodeClimate JSON.

```bash
npx violations-command-line -cc code-climate-report.json \
 -v "CHECKSTYLE" "." ".*checkstyle/main\.xml$" "Checkstyle"
```

- Export to Sarif JSON.

```bash
npx violations-command-line -sa sarif-report.json \
 -v "CHECKSTYLE" "." ".*checkstyle/main\.xml$" "Checkstyle"
```

- Optionally fail the build depending on violations found.

A snippet of the output may look like this:

```
...
se/bjurr/violations/lib/example/OtherClass.java
╔══════════╤════════════╤══════════╤══════╤════════════════════════════════════════════════════╗
║ Reporter │ Rule       │ Severity │ Line │ Message                                            ║
╠══════════╪════════════╪══════════╪══════╪════════════════════════════════════════════════════╣
║ Findbugs │ MS_SHOULD_ │ INFO     │ 7    │ Field isn't final but should be                    ║
║          │ BE_FINAL   │          │      │                                                    ║
║          │            │          │      │                                                    ║
║          │            │          │      │    <p>                                             ║
║          │            │          │      │ This static field public but not final, and        ║
║          │            │          │      │ could be changed by malicious code or              ║
║          │            │          │      │         by accident from another package.          ║
║          │            │          │      │         The field could be made final to avoid     ║
║          │            │          │      │         this vulnerability.</p>                    ║
╟──────────┼────────────┼──────────┼──────┼────────────────────────────────────────────────────╢
║ Findbugs │ NM_FIELD_N │ INFO     │ 6    │ Field names should start with a lower case letter  ║
║          │ AMING_CONV │          │      │                                                    ║
║          │ ENTION     │          │      │                                                    ║
║          │            │          │      │   <p>                                              ║
║          │            │          │      │ Names of fields that are not final should be in mi ║
║          │            │          │      │ xed case with a lowercase first letter and the fir ║
║          │            │          │      │ st letters of subsequent words capitalized.        ║
║          │            │          │      │ </p>                                               ║
╚══════════╧════════════╧══════════╧══════╧════════════════════════════════════════════════════╝

Summary of se/bjurr/violations/lib/example/OtherClass.java
╔══════════╤══════╤══════╤═══════╤═══════╗
║ Reporter │ INFO │ WARN │ ERROR │ Total ║
╠══════════╪══════╪══════╪═══════╪═══════╣
║ Findbugs │ 2    │ 0    │ 0     │ 2     ║
╟──────────┼──────┼──────┼───────┼───────╢
║          │ 2    │ 0    │ 0     │ 2     ║
╚══════════╧══════╧══════╧═══════╧═══════╝


Summary
╔════════════╤══════╤══════╤═══════╤═══════╗
║ Reporter   │ INFO │ WARN │ ERROR │ Total ║
╠════════════╪══════╪══════╪═══════╪═══════╣
║ Checkstyle │ 4    │ 1    │ 1     │ 6     ║
╟────────────┼──────┼──────┼───────┼───────╢
║ Findbugs   │ 2    │ 2    │ 5     │ 9     ║
╟────────────┼──────┼──────┼───────┼───────╢
║            │ 6    │ 3    │ 6     │ 15    ║
╚════════════╧══════╧══════╧═══════╧═══════╝
```

## GitHub

GitHub is supported via `SARIF`. This tool can export `SARIF` format and it can be uploaded to Github to get feedback in pull-requests.

```yml
name: My workflow

on: [workflow_call, push, pull_request]

jobs:
  build:
    permissions:
      security-events: write
      actions: read
      contents: read
    steps:
      - name: Build
        run: |
          your-build-command-here
      - name: Transorm static code analysis to SARIF
        if: success() || failure()
        run: |
          npx violations-command-line -sarif sarif-report.json \
          -v "FINDBUGS" "." ".*spotbugs/main\.xml$" "Spotbugs" \
          -v "CHECKSTYLE" "." ".*checkstyle/main\.xml$" "Checkstyle" \
          -v "PMD" "." ".*pmd/main\.xml$" "PMD" \
          -v "JUNIT" "." ".*test/TEST-.*\.xml$" "JUNIT"
      - uses: github/codeql-action/upload-sarif@v3
        if: success() || failure()
        with:
          sarif_file: sarif-report.json
          category: violations-lib
```

## GitLab

GitLab is supported via `CodeClimate`. This tool can export `CodeClimate` format and it can be uploaded to GitLab to get feedback in pull-requests.

If you export `CodeClimate` like this:

```sh
npx violations-command-line -cc code-climate-report.json \
  -v "FINDBUGS" "." ".*spotbugs/main\.xml$" "Spotbugs" \
  -v "CHECKSTYLE" "." ".*checkstyle/main\.xml$" "Checkstyle" \
  -v "PMD" "." ".*pmd/main\.xml$" "PMD" \
  -v "JUNIT" "." ".*test/TEST-.*\.xml$" "JUNIT"
```

You can upload it like this:

```yml
  artifacts:
    paths:
      - code-climate-report.json
    reports:
      codequality: code-climate-report.json
```

## Formats


Example of supported reports are available [here](https://github.com/tomasbjerre/violations-lib/tree/master/src/test/resources).

A number of **parsers** have been implemented. Some **parsers** can parse output from several **reporters**.

| Reporter | Parser | Notes
| --- | --- | ---
| [_ARM-GCC_](https://developer.arm.com/open-source/gnu-toolchain/gnu-rm)               | `CLANG`              | 
| [_AndroidLint_](http://developer.android.com/tools/help/lint.html)                    | `ANDROIDLINT`        | 
| [_Ansible-Later_](https://github.com/thegeeklab/ansible-later)                        | `ANSIBLELATER`       | With `json` format
| [_AnsibleLint_](https://github.com/willthames/ansible-lint)                           | `FLAKE8`             | With `-p`
| [_Bandit_](https://github.com/PyCQA/bandit)                                           | `CLANG`              | With `bandit -r examples/ -f custom -o bandit.out --msg-template "{abspath}:{line}: {severity}: {test_id}: {msg}"`
| [_CLang_](https://clang-analyzer.llvm.org/)                                           | `CLANG`              | 
| [_CPD_](http://pmd.sourceforge.net/pmd-4.3.0/cpd.html)                                | `CPD`                | 
| [_CPPCheck_](http://cppcheck.sourceforge.net/)                                        | `CPPCHECK`           | With `cppcheck test.cpp --output-file=cppcheck.xml --xml`
| [_CPPLint_](https://github.com/theandrewdavis/cpplint)                                | `CPPLINT`            | 
| [_CSSLint_](https://github.com/CSSLint/csslint)                                       | `CSSLINT`            | 
| [_Checkstyle_](http://checkstyle.sourceforge.net/)                                    | `CHECKSTYLE`         | 
| [_CloudFormation Linter_](https://github.com/aws-cloudformation/cfn-lint)             | `JUNIT`              | `cfn-lint . -f junit --output-file report-junit.xml`
| [_CodeClimate_](https://codeclimate.com/)                                             | `CODECLIMATE`        | 
| [_CodeNarc_](http://codenarc.sourceforge.net/)                                        | `CODENARC`           | 
| [_Coverity_](https://scan.coverity.com/)                                              | `COVERITY`           | 
| [_Dart_](https://dart.dev/)                                                           | `MACHINE`            | With `dart analyze --format=machine`
| [_Dependency Check_](https://jeremylong.github.io/DependencyCheck/)                   | `SARIF`              | Using `--format SARIF`
| [_Detekt_](https://github.com/arturbosch/detekt)                                      | `CHECKSTYLE`         | With `--output-format xml`.
| [_DocFX_](http://dotnet.github.io/docfx/)                                             | `DOCFX`              | 
| [_Doxygen_](https://www.stack.nl/~dimitri/doxygen/)                                   | `CLANG`              | 
| [_ERB_](https://www.puppetcookbook.com/posts/erb-template-validation.html)            | `CLANG`              | With `erb -P -x -T '-' "${it}" \| ruby -c 2>&1 >/dev/null \| grep '^-' \| sed -E 's/^-([a-zA-Z0-9:]+)/${filename}\1 ERROR:/p' > erbfiles.out`.
| [_ESLint_](https://github.com/sindresorhus/grunt-eslint)                              | `CHECKSTYLE`         | With `format: 'checkstyle'`.
| [_Findbugs_](http://findbugs.sourceforge.net/)                                        | `FINDBUGS`           | 
| [_Flake8_](http://flake8.readthedocs.org/en/latest/)                                  | `FLAKE8`             | 
| [_FxCop_](https://en.wikipedia.org/wiki/FxCop)                                        | `FXCOP`              | 
| [_GCC_](https://gcc.gnu.org/)                                                         | `CLANG`              | 
| [_GHS_](https://www.ghs.com/)                                                         | `GHS`                | 
| [_Gendarme_](http://www.mono-project.com/docs/tools+libraries/tools/gendarme/)        | `GENDARME`           | 
| [_Generic reporter_]()                                                                | `GENERIC`            | Will create one single violation with all the content as message.
| [_GoLint_](https://github.com/golang/lint)                                            | `GOLINT`             | 
| [_GoVet_](https://golang.org/cmd/vet/)                                                | `GOLINT`             | Same format as GoLint.
| [_GolangCI-Lint_](https://github.com/golangci/golangci-lint/)                         | `CHECKSTYLE`         | With `--out-format=checkstyle`.
| [_GoogleErrorProne_](https://github.com/google/error-prone)                           | `GOOGLEERRORPRONE`   | 
| [_HadoLint_](https://github.com/hadolint/hadolint/)                                   | `CHECKSTYLE`         | With `-f checkstyle`
| [_IAR_](https://www.iar.com/iar-embedded-workbench/)                                  | `IAR`                | With `--no_wrap_diagnostics`
| [_Infer_](http://fbinfer.com/)                                                        | `PMD`                | Facebook Infer. With `--pmd-xml`.
| [_JACOCO_](https://www.jacoco.org/)                                                   | `JACOCO`             | 
| [_JCReport_](https://github.com/jCoderZ/fawkez/wiki/JcReport)                         | `JCREPORT`           | 
| [_JSHint_](http://jshint.com/)                                                        | `JSLINT`             | With `--reporter=jslint` or the CHECKSTYLE parser with `--reporter=checkstyle`
| [_JUnit_](https://junit.org/junit4/)                                                  | `JUNIT`              | It only contains the failures.
| [_KTLint_](https://github.com/shyiko/ktlint)                                          | `CHECKSTYLE`         | 
| [_Klocwork_](http://www.klocwork.com/products-services/klocwork/static-code-analysis)  | `KLOCWORK`           | 
| [_KotlinGradle_](https://github.com/JetBrains/kotlin)                                 | `KOTLINGRADLE`       | Output from Kotlin Gradle Plugin.
| [_KotlinMaven_](https://github.com/JetBrains/kotlin)                                  | `KOTLINMAVEN`        | Output from Kotlin Maven Plugin.
| [_Lint_]()                                                                            | `LINT`               | A common XML format, used by different linters.
| [_MSBuildLog_](https://docs.microsoft.com/en-us/visualstudio/msbuild/obtaining-build-logs-with-msbuild?view=vs-2019)  | `MSBULDLOG`          | With `-fileLogger` use `.*msbuild\\.log$` as pattern or `-fl -flp:logfile=MyProjectOutput.log;verbosity=diagnostic` for a custom output filename
| [_MSCpp_](https://visualstudio.microsoft.com/vs/features/cplusplus/)                  | `MSCPP`              | 
| [_Mccabe_](https://pypi.python.org/pypi/mccabe)                                       | `FLAKE8`             | 
| [_MyPy_](https://pypi.python.org/pypi/mypy-lang)                                      | `MYPY`               | 
| [_NullAway_](https://github.com/uber/NullAway)                                        | `GOOGLEERRORPRONE`   | Same format as Google Error Prone.
| [_PCLint_](http://www.gimpel.com/html/pcl.htm)                                        | `PCLINT`             | PC-Lint using the same output format as the Jenkins warnings plugin, [_details here_](https://wiki.jenkins.io/display/JENKINS/PcLint+options)
| [_PHPCS_](https://github.com/squizlabs/PHP_CodeSniffer)                               | `CHECKSTYLE`         | With `phpcs api.php --report=checkstyle`.
| [_PHPPMD_](https://phpmd.org/)                                                        | `PMD`                | With `phpmd api.php xml ruleset.xml`.
| [_PMD_](https://pmd.github.io/)                                                       | `PMD`                | 
| [_Pep8_](https://github.com/PyCQA/pycodestyle)                                        | `FLAKE8`             | 
| [_PerlCritic_](https://github.com/Perl-Critic)                                        | `PERLCRITIC`         | 
| [_PiTest_](http://pitest.org/)                                                        | `PITEST`             | 
| [_ProtoLint_](https://github.com/yoheimuta/protolint)                                 | `PROTOLINT`          | 
| [_Puppet-Lint_](http://puppet-lint.com/)                                              | `CLANG`              | With `-log-format %{fullpath}:%{line}:%{column}: %{kind}: %{message}`
| [_PyDocStyle_](https://pypi.python.org/pypi/pydocstyle)                               | `PYDOCSTYLE`         | 
| [_PyFlakes_](https://pypi.python.org/pypi/pyflakes)                                   | `FLAKE8`             | 
| [_PyLint_](https://www.pylint.org/)                                                   | `PYLINT`             | With `pylint --output-format=parseable`.
| [_ReSharper_](https://www.jetbrains.com/resharper/)                                   | `RESHARPER`          | 
| [_RubyCop_](http://rubocop.readthedocs.io/en/latest/formatters/)                      | `CLANG`              | With `rubycop -f clang file.rb`
| [_SARIF_](https://github.com/oasis-tcs/sarif-spec)                                    | `SARIF`              | v2.x. Microsoft Visual C# can generate it with `ErrorLog="BuildErrors.sarif,version=2"`.
| [_SbtScalac_](http://www.scala-sbt.org/)                                              | `SBTSCALAC`          | 
| [_Scalastyle_](http://www.scalastyle.org/)                                            | `CHECKSTYLE`         | 
| [_Semgrep_](https://semgrep.dev/)                                                     | `SEMGREP`            | With `--json`.
| [_Simian_](http://www.harukizaemon.com/simian/)                                       | `SIMIAN`             | 
| [_Sonar_](https://www.sonarqube.org/)                                                 | `SONAR`              | With `mvn sonar:sonar -Dsonar.analysis.mode=preview -Dsonar.report.export.path=sonar-report.json`. Removed in 7.7, see [SONAR-11670](https://jira.sonarsource.com/browse/SONAR-11670) but can be retrieved with: `curl --silent 'http://sonar-server/api/issues/search?componentKeys=unique-key&resolved=false' \| jq -f sonar-report-builder.jq > sonar-report.json`.
| [_Spotbugs_](https://spotbugs.github.io/)                                             | `FINDBUGS`           | 
| [_StyleCop_](https://stylecop.codeplex.com/)                                          | `STYLECOP`           | 
| [_SwiftLint_](https://github.com/realm/SwiftLint)                                     | `CHECKSTYLE`         | With `--reporter checkstyle`.
| [_TSLint_](https://palantir.github.io/tslint/usage/cli/)                              | `CHECKSTYLE`         | With `-t checkstyle`
| [_Valgrind_](https://valgrind.org/)                                                   | `VALGRIND`           | With `--xml=yes`.
| [_XMLLint_](http://xmlsoft.org/xmllint.html)                                          | `XMLLINT`            | 
| [_XUnit_](https://xunit.net/)                                                         | `XUNIT`              | It only contains the failures.
| [_YAMLLint_](https://yamllint.readthedocs.io/en/stable/index.html)                    | `YAMLLINT`           | With `-f parsable`
| [_ZPTLint_](https://pypi.python.org/pypi/zptlint)                                     | `ZPTLINT`            |

52 parsers and 79 reporters.

Missing a format? Open an issue [here](https://github.com/tomasbjerre/violations-lib/issues)!

# Usage

```shell
Available parsers are:
ANDROIDLINT, ANSIBLELATER, CHECKSTYLE, CODENARC, CLANG, COVERITY, CPD, CPPCHECK, CPPLINT, CSSLINT, GENERIC, GHS, FINDBUGS, FLAKE8, MACHINE, FXCOP, GENDARME, IAR, JACOCO, JCREPORT, JSLINT, JUNIT, LINT, KLOCWORK, KOTLINMAVEN, KOTLINGRADLE, MSCPP, MSBULDLOG, MYPY, GOLINT, GOOGLEERRORPRONE, PERLCRITIC, PITEST, PMD, PROTOLINT, PYDOCSTYLE, PYLINT, RESHARPER, SARIF, SBTSCALAC, SEMGREP, SIMIAN, SONAR, STYLECOP, XMLLINT, YAMLLINT, ZPTLINT, DOCFX, PCLINT, CODECLIMATE, XUNIT, VALGRIND

Usage: violations-command-line [-dpv] [--help] [-pv] [-show-debug-info]
                               [-show-json-config] [-cc=<codeClimateFileArg>]
                               [-cf=<configFileArg>] [-ddl=<diffDetailLevel>]
                               [-df=<diffFrom>] [-dl=<detailLevelArg>]
                               [-dmv=<diffMaxViolations>]
                               [-ds=<diffMinSeverity>] [-dt=<diffTo>]
                               [-gr=<gitRepoArg>] [-jmc=<jacocoMinCoverage>]
                               [-jmlc=<jacocoMinLineCount>]
                               [-mlcw=<maxLineColumnWidth>]
                               [-mmcw=<maxMessageColumnWidth>]
                               [-mrcw=<maxReporterColumnWidth>]
                               [-mrucw=<maxRuleColumnWidth>]
                               [-mscw=<maxSeverityColumnWidth>]
                               [-mv=<maxViolationsArg>] [-s=<minSeverityArg>]
                               [-ss=<sarifFileArg>] [-vf=<violationsFileArg>]
                               [-v=<violationsArg>]...
      -cc, -code-climate=<codeClimateFileArg>
                          Create a CodeClimate file with all the violations.
      -cf, -config-file=<configFileArg>
                          Will read config from given file. Can also be
                            configured with environment variable
                            VIOLATIONS_CONFIG. Format is what you get from
                            -show-json-config.
      -ddl, -diff-detail-level=<diffDetailLevel>
                          VERBOSE, COMPACT, PER_FILE_COMPACT
      -df, -diff-from=<diffFrom>
                          Can be empty (ignored), Git-commit or any
                            Git-reference
      -dl, -detail-level=<detailLevelArg>
                          Verbosity VERBOSE, COMPACT, PER_FILE_COMPACT
      -dmv, -diff-max-violations=<diffMaxViolations>
                          Will fail the build if total number of found
                            violations is higher
      -dpv, -diff-print-violations
                          Will print violations found in diff
      -ds, -diff-severity=<diffMinSeverity>
                          INFO, WARN, ERROR
      -dt, -diff-to=<diffTo>
                          Can be empty (ignored), Git-commit or any
                            Git-reference
      -gr, -git-repo=<gitRepoArg>
                          Where to look for Git.
      --help              display this help and exit
      -jmc, -jacoco-min-coverage=<jacocoMinCoverage>
                          Minimum coverage in Jacoco that will generate a
                            violation.
      -jmlc, -jacoco-min-line-count=<jacocoMinLineCount>
                          Minimum line count in Jacoco that will generate a
                            violation.
      -mlcw, -max-line-column-width=<maxLineColumnWidth>
                          0 means no limit
      -mmcw, -max-message-column-width=<maxMessageColumnWidth>
                          0 means no limit
      -mrcw, -max-reporter-column-width=<maxReporterColumnWidth>
                          0 means no limit
      -mrucw, -max-rule-column-width=<maxRuleColumnWidth>
                          0 means no limit
      -mscw, -max-severity-column-width=<maxSeverityColumnWidth>
                          0 means no limit
      -mv, -max-violations=<maxViolationsArg>
                          Will fail the build if total number of found
                            violations is higher.
      -pv, -print-violations
                          Will print violations found
  -s, -severity=<minSeverityArg>
                          Minimum severity level to report. INFO, WARN, ERROR
      -show-debug-info    Please run your command with this parameter and
                            supply output when reporting bugs.
      -show-json-config   Will print the given config as JSON.
      -ss, -sarif=<sarifFileArg>
                          Create a Sarif file with all the violations.
  -v, --violations=<violationsArg>
                          Format: <PARSER> <FOLDER> <REGEXP PATTERN> <NAME>,
                            Example: -v "JSLINT" "." ".*/jshint.xml$" "JSHint"
      -vf, -violations-file=<violationsFileArg>
                          Create a JSON file with all the violations.
```

Checkout the [Violations Lib](https://github.com/tomasbjerre/violations-lib) for more documentation.
