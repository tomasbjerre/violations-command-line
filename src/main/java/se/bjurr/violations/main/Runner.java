package se.bjurr.violations.main;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static se.bjurr.violations.git.ViolationsReporterApi.violationsReporterApi;
import static se.bjurr.violations.git.ViolationsReporterDetailLevel.VERBOSE;
import static se.bjurr.violations.lib.ViolationsApi.violationsApi;
import static se.bjurr.violations.lib.model.SEVERITY.INFO;
import static se.bjurr.violations.lib.model.codeclimate.CodeClimateTransformer.fromViolations;
import static se.softhouse.jargo.Arguments.bigDecimalArgument;
import static se.softhouse.jargo.Arguments.booleanArgument;
import static se.softhouse.jargo.Arguments.enumArgument;
import static se.softhouse.jargo.Arguments.fileArgument;
import static se.softhouse.jargo.Arguments.helpArgument;
import static se.softhouse.jargo.Arguments.integerArgument;
import static se.softhouse.jargo.Arguments.optionArgument;
import static se.softhouse.jargo.Arguments.stringArgument;
import static se.softhouse.jargo.CommandLineParser.withArguments;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.script.ScriptException;
import se.bjurr.violations.git.ViolationsGit;
import se.bjurr.violations.git.ViolationsReporterDetailLevel;
import se.bjurr.violations.lib.FilteringViolationsLogger;
import se.bjurr.violations.lib.ViolationsLogger;
import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.parsers.JacocoParser;
import se.bjurr.violations.lib.parsers.JacocoParserSettings;
import se.bjurr.violations.lib.parsers.ViolationsParser;
import se.bjurr.violations.lib.reports.Parser;
import se.bjurr.violations.lib.util.Filtering;
import se.bjurr.violations.violationslib.com.google.gson.Gson;
import se.bjurr.violations.violationslib.com.google.gson.GsonBuilder;
import se.softhouse.jargo.Argument;
import se.softhouse.jargo.ArgumentException;
import se.softhouse.jargo.ParsedArguments;

public class Runner {
  private static final String SHOW_JSON_CONFIG = "-show-json-config";
  private static final String VIOLATIONS_CONFIG = "VIOLATIONS_CONFIG";
  private ViolationsConfig violationsConfig = new ViolationsConfig();

