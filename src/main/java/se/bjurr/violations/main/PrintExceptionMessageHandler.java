package se.bjurr.violations.main;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

public class PrintExceptionMessageHandler implements IExecutionExceptionHandler {

  @Override
  @SuppressFBWarnings(
      value = "INFORMATION_EXPOSURE_THROUGH_AN_ERROR_MESSAGE",
      justification =
          "This is a command line tool, the stack trace is meant to be seen by the user running"
              + " it, not exposed to a remote party.")
  public int handleExecutionException(
      final Exception ex, final CommandLine commandLine, final ParseResult parseResult)
      throws Exception {
    if (ex instanceof TooManyViolationsException) {
      System.err.println(ex.getMessage()); // NOPMD
    } else {
      ex.printStackTrace(System.err);
    }
    return 1;
  }
}
