package se.bjurr.violations.main;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static se.bjurr.violations.git.ViolationsReporterApi.violationsReporterApi;
import static se.bjurr.violations.git.ViolationsReporterDetailLevel.VERBOSE;
import static se.bjurr.violations.lib.ViolationsApi.violationsApi;
import static se.bjurr.violations.lib.model.SEVERITY.INFO;
import static se.bjurr.violations.lib.model.codeclimate.CodeClimateTransformer.fromViolations;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
import se.bjurr.violations.lib.reports.Parser;
import se.bjurr.violations.lib.util.Filtering;
import se.bjurr.violations.violationslib.com.google.gson.GsonBuilder;
import se.softhouse.jargo.Argument;
import se.softhouse.jargo.ArgumentException;
import se.softhouse.jargo.ParsedArguments;

/** @author bjerre */
public class Runner {
  private List<List<String>> violations;
  private SEVERITY minSeverity;
  private ViolationsReporterDetailLevel detailLevel;
  private Integer maxViolations;
  private boolean printViolations;
  private String diffFrom;
  private String diffTo;
  private SEVERITY diffMinSeverity;
  private File gitRepo;
  private boolean diffPrintViolations;
  private Integer diffMaxViolations;
  private ViolationsReporterDetailLevel diffDetailLevel;
  private int maxReporterColumnWidth;
  private int maxRuleColumnWidth;
  private int maxSeverityColumnWidth;
  private int maxLineColumnWidth;
  private int maxMessageColumnWidth;
  private File codeClimateFile;
  private File violationsFile;
  private boolean showDebugInfo;
  private ViolationsLogger violationsLogger;

