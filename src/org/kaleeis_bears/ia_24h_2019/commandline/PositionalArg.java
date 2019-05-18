package org.kaleeis_bears.ia_24h_2019.commandline;

public class PositionalArg<T> {
  public interface Validator<T> {
    T validate(String value) throws Throwable;
  }
  
  public final String name;
  private final Validator<T> validator;

  public PositionalArg(String name, Validator<T> validator) {
    this.name = name;
    this.validator = validator;
  }

  public T compute(String value) throws Throwable {
    return this.validator.validate(value);
  }
}