  public void main(final String args[]) throws Exception {
    final Argument<?> helpArgument = helpArgument("-h", "--help");
    final String parsersString =
        Arrays.asList(Parser.values()).stream()
            .map((it) -> it.toString())
            .collect(Collectors.joining(", "));
    final Argument<List<List<String>>> violationsArg =
        stringArgument("--violations", "-v")
            .arity(4)
            .repeated()
            .description(
                "The violations to look for. <PARSER> <FOLDER> <REGEXP PATTERN> <NAME> where PARSER is one of: "
                    + parsersString
                    + "\n Example: -v \"JSHINT\" \".\" \".*/jshint.xml$\" \"JSHint\"")
            .build();
    final Argument<SEVERITY> minSeverityArg =
        enumArgument(SEVERITY.class, "-severity", "-s")
            .defaultValue(INFO)
            .description("Minimum severity level to report.")
            .build();
    final Argument<File> codeClimateFileArg =
        fileArgument("-code-climate", "-cc")
            .description("Create a CodeClimate file with all the violations.")
            .build();
    final Argument<File> sarifFileArg =
        fileArgument("-sarif", "-ss")
            .description("Create a Sarif file with all the violations.")
            .build();
    final Argument<File> violationsFileArg =
        fileArgument("-violations-file", "-vf")
            .description("Create a JSON file with all the violations.")
            .build();
    final Argument<Integer> maxViolationsArg =
        integerArgument("-max-violations", "-mv")
            .defaultValue(Integer.MAX_VALUE)
            .description("Will fail the build if total number of found violations is higher.")
            .build();

    final Argument<ViolationsReporterDetailLevel> detailLevelArg =
        enumArgument(ViolationsReporterDetailLevel.class, "-detail-level", "-dl")
            .defaultValue(VERBOSE)
            .description("Verbosity")
            .build();

    final Argument<Boolean> printViolationsArg =
        booleanArgument("-print-violations", "-pv")
            .defaultValue(true)
            .description("Will print violations found")
            .build();
    final Argument<File> gitRepoArg =
        fileArgument("-git-repo", "-gr")
            .defaultValue(new File("."))
            .description("Where to look for Git.")
            .build();
    final Argument<String> diffFrom =
        stringArgument("-diff-from", "-df")
            .description("Can be empty (ignored), Git-commit or any Git-reference")
            .build();
    final Argument<String> diffTo =
        stringArgument("-diff-to", "-dt")
            .description("Can be empty (ignored), Git-commit or any Git-reference")
            .build();
    final Argument<SEVERITY> diffMinSeverity =
        enumArgument(SEVERITY.class, "-diff-severity", "-ds").defaultValue(INFO).build();
    final Argument<Boolean> diffPrintViolations =
        booleanArgument("-diff-print-violations", "-dpv")
            .defaultValue(false)
            .description("Will print violations found in diff")
            .build();
    final Argument<Integer> diffMaxViolations =
        integerArgument("-diff-max-violations", "-dmv")
            .defaultValue(Integer.MAX_VALUE)
            .description("Will fail the build if total number of found violations is higher")
            .build();
    final Argument<ViolationsReporterDetailLevel> diffDetailLevel =
        enumArgument(ViolationsReporterDetailLevel.class, "-diff-detail-level", "-ddl")
            .defaultValue(VERBOSE)
            .build();
    final Argument<Integer> maxReporterColumnWidth =
        integerArgument("-max-reporter-column-width", "-mrcw")
            .description("0 means no limit")
            .defaultValue(0)
            .build();
    final Argument<Integer> maxRuleColumnWidth =
        integerArgument("-max-rule-column-width", "-mrucw")
            .description("0 means no limit")
            .defaultValue(10)
            .build();
    final Argument<Integer> maxSeverityColumnWidth =
        integerArgument("-max-severity-column-width", "-mscw")
            .description("0 means no limit")
            .defaultValue(0)
            .build();
    final Argument<Integer> maxLineColumnWidth =
        integerArgument("-max-line-column-width", "-mlcw")
            .description("0 means no limit")
            .defaultValue(0)
            .build();
    final Argument<Integer> maxMessageColumnWidth =
        integerArgument("-max-message-column-width", "-mmcw")
            .description("0 means no limit")
            .defaultValue(50)
            .build();
    final Argument<Boolean> showDebugInfo =
        optionArgument("-show-debug-info")
            .description(
                "Please run your command with this parameter and supply output when reporting bugs.")
            .build();
    final Argument<Boolean> showJsonConfig =
        optionArgument(SHOW_JSON_CONFIG)
            .description("Will print the given config as JSON.")
            .build();
    final Argument<File> configFileArg =
        fileArgument("-config-file", "-cf")
            .defaultValue(new File("."))
            .description(
                "Will read config from given file. Can also be configured"
                    + " with environment variable "
                    + VIOLATIONS_CONFIG
                    + ". Format is what you get from "
                    + SHOW_JSON_CONFIG
                    + ".")
            .build();
    final Argument<Integer> jacocoMinLineCount =
        integerArgument("-jacoco-min-line-count", "-jmlc")
            .description("Minimum line count in Jacoco that will generate a violation.")
            .defaultValue(JacocoParserSettings.DEFAULT_MIN_LINE_COUNT)
            .build();

    final Argument<BigDecimal> jacocoMinCoverage =
        bigDecimalArgument("-jacoco-min-coverage", "-jmc")
            .description("Minimum coverage in Jacoco that will generate a violation.")
            .defaultValue(BigDecimal.valueOf(JacocoParserSettings.DEFAULT_MIN_COVERAGE))
            .build();

    try {
      final ParsedArguments parsed =
          withArguments( //
                  showJsonConfig, //
                  configFileArg, //
                  showDebugInfo, //
                  maxViolationsArg, //
                  helpArgument, //
                  violationsArg, //
                  minSeverityArg, //
                  codeClimateFileArg, //
                  sarifFileArg, //
                  violationsFileArg, //
                  detailLevelArg, //
                  printViolationsArg, //
                  diffFrom, //
                  diffTo, //
                  diffMinSeverity, //
                  diffPrintViolations, //
                  diffMaxViolations, //
                  diffDetailLevel, //
                  maxReporterColumnWidth, //
                  maxRuleColumnWidth, //
                  maxSeverityColumnWidth, //
                  maxLineColumnWidth, //
                  maxMessageColumnWidth, //
                  gitRepoArg, //
                  jacocoMinLineCount, //
                  jacocoMinCoverage) //
              .parse(args);
      final String violationsConfigPropertyValue = System.getenv(VIOLATIONS_CONFIG);
      final boolean violationsConfigPropertyValueGiven = violationsConfigPropertyValue != null;
      final boolean shouldUseConfigFile =
          parsed.wasGiven(configFileArg) || violationsConfigPropertyValueGiven;
      if (shouldUseConfigFile) {
        Path jsonFile = null;
        if (parsed.wasGiven(configFileArg)) {
          jsonFile = parsed.get(configFileArg).toPath();
        }
        if (violationsConfigPropertyValueGiven) {
          jsonFile = Paths.get(violationsConfigPropertyValue);
        }
        final String json = new String(Files.readAllBytes(jsonFile), UTF_8);
        this.violationsConfig = new Gson().fromJson(json, ViolationsConfig.class);
      } else {
        this.violationsConfig.setViolations(parsed.get(violationsArg));
        this.violationsConfig.setMinSeverity(parsed.get(minSeverityArg));
        this.violationsConfig.setMaxViolations(parsed.get(maxViolationsArg));
        this.violationsConfig.setPrintViolations(parsed.get(printViolationsArg));
        this.violationsConfig.setDetailLevel(parsed.get(detailLevelArg));
        this.violationsConfig.setPrintViolations(parsed.get(printViolationsArg));
        this.violationsConfig.setDiffFrom(parsed.get(diffFrom));
        this.violationsConfig.setDiffTo(parsed.get(diffTo));
        this.violationsConfig.setDiffMinSeverity(parsed.get(diffMinSeverity));
        this.violationsConfig.setDiffPrintViolations(parsed.get(diffPrintViolations));
        this.violationsConfig.setDiffMaxViolations(parsed.get(diffMaxViolations));
        this.violationsConfig.setDiffDetailLevel(parsed.get(diffDetailLevel));
        this.violationsConfig.setMaxReporterColumnWidth(parsed.get(maxReporterColumnWidth));
        this.violationsConfig.setMaxRuleColumnWidth(parsed.get(maxRuleColumnWidth));
        this.violationsConfig.setMaxSeverityColumnWidth(parsed.get(maxSeverityColumnWidth));
        this.violationsConfig.setMaxLineColumnWidth(parsed.get(maxLineColumnWidth));
        this.violationsConfig.setMaxMessageColumnWidth(parsed.get(maxMessageColumnWidth));
        this.violationsConfig.setGitRepo(parsed.get(gitRepoArg));
        this.violationsConfig.setJacocoMinCoverage(parsed.get(jacocoMinCoverage).doubleValue());
        this.violationsConfig.setJacocoMinLineCount(parsed.get(jacocoMinLineCount));
        if (parsed.wasGiven(codeClimateFileArg)) {
          this.violationsConfig.setCodeClimateFile(parsed.get(codeClimateFileArg));
        } else {
          this.violationsConfig.setCodeClimateFile(null);
        }
        if (parsed.wasGiven(sarifFileArg)) {
          this.violationsConfig.setSarifFile(parsed.get(sarifFileArg));
        } else {
          this.violationsConfig.setSarifFile(null);
        }
        if (parsed.wasGiven(violationsFileArg)) {
          this.violationsConfig.setViolationsFile(parsed.get(violationsFileArg));
        } else {
          this.violationsConfig.setViolationsFile(null);
        }
        this.violationsConfig.setShowDebugInfo(parsed.wasGiven(showDebugInfo));
        if (this.violationsConfig.isShowDebugInfo()) {
          System.out.println(
              "Given parameters:\n"
                  + Arrays.asList(args).stream()
                      .map((it) -> it.toString())
                      .collect(Collectors.joining(", "))
                  + "\n\nParsed parameters:\n"
                  + this.toString());
        }
      }

      if (parsed.wasGiven(showJsonConfig)) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String jsonString = gson.toJson(this.violationsConfig);
        System.out.println(jsonString);
        return;
      }
      this.violationsConfig.setViolationsLogger(
          new ViolationsLogger() {
            @Override
            public void log(final Level level, final String string) {
              System.out.println(level + " " + string);
            }

            @Override
            public void log(final Level level, final String string, final Throwable t) {
              final StringWriter sw = new StringWriter();
              t.printStackTrace(new PrintWriter(sw));
              System.out.println(level + " " + string + "\n" + sw.toString());
            }
          });
      if (!this.violationsConfig.isShowDebugInfo()) {
        this.violationsConfig.setViolationsLogger(
            FilteringViolationsLogger.filterLevel(this.violationsConfig.getViolationsLogger()));
      }
      this.performTask();
    } catch (final ArgumentException exception) {
      System.out.println(exception.getMessageAndUsage());
      System.exit(1);
    }
  }

  public void performTask() throws Exception {
    final Set<Violation> allParsedViolations = new TreeSet<>();
    final Set<Violation> allParsedViolationsInDiff = new TreeSet<>();
    for (final List<String> configuredViolation : this.violationsConfig.getViolations()) {
      final Set<Violation> parsedViolations = this.getAllParsedViolations(configuredViolation);

      allParsedViolations.addAll(
          this.getFiltered(parsedViolations, this.violationsConfig.getMinSeverity()));

      allParsedViolationsInDiff.addAll(this.getAllViolationsInDiff(parsedViolations));
    }

    if (this.violationsConfig.getCodeClimateFile() != null) {
      this.createJsonFile(
          fromViolations(allParsedViolations), this.violationsConfig.getCodeClimateFile());
    }
    if (this.violationsConfig.getSarifFile() != null) {
      this.createJsonFile(
          fromViolations(allParsedViolations), this.violationsConfig.getSarifFile());
    }
    if (this.violationsConfig.getViolationsFile() != null) {
      this.createJsonFile(allParsedViolations, this.violationsConfig.getViolationsFile());
    }
    this.checkGlobalViolations(allParsedViolations);
    this.checkDiffViolations(allParsedViolationsInDiff);
  }

  private void createJsonFile(final Object object, final File file) throws IOException {
    final String codeClimateReport = new GsonBuilder().setPrettyPrinting().create().toJson(object);
    Files.write(
        file.toPath(),
        codeClimateReport.getBytes(StandardCharsets.UTF_8),
        TRUNCATE_EXISTING,
        CREATE,
        WRITE);
  }

  private void checkGlobalViolations(final Set<Violation> violations) throws ScriptException {
    final boolean tooManyViolations = violations.size() > this.violationsConfig.getMaxViolations();
    if (!tooManyViolations && !this.violationsConfig.isPrintViolations()) {
      return;
    }

    final String report =
        violationsReporterApi()
            .withViolations(violations)
            .withMaxLineColumnWidth(this.violationsConfig.getMaxLineColumnWidth())
            .withMaxMessageColumnWidth(this.violationsConfig.getMaxMessageColumnWidth())
            .withMaxReporterColumnWidth(this.violationsConfig.getMaxReporterColumnWidth()) //
            .withMaxRuleColumnWidth(this.violationsConfig.getMaxRuleColumnWidth()) //
            .withMaxSeverityColumnWidth(this.violationsConfig.getMaxSeverityColumnWidth()) //
            .getReport(this.violationsConfig.getDetailLevel());

    if (tooManyViolations) {
      System.err.println("\nViolations in repo\n\n" + report);
      throw new ScriptException(
          "Too many violations found, max is "
              + this.violationsConfig.getMaxViolations()
              + " but found "
              + violations.size());
    } else {
      if (this.violationsConfig.isPrintViolations()) {
        System.out.println("\nViolations in repo\n\n" + report);
      }
    }
  }

  private void checkDiffViolations(final Set<Violation> violations) throws ScriptException {
    final boolean tooManyViolations =
        violations.size() > this.violationsConfig.getDiffMaxViolations();
    if (!tooManyViolations && !this.violationsConfig.isDiffPrintViolations()) {
      return;
    }

    final String report =
        violationsReporterApi()
            .withViolations(violations)
            .withMaxLineColumnWidth(this.violationsConfig.getMaxLineColumnWidth())
            .withMaxMessageColumnWidth(this.violationsConfig.getMaxMessageColumnWidth())
            .withMaxReporterColumnWidth(this.violationsConfig.getMaxReporterColumnWidth()) //
            .withMaxRuleColumnWidth(this.violationsConfig.getMaxRuleColumnWidth()) //
            .withMaxSeverityColumnWidth(this.violationsConfig.getMaxSeverityColumnWidth()) //
            .getReport(this.violationsConfig.getDiffDetailLevel());

    if (tooManyViolations) {
      System.err.println("\nViolations in repo\n\n" + report);
      throw new ScriptException(
          "Too many violations found in diff, max is "
              + this.violationsConfig.getMaxViolations()
              + " but found "
              + violations.size());
    } else {
      if (this.violationsConfig.isDiffPrintViolations()) {
        System.out.println("\nViolations in diff\n\n" + report);
      }
    }
  }

  private Set<Violation> getAllViolationsInDiff(final Set<Violation> unfilteredViolations)
      throws Exception {
    if (!this.isDefined(this.violationsConfig.getDiffFrom())
        || !this.isDefined(this.violationsConfig.getDiffTo())) {
      // No references specified, will not report violations in diff
      return new TreeSet<>();
    } else {
      final Set<Violation> candidates =
          this.getFiltered(unfilteredViolations, this.violationsConfig.getDiffMinSeverity());
      return new ViolationsGit(this.violationsConfig.getViolationsLogger(), candidates) //
          .getViolationsInChangeset(
              this.violationsConfig.getGitRepo(),
              this.violationsConfig.getDiffFrom(),
              this.violationsConfig.getDiffTo());
    }
  }

  private Set<Violation> getFiltered(final Set<Violation> unfiltered, final SEVERITY filter) {
    if (filter != null) {
      return Filtering.withAtLEastSeverity(unfiltered, filter);
    }
    return unfiltered;
  }

  private Set<Violation> getAllParsedViolations(final List<String> configuredViolation) {
    final String reporter = configuredViolation.size() >= 4 ? configuredViolation.get(3) : null;

    ViolationsParser parser = null;
    try {
      final String parserName = configuredViolation.get(0);
      if (parserName.equals(Parser.JACOCO.name())) {
        final int minLineCount = this.violationsConfig.getJacocoMinLineCount();
        final double minCoverage = this.violationsConfig.getJacocoMinCoverage();
        final JacocoParserSettings settings = new JacocoParserSettings(minLineCount, minCoverage);
        parser = new JacocoParser(settings);
      } else {
        parser = Parser.valueOf(parserName).getViolationsParser();
      }
    } catch (final Exception e) {
      throw new RuntimeException(
          Arrays.asList(Parser.values()).stream()
              .map((it) -> it.toString())
              .collect(Collectors.joining("\n")),
          e);
    }
    final Set<Violation> parsedViolations =
        violationsApi() //
            .withViolationsLogger(this.violationsConfig.getViolationsLogger()) //
            .withViolationsParser(parser) //
            .inFolder(configuredViolation.get(1)) //
            .withPattern(configuredViolation.get(2)) //
            .withReporter(reporter) //
            .violations();
    return parsedViolations;
  }

  private boolean isDefined(final String str) {
    return str != null && !str.isEmpty();
  }

  @Override
  public String toString() {
    return "Runner [violations="
        + this.violationsConfig.getViolations()
        + ", minSeverity="
        + this.violationsConfig.getMinSeverity()
        + ", detailLevel="
        + this.violationsConfig.getDetailLevel()
        + ", maxViolations="
        + this.violationsConfig.getMaxViolations()
        + ", printViolations="
        + this.violationsConfig.isPrintViolations()
        + ", diffFrom="
        + this.violationsConfig.getDiffFrom()
        + ", diffTo="
        + this.violationsConfig.getDiffTo()
        + ", diffMinSeverity="
        + this.violationsConfig.getDiffMinSeverity()
        + ", gitRepo="
        + this.violationsConfig.getGitRepo()
        + ", diffPrintViolations="
        + this.violationsConfig.isDiffPrintViolations()
        + ", diffMaxViolations="
        + this.violationsConfig.getDiffMaxViolations()
        + ", diffDetailLevel="
        + this.violationsConfig.getDiffDetailLevel()
        + ", maxReporterColumnWidth="
        + this.violationsConfig.getMaxReporterColumnWidth()
        + ", maxRuleColumnWidth="
        + this.violationsConfig.getMaxRuleColumnWidth()
        + ", maxSeverityColumnWidth="
        + this.violationsConfig.getMaxSeverityColumnWidth()
        + ", maxLineColumnWidth="
        + this.violationsConfig.getMaxLineColumnWidth()
        + ", maxMessageColumnWidth="
        + this.violationsConfig.getMaxMessageColumnWidth()
        + "]";
  }
}
