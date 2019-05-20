package org.kaleeis_bears.ai.commandline;

import org.kaleeis_bears.ai.commandline.arguments.AbstractArgDefinition;
import org.kaleeis_bears.ai.commandline.arguments.ArgList;
import org.kaleeis_bears.ai.commandline.arguments.OptionalArgDefinition;
import org.kaleeis_bears.ai.commandline.arguments.RequiredArgDefinition;
import org.kaleeis_bears.ai.commandline.options.OptionDefinition;
import org.kaleeis_bears.ai.commandline.options.OptionValue;

import java.util.HashMap;
import java.util.Map;

public class CommandLineDefinition extends ArgList {
  public final String header, filename;
  public final OptionDefinition[] options;
  private final int optionPadding;
  private final Map<String, Integer> optionNameToIndex = new HashMap<>();
  private final Map<Character, Integer> optionShortNameToIndex = new HashMap<>();

  public CommandLineDefinition(String header, String filename, OptionDefinition[] options, RequiredArgDefinition<?>[] required, OptionalArgDefinition<?>[] optional) {
    super(required, optional);
    this.options = options;
    this.header = header;
    this.filename = filename;
    int optionPadding = 0;
    for (int i = 0; i < options.length; i++) {
      if (options[i].shortName != null) {
        if (this.optionShortNameToIndex.containsKey(options[i].shortName))
          throw new IllegalArgumentException("L'option « -" + options[i].shortName + " » est définie plusieurs fois.");
        this.optionShortNameToIndex.put(options[i].shortName, i);
      }
      final String name = options[i].name;
      if (this.optionNameToIndex.containsKey(name))
        throw new IllegalArgumentException("L'option « --" + name + " » est définie plusieurs fois.");
      this.optionNameToIndex.put(name, i);
      final int fullnameLength = options[i].toString().length();
      if (fullnameLength > optionPadding)
        optionPadding = fullnameLength;
    }
    this.optionPadding = optionPadding;
  }

  public static String IDENTITY_VALIDATOR(final String value) {
    return value;
  }

  public static Integer INTEGER_VALIDATOR(final String value) {
    return new Integer(value);
  }

  public static Validator<Integer> INTEGER_RANGE_VALIDATOR(final int minValue, final int maxValue) {
    return INTEGER_RANGE_VALIDATOR(CommandLineDefinition::INTEGER_VALIDATOR, minValue, maxValue);
  }

  public static Validator<Integer> INTEGER_RANGE_VALIDATOR(final Validator<Integer> parser, final int minValue, final int maxValue) {
    return value -> {
      Integer number = parser.validate(value);
      if (number < minValue || number >= maxValue)
        throw new IndexOutOfBoundsException("doit être compris entre " + minValue + " et " + (maxValue - 1));
      return number;
    };
  }

  public String help() {
    final StringBuilder builder = new StringBuilder();
    builder
        .append(this.header).append("\n")
        .append("\n")
        .append("Usage : ").append(this.filename).append(this.options.length == 0 ? "" : " <options>").append(super.toString()).append("\n")
        .append("\n")
        .append(super.help());
    if (this.options.length != 0) {
      builder.append("\nOptions possibles :\n");
      final boolean requireShortNamePadding = this.optionShortNameToIndex.size() != 0;
      for (OptionDefinition option : this.options) {
        if (requireShortNamePadding)
          if (option.shortName == null)
            builder.append("    ");
          else
            builder.append(" -").append(option.shortName).append(",");
        builder.append(" --").append(String.format("%-" + this.optionPadding + "s", option.toString())).append(" : ").append(option.description).append("\n");
      }
    }
    return builder.toString();
  }

