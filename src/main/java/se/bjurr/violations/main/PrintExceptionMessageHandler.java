package se.bjurr.violations.main;

import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

public class PrintExceptionMessageHandler implements IExecutionExceptionHandler {

  @Override
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