  public void main(final String args[]) throws Exception {
    final Argument<?> helpArgument = helpArgument("-h", "--help");
    final String parsersString =
        Arrays.asList(Parser.values())
            .stream()
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

    try {
      final ParsedArguments parsed =
          withArguments( //
                  showDebugInfo, //
                  maxViolationsArg, //
                  helpArgument, //
                  violationsArg, //
                  minSeverityArg, //
                  codeClimateFileArg, //
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
                  gitRepoArg) //
              .parse(args);

      this.violations = parsed.get(violationsArg);
      this.minSeverity = parsed.get(minSeverityArg);
      this.maxViolations = parsed.get(maxViolationsArg);
      this.printViolations = parsed.get(printViolationsArg);
      this.detailLevel = parsed.get(detailLevelArg);
      this.printViolations = parsed.get(printViolationsArg);
      this.diffFrom = parsed.get(diffFrom);
      this.diffTo = parsed.get(diffTo);
      this.diffMinSeverity = parsed.get(diffMinSeverity);
      this.diffPrintViolations = parsed.get(diffPrintViolations);
      this.diffMaxViolations = parsed.get(diffMaxViolations);
      this.diffDetailLevel = parsed.get(diffDetailLevel);
      this.maxReporterColumnWidth = parsed.get(maxReporterColumnWidth);
      this.maxRuleColumnWidth = parsed.get(maxRuleColumnWidth);
      this.maxSeverityColumnWidth = parsed.get(maxSeverityColumnWidth);
      this.maxLineColumnWidth = parsed.get(maxLineColumnWidth);
      this.maxMessageColumnWidth = parsed.get(maxMessageColumnWidth);
      this.gitRepo = parsed.get(gitRepoArg);
      if (parsed.wasGiven(codeClimateFileArg)) {
        this.codeClimateFile = parsed.get(codeClimateFileArg);
      } else {
        this.codeClimateFile = null;
      }
      if (parsed.wasGiven(violationsFileArg)) {
        this.violationsFile = parsed.get(violationsFileArg);
      } else {
        this.violationsFile = null;
      }
      this.showDebugInfo = parsed.wasGiven(showDebugInfo);
      if (this.showDebugInfo) {
        System.out.println(
            "Given parameters:\n"
                + Arrays.asList(args)
                    .stream()
                    .map((it) -> it.toString())
                    .collect(Collectors.joining(", "))
                + "\n\nParsed parameters:\n"
                + this.toString());
      }

      this.violationsLogger =
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
          };
      if (!this.showDebugInfo) {
        this.violationsLogger = FilteringViolationsLogger.filterLevel(this.violationsLogger);
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
    for (final List<String> configuredViolation : this.violations) {
      final Set<Violation> parsedViolations = this.getAllParsedViolations(configuredViolation);

      allParsedViolations.addAll(this.getFiltered(parsedViolations, this.minSeverity));

      allParsedViolationsInDiff.addAll(this.getAllViolationsInDiff(parsedViolations));
    }

    if (this.codeClimateFile != null) {
      this.createJsonFile(fromViolations(allParsedViolations), this.codeClimateFile);
    }
    if (this.violationsFile != null) {
      this.createJsonFile(allParsedViolations, this.violationsFile);
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
    final boolean tooManyViolations = violations.size() > this.maxViolations;
    if (!tooManyViolations && !this.printViolations) {
      return;
    }

    final String report =
        violationsReporterApi()
            .withViolations(violations)
            .withMaxLineColumnWidth(this.maxLineColumnWidth)
            .withMaxMessageColumnWidth(this.maxMessageColumnWidth)
            .withMaxReporterColumnWidth(this.maxReporterColumnWidth) //
            .withMaxRuleColumnWidth(this.maxRuleColumnWidth) //
            .withMaxSeverityColumnWidth(this.maxSeverityColumnWidth) //
            .getReport(this.detailLevel);

    if (tooManyViolations) {
      System.err.println("\nViolations in repo\n\n" + report);
      throw new ScriptException(
          "Too many violations found, max is "
              + this.maxViolations
              + " but found "
              + violations.size());
    } else {
      if (this.printViolations) {
        System.out.println("\nViolations in repo\n\n" + report);
      }
    }
  }

  private void checkDiffViolations(final Set<Violation> violations) throws ScriptException {
    final boolean tooManyViolations = violations.size() > this.diffMaxViolations;
    if (!tooManyViolations && !this.diffPrintViolations) {
      return;
    }

    final String report =
        violationsReporterApi()
            .withViolations(violations)
            .withMaxLineColumnWidth(this.maxLineColumnWidth)
            .withMaxMessageColumnWidth(this.maxMessageColumnWidth)
            .withMaxReporterColumnWidth(this.maxReporterColumnWidth) //
            .withMaxRuleColumnWidth(this.maxRuleColumnWidth) //
            .withMaxSeverityColumnWidth(this.maxSeverityColumnWidth) //
            .getReport(this.diffDetailLevel);

    if (tooManyViolations) {
      System.err.println("\nViolations in repo\n\n" + report);
      throw new ScriptException(
          "Too many violations found in diff, max is "
              + this.maxViolations
              + " but found "
              + violations.size());
    } else {
      if (this.diffPrintViolations) {
        System.out.println("\nViolations in diff\n\n" + report);
      }
    }
  }

  private Set<Violation> getAllViolationsInDiff(final Set<Violation> unfilteredViolations)
      throws Exception {
    if (!this.isDefined(this.diffFrom) || !this.isDefined(this.diffTo)) {
      // No references specified, will not report violations in diff
      return new TreeSet<>();
    } else {
      final Set<Violation> candidates =
          this.getFiltered(unfilteredViolations, this.diffMinSeverity);
      return new ViolationsGit(this.violationsLogger,candidates) //
          .getViolationsInChangeset(this.gitRepo, this.diffFrom, this.diffTo);
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

    Parser parser = null;
    try {
      parser = Parser.valueOf(configuredViolation.get(0));
    } catch (final Exception e) {
      throw new RuntimeException(
          Arrays.asList(Parser.values())
              .stream()
              .map((it) -> it.toString())
              .collect(Collectors.joining("\n")),
          e);
    }
    final Set<Violation> parsedViolations =
        violationsApi() //
            .withViolationsLogger(this.violationsLogger) //
            .findAll(parser) //
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
        + this.violations
        + ", minSeverity="
        + this.minSeverity
        + ", detailLevel="
        + this.detailLevel
        + ", maxViolations="
        + this.maxViolations
        + ", printViolations="
        + this.printViolations
        + ", diffFrom="
        + this.diffFrom
        + ", diffTo="
        + this.diffTo
        + ", diffMinSeverity="
        + this.diffMinSeverity
        + ", gitRepo="
        + this.gitRepo
        + ", diffPrintViolations="
        + this.diffPrintViolations
        + ", diffMaxViolations="
        + this.diffMaxViolations
        + ", diffDetailLevel="
        + this.diffDetailLevel
        + ", maxReporterColumnWidth="
        + this.maxReporterColumnWidth
        + ", maxRuleColumnWidth="
        + this.maxRuleColumnWidth
        + ", maxSeverityColumnWidth="
        + this.maxSeverityColumnWidth
        + ", maxLineColumnWidth="
        + this.maxLineColumnWidth
        + ", maxMessageColumnWidth="
        + this.maxMessageColumnWidth
        + "]";
  }
}
