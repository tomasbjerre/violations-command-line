package se.bjurr.violations.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.bjurr.violations.lib.reports.Parser;

public class ViolationConfig {

  private final Parser parser;
  private final String folder;
  private final String regexp;
  private final String name;

  @JsonCreator
  public ViolationConfig(
      @JsonProperty("parser") final Parser parser,
      @JsonProperty("folder") final String folder,
      @JsonProperty("regexp") final String regexp,
      @JsonProperty("name") final String name) {
    this.parser = parser;
    this.folder = folder;
    this.regexp = regexp;
    this.name = name;
  }

  public String getFolder() {
    return this.folder;
  }

  public String getName() {
    return this.name;
  }

  public Parser getParser() {
    return this.parser;
  }

  public String getRegexp() {
    return this.regexp;
  }

  @Override
  public String toString() {
    return "ViolationConfig [parser="
        + this.parser
        + ", folder="
        + this.folder
        + ", regexp="
        + this.regexp
        + ", name="
        + this.name
        + "]";
  }
}
