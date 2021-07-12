# Violations Command Line

[![Build Status](https://travis-ci.org/tomasbjerre/violations-command-line.svg?branch=master)](https://travis-ci.org/tomasbjerre/violations-command-line)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.bjurr.violations/violations-command-line/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.bjurr.violations/violations-command-line)
[![NPM](https://img.shields.io/npm/v/violations-command-line.svg?style=flat-square) ](https://www.npmjs.com/package/violations-command-line)

This is a command line tool that will find report files from static code analysis, present and optionally fail the command. It uses the [Violations Lib](https://github.com/tomasbjerre/violations-lib).

The runnable can be found in [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22violations-command-line%22) or [NPM](https://www.npmjs.com/package/violations-command-line).

Run it with:

```bash
npx violations-command-line -s ERROR -mv 0 \
 -v "CHECKSTYLE" "." ".*checkstyle/main\.xml$" "Checkstyle" \
 -v "JSHINT" "." ".*jshint/report\.xml$" "JSHint"
```

It can parse results from static code analysis and:

 * Report violations in the build log.
 * Export to a normalized JSON format.
```bash
npx violations-command-line -vf violations-report.json \
 -v "CHECKSTYLE" "." ".*checkstyle/main\.xml$" "Checkstyle"
```
 * Export to CodeClimate JSON.
```bash
npx violations-command-line -cc code-climate-report.json \
 -v "CHECKSTYLE" "." ".*checkstyle/main\.xml$" "Checkstyle"
```
 * Optionally fail the build depending on violations found.

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

Example of supported reports are available [here](https://github.com/tomasbjerre/violations-lib/tree/master/src/test/resources).

A number of **parsers** have been implemented. Some **parsers** can parse output from several **reporters**.

| Reporter | Parser | Notes
| --- | --- | ---
| [_ARM-GCC_](https://developer.arm.com/open-source/gnu-toolchain/gnu-rm)               | `CLANG`              | 
| [_AndroidLint_](http://developer.android.com/tools/help/lint.html)                    | `ANDROIDLINT`        | 
| [_AnsibleLint_](https://github.com/willthames/ansible-lint)                           | `FLAKE8`             | With `-p`
| [_Bandit_](https://github.com/PyCQA/bandit)                                           | `CLANG`              | With `bandit -r examples/ -f custom -o bandit.out --msg-template "{abspath}:{line}: {severity}: {test_id}: {msg}"`
| [_CLang_](https://clang-analyzer.llvm.org/)                                           | `CLANG`              | 
| [_CPD_](http://pmd.sourceforge.net/pmd-4.3.0/cpd.html)                                | `CPD`                | 
| [_CPPCheck_](http://cppcheck.sourceforge.net/)                                        | `CPPCHECK`           | With `cppcheck test.cpp --output-file=cppcheck.xml --xml`
| [_CPPLint_](https://github.com/theandrewdavis/cpplint)                                | `CPPLINT`            | 
| [_CSSLint_](https://github.com/CSSLint/csslint)                                       | `CSSLINT`            | 
| [_Checkstyle_](http://checkstyle.sourceforge.net/)                                    | `CHECKSTYLE`         | 
| [_CodeClimate_](https://codeclimate.com/)                                             | `CODECLIMATE`        | 
| [_CodeNarc_](http://codenarc.sourceforge.net/)                                        | `CODENARC`           | 
| [_Detekt_](https://github.com/arturbosch/detekt)                                      | `CHECKSTYLE`         | With `--output-format xml`.
| [_DocFX_](http://dotnet.github.io/docfx/)                                             | `DOCFX`              | 
| [_Doxygen_](https://www.stack.nl/~dimitri/doxygen/)                                   | `CLANG`              | 
| [_ERB_](https://www.puppetcookbook.com/posts/erb-template-validation.html)            | `CLANG`              | With `erb -P -x -T '-' "${it}" \| ruby -c 2>&1 >/dev/null \| grep '^-' \| sed -E 's/^-([a-zA-Z0-9:]+)/${filename}\1 ERROR:/p' > erbfiles.out`.
| [_ESLint_](https://github.com/sindresorhus/grunt-eslint)                              | `CHECKSTYLE`         | With `format: 'checkstyle'`.
| [_Findbugs_](http://findbugs.sourceforge.net/)                                        | `FINDBUGS`           | 
| [_Flake8_](http://flake8.readthedocs.org/en/latest/)                                  | `FLAKE8`             | 
| [_FxCop_](https://en.wikipedia.org/wiki/FxCop)                                        | `FXCOP`              | 
| [_GCC_](https://gcc.gnu.org/)                                                         | `CLANG`              | 
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
| [_SbtScalac_](http://www.scala-sbt.org/)                                              | `SBTSCALAC`          | 
| [_Scalastyle_](http://www.scalastyle.org/)                                            | `CHECKSTYLE`         | 
| [_Simian_](http://www.harukizaemon.com/simian/)                                       | `SIMIAN`             | 
| [_Sonar_](https://www.sonarqube.org/)                                                 | `SONAR`              | With `mvn sonar:sonar -Dsonar.analysis.mode=preview -Dsonar.report.export.path=sonar-report.json`. Removed in 7.7, see [SONAR-11670](https://jira.sonarsource.com/browse/SONAR-11670) but can be retrieved with: `curl --silent 'http://sonar-server/api/issues/search?componentKeys=unique-key&resolved=false' \| jq -f sonar-report-builder.jq > sonar-report.json`.
| [_Spotbugs_](https://spotbugs.github.io/)                                             | `FINDBUGS`           | 
| [_StyleCop_](https://stylecop.codeplex.com/)                                          | `STYLECOP`           | 
| [_SwiftLint_](https://github.com/realm/SwiftLint)                                     | `CHECKSTYLE`         | With `--reporter checkstyle`.
| [_TSLint_](https://palantir.github.io/tslint/usage/cli/)                              | `CHECKSTYLE`         | With `-t checkstyle`
| [_XMLLint_](http://xmlsoft.org/xmllint.html)                                          | `XMLLINT`            | 
| [_XUnit_](https://xunit.net/)                                                         | `XUNIT`              | It only contains the failures.
| [_YAMLLint_](https://yamllint.readthedocs.io/en/stable/index.html)                    | `YAMLLINT`           | With `-f parsable`
| [_ZPTLint_](https://pypi.python.org/pypi/zptlint)                                     | `ZPTLINT`            |

Missing a format? Open an issue [here](https://github.com/tomasbjerre/violations-lib/issues)!

# Usage

```shell
-code-climate, -cc <path>                               Create a CodeClimate 
                                                        file with all the 
                                                        violations.
                                                        <path>: a file path
                                                        Default: /home/bjerre/workspace/violations/violations-command-line/.
-config-file, -cf <path>                                Will read config from 
                                                        given file. Can also be 
                                                        configured with environment 
                                                        variable VIOLATIONS_CONFIG. 
                                                        Format is what you get from -
                                                        show-json-config.
                                                        <path>: a file path
                                                        Default: /home/bjerre/workspace/violations/violations-command-line/.
-detail-level, -dl <ViolationsReporterDetailLevel>      Verbosity
                                                        <ViolationsReporterDetailLevel>: {VERBOSE | COMPACT | PER_FILE_COMPACT}
                                                        Default: VERBOSE
-diff-detail-level, -ddl                                <ViolationsReporterDetailLevel>: {VERBOSE | COMPACT | PER_FILE_COMPACT}
<ViolationsReporterDetailLevel>                         Default: VERBOSE
-diff-from, -df <string>                                Can be empty 
                                                        (ignored), Git-commit or any Git-
                                                        reference
                                                        <string>: any string
                                                        Default: 
-diff-max-violations, -dmv <integer>                    Will fail the build if 
                                                        total number of found 
                                                        violations is higher
                                                        <integer>: -2,147,483,648 to 2,147,483,647
                                                        Default: 2,147,483,647
-diff-print-violations, -dpv <boolean>                  Will print violations 
                                                        found in diff
                                                        <boolean>: true or false
                                                        Default: false
-diff-severity, -ds <SEVERITY>                          <SEVERITY>: {INFO | WARN | ERROR}
                                                        Default: INFO
-diff-to, -dt <string>                                  Can be empty 
                                                        (ignored), Git-commit or any Git-
                                                        reference
                                                        <string>: any string
                                                        Default: 
-git-repo, -gr <path>                                   Where to look for Git.
                                                        <path>: a file path
                                                        Default: /home/bjerre/workspace/violations/violations-command-line/.
-h, --help <argument-to-print-help-for>                 <argument-to-print-help-for>: an argument to print help for
                                                        Default: If no specific parameter is given the whole usage text is given
-jacoco-min-coverage, -jmc <big-decimal>                Minimum coverage in 
                                                        Jacoco that will generate a 
                                                        violation.
                                                        <big-decimal>: an arbitrary decimal number (practically no limits)
                                                        Default: 0.7
-jacoco-min-line-count, -jmlc <integer>                 Minimum line count in 
                                                        Jacoco that will generate a 
                                                        violation.
                                                        <integer>: -2,147,483,648 to 2,147,483,647
                                                        Default: 4
-max-line-column-width, -mlcw <integer>                 0 means no limit
                                                        <integer>: -2,147,483,648 to 2,147,483,647
                                                        Default: 0
-max-message-column-width, -mmcw <integer>              0 means no limit
                                                        <integer>: -2,147,483,648 to 2,147,483,647
                                                        Default: 50
-max-reporter-column-width, -mrcw <integer>             0 means no limit
                                                        <integer>: -2,147,483,648 to 2,147,483,647
                                                        Default: 0
-max-rule-column-width, -mrucw <integer>                0 means no limit
                                                        <integer>: -2,147,483,648 to 2,147,483,647
                                                        Default: 10
-max-severity-column-width, -mscw <integer>             0 means no limit
                                                        <integer>: -2,147,483,648 to 2,147,483,647
                                                        Default: 0
-max-violations, -mv <integer>                          Will fail the build if 
                                                        total number of found 
                                                        violations is higher.
                                                        <integer>: -2,147,483,648 to 2,147,483,647
                                                        Default: 2,147,483,647
-print-violations, -pv <boolean>                        Will print violations 
                                                        found
                                                        <boolean>: true or false
                                                        Default: true
-severity, -s <SEVERITY>                                Minimum severity level 
                                                        to report.
                                                        <SEVERITY>: {INFO | WARN | ERROR}
                                                        Default: INFO
-show-debug-info                                        Please run your 
                                                        command with this parameter 
                                                        and supply output when 
                                                        reporting bugs.
                                                        Default: disabled
-show-json-config                                       Will print the given 
                                                        config as JSON.
                                                        Default: disabled
--violations, -v <string>                               The violations to look 
                                                        for. <PARSER> <FOLDER> 
                                                        <REGEXP PATTERN> <NAME> where 
                                                        PARSER is one of: 
                                                        ANDROIDLINT, CHECKSTYLE, CODENARC, 
                                                        CLANG, CPD, CPPCHECK, 
                                                        CPPLINT, CSSLINT, GENERIC, 
                                                        FINDBUGS, FLAKE8, FXCOP, 
                                                        GENDARME, IAR, JACOCO, JCREPORT, 
                                                        JSLINT, JUNIT, LINT, KLOCWORK, 
                                                        KOTLINMAVEN, KOTLINGRADLE, MSCPP, 
                                                        MSBULDLOG, MYPY, GOLINT, 
                                                        GOOGLEERRORPRONE, PERLCRITIC, PITEST, 
                                                        PMD, PROTOLINT, PYDOCSTYLE, 
                                                        PYLINT, RESHARPER, SBTSCALAC, 
                                                        SIMIAN, SONAR, STYLECOP, 
                                                        XMLLINT, YAMLLINT, ZPTLINT, 
                                                        DOCFX, PCLINT, CODECLIMATE, 
                                                        XUNIT
                                                         Example: -v "JSHINT" 
                                                        "." ".*/jshint.xml$" 
                                                        "JSHint" [Supports Multiple occurrences]
                                                        <string>: any string
                                                        Default: Empty list
-violations-file, -vf <path>                            Create a JSON file 
                                                        with all the violations.
                                                        <path>: a file path
                                                        Default: /home/bjerre/workspace/violations/violations-command-line/.
```

Checkout the [Violations Lib](https://github.com/tomasbjerre/violations-lib) for more documentation.
