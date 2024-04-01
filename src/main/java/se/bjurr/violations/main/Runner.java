package se.bjurr.violations.main;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static se.bjurr.violations.git.ViolationsReporterApi.violationsReporterApi;
import static se.bjurr.violations.lib.ViolationsApi.violationsApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.script.ScriptException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import se.bjurr.violations.git.ViolationsGit;
import se.bjurr.violations.git.ViolationsReporterDetailLevel;
import se.bjurr.violations.lib.FilteringViolationsLogger;
import se.bjurr.violations.lib.ViolationsLogger;
import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.model.codeclimate.CodeClimateTransformer;
import se.bjurr.violations.lib.model.sarif.SarifTransformer;
import se.bjurr.violations.lib.parsers.JacocoParser;
import se.bjurr.violations.lib.parsers.JacocoParserSettings;
import se.bjurr.violations.lib.parsers.ViolationsParser;
import se.bjurr.violations.lib.reports.Parser;
import se.bjurr.violations.lib.util.Filtering;

@Command(name = "violations-command-line")
public class Runner implements Runnable {
  private static final String SHOW_JSON_CONFIG = "-show-json-config";
  private static final String VIOLATIONS_CONFIG = "VIOLATIONS_CONFIG";

  @Option(
      names = {"-v", "--violations"},
      hideParamSyntax = true,
      description =
          "Format: <PARSER> <FOLDER> <REGEXP PATTERN> <NAME>, Example: -v \"JSLINT\" \".\" \".*/jshint.xml$\" \"JSHint\"",
      parameterConsumer = ViolationConfigConverter.class,
      arity = "3..4")
  List<ViolationConfig> violationsArg = new ArrayList<ViolationConfig>();

  @Option(
      defaultValue = "INFO",
      names = {"-severity", "-s"},
      description = "Minimum severity level to report. ${COMPLETION-CANDIDATES}")
  SEVERITY minSeverityArg;

  @Option(
      names = {"-code-climate", "-cc"},
      description = "Create a CodeClimate file with all the violations.")
  File codeClimateFileArg;

  @Option(
      names = {"-sarif", "-ss"},
      description = "Create a Sarif file with all the violations.")
  File sarifFileArg;

  @Option(
      names = {"-violations-file", "-vf"},
      description = "Create a JSON file with all the violations.")
  File violationsFileArg;

  @Option(
      defaultValue = Integer.MAX_VALUE + "",
      names = {"-max-violations", "-mv"},
      description = "Will fail the build if total number of found violations is higher.")
  Integer maxViolationsArg;

  @Option(
      defaultValue = "VERBOSE",
      names = {"-detail-level", "-dl"},
      description = "Verbosity ${COMPLETION-CANDIDATES}")
  ViolationsReporterDetailLevel detailLevelArg;

  @Option(
      defaultValue = "true",
      names = {"-print-violations", "-pv"},
      description = "Will print violations found")
  Boolean printViolationsArg;

  @Option(
      defaultValue = ".",
      names = {"-git-repo", "-gr"},
      description = "Where to look for Git.")
  File gitRepoArg;

  @Option(
      names = {"-diff-from", "-df"},
      description = "Can be empty (ignored), Git-commit or any Git-reference")
  String diffFrom;

  @Option(
      names = {"-diff-to", "-dt"},
      description = "Can be empty (ignored), Git-commit or any Git-reference")
  String diffTo;

  @Option(
      defaultValue = "INFO",
      names = {"-diff-severity", "-ds"},
      description = "${COMPLETION-CANDIDATES}")
  SEVERITY diffMinSeverity;

  @Option(
      defaultValue = "false",
      names = {"-diff-print-violations", "-dpv"},
      description = "Will print violations found in diff")
  Boolean diffPrintViolations;

  @Option(
      defaultValue = Integer.MAX_VALUE + "",
      names = {"-diff-max-violations", "-dmv"},
      description = "Will fail the build if total number of found violations is higher")
  Integer diffMaxViolations;

