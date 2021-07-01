package se.bjurr.violations.main;

import java.io.File;
import java.util.List;
import se.bjurr.violations.git.ViolationsReporterDetailLevel;
import se.bjurr.violations.lib.ViolationsLogger;
import se.bjurr.violations.lib.model.SEVERITY;

public class ViolationsConfig {
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

  public ViolationsConfig() {}

  public List<List<String>> getViolations() {
    return violations;
  }

  public void setViolations(List<List<String>> violations) {
    this.violations = violations;
  }

  public SEVERITY getMinSeverity() {
    return minSeverity;
  }

  public void setMinSeverity(SEVERITY minSeverity) {
    this.minSeverity = minSeverity;
  }

  public ViolationsReporterDetailLevel getDetailLevel() {
    return detailLevel;
  }

  public void setDetailLevel(ViolationsReporterDetailLevel detailLevel) {
    this.detailLevel = detailLevel;
  }

  public Integer getMaxViolations() {
    return maxViolations;
  }

  public void setMaxViolations(Integer maxViolations) {
    this.maxViolations = maxViolations;
  }

  public boolean isPrintViolations() {
    return printViolations;
  }

  public void setPrintViolations(boolean printViolations) {
    this.printViolations = printViolations;
  }

  public String getDiffFrom() {
    return diffFrom;
  }

  public void setDiffFrom(String diffFrom) {
    this.diffFrom = diffFrom;
  }

  public String getDiffTo() {
    return diffTo;
  }

  public void setDiffTo(String diffTo) {
    this.diffTo = diffTo;
  }

  public SEVERITY getDiffMinSeverity() {
    return diffMinSeverity;
  }

  public void setDiffMinSeverity(SEVERITY diffMinSeverity) {
    this.diffMinSeverity = diffMinSeverity;
  }

  public File getGitRepo() {
    return gitRepo;
  }

  public void setGitRepo(File gitRepo) {
    this.gitRepo = gitRepo;
  }

  public boolean isDiffPrintViolations() {
    return diffPrintViolations;
  }

  public void setDiffPrintViolations(boolean diffPrintViolations) {
    this.diffPrintViolations = diffPrintViolations;
  }

  public Integer getDiffMaxViolations() {
    return diffMaxViolations;
  }

  public void setDiffMaxViolations(Integer diffMaxViolations) {
    this.diffMaxViolations = diffMaxViolations;
  }

  public ViolationsReporterDetailLevel getDiffDetailLevel() {
    return diffDetailLevel;
  }

  public void setDiffDetailLevel(ViolationsReporterDetailLevel diffDetailLevel) {
    this.diffDetailLevel = diffDetailLevel;
  }

  public int getMaxReporterColumnWidth() {
    return maxReporterColumnWidth;
  }

  public void setMaxReporterColumnWidth(int maxReporterColumnWidth) {
    this.maxReporterColumnWidth = maxReporterColumnWidth;
  }

  public int getMaxRuleColumnWidth() {
    return maxRuleColumnWidth;
  }

  public void setMaxRuleColumnWidth(int maxRuleColumnWidth) {
    this.maxRuleColumnWidth = maxRuleColumnWidth;
  }

  public int getMaxSeverityColumnWidth() {
    return maxSeverityColumnWidth;
  }

  public void setMaxSeverityColumnWidth(int maxSeverityColumnWidth) {
    this.maxSeverityColumnWidth = maxSeverityColumnWidth;
  }

  public int getMaxLineColumnWidth() {
    return maxLineColumnWidth;
  }

  public void setMaxLineColumnWidth(int maxLineColumnWidth) {
    this.maxLineColumnWidth = maxLineColumnWidth;
  }

  public int getMaxMessageColumnWidth() {
    return maxMessageColumnWidth;
  }

  public void setMaxMessageColumnWidth(int maxMessageColumnWidth) {
    this.maxMessageColumnWidth = maxMessageColumnWidth;
  }

  public File getCodeClimateFile() {
    return codeClimateFile;
  }

  public void setCodeClimateFile(File codeClimateFile) {
    this.codeClimateFile = codeClimateFile;
  }

  public File getViolationsFile() {
    return violationsFile;
  }

  public void setViolationsFile(File violationsFile) {
    this.violationsFile = violationsFile;
  }

  public boolean isShowDebugInfo() {
    return showDebugInfo;
  }

  public void setShowDebugInfo(boolean showDebugInfo) {
    this.showDebugInfo = showDebugInfo;
  }

  public ViolationsLogger getViolationsLogger() {
    return violationsLogger;
  }

  public void setViolationsLogger(ViolationsLogger violationsLogger) {
    this.violationsLogger = violationsLogger;
  }
}
