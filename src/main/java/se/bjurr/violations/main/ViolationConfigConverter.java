package se.bjurr.violations.main;

import java.util.List;
import java.util.Stack;
import picocli.CommandLine.IParameterConsumer;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;
import se.bjurr.violations.lib.reports.Parser;

class ViolationConfigConverter implements IParameterConsumer {

  @Override
  public void consumeParameters(
      final Stack<String> args, final ArgSpec argSpec, final CommandSpec commandSpec) {
    if (args.size() < 3) {
      throw new ParameterException(
          commandSpec.commandLine(),
          "Specify violation with parameters: <PARSER> <FOLDER> <REGEXP PATTERN> [NAME]");
    }
    final Parser parser = Parser.valueOf(args.pop());
    final String folder = args.pop();
    final String regexp = args.pop();
    String name = parser.name();
    if (!args.isEmpty()) {
      name = args.pop();
    }
    final List<ViolationConfig> currentValue = (List<ViolationConfig>) argSpec.getValue();
    currentValue.add(new ViolationConfig(parser, folder, regexp, name));
  }
}
