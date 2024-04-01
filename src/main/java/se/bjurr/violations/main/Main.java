package se.bjurr.violations.main;

import java.util.Arrays;
import java.util.stream.Collectors;
import picocli.CommandLine;
import se.bjurr.violations.lib.reports.Parser;

public class Main {

  public static void main(final String[] args) throws Exception {
    final CommandLine commandLine = new CommandLine(new Runner());
    commandLine.setExecutionExceptionHandler(new PrintExceptionMessageHandler());
    commandLine.parseArgs(args);
    if (commandLine.isUsageHelpRequested()) {
      final String parsers =
          Arrays.asList(Parser.values()).stream()
              .map((it) -> it.name())
              .collect(Collectors.joining(", "));
      System.out.println("Available parsers are:\n" + parsers + "\n");
    }
    System.exit(commandLine.execute(args));
  }
}