  @Option(
      defaultValue = "VERBOSE",
      names = {"-diff-detail-level", "-ddl"},
      description = "${COMPLETION-CANDIDATES}")
  ViolationsReporterDetailLevel diffDetailLevel;

  @Option(
      defaultValue = "0",
      names = {"-max-reporter-column-width", "-mrcw"},
      description = "0 means no limit")
  Integer maxReporterColumnWidth;

  @Option(
      defaultValue = "10",
      names = {"-max-rule-column-width", "-mrucw"},
      description = "0 means no limit")
  Integer maxRuleColumnWidth;

  @Option(
      defaultValue = "0",
      names = {"-max-severity-column-width", "-mscw"},
      description = "0 means no limit")
  Integer maxSeverityColumnWidth;

  @Option(
      defaultValue = "0",
      names = {"-max-line-column-width", "-mlcw"},
      description = "0 means no limit")
  Integer maxLineColumnWidth;

  @Option(
      defaultValue = "50",
      names = {"-max-message-column-width", "-mmcw"},
      description = "0 means no limit")
  Integer maxMessageColumnWidth;

  @Option(
      names = {"-show-debug-info"},
      description =
          "Please run your command with this parameter and supply output when reporting bugs.")
  Boolean showDebugInfo;

  @Option(
      names = {SHOW_JSON_CONFIG},
      description = "Will print the given config as JSON.")
  Boolean showJsonConfig;

  @Option(
      names = {"-config-file", "-cf"},
      description =
          "Will read config from given file. Can also be configured"
              + " with environment variable "
              + VIOLATIONS_CONFIG
              + ". Format is what you get from "
              + SHOW_JSON_CONFIG
              + ".")
  File configFileArg;

  @Option(
      defaultValue = JacocoParserSettings.DEFAULT_MIN_LINE_COUNT + "",
      names = {"-jacoco-min-line-count", "-jmlc"},
      description = "Minimum line count in Jacoco that will generate a violation.")
  Integer jacocoMinLineCount;

  @Option(
      defaultValue = "0.7",
      names = {"-jacoco-min-coverage", "-jmc"},
      description = "Minimum coverage in Jacoco that will generate a violation.")
  BigDecimal jacocoMinCoverage;

  @Option(names = "--help", usageHelp = true, description = "display this help and exit")
  boolean help;

  ViolationsConfig violationsConfig = new ViolationsConfig();

