package org.kaleeis_bears.ai.commandline.arguments;

public interface ArgDefinition<T> {
  String getName();

  T compute(String value) throws Throwable;

  String toString();
}
