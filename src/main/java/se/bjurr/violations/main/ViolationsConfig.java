package se.bjurr.violations.main;

import java.io.File;
import java.util.List;
import se.bjurr.violations.git.ViolationsReporterDetailLevel;
import se.bjurr.violations.lib.ViolationsLogger;
import se.bjurr.violations.lib.model.SEVERITY;

public class ViolationsConfig {
  private List<ViolationConfig> violations;
  private SEVERITY minSeverity;
  private ViolationsReporterDetailLevel detailLevel;
  private Integer maxViolations;
  private boolean printViolations;
  private String diffFrom;
  private String diffTo;
  private SEVERITY diffMinSeverity;
  private String gitRepo;
  private boolean diffPrintViolations;
  private Integer diffMaxViolations;
  private ViolationsReporterDetailLevel diffDetailLevel;
  private int maxReporterColumnWidth;
  private int maxRuleColumnWidth;
  private int maxSeverityColumnWidth;
  private int maxLineColumnWidth;
  private int maxMessageColumnWidth;
  private String codeClimateFile;
  private String sarifFile;
  private String violationsFile;
  private boolean showDebugInfo;
  private ViolationsLogger violationsLogger;
  private int jacocoMinLineCount;
  private double jacocoMinCoverage;

  public ViolationsConfig() {}

  public List<ViolationConfig> getViolations() {
    return this.violations;
  }

  public void setViolations(final List<ViolationConfig> violations) {
    this.violations = violations;
  }

  public SEVERITY getMinSeverity() {
    return this.minSeverity;
  }

  public void setMinSeverity(final SEVERITY minSeverity) {
    this.minSeverity = minSeverity;
  }

  public ViolationsReporterDetailLevel getDetailLevel() {
    return this.detailLevel;
  }

  public void setDetailLevel(final ViolationsReporterDetailLevel detailLevel) {
    this.detailLevel = detailLevel;
  }

  public Integer getMaxViolations() {
    return this.maxViolations;
  }

  public void setMaxViolations(final Integer maxViolations) {
    this.maxViolations = maxViolations;
  }

  public boolean isPrintViolations() {
    return this.printViolations;
  }

  public void setPrintViolations(final boolean printViolations) {
    this.printViolations = printViolations;
  }

  public String getDiffFrom() {
    return this.diffFrom;
  }

  public void setDiffFrom(final String diffFrom) {
    this.diffFrom = diffFrom;
  }

  public String getDiffTo() {
    return this.diffTo;
  }

  public void setDiffTo(final String diffTo) {
    this.diffTo = diffTo;
  }

  public SEVERITY getDiffMinSeverity() {
    return this.diffMinSeverity;
  }

  public void setDiffMinSeverity(final SEVERITY diffMinSeverity) {
    this.diffMinSeverity = diffMinSeverity;
  }

  public String getGitRepo() {
    return this.gitRepo;
  }

  public void setGitRepo(final String gitRepo) {
    this.gitRepo = gitRepo;
  }

  public void setGitRepo(final File gitRepo) {
    this.gitRepo = this.setFileAsString(gitRepo);
  }

  public boolean isDiffPrintViolations() {
    return this.diffPrintViolations;
  }

  public void setDiffPrintViolations(final boolean diffPrintViolations) {
    this.diffPrintViolations = diffPrintViolations;
  }

  public Integer getDiffMaxViolations() {
    return this.diffMaxViolations;
  }

  public void setDiffMaxViolations(final Integer diffMaxViolations) {
    this.diffMaxViolations = diffMaxViolations;
  }

  public ViolationsReporterDetailLevel getDiffDetailLevel() {
    return this.diffDetailLevel;
  }

  public void setDiffDetailLevel(final ViolationsReporterDetailLevel diffDetailLevel) {
    this.diffDetailLevel = diffDetailLevel;
  }

  public int getMaxReporterColumnWidth() {
    return this.maxReporterColumnWidth;
  }

  public void setMaxReporterColumnWidth(final int maxReporterColumnWidth) {
    this.maxReporterColumnWidth = maxReporterColumnWidth;
  }

  public int getMaxRuleColumnWidth() {
    return this.maxRuleColumnWidth;
  }

  public void setMaxRuleColumnWidth(final int maxRuleColumnWidth) {
    this.maxRuleColumnWidth = maxRuleColumnWidth;
  }

  public int getMaxSeverityColumnWidth() {
    return this.maxSeverityColumnWidth;
  }

  public void setMaxSeverityColumnWidth(final int maxSeverityColumnWidth) {
    this.maxSeverityColumnWidth = maxSeverityColumnWidth;
  }

  public int getMaxLineColumnWidth() {
    return this.maxLineColumnWidth;
  }

  public void setMaxLineColumnWidth(final int maxLineColumnWidth) {
    this.maxLineColumnWidth = maxLineColumnWidth;
  }

  public int getMaxMessageColumnWidth() {
    return this.maxMessageColumnWidth;
  }

  public void setMaxMessageColumnWidth(final int maxMessageColumnWidth) {
    this.maxMessageColumnWidth = maxMessageColumnWidth;
  }

  public String getCodeClimateFile() {
    return this.codeClimateFile;
  }

  public void setCodeClimateFile(final String codeClimateFile) {
    this.codeClimateFile = codeClimateFile;
  }

  public void setCodeClimateFile(final File codeClimateFile) {
    this.codeClimateFile = this.setFileAsString(codeClimateFile);
  }

  public void setSarifFile(final String file) {
    this.sarifFile = file;
  }

  public void setSarifFile(final File file) {
    this.sarifFile = this.setFileAsString(file);
  }

  public String getSarifFile() {
    return this.sarifFile;
  }

  public String getViolationsFile() {
    return this.violationsFile;
  }

  public void setViolationsFile(final String violationsFile) {
    this.violationsFile = violationsFile;
  }

  public void setViolationsFile(final File violationsFile) {
    this.violationsFile = this.setFileAsString(violationsFile);
  }

  public boolean isShowDebugInfo() {
    return this.showDebugInfo;
  }

  public void setShowDebugInfo(final boolean showDebugInfo) {
    this.showDebugInfo = showDebugInfo;
  }

  public ViolationsLogger getViolationsLogger() {
    return this.violationsLogger;
  }

  public void setViolationsLogger(final ViolationsLogger violationsLogger) {
    this.violationsLogger = violationsLogger;
  }

  public double getJacocoMinCoverage() {
    return this.jacocoMinCoverage;
  }

  public void setJacocoMinCoverage(final double jacocoMinCoverage) {
    this.jacocoMinCoverage = jacocoMinCoverage;
  }

  public int getJacocoMinLineCount() {
    return this.jacocoMinLineCount;
  }

  public void setJacocoMinLineCount(final int jacocoMinLineCount) {
    this.jacocoMinLineCount = jacocoMinLineCount;
  }

  private String setFileAsString(final File from) {
    if (from == null) {
      return null;
    }
    return from.getAbsolutePath();
  }

  @Override
  public String toString() {
    return "ViolationsConfig [violations="
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
        + ", codeClimateFile="
        + this.codeClimateFile
        + ", sarifFile="
        + this.sarifFile
        + ", violationsFile="
        + this.violationsFile
        + ", showDebugInfo="
        + this.showDebugInfo
        + ", violationsLogger="
        + this.violationsLogger
        + ", jacocoMinLineCount="
        + this.jacocoMinLineCount
        + ", jacocoMinCoverage="
        + this.jacocoMinCoverage
        + "]";
  }
}
