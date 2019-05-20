package org.kaleeis_bears.ai.commandline.arguments;

import org.kaleeis_bears.ai.commandline.CommandLineDefinition;

public class OptionalArgDefinition<T> extends AbstractArgDefinition<T> {
  public final T defaultValue;
  private final CommandLineDefinition.Validator<T> validator;

  public OptionalArgDefinition(String name, T defaultValue, String description, CommandLineDefinition.Validator<T> validator) {
    super(name, description);
    this.defaultValue = defaultValue;
    this.validator = validator;
  }

  public T compute(String value) throws Throwable {
    return this.validator.validate(value);
  }

  @Override
  public String toString() {
    return "<" + this.name + ">";
  }
}