  @Override
  public void run() {
    final String violationsConfigPropertyValue = System.getenv(VIOLATIONS_CONFIG);
    final boolean violationsConfigPropertyValueGiven = violationsConfigPropertyValue != null;
    final boolean shouldUseConfigFile =
        this.wasGiven(this.configFileArg) || violationsConfigPropertyValueGiven;
    if (shouldUseConfigFile) {
      Path jsonFile = null;
      if (this.wasGiven(this.configFileArg)) {
        jsonFile = this.configFileArg.toPath();
      }
      if (violationsConfigPropertyValueGiven) {
        jsonFile = Paths.get(violationsConfigPropertyValue);
      }
      String json;
      try {
        json = new String(Files.readAllBytes(jsonFile), UTF_8);
      } catch (final IOException e) {
        throw new RuntimeException(jsonFile.toString(), e);
      }
      this.violationsConfig = new Gson().fromJson(json, ViolationsConfig.class);
    } else {
      this.violationsConfig.setViolations(this.violationsArg);
      this.violationsConfig.setMinSeverity(this.minSeverityArg);
      this.violationsConfig.setMaxViolations(this.maxViolationsArg);
      this.violationsConfig.setPrintViolations(this.printViolationsArg);
      this.violationsConfig.setDetailLevel(this.detailLevelArg);
      this.violationsConfig.setPrintViolations(this.printViolationsArg);
      this.violationsConfig.setDiffFrom(this.diffFrom);
      this.violationsConfig.setDiffTo(this.diffTo);
      this.violationsConfig.setDiffMinSeverity(this.diffMinSeverity);
      this.violationsConfig.setDiffPrintViolations(this.diffPrintViolations);
      this.violationsConfig.setDiffMaxViolations(this.diffMaxViolations);
      this.violationsConfig.setDiffDetailLevel(this.diffDetailLevel);
      this.violationsConfig.setMaxReporterColumnWidth(this.maxReporterColumnWidth);
      this.violationsConfig.setMaxRuleColumnWidth(this.maxRuleColumnWidth);
      this.violationsConfig.setMaxSeverityColumnWidth(this.maxSeverityColumnWidth);
      this.violationsConfig.setMaxLineColumnWidth(this.maxLineColumnWidth);
      this.violationsConfig.setMaxMessageColumnWidth(this.maxMessageColumnWidth);
      this.violationsConfig.setGitRepo(this.gitRepoArg);
      this.violationsConfig.setJacocoMinCoverage(this.jacocoMinCoverage.doubleValue());
      this.violationsConfig.setJacocoMinLineCount(this.jacocoMinLineCount);
      if (this.wasGiven(this.codeClimateFileArg)) {
        this.violationsConfig.setCodeClimateFile(this.codeClimateFileArg);
      }
      if (this.wasGiven(this.sarifFileArg)) {
        this.violationsConfig.setSarifFile(this.sarifFileArg);
      }
      if (this.wasGiven(this.violationsFileArg)) {
        this.violationsConfig.setViolationsFile(this.violationsFileArg);
      }
      this.violationsConfig.setShowDebugInfo(this.wasGiven(this.showDebugInfo));
      if (this.violationsConfig.isShowDebugInfo()) {
        System.out.println("Parsed parameters:\n" + this.toString());
      }
    }

    if (this.wasGiven(this.showJsonConfig)) {
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
    try {
      this.performTask();
    } catch (final TooManyViolationsException e) {
      throw e;
    } catch (final Exception e) {
      throw new RuntimeException(this.toString(), e);
    }
  }

  private boolean wasGiven(final File it) {
    return it != null;
  }

  private boolean wasGiven(final Boolean it) {
    return it != null;
  }

  public void performTask() throws Exception {
    final Set<Violation> allParsedViolations = new TreeSet<>();
    final Set<Violation> allParsedViolationsInDiff = new TreeSet<>();
    for (final ViolationConfig configuredViolation : this.violationsConfig.getViolations()) {
      final Set<Violation> parsedViolations = this.getAllParsedViolations(configuredViolation);

      allParsedViolations.addAll(
          this.getFiltered(parsedViolations, this.violationsConfig.getMinSeverity()));

      allParsedViolationsInDiff.addAll(this.getAllViolationsInDiff(parsedViolations));
    }

    if (this.violationsConfig.getCodeClimateFile() != null) {
      this.createJsonFile(
          CodeClimateTransformer.fromViolations(allParsedViolations),
          this.violationsConfig.getCodeClimateFile());
    }
    if (this.violationsConfig.getSarifFile() != null) {
      this.createJsonFile(
          SarifTransformer.fromViolations(allParsedViolations),
          this.violationsConfig.getSarifFile());
    }
    if (this.violationsConfig.getViolationsFile() != null) {
      this.createJsonFile(allParsedViolations, this.violationsConfig.getViolationsFile());
    }
    this.checkGlobalViolations(allParsedViolations);
    this.checkDiffViolations(allParsedViolationsInDiff);
  }

  private void createJsonFile(final Object object, final String file) throws IOException {
    final String codeClimateReport = new GsonBuilder().setPrettyPrinting().create().toJson(object);
    Files.write(
        Paths.get(file),
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
      throw new TooManyViolationsException(
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
              new File(this.violationsConfig.getGitRepo()),
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

  private Set<Violation> getAllParsedViolations(final ViolationConfig configuredViolation) {
    final String reporter =
        Optional.ofNullable(configuredViolation.getName())
            .orElse(configuredViolation.getParser().name());

    ViolationsParser parser = null;
    try {
      final String parserName = configuredViolation.getParser().name();
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
            .inFolder(configuredViolation.getFolder()) //
            .withPattern(configuredViolation.getRegexp()) //
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
