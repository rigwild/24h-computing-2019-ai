package org.kaleeis_bears.ai.commandline.arguments;

public abstract class AbstractArgDefinition<T> implements ArgDefinition<T> {
  public final String name, description;

  public AbstractArgDefinition(String name, String description) {
    this.name = name;
    this.description = description;
  }

  @Override
  public final String getName() {
    return this.name;
  }

  public abstract T compute(String value) throws Throwable;

  public abstract String toString();
}