  public CommandLineResult parse(String[] args) throws Throwable {
    final CommandLineResult result = new CommandLineResult(
        new OptionValue[this.options.length],
        new Object[this.required.length + this.optional.length]
    );
    for (int i = 0; i < result.options.length; i++)
      result.options[i] = new OptionValue(false, null);
    int positionalArgNo = 0;
    String arg;
    for (int i = 0; i < args.length; i++) {
      arg = args[i];
      if (arg.startsWith("-"))
        if (arg.startsWith("--")) {
          final String name = arg.substring(2);
          if (!this.optionNameToIndex.containsKey(name))
            throw new IllegalArgumentException("L'option « --" + name + " » n'est pas reconnue.");
          final int index = this.optionNameToIndex.get(name);
          final OptionDefinition definition = this.options[index];
          Object[] argsValues = null;
          if (definition.args != null) {
            argsValues = new Object[definition.args.required.length + definition.args.optional.length];
            int j = 0;
            for (; j < argsValues.length && (i + j + 1) < args.length; j++) {
              if (j >= definition.args.required.length && args[i + j + 1].startsWith("-"))
                break;
              final AbstractArgDefinition<?> optionArgDef = j < definition.args.required.length ? definition.args.required[j] : definition.args.optional[j];
              argsValues[j] = optionArgDef.compute(args[i + j + 1]);
            }
            if (j < definition.args.required.length) {
              final StringBuilder builder = new StringBuilder();
              for (; j < definition.args.required.length; j++)
                builder.append(builder.length() == 0 ? "" : ", ").append(definition.args.required[j].name);
              throw new IllegalArgumentException("L'option « --" + name + " » requière les arguments manquants suivants : " + builder);
            }
            i += j;
          }
          result.options[index] = new OptionValue(true, argsValues);
        } else
          for (int j = 1; j < arg.length(); j++) {
            final Character character = arg.charAt(j);
            if (!this.optionShortNameToIndex.containsKey(character))
              throw new IllegalArgumentException("L'option « -" + character + " » n'est pas reconnue.");
            final int index = this.optionShortNameToIndex.get(character);
            final OptionDefinition definition = this.options[index];
            Object[] argsValues = null;
            if (definition.args != null) {
              argsValues = new Object[definition.args.required.length + definition.args.optional.length];
              int k = 0;
              for (; k < argsValues.length && (i + k + 1) < args.length; k++) {
                if (k >= definition.args.required.length && args[i + k + 1].startsWith("-"))
                  break;
                final AbstractArgDefinition<?> optionArgDef = k < definition.args.required.length ? definition.args.required[k] : definition.args.optional[k];
                argsValues[k] = optionArgDef.compute(args[i + k + 1]);
              }
              if (k < definition.args.required.length) {
                final StringBuilder builder = new StringBuilder();
                for (; k < definition.args.required.length; k++)
                  builder.append(builder.length() == 0 ? "" : ", ").append(definition.args.required[k].name);
                throw new IllegalArgumentException("L'option « -" + character + " » requière les arguments manquants suivants : " + builder);
              }
              i += k;
            }
            result.options[index] = new OptionValue(true, argsValues);
          }
      else {
        if (positionalArgNo >= result.arguments.length)
          throw new IllegalArgumentException("L'argument « " + arg + " » est en trop");
        AbstractArgDefinition<?> argDefinition = positionalArgNo < this.required.length ? this.required[positionalArgNo] : this.optional[positionalArgNo - this.required.length];
        try {
          result.arguments[positionalArgNo] = argDefinition.compute(arg);
        } catch (NumberFormatException ex) {
          throw new IllegalArgumentException("L'argument « " + argDefinition.name + " » doit être un nombre");
        } catch (Exception ex) {
          throw new IllegalArgumentException("L'argument « " + argDefinition.name + " » " + ex.getMessage());
        }
        positionalArgNo++;
      }
    }
    if (args.length != 0 && positionalArgNo < this.required.length) {
      StringBuilder builder = new StringBuilder();
      for (int i = positionalArgNo; i < this.required.length; i++)
        builder.append(i == positionalArgNo ? "" : ", ").append(this.required[i].name);
      throw new IllegalArgumentException("Les arguments suivants sont manquants : " + builder);
    }
    return (positionalArgNo >= this.required.length) ? result : null;
  }

  public interface Validator<T> {
    T validate(String value) throws Throwable;
  }

  public class CommandLineResult {
    private final OptionValue[] options;
    private final Object[] arguments;

    public CommandLineResult(OptionValue[] options, Object[] arguments) {
      this.options = options;
      this.arguments = arguments;
    }

    private OptionValue getOptionFromName(String name) {
      return this.options[CommandLineDefinition.this.optionNameToIndex.get(name)];
    }

    public boolean isOptionSet(int index) {
      return this.options[index].isSet;
    }

    public boolean isOptionSet(String name) {
      return this.getOptionFromName(name).isSet;
    }

    public Object getOptionArgument(int index, String argumentName) {
      final OptionDefinition option = CommandLineDefinition.this.options[index];
      final int argumentIndex = option.args.getIndex(argumentName);
      return this.options[index].argsValue[argumentIndex];
    }

    public Object getOptionArgument(String optionName, String argumentName) {
      return this.getOptionArgument(CommandLineDefinition.this.optionNameToIndex.get(optionName), argumentName);
    }

    public Object getArgument(int index) {
      return this.arguments[index];
    }

    public Object getArgument(String name) {
      return this.getArgument(CommandLineDefinition.this.argumentNameToIndex.get(name));
    }

  }
}