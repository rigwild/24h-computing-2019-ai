package org.kaleeis_bears.ai.commandline.arguments;

import org.kaleeis_bears.ai.commandline.CommandLineDefinition;

public class RequiredArgDefinition<T> extends AbstractArgDefinition<T> {
  private final CommandLineDefinition.Validator<T> validator;

  public RequiredArgDefinition(String name, String description, CommandLineDefinition.Validator<T> validator) {
    super(name, description);
    this.validator = validator;
  }

  public T compute(String value) throws Throwable {
    return this.validator.validate(value);
  }

  @Override
  public String toString() {
    return "[" + this.name + "]";
  }
}
